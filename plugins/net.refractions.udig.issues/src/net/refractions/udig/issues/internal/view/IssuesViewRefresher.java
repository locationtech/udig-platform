/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.issues.internal.view;

import net.refractions.udig.issues.IRefreshControl;
import net.refractions.udig.issues.IssueConstants;
import net.refractions.udig.issues.internal.IssuesActivator;
import net.refractions.udig.ui.PlatformGIS;

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

    public void refresh() {
        refresh(true);
    }

    public void refresh( boolean updateLabels ) {
        getView().refresh(updateLabels);
    }

    public void refresh( Object element ) {
        refresh(element);
    }

    public void refresh( Object element, boolean updateLabels ) {
        getView().refresh(element, updateLabels);
    }

    private IssuesView getView() {
        final IViewPart[] view =new IViewPart[1];
        Runnable runnable = new Runnable(){
            public void run() {
                view[0]=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(IssueConstants.VIEW_ID);
                if( view[0]==null ){
                    try {
                        view[0]=PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(IssueConstants.VIEW_ID);
                    } catch (PartInitException e) {
                        IssuesActivator.log("Error finding issues view", e); //$NON-NLS-1$
                    }
                }
            }
        };

            PlatformGIS.syncInDisplayThread(runnable);

        return (IssuesView) view[0];
    }

}
