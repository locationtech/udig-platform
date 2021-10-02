/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.junit.Test;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;

public class JGrassServiceTest {
    @Test
    public void testCreateService() {
        try {

            // build the URL to the test location
            URL locationPath = JGrassServiceTest.class.getResource("/test-data/jgrass-test"); //$NON-NLS-1$
            locationPath = FileLocator.toFileURL(locationPath);

            // get the service factory
            IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
            // create the service
            List<IService> services = factory.createService(locationPath);
            System.out.println(services.toArray());

            // ensure the service was created
            assertNotNull(services);
            assertEquals(services.size(), 1);

            // ensure the right type of service was created
            IService service;
            Iterator<IService> iterator = services.stream().filter(s -> s instanceof JGrassService).iterator();
            service= (iterator.hasNext() ? iterator.next() : null);

            assertTrue(service instanceof JGrassService);

            // get all resources from the service and test if we got just the
            // one mapset
            List<IResolve> mapsetresources = service.members(null);
            assertNotNull(mapsetresources);
            assertEquals(mapsetresources.size(), 1);

            for (IResolve mapset : mapsetresources) {

                // get the list of maps from the mapset
                List<IResolve> mapresources = mapset.members(null);
                assertNotNull(mapresources);
                assertEquals(mapresources.size(), 1);

                boolean arcgrid = false;
                for (IResolve map : mapresources) {
                    assertTrue(map instanceof JGrassMapGeoResource);
                    ID id = ((JGrassMapGeoResource)map).getID();

                    if (id.toString().endsWith("ArcGrid")) {//$NON-NLS-1$
                        arcgrid = true;
                    }
                }
                assertTrue(arcgrid);
            }

        } catch (IOException e) {
            e.printStackTrace();
            fail("error creating service"); //$NON-NLS-1$
        }
    }
}
