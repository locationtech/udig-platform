package net.refractions.udig.catalog.geotools.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.geotools.Activator;
import net.refractions.udig.catalog.internal.ServiceFactoryImpl;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataAccess;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.postgis.VersionedPostgisDataStore;
import org.geotools.data.postgis.VersionedPostgisDataStoreFactory;
import org.geotools.data.postgis.WrappingPostgisFeatureSource;
import org.geotools.feature.NameImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.type.Name;

/**
 * Tests support for the Versioned PostGIS DataStore support
 * 
 * <p>
 * We are testing using the sample data in the "test-data" folder; although not common the use of
 * folders that do not follow package naming conventions is actually the java standard (explicitly
 * for use for "test-data" and "doc-files).
 * </p>
 * 
 * @author Mark Leslie
 * @since 1.2.0
 */
public class VersionedPostgisDataStoreService {
    private static Activator activator;
    private static ServiceFactoryImpl serviceFactory;
    private static IRepository local;
    private FixtureUtils.Fixture f;
    
    @BeforeClass
    public static void onlyOnce() {
        activator = net.refractions.udig.catalog.geotools.Activator.getDefault();
        assertNotNull("Run as Plug-in Test", activator);
        serviceFactory = (ServiceFactoryImpl) CatalogPlugin.getDefault().getServiceFactory();
        local = CatalogPlugin.getDefault().getLocal();

    }
    
    /**
     * Connects to the databased described in the test-data/versioned.properties file and 
     * attempts to 
     *
     * @throws Exception
     */
    @Test
    public void testDataStoreServiceExtension() throws Exception {
        // DataStoreServiceExtension extension = new DataStoreServiceExtension();
        f = FixtureUtils.newFixture("test-fixture/versioned.properties");
        DataStoreServiceExtension serviceExtension = serviceFactory.serviceImplementation(DataStoreServiceExtension.class);
        
        assertNotNull("Fixture created", f);
        
        IService service = serviceExtension.createService( null, FixtureUtils.getParams(f));
        assertNotNull("connected", service );
        
        assertTrue("Datastore available", service.canResolve( DataStore.class));
        
        DataAccess dataStore = service.resolve( DataStore.class, null );
        assertNotNull("DataStore connected", dataStore );
        
        List list = dataStore.getNames();
        assertTrue("VersionedPostgisDataStore connected", dataStore instanceof VersionedPostgisDataStore);
        VersionedPostgisDataStore vpDataStore = (VersionedPostgisDataStore)dataStore;
        
        for(Object typeNameObj : list) {
        	String typeName = ((Name)typeNameObj).toString();
        	if(typeName.equals("road") || typeName.equals("river")) {
        		assertTrue(typeName + " is versioned", vpDataStore.isVersioned(typeName));
        	} else {
        	    assertTrue(typeName + " is unversioned", !vpDataStore.isVersioned(typeName));
        	}
        }
        
        IServiceInfo info = service.getInfo(new NullProgressMonitor());
        assertEquals("Database host used for title", 
                f.host + ":" + f.port, info.getTitle());
        assertEquals("Data store description used for description",
                "Features from PostGIS, managed with a version history", info.getDescription());
        
        List<? extends IGeoResource> m = service.resources(new NullProgressMonitor());
        boolean hasLake = false, 
                hasRoad = false, 
                hasRiver = false, 
                hasStuff = false;
        for(IGeoResource resource: m) {
            ID id = resource.getID();
            assertNotNull(id);
            IGeoResourceInfo grinfo = resource.getInfo(new NullProgressMonitor());
            assertNotNull(grinfo);
            if("lake".equals(grinfo.getTitle()))
                hasLake = true;
            if("river".equals(grinfo.getTitle()))
                hasRiver = true;
            if("road".equals(grinfo.getTitle()))
                hasRoad = true;
            if("stuff".equals(grinfo.getTitle()))
                hasStuff = true;
        }
        assertTrue("Lake table accessible", hasLake);
        assertTrue("Road table accessible", hasRoad);
        assertTrue("River table accessible", hasRiver);
        assertTrue("Stuff table accessible", hasStuff);
    }
}
