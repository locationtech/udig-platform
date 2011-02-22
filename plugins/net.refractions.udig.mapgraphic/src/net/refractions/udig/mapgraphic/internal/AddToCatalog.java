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
package net.refractions.udig.mapgraphic.internal;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.ui.IStartup;

/**
 * Adds the MapGraphic Service to the LocalCatalog...
 *
 * TODO this should be an extension point that is part of catalog
 *
 * @author Jesse
 * @since 1.1.0
 */
public class AddToCatalog implements IStartup {

    public void earlyStartup() {
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        IService service = localCatalog.getById(IService.class, MapGraphicService.SERVICE_URL, ProgressManager.instance().get());
        if( service !=null )
            return;

        service=new MapGraphicService();
        localCatalog.add(service);

    }

}
