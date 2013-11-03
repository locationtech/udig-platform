/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.sound.midi.MidiDevice.Info;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IRepository;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.ui.ProgressManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

/**
 * This example code shows the use of the Catalog plug-in.
 * <p>
 * For more information please visit the wiki
 * <a href="http://udig.refractions.net/confluence/display/DEV/2+Catalog">Catalog</a>
 * page.
 * 
 * @author Jody Garnett (Refractions Research, Inc.)
 * @since 1.1.0
 */
public class CatalogExample {

    public static void addFileToCatalog() throws Exception {        
        File file = new File( "C:\\data\\cities.shp" );
        URL url = file.toURI().toURL();
        
        IProgressMonitor monitor = ProgressManager.instance().get();
        
        IRepository local = CatalogPlugin.getDefault().getLocal();
        IService service = local.acquire( url, monitor );        
    }
    
	/**
	 * This is the "long" way to add an entry to the catalog.
	 */
	public static void addFileToCatalogLong() throws Exception {
		File file = new File( "C:\\data\\cities.shp" );
		URL url = file.toURI().toURL();
		
		IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
		List<IService> created = serviceFactory.createService( url );
		
		IRepository local = CatalogPlugin.getDefault().getLocal();
		for( IService service : created ){
		    IService registered = local.add(service);
		    //...
		}
	}

}
