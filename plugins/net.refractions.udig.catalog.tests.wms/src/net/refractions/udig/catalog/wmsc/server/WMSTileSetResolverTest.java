/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.catalog.wmsc.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.tests.wmsc.Activator;
import net.refractions.udig.project.ui.preferences.PreferenceConstants;

import org.junit.Test;

public class WMSTileSetResolverTest {

    @SuppressWarnings("nls")
    @Test
    public void testResolver() throws Exception {
        Activator instance = Activator.getDefault();
        assertNotNull("Run as a JUnit Plug-in Test", instance);

        // get the service factory
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();

        // create the service
        URL url = new URL(
                "http://localhost:8080/geoserver/ows?service=wms&version=1.3.0&request=GetCapabilities");
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
