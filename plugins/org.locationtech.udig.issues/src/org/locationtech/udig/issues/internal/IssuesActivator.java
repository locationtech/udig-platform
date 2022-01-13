/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal;

import org.locationtech.udig.core.AbstractUdigUIPlugin;
import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.issues.IIssuesManager;
import org.locationtech.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class IssuesActivator extends AbstractUdigUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.locationtech.udig.issues"; //$NON-NLS-1$

    static final String ICONS_PATH = "icons/"; //$NON-NLS-1$

    private static IssuesActivator INSTANCE;

    /**
     * The constructor
     */
    public IssuesActivator() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        INSTANCE = this;
        PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {

            @Override
            public void postShutdown(IWorkbench workbench) {
            }

            @Override
            public boolean preShutdown(IWorkbench workbench, boolean forced) {
                try {
                    IIssuesManager.defaultInstance.save(ProgressManager.instance().get());
                } catch (Exception e) {
                    LoggingSupport.log(getDefault(), "Error saving issues", e); //$NON-NLS-1$
                    boolean result = MessageDialog.openQuestion(Display.getCurrent()
                            .getActiveShell(), Messages.IssuesActivator_errorTitle,
                            Messages.IssuesActivator_errorMessage);
                    return result;

                }
                return true;
            }

        });
    }

    public static IssuesActivator getDefault() {
        return INSTANCE;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.locationtech.udig.core.AbstractUdigUIPlugin#getIconPath()
     */
    @Override
    public IPath getIconPath() {
        return new Path(ICONS_PATH);
    }

}
