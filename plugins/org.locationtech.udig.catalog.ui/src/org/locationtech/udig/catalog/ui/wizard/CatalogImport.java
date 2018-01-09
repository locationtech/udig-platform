/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.ui.wizard;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.internal.ui.ConnectionPageDecorator;
import org.locationtech.udig.catalog.internal.ui.ReflectionWorkflowWizardPageProvider;
import org.locationtech.udig.catalog.ui.ConnectionErrorPage;
import org.locationtech.udig.catalog.ui.DataSourceSelectionPage;
import org.locationtech.udig.catalog.ui.internal.Messages;
import org.locationtech.udig.catalog.ui.workflow.BasicWorkflowWizardPageFactory;
import org.locationtech.udig.catalog.ui.workflow.ConnectionErrorState;
import org.locationtech.udig.catalog.ui.workflow.ConnectionFailurePage;
import org.locationtech.udig.catalog.ui.workflow.ConnectionFailureState;
import org.locationtech.udig.catalog.ui.workflow.DataSourceSelectionState;
import org.locationtech.udig.catalog.ui.workflow.EndConnectionState;
import org.locationtech.udig.catalog.ui.workflow.IntermediateState;
import org.locationtech.udig.catalog.ui.workflow.State;
import org.locationtech.udig.catalog.ui.workflow.Workflow;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizard;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardAdapter;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardDialog;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPage;
import org.locationtech.udig.catalog.ui.workflow.WorkflowWizardPageProvider;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.PlatformUI;

/**
 * Used to configure a {@link WorkflowWizardDialog} and {@link WorkflowWizard} with an initial
 * workflow provided by {@link #createWorkflow()}.
 * <p>
 * Example use: dragging and dropping a URL into the catalog.
 * </p>
 * 
 * @since 1.2.0
 */
public class CatalogImport {

    Shell shell;

    private WorkflowWizardDialog dialog;

    protected WorkflowWizard wizard;

    public CatalogImport() {
        initWorkflow();
    }

    private void initDialog() {
        if (Display.getCurrent() == null) {
            PlatformGIS.syncInDisplayThread(new Runnable() {
                public void run() {
                    shell = createShell();
                }
            });
        } else {
            shell = createShell();
        }

        dialog = new WorkflowWizardDialog(shell, wizard);
        dialog.setBlockOnOpen(true);
    }

    private void initWorkflow() {
        Workflow workflow = createWorkflow();
        Map<Class<? extends State>, WorkflowWizardPageProvider> map = createPageMapping();
        wizard = createWorkflowWizard(workflow, map);
    }

    /**
     * Must be called in the Display thread.
     */
    Shell createShell() {
        Display d = PlatformUI.getWorkbench().getDisplay();
        if (d == null)
            d = Display.getCurrent();
        Shell parent = d.getActiveShell();
        return parent != null ? parent : new Shell(parent);
    }

    public WorkflowWizardDialog getDialog() {
        if (dialog == null) {
            initDialog();
        }

        return dialog;
    }

    public void open() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                dialog.open();
            };
        });
    }

    /**
     * Runs the workflow.
     * 
     * @param monitor the monitor for
     * @param context
     * @return
     */
    public boolean run(IProgressMonitor monitor, Object context) {
        if (dialog == null) {
            initDialog();
        }
        dialog.getWorkflowWizard().getWorkflow().setContext(context);
        String bind = MessageFormat.format(Messages.CatalogImport_monitor_task,
                new Object[] { format(context) });
        monitor.beginTask(bind, IProgressMonitor.UNKNOWN);
        monitor.setTaskName(bind);
        try {
            return dialog.runHeadless(new SubProgressMonitor(monitor, 100));
        } finally {
            monitor.done();
        }
    }

    private String format(Object data) {
        if (data instanceof URL) {
            return formatURL((URL) data);
        }
        if (data instanceof IGeoResource) {
            return ((IGeoResource) data).getIdentifier().getRef();
        }
        if (data instanceof IResolve) {
            return formatURL(((IResolve) data).getIdentifier());
        }
        return data.toString();
    }

    private String formatURL(URL url) {
        return url.getProtocol() + "://" + url.getPath(); //$NON-NLS-1$
    }
    /**
     * Workflow used when configuring {@link #wizard}.
     * <p>
     * Override to provide a custom workflow, be sure that all states mentioned
     * in your workflow are covered by the {@link #createPageMapping()} method.
     * 
     * @return workflow
     */
    protected Workflow createWorkflow() {
        DataSourceSelectionState state = new DataSourceSelectionState(false);
        Workflow workflow = new Workflow(new State[] { state });

        return workflow;
    }
    /**
     * Page mapping workflow states required for operation to the workflow wizard
     * responsible for any user interaction required to complete that state.
     * <p>
     * Subclasses can override to account for any states added in {@link #createWorkflow()}.
     * <p>
     * <ul>
     * <li>{@link #pageProvider(Class)} register class (suitable for lazy creation)
     * <li>{@link #pageProvider(WorkflowWizardPage)} register a ready to use page
     * </ul>
     * 
     * @return mapping between workflow states and wizard pages required for any interaction
     */
    protected Map<Class<? extends State>, WorkflowWizardPageProvider> createPageMapping() {
        HashMap<Class<? extends State>, WorkflowWizardPageProvider> map = new HashMap<Class<? extends State>, WorkflowWizardPageProvider>();

        addToMap(map, DataSourceSelectionState.class, DataSourceSelectionPage.class);

        WorkflowWizardPageProvider provider = pageProvider( ConnectionPageDecorator.class );
        
        map.put(IntermediateState.class, provider);
        map.put(EndConnectionState.class, provider);

        addToMap(map, ConnectionErrorState.class, ConnectionErrorPage.class);
        addToMap(map, ConnectionFailureState.class, ConnectionFailurePage.class);
        
        return map;
    }
    protected void addToMap(Map<Class<? extends State>, WorkflowWizardPageProvider> map,
            Class<? extends State> key, Class<? extends WorkflowWizardPage> workflowPage) {
        WorkflowWizardPageProvider pageFactory = pageProvider(workflowPage);
        map.put(key, pageFactory);
    }
    protected WorkflowWizardPageProvider pageProvider(Class< ? extends WorkflowWizardPage> workflowPage ){
        return new ReflectionWorkflowWizardPageProvider( workflowPage );
    }
    protected WorkflowWizardPageProvider pageProvider( WorkflowWizardPage page ){
        return new BasicWorkflowWizardPageFactory( page );
    }
    
    /**
     * Override for custom wizard implementation. 
     * 
     * @param workflow
     * @param map
     * @return Implementation of WorkflowWizard being configured
     */
    protected WorkflowWizard createWorkflowWizard(Workflow workflow,
            Map<Class<? extends State>, WorkflowWizardPageProvider> map) {
        return new CatalogImportWizard(workflow, map);
    }

    /**
     * Extends {@link WorkflowWizardAdapter} by passing the CatalogImport wizard to the constructor.
     * 
     * @author jesse
     * @since 1.1.0
     */
    public static class CatalogImportAdapter extends WorkflowWizardAdapter implements IImportWizard {

        public CatalogImportAdapter() {
            super(new CatalogImport().wizard);
        }
    }

}
