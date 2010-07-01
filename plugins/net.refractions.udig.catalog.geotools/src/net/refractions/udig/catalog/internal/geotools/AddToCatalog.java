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
package net.refractions.udig.catalog.internal.geotools;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.geotools.process.LocalProcessService;
import net.refractions.udig.ui.ProgressManager;

import org.eclipse.ui.IStartup;

/**
 * Adds the LocalProcessService to the local catalog if needed.
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
        IService service = localCatalog.getById(IService.class, LocalProcessService.SERVICE_ID, ProgressManager.instance().get());
        if( service !=null ){
            return; // already available
        }
        // create a "builtin" instance and register it in the catalog        
        service = new LocalProcessService();
        localCatalog.add(service);        
    }

}
