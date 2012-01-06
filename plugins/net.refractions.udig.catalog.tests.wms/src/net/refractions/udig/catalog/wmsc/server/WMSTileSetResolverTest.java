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

import org.junit.Test;

public class WMSTileSetResolverTest {

    @Test
    public void testResolver() throws Exception {
        Activator instance = Activator.getDefault();
        assertNotNull("Run as a JUnit Plug-in Test", instance );

        //get the service factory 
        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();

        //create the service
        URL url = new URL("http://localhost:8080/geoserver/ows?service=wms&version=1.3.0&request=GetCapabilities");
        List<IService> services = factory.createService(url);

        //ensure the service was created
        assertNotNull(services);
        assertEquals(1, services.size() );

        //ensure the right type of service was created
        IService service = services.get(0);
        assertNotNull( service );

        ICatalog catalog = CatalogPlugin.getDefault().getLocalCatalog();
        catalog.add( service ); // we can now find this service!

        ID id = new ID(new ID(url), "tasmania");

        IGeoResource resource = (IGeoResource)catalog.getById(IGeoResource.class, id, null);
        assertNotNull( resource );

        TileSet ts = resource.resolve(TileSet.class, null);
        assertNotNull( ts );
    }
}
