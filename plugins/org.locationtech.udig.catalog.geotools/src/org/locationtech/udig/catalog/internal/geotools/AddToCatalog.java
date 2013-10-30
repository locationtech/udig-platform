/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.geotools;

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
//        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
//        IService service = localCatalog.getById(IService.class, LocalProcessService.SERVICE_ID, ProgressManager.instance().get());
//        if( service !=null ){
//            return; // already available
//        }
//        // create a "builtin" instance and register it in the catalog        
//        service = new LocalProcessService();
//        localCatalog.add(service);        
    }

}
