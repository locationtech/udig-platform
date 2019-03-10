/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ICatalog;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.tests.wmsc.Activator;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;

import org.junit.BeforeClass;
import org.junit.Test;

public class WMSTileSetResolverTest {
    
    public static String getCapabilities = "http://localhost:8080/geoserver/ows?service=wms&version=1.3.0&request=GetCapabilities";
    static boolean localGeoserver = false;
    
    @BeforeClass
    public static void checkThatWeHaveLocalGeoServer() throws Exception {
        URL url = new URL( getCapabilities );
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            int responseCode = connection.getResponseCode();
            if( responseCode == 200 ){
                localGeoserver = true;
            }
        }
        catch (Throwable ignore){
            localGeoserver = false; // obviously not avaialble!
        } finally {
            connection.disconnect();
        }
    }
    @SuppressWarnings("nls")
    @Test
    public void testResolver() throws Exception {
        if( !localGeoserver){
            return; // ignore this test
        }
        Activator instance = Activator.getDefault();
        assertNotNull("Run as a JUnit Plug-in Test", instance);

        // get the service factory
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();

        // create the service
        URL url = new URL( getCapabilities );
        List<IService> services = factory.createService(url);

        // ensure the service was created
        assertNotNull(services);
        assertEquals(1, services.size());

        // ensure the right type of service was created
        IService service = services.get(0);
        assertNotNull(service);

        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        catalog.add(service); // we can now find this service!

        ID id = new ID(new ID(url), "tasmania");

        IGeoResource resource = (IGeoResource) catalog.getById(IGeoResource.class, id, null);

        /*
         * setup the properties so this adapter is enabled
         */
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_ON_OFF, true);
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_WIDTH, PreferenceConstants.DEFAULT_TILE_SIZE+"");
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_HEIGHT,PreferenceConstants.DEFAULT_TILE_SIZE+"");
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_IMAGE_TYPE,PreferenceConstants.DEFAULT_IMAGE_TYPE);
        resource.getPersistentProperties().put(PreferenceConstants.P_TILESET_SCALES,
                "1000000.0 100000.0 50000.0 20000.0 10000.0 5000.0 2500.0 1000.0");
        
        assertNotNull(resource);

        TileSet ts = resource.resolve(TileSet.class, null);
        assertNotNull(ts);
    }
}
