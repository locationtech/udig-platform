/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui.operations;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.core.AdapterUtil;
import org.locationtech.udig.internal.ui.URLImageDescriptor;
import org.locationtech.udig.internal.ui.UiPlugin;
import org.locationtech.udig.internal.ui.operations.OperationCategory;
import org.locationtech.udig.internal.ui.operations.OperationMenuFactory;
import org.locationtech.udig.ui.internal.Messages;
import org.locationtech.udig.ui.operations.EnablementUtil.EnablesForData;

/**
 * Creates an Action that runs an operation in a background thread when triggered.
 *
 * @author jeichar
 * @since 0.3
 */
public class OpAction extends Action implements ISelectionListener {
    IConfigurationElement configElem;

    volatile IStructuredSelection selection;

    private IOp operation;

    private RunJob runJob;

    private String targetClass;

    OperationCategory category;

    String menuPath;

    private OpFilter filter;

    private EnablesForData enablesForData;

    private volatile NullProgressMonitor monitor = new NullProgressMonitor();

    private boolean loadingError;

    private static final Executor executor = Executors.newFixedThreadPool(1);

    /**
     * Subclasses must have the same constructor signature as this constructor,
     * {@linkplain OperationMenuFactory} creates OpActions using this constructor.
     *
     * Construct <code>OpAction</code>.
     *
     */
    public OpAction(IConfigurationElement element) {
        this.configElem = element;
        setId(element.getAttribute("id")); //$NON-NLS-1$
        setText(element.getAttribute("name")); //$NON-NLS-1$
        String icon = element.getAttribute("icon"); //$NON-NLS-1$
        if (icon != null) {
            setImageDescriptor(createImageDescriptor(icon, element));
        }
        String tooltip = element.getAttribute("tooltip"); //$NON-NLS-1$
        if (tooltip != null) {
            setToolTipText(tooltip);
        }
        this.targetClass = element.getAttribute("targetClass"); //$NON-NLS-1$
        this.menuPath = element.getAttribute("menuPath"); //$NON-NLS-1$
        this.runJob = new RunJob(getText());
        enablesForData = EnablementUtil.parseEnablesFor(element.getAttribute("enablesFor"), //$NON-NLS-1$
                configElem);
        filter = EnablementUtil.parseEnablement(
                element.getNamespaceIdentifier() + "." + element.getName(), //$NON-NLS-1$
                element.getChildren("enablement")); //$NON-NLS-1$
    }

    private ImageDescriptor createImageDescriptor(String icon, IConfigurationElement element) {
        URL url = Platform.getBundle(element.getNamespaceIdentifier()).getEntry(icon);
        return new URLImageDescriptor(url);
    }

    @Override
    public void run() {
        runJob.display = Display.getCurrent();
        runJob.schedule();
    }

    @Override
    public void runWithEvent(Event event) {
        runJob.display = event.display;
        runJob.schedule();
    }

    IOp getOperation() {
        if (operation == null) {
            try {
                operation = (IOp) configElem.createExecutableExtension("class"); //$NON-NLS-1$
            } catch (CoreException e) {
                UiPlugin.log("Error loading operation implementation", e); //$NON-NLS-1$
                final Display display = Display.getDefault();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        setEnabled(false);
                        MessageDialog.openError(display.getActiveShell(),
                                Messages.OpAction_errorTitle, Messages.OpAction_errorMessage);
                    }
                };
                loadingError = true;
                display.asyncExec(runnable);
            }
        }

        return operation;
    }

    class RunJob extends Job {
        Display display;

        /**
         * Construct <code>RunJob</code>.
         *
         * @param name
         */
        public RunJob(String name) {
            super(name);
        }

        @Override
        public boolean belongsTo(Object family) {
            return family == OpAction.class;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            if (selection == null) {
                return new Status(IStatus.CANCEL, UiPlugin.ID, "OpAction", //$NON-NLS-1$
                        new IllegalArgumentException());
            }
            try {
                Object target;
                if (enablesForData.minHits == 1 && enablesForData.exactMatch) {
                    try {
                        target = AdapterUtil.instance.adapt(targetClass,
                                selection.getFirstElement(), monitor);
                        if (target == null) {
                            UiPlugin.log("Factory adapting " //$NON-NLS-1$
                                    + selection.getFirstElement().getClass().getName() + " to a " + //$NON-NLS-1$
                                    targetClass
                                    + " is returning null even though it is advertising that it can " //$NON-NLS-1$
                                    + "do the adaptation", null);
                            return Status.OK_STATUS;
                        }
                    } catch (Throwable e) {
                        UiPlugin.log(null, e);
                        return Status.OK_STATUS;
                    }
                } else {
                    List<Object> targets = new LinkedList<>();
                    for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
                        Object entry = iter.next();
                        try {
                            Object operationTarget = AdapterUtil.instance.adapt(targetClass, entry,
                                    monitor);
                            if (operationTarget == null) {
                                UiPlugin.log("Factory adapting " + entry.getClass().getName() //$NON-NLS-1$
                                        + " to a " + //$NON-NLS-1$
                                        targetClass
                                        + " is returning null even though it is advertising that it can " //$NON-NLS-1$
                                        + "do the adaptation", null);
                                return Status.OK_STATUS;
                            } else {
                                targets.add(operationTarget);
                            }
                        } catch (Throwable e) {
                            UiPlugin.log(null, e);
                            return Status.OK_STATUS;
                        }
                    }
                    Class<?> targetClass = targets.get(0).getClass();
                    Object[] tmp = (Object[]) Array.newInstance(targetClass, targets.size());
                    if (enablesForData.minHits == 1 && enablesForData.exactMatch) {
                        target = !targets.isEmpty() ? targets.get(0) : null;
                    } else {
                        target = targets.toArray(tmp);
                    }

                }
                IOp op = getOperation();
                op.op(display, target, monitor);
            } catch (Throwable e) {
                UiPlugin.log(null, e);
            }
            return Status.OK_STATUS;
        }

    }

    /**
     * Determines whether the current operation can operate on the object.
     *
     * @return true if object can be used as the operation input.
     */
    public boolean isValid(Object obj) {
        return !this.loadingError && AdapterUtil.instance.canAdaptTo(targetClass, obj)
                && filter.accept(obj);
    }

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            synchronized (this) {
                IStructuredSelection structured = (IStructuredSelection) selection;

                updateEnablement(structured, false);

            }
        }
    }

    /**
     * Updates the enablement status of this action.
     *
     * @param structured the selection to use to determine the enablement.
     * @param executeSynchronous
     */
    public void updateEnablement(IStructuredSelection structured, boolean executeSynchronous) {
        if (PlatformUI.getWorkbench().isClosing())
            return;

        NullProgressMonitor lastMonitor = monitor;
        monitor = new NullProgressMonitor();
        lastMonitor.setCanceled(true);

        SetEnablement enablement = new SetEnablement(structured, monitor, executeSynchronous);
        if (!executeSynchronous) {
            setEnabled(false);
            executor.execute(enablement);
        } else {
            enablement.run();
        }
    }

    public void setCategory(OperationCategory category) {
        this.category = category;
    }

    public String getMenuPath() {
        return menuPath;
    }

    @Override
    public String toString() {
        return getMenuPath() + "/" + getText(); //$NON-NLS-1$
    }

    private class SetEnablement implements Runnable {

        private IStructuredSelection structured;

        private IProgressMonitor monitor;

        private boolean execSync;

        public SetEnablement(IStructuredSelection structured, IProgressMonitor monitor,
                boolean executeSynchronous) {
            this.structured = structured;
            this.monitor = monitor;
            this.execSync = executeSynchronous;
        }

        @Override
        public void run() {
            if (PlatformUI.getWorkbench().isClosing())
                return;
            boolean enabled = false;
            int hits = 0;

            for (Iterator<?> iter = structured.iterator(); iter.hasNext();) {
                if (monitor.isCanceled())
                    return;
                Object obj = iter.next();
                if (isValid(obj)) {
                    hits++;
                } else {
                    hits = -1;
                    break;
                }
            }

            if (monitor.isCanceled())
                return;

            if (hits >= enablesForData.minHits) {
                if (enablesForData.exactMatch && hits == enablesForData.minHits) {
                    enabled = true;
                } else if (!enablesForData.exactMatch && hits >= enablesForData.minHits) {
                    enabled = true;
                }
            }

            final boolean finalEnabled = enabled;
            final boolean oldEnabledState = isEnabled();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    OpAction.this.selection = structured;

                    setEnabled(finalEnabled);

                    if (category != null && oldEnabledState != finalEnabled) {
                        category.enablementChanged();
                    }
                }
            };
            if (execSync) {
                if (Display.getCurrent() != null) {
                    runnable.run();
                } else {
                    Display.getDefault().syncExec(runnable);
                }
            } else {
                Display.getDefault().asyncExec(runnable);
            }
        }
    }
}
