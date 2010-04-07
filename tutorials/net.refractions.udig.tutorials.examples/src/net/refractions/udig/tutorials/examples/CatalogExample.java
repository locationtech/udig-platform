/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2008, Refractions Research Inc.
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
package net.refractions.udig.tutorials.examples;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.sound.midi.MidiDevice.Info;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.ui.ProgressManager;

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
