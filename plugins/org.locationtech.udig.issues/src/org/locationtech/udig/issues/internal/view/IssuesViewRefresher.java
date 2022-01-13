/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.issues.internal.view;

import org.locationtech.udig.core.logging.LoggingSupport;
import org.locationtech.udig.issues.IRefreshControl;
import org.locationtech.udig.issues.IssueConstants;
import org.locationtech.udig.issues.internal.IssuesActivator;
import org.locationtech.udig.ui.PlatformGIS;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Handles refreshing the issues viewer
 *
 * @author Jesse
 * @since 1.1.0
 */
public class IssuesViewRefresher implements IRefreshControl {

    IssuesView view;

    @Override
    public void refresh() {
        refresh(true);
    }

    @Override
    public void refresh( boolean updateLabels ) {
        getView().refresh(updateLabels);
    }

    @Override
    public void refresh( Object element ) {
        refresh(element);
    }

    @Override
    public void refresh( Object element, boolean updateLabels ) {
        getView().refresh(element, updateLabels);
    }

    private IssuesView getView() {
        final IViewPart[] view =new IViewPart[1];
        Runnable runnable = new Runnable(){
            @Override
            public void run() {
                view[0]=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IssueConstants.VIEW_ID);
                if( view[0]==null ){
                    try {
                        view[0]=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IssueConstants.VIEW_ID);
                    } catch (PartInitException e) {
                        LoggingSupport.log(IssuesActivator.getDefault(), "Error finding issues view", e); //$NON-NLS-1$
                    }
                }
            }
        };

            PlatformGIS.syncInDisplayThread(runnable);

        return (IssuesView) view[0];
    }

}
