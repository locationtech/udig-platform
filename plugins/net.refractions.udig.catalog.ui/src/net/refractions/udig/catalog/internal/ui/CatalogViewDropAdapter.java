/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.internal.ui;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.core.internal.CorePlugin;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TransferData;

public class CatalogViewDropAdapter extends ViewerDropAdapter {

    protected CatalogViewDropAdapter( Viewer viewer ) {
        super(viewer);
    }

    @Override
    public boolean performDrop( Object data ) {

        IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
        IRepository local = CatalogPlugin.getDefault().getLocal();
        
        if (data instanceof URL) {
            URL url = (URL) data;
            try {
                local.acquire( url, null );  // add to catalog if needed
                
            } catch (IOException e) {
                CatalogUIPlugin.log( "Drag and Drop "+url, e);
            }
            //List<IService> candidates = serviceFactory.createService((URL) data);
        } else if (data instanceof java.util.Map) {
            java.util.Map<String, Serializable> connectionParams = (java.util.Map<String, Serializable>) data;
            try {
                local.acquire(connectionParams, null);
            } catch (IOException e) {
                CatalogUIPlugin.log( "Drag and Drop "+connectionParams, e);
            }
            //List<IService> candidates = serviceFactory.createService( connectionParams );            
        } else if (data instanceof String || data instanceof String[]) {
            List<URL> urls = null;
            if (data instanceof String) {
                urls = CorePlugin.stringsToURLs((String) data);
            } else {
                urls = CorePlugin.stringsToURLs((String[]) data);
            }
            for( URL url : urls ) {
                List<IService> services = serviceFactory
                        .createService(url);
                for( IService service : services ) {
                    CatalogPlugin.getDefault().getLocalCatalog().add(service);
                }
            }
        }
        return true;
    }

    @Override
    public boolean validateDrop( Object target, int operation, TransferData transferType ) {
        return true;
    }

}
