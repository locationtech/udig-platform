/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IRepository;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.ui.CatalogUIPlugin;
import org.locationtech.udig.core.internal.CorePlugin;

public class CatalogViewDropAdapter extends ViewerDropAdapter {

    protected CatalogViewDropAdapter(Viewer viewer) {
        super(viewer);
    }

    @Override
    public boolean performDrop(Object data) {

        IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
        IRepository local = CatalogPlugin.getDefault().getLocal();

        if (data instanceof URL) {
            URL url = (URL) data;
            try {
                local.acquire(url, null); // add to catalog if needed

            } catch (IOException e) {
                CatalogUIPlugin.log("Drag and Drop " + url, e); //$NON-NLS-1$
            }
            // List<IService> candidates = serviceFactory.createService((URL) data);
        } else if (data instanceof java.util.Map) {
            java.util.Map<String, Serializable> connectionParams = (java.util.Map<String, Serializable>) data;
            try {
                local.acquire(connectionParams, null);
            } catch (IOException e) {
                CatalogUIPlugin.log("Drag and Drop " + connectionParams, e); //$NON-NLS-1$
            }
            // List<IService> candidates = serviceFactory.createService( connectionParams );
        } else if (data instanceof String || data instanceof String[]) {
            List<URL> urls = null;
            if (data instanceof String) {
                urls = CorePlugin.stringsToURLs((String) data);
            } else {
                urls = CorePlugin.stringsToURLs((String[]) data);
            }
            for (URL url : urls) {
                List<IService> services = serviceFactory.createService(url);
                for (IService service : services) {
                    CatalogPlugin.getDefault().getLocalCatalog().add(service);
                }
            }
        }
        return true;
    }

    @Override
    public boolean validateDrop(Object target, int operation, TransferData transferType) {
        return true;
    }

}
