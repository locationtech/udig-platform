/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.catalog.jgrass.core;

//package eu.hydrologis.udig.catalog.internal.jgrass;
//
//import java.io.IOException;
//import java.net.URL;
//import java.util.List;
//
//import junit.framework.TestCase;
//import net.refractions.udig.catalog.CatalogPlugin;
//import net.refractions.udig.catalog.IGeoResourceInfo;
//import net.refractions.udig.catalog.IResolve;
//import net.refractions.udig.catalog.IService;
//import net.refractions.udig.catalog.IServiceFactory;
//
//import org.eclipse.core.runtime.Platform;
//
//public class JGrassServiceTest extends TestCase {
//    @SuppressWarnings("deprecation")
//    public void testCreateService() {
//        try {
//
//            // build the url to the test location
//            URL locationPath = JGrassPlugin.getDefault().getBundle().getEntry("spearfish60");
//            locationPath = Platform.asLocalURL(locationPath);
//
//            // get the service factory
//            IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();
//            // create the service
//            List<IService> services = factory.acquire(locationPath);
//
//            // ensure the service was created
//            assertNotNull(services);
//            assertEquals(services.size(), 1);
//
//            // ensure the right type of service was created
//            IService service = services.get(0);
//            assertTrue(service instanceof JGrassService);
//
//            // get all resources from the service and test if we got just the
//            // one mapset
//            List<IResolve> mapsetresources = service.members(null);
//            assertNotNull(mapsetresources);
//            assertEquals(mapsetresources.size(), 1);
//
//            for( IResolve mapset : mapsetresources ) {
//
//                // get the lsit of maps from the mapset
//                List<IResolve> mapresources = mapset.members(null);
//                assertNotNull(mapresources);
//                assertEquals(mapresources.size(), 7);
//
//                boolean archsites = false;
//                boolean geology = false;
//                boolean elevationdem = false;
//                boolean aspect = false;
//                boolean bugsites = false;
//                boolean roads = false;
//                boolean fields = false;
//                for( IResolve map : mapresources ) {
//                    assertTrue(map instanceof JGrassMapGeoResource);
//
//                    IGeoResourceInfo info = ((JGrassMapGeoResource) map).getInfo(null);
//
//                    if ("archsites".equals(info.getName()))
//                        archsites = true;
//                    if ("geology".equals(info.getName()))
//                        geology = true;
//                    if ("elevation.dem".equals(info.getName()))
//                        elevationdem = true;
//                    if ("aspect".equals(info.getName()))
//                        aspect = true;
//                    if ("bugsites".equals(info.getName()))
//                        bugsites = true;
//                    if ("roads".equals(info.getName()))
//                        roads = true;
//                    if ("fields".equals(info.getName()))
//                        fields = true;
//
//                }
//                assertTrue(archsites);
//                assertTrue(geology);
//                assertTrue(elevationdem);
//                assertTrue(aspect);
//                assertTrue(bugsites);
//                assertTrue(roads);
//                assertTrue(fields);
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            fail("error creating service");
//        }
//    }
//}
