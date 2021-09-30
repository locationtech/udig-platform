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
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.jgrass.JGrassPlugin;

@Ignore("Fix NoClassDefFoundError to run the test case")
public class JGrassServiceTest {
    @SuppressWarnings("deprecation")
    @Test
    public void testCreateService() {
        try {

            // build the URL to the test location
            URL locationPath = JGrassPlugin.getDefault().getBundle().getEntry("spearfish60"); //$NON-NLS-1$
            locationPath = Platform.asLocalURL(locationPath);

            // get the service factory
            IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
            // create the service
            List<IService> services = factory.createService(locationPath);

            // ensure the service was created
            assertNotNull(services);
            assertEquals(services.size(), 1);

            // ensure the right type of service was created
            IService service = services.get(0);
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
                assertEquals(mapresources.size(), 7);

                boolean archsites = false;
                boolean geology = false;
                boolean elevationdem = false;
                boolean aspect = false;
                boolean bugsites = false;
                boolean roads = false;
                boolean fields = false;
                for (IResolve map : mapresources) {
                    assertTrue(map instanceof JGrassMapGeoResource);

                    IGeoResourceInfo info = ((JGrassMapGeoResource) map).getInfo(null);

                    if ("archsites".equals(info.getName())) //$NON-NLS-1$
                        archsites = true;
                    if ("geology".equals(info.getName())) //$NON-NLS-1$
                        geology = true;
                    if ("elevation.dem".equals(info.getName())) //$NON-NLS-1$
                        elevationdem = true;
                    if ("aspect".equals(info.getName())) //$NON-NLS-1$
                        aspect = true;
                    if ("bugsites".equals(info.getName())) //$NON-NLS-1$
                        bugsites = true;
                    if ("roads".equals(info.getName())) //$NON-NLS-1$
                        roads = true;
                    if ("fields".equals(info.getName())) //$NON-NLS-1$
                        fields = true;

                }
                assertTrue(archsites);
                assertTrue(geology);
                assertTrue(elevationdem);
                assertTrue(aspect);
                assertTrue(bugsites);
                assertTrue(roads);
                assertTrue(fields);

            }

        } catch (IOException e) {
            e.printStackTrace();
            fail("error creating service"); //$NON-NLS-1$
        }
    }
}
