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
package net.refractions.catalog.util.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.internal.wms.WMSGeoResourceImpl;
import org.locationtech.udig.catalog.util.IFriend;

import org.eclipse.core.runtime.IProgressMonitor;

public class GeoServerTileSetFriend extends IFriend {

    public GeoServerTileSetFriend() {
    }

    @Override
    public List<IResolve> friendly( IResolve handle, IProgressMonitor monitor ) {
        if( !(handle instanceof IGeoResource) ) {
            return Collections.emptyList();
        }
        
        WMSGeoResourceImpl layer = (WMSGeoResourceImpl) handle;
        URL url = layer.getIdentifier();
        
        String uri = url.toString();
        String file = url.getFile();
        String host = url.getHost();
        int port = url.getPort();
        String ref = url.getRef();
        String protocol = url.getProtocol();
        
        if( !uri.contains("geoserver")) { //$NON-NLS-1$
            return Collections.emptyList();
        }
        String associate;
        associate = file.replace("wms", "geowebcache/gwc/service"); //$NON-NLS-1$ //$NON-NLS-2$
        int split = associate.indexOf('?');
        if( split != -1 ) associate = associate.substring(0,split+1);
        associate += "Request=GetCapabilities&VERSION=1.1.0"; //$NON-NLS-1$
        associate += "#"; //$NON-NLS-1$
        associate += ref;
        
        URL target;
        try {
            target = new URL( protocol, host, port, associate);
        } catch (MalformedURLException e) {
            return Collections.emptyList();         
        }       
        ICatalog local = CatalogPlugin.getDefault().getLocalCatalog();
        
        // look up frendly wfs entry from local catalog (if present)
        IGeoResource friend = local.getById( IGeoResource.class, new ID(target), monitor );
        if( friend == null ) {
            return Collections.emptyList();
        }
        else {
            return new ArrayList<IResolve>( Collections.singletonList( friend ) );
        }
    }

}
