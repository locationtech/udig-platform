/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionDelegate;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.ui.PlatformGIS;

/**
 * Resets a selection of services
 *
 * @author Jody Garnett
 * @since 0.8
 */
public class ResetService extends ActionDelegate {

    IStructuredSelection current;

    @Override
    public void run(IAction action) {
        if ((current == null)) {
            return;
        }
        PlatformGIS.run(new ISafeRunnable() {

            @Override
            public void handleException(Throwable exception) {
                CatalogUIPlugin.log("Error resetting: " + current, exception); //$NON-NLS-1$
            }

            @Override
            public void run() throws Exception {
                List<IService> servers = new ArrayList<>();
                for (Iterator selection = current.iterator(); selection.hasNext();) {
                    try {
                        servers.add((IService) selection.next());
                    } catch (ClassCastException huh) {
                        CatalogUIPlugin.trace("Should not happen: " + huh); //$NON-NLS-1$
                    }
                }
                reset(servers, null);
            }

        });
    }

    /**
     * Allows a list of services to be reset.
     * <p>
     * In each case a replacement service is made using the same connection parameters; the old
     * service is disposed; and the replacement placed into the catalog.
     * <p>
     * Client code listing to catalog change events will see the event fired off any client code
     * that has tried to cache the IService (to avoid doing a look up each time) will be in trouble.
     *
     * @param servers List of IService handles to reset
     * @param monitor Progress Monitor used to interrupt the command if needed
     */
    public static void reset(List<IService> servers, IProgressMonitor monitor) {
        IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();

        for (IService original : servers) {
            try {
                final ID id = original.getID();
                CatalogUIPlugin.trace("Reset service " + original.getIdentifier()); //$NON-NLS-1$

                Map<java.lang.String, java.io.Serializable> connectionParams = original
                        .getConnectionParams();

                IService replacement = null; // unknown
                TEST: for (IService candidate : serviceFactory.createService(connectionParams)) {
                    try {
                        CatalogUIPlugin.trace(id + " : connecting"); //$NON-NLS-1$
                        IServiceInfo info = candidate.getInfo(monitor);

                        CatalogUIPlugin.trace(id + " : found " + info.getTitle()); //$NON-NLS-1$
                        replacement = candidate;

                        break TEST;
                    } catch (Throwable t) {
                        CatalogUIPlugin.trace(id + " : ... " + t.getLocalizedMessage()); //$NON-NLS-1$
                    }
                }
                if (replacement == null) {
                    CatalogUIPlugin.log("Could not reset " + id + " - as we could not connect!", //$NON-NLS-1$ //$NON-NLS-2$
                            null);
                    continue; // skip - too bad we cannot update status the original
                }
                catalog.replace(id, replacement);
            } catch (Throwable failed) {
                CatalogUIPlugin.log("Reset failed", failed); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
            current = (IStructuredSelection) selection;
        }
    }

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }
}
