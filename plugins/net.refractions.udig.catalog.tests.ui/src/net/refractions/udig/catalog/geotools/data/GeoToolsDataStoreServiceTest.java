/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.geotools.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataAccess;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.junit.BeforeClass;
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
        
        //IServiceInfo info = service.getInfo(new NullProgressMonitor());
        IServiceInfo info = getInfo(service, new NullProgressMonitor());
        assertNotNull("Title available", info.getTitle());
        assertNotNull("Description available", info.getDescription());
        
        List<? extends IGeoResource> m = service.resources(new NullProgressMonitor());
        for(IGeoResource resource: m) {
            ID id = resource.getID();
            assertNotNull(id);
            //IGeoResourceInfo grinfo = resource.getInfo(new NullProgressMonitor());
            IGeoResourceInfo grinfo = getInfo(resource, new NullProgressMonitor());
            assertNotNull(grinfo);
            assertEquals("GeoResource title matches filename", "sample data", grinfo.getTitle());
        }
        
    }
    
    private IServiceInfo getInfo(final IService service, final IProgressMonitor monitor) {
        final Callable<IServiceInfo> job = new Callable<IServiceInfo>() {

            @Override
            public IServiceInfo call() throws Exception {
                return service.getInfo(monitor);
            }
            
        };
        FutureTask<IServiceInfo> task = new FutureTask<IServiceInfo>(job);
        Thread t = new Thread(task);
        t.start();
        IServiceInfo info = null;
        
        try {
            info = task.get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        
        return info;
    }
    
    private IGeoResourceInfo getInfo(final IGeoResource resource, final IProgressMonitor monitor) {
        final Callable<IGeoResourceInfo> job = new Callable<IGeoResourceInfo>() {

            @Override
            public IGeoResourceInfo call() throws Exception {
                return resource.getInfo(monitor);
            }
            
        };
        FutureTask<IGeoResourceInfo> task = new FutureTask<IGeoResourceInfo>(job);
        Thread t = new Thread(task);
        t.start();
        IGeoResourceInfo info = null;
        
        try {
            info = task.get();
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }
        
        return info;
    }
}
