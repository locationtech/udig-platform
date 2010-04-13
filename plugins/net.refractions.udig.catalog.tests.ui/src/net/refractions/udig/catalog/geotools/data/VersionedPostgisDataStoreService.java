package net.refractions.udig.catalog.geotools.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.geotools.Activator;
import net.refractions.udig.catalog.internal.ServiceFactoryImpl;

import org.geotools.data.DataAccess;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.postgis.VersionedPostgisDataStore;
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
    private VersionedPostgisUtils.Fixture f;
    
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
        f = VersionedPostgisUtils.newFixture("test-data/versioned.properties");
        DataStoreServiceExtension serviceExtension = serviceFactory.serviceImplementation(DataStoreServiceExtension.class);
        
//        URL target = VersionedPostgisDataStoreService.class.getResource("test-data/sample_data.properties");
        assertNotNull("Fixture created", f);
//        Map<String, Serializable> params = serviceExtension.createParams( target );
        
//        assertNotNull("canProcess", params );
        
        IService service = serviceExtension.createService( null, VersionedPostgisUtils.getParams(f));
        assertNotNull("connected", service );
        
        assertTrue("Datastore available", service.canResolve( DataStore.class));
        
        DataAccess dataStore = service.resolve( DataStore.class, null );
        assertNotNull("DataStore connected", dataStore );
        
        List list = dataStore.getNames();
        Name typeName = new NameImpl(null, "versioned_test");
        FeatureSource featureSource = dataStore.getFeatureSource( typeName );
        
        assertEquals( 3, featureSource.getCount( Query.ALL ) );
        
        
        
    }
}
