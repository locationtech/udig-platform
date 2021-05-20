/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.tests.AbstractResolveTest.FakeProgress;

/**
 * Test raster services getInfo.getMetric function 
 * 
 * @author Emily
 *
 */
public class AbstractRasterServiceTest {

	@Test
	public void testMetric() throws Exception {
		File file = new File("data/raster.tif");
		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("URL", file.toURI().toURL());

		IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
		List<IService> services = serviceFactory.createService(map);
        
		//we should have two services: geotiff and worldimage
		IService gtiff = null;
		IService world = null;
		for (IService service : services) {
			if (service.getClass().getName().equals("org.locationtech.udig.catalog.internal.geotiff.GeoTiffServiceImpl")) {
				gtiff = service;
			}else if (service.getClass().getName().equals("org.locationtech.udig.catalog.internal.worldimage.WorldImageServiceImpl")) {
				world = service;
			}
		}
		
		assertTrue("Geotiff service not found", gtiff != null);
		assertTrue("World file service not found", world != null);
		
		final IService fgtiff = gtiff;
		final IService fworld = world;
		final List<Exception> exceptions = new ArrayList<>();
		
		//getInfo is not supported in display thread so I had to do this
		//in it's own thread
        Thread t = new Thread() {
        	
        	  public void run() {
        		  FakeProgress monitor = new FakeProgress();
        		  try {
						assertTrue("Geotiff service info metric incorrect.", fgtiff.getInfo(monitor).getMetric() == 1.0) ;
						assertTrue("World file service info metric incorrect.", fworld.getInfo(monitor).getMetric() == 0.5);
					} catch (IOException e) {
						exceptions.add(e);
					}
        	  }
        };
        t.start();
        t.join();
        if (!exceptions.isEmpty()) throw exceptions.get(0);
	}
}
