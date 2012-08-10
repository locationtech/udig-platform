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
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.geotools.Activator;
import net.refractions.udig.catalog.internal.ServiceFactoryImpl;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataAccess;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.type.Name;

/**
 * Tests support for the "generic" GeoTools DataStore support as provided by the
 * net.refractions.udig.catalog.geotools.data.
 * <p>
 * We are testing using the sample data in the "test-data" folder; although not common the use of
 * folders that do not follow package naming conventions is actually the java standard (explicitly
 * for use for "test-data" and "doc-files).
 * </p>
 * 
 * @author Jody Garnett
 * @since 1.2.0
 */
public class GeoToolsDataStoreServiceTest {
    private static Activator activator;
    private static ServiceFactoryImpl serviceFactory;
    private static IRepository local;

    @BeforeClass
    public static void onlyOnce() {
        activator = net.refractions.udig.catalog.geotools.Activator.getDefault();
        assertNotNull("Run as Plug-in Test", activator);
        serviceFactory = (ServiceFactoryImpl) CatalogPlugin.getDefault().getServiceFactory();
        local = CatalogPlugin.getDefault().getLocal();

    }
    @Test
    public void testDataStoreServiceExtension() throws Exception {
        // DataStoreServiceExtension extension = new DataStoreServiceExtension();
        DataStoreServiceExtension serviceExtension = CatalogPlugin.getDefault().serviceImplementation(DataStoreServiceExtension.class);
        
        URL target = GeoToolsDataStoreServiceTest.class.getResource("test-data/sample_data.properties");
        if( "bundleresource".equals(target.getProtocol())){
            target = FileLocator.toFileURL( target );
        }
        
        assertNotNull("sample data found", target );
        Map<String, Serializable> params = serviceExtension.createParams( target );
        
        assertNotNull("canProcess", params );
        
        IService service = serviceExtension.createService( null, params );
        assertNotNull("connected", service );
        
        assertTrue("Datastore available", service.canResolve( DataStore.class));
        
        DataAccess dataStore = service.resolve( DataStore.class, null );
        assertNotNull("DataStore connected", dataStore );
        
        Name typeName = (Name) dataStore.getNames().get(0);;
        FeatureSource featureSource = dataStore.getFeatureSource( typeName );
        
        assertEquals( 4, featureSource.getCount( Query.ALL ) );
        
        IServiceInfo info = service.getInfo(new NullProgressMonitor());
        assertNotNull("Title available", info.getTitle());
        assertNotNull("Description available", info.getDescription());
        
        List<? extends IGeoResource> m = service.resources(new NullProgressMonitor());
        for(IGeoResource resource: m) {
            ID id = resource.getID();
            assertNotNull(id);
            IGeoResourceInfo grinfo = resource.getInfo(new NullProgressMonitor());
            assertNotNull(grinfo);
            assertEquals("GeoResource title matches filename", "sample data", grinfo.getTitle());
        }
        
    }
}
