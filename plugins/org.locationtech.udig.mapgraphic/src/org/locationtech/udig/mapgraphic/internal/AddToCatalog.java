/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.internal;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.ui.ProgressManager;

import org.eclipse.ui.IStartup;

/**
 * Adds the MapGraphic Service to the LocalCatalog.
 * <p>
 * Currently implemented as a workbench startup; this may be considered 
 * as an extension point defined by the catalog.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class AddToCatalog implements IStartup {

    public void earlyStartup() {
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        ID serviceUrl = new ID(MapGraphicService.SERVICE_URL);
        IService service = localCatalog.getById(IService.class, serviceUrl, ProgressManager.instance().get());
        if( service !=null )
            return;
        
        service=new MapGraphicService();
        localCatalog.add(service);
    }

}
