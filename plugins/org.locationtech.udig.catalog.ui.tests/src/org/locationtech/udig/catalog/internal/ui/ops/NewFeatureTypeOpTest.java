/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.ui.ops;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceFactory;
import org.locationtech.udig.catalog.internal.wfs.WFSServiceExtension;
import org.locationtech.udig.catalog.tests.ui.CatalogTestsUIPlugin;
import org.locationtech.udig.catalog.util.CatalogTestUtils;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.junit.Ignore;
import org.junit.Test;

public class NewFeatureTypeOpTest {

    @Ignore
    @Test
    public void testCreateShapefileType() throws Exception {
        
        URL fileURL = FileLocator.toFileURL( CatalogTestsUIPlugin.getDefault().
                getBundle().getEntry("data/streams.shp")); //$NON-NLS-1$
        URL dirURL = FileLocator.toFileURL( CatalogTestsUIPlugin.getDefault().
                getBundle().getEntry("data")); //$NON-NLS-1$
        List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(fileURL);
        
        NewFeatureTypeOp op=new NewFeatureTypeOp();
        op.testingSetTesting(true);
        op.op(Display.getCurrent(), services.get(0), new NullProgressMonitor());
        
        UDIGTestUtil.inDisplayThreadWait(500, WaitCondition.FALSE_CONDITION, false);
        File file = new File(dirURL.getFile()+"TestName.shp"); //$NON-NLS-1$
        assertTrue( file.exists() ); 
        List<IResolve> resources = CatalogPlugin.getDefault().getLocalCatalog().
                find(new URL(dirURL.toExternalForm()+"TestName.shp#TestName"),  //$NON-NLS-1$
                        new NullProgressMonitor());
        assertEquals(1, resources.size());
        
        assertTrue( resources.get(0).parent(null).canResolve(ShapefileDataStore.class));
        
        delete(file);
        // test if shapefile is not local
        URL url=new URL("https://github.com/uDig/udig-platform/blob/master/plugins/org.locationtech.udig.catalog.tests.ui/data/streams.shp?raw=true"); //$NON-NLS-1$
        
        services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
        
        op.op(Display.getCurrent(), services.get(0), new NullProgressMonitor());
        
        UDIGTestUtil.inDisplayThreadWait(500, WaitCondition.FALSE_CONDITION, false);
        file=null;
        try {
            file=new File(FileLocator.toFileURL(Platform.getInstanceLocation().getURL()).getFile()+"TestName.shp"); //$NON-NLS-1$
        } catch (IOException e) {
            file=new File(System.getProperty("java.user")+"TestName.shp"); //$NON-NLS-1$ //$NON-NLS-2$
        }
       
        assertTrue(file.exists());
        delete(file);
         URL url2 = new URL("file://"+file.getAbsoluteFile()+"#TestName"); //$NON-NLS-1$ //$NON-NLS-2$
        resources = CatalogPlugin.getDefault().getLocalCatalog().
                find(url2,  
                        new NullProgressMonitor());
        assertEquals(1, resources.size());
        
        assertTrue( resources.get(0).parent(null).canResolve(ShapefileDataStore.class));
    }

    private void delete( final File file ) {
        file.delete();
        file.deleteOnExit();
        File[] files = file.getParentFile().listFiles(new FilenameFilter(){

            public boolean accept( File dir, String name ) {
                return name.contains(file.getName().substring(0,file.getName().lastIndexOf('.')));
            }
            
        });
        
        for( File file2 : files ) {
            file2.delete();
            file2.deleteOnExit();
        }
    }

	@Ignore("Connection refused ..")
    @Test
    public void testMemoryDataStoreType() throws Exception {
        IServiceFactory serviceFactory = CatalogPlugin.getDefault().getServiceFactory();
        URL dragNdrop = new URL("http://localhost/scratch/TestNewFeatureTypeOp");
        List<IService> services = serviceFactory.createService(dragNdrop); //$NON-NLS-1$
        
        NewFeatureTypeOp op=new NewFeatureTypeOp();
        op.testingSetTesting(true);
        op.op(Display.getCurrent(), services.get(0), new NullProgressMonitor());
        
        UDIGTestUtil.inDisplayThreadWait(500, WaitCondition.FALSE_CONDITION, false);
        
        List< ? extends IGeoResource> members = services.get(0).resources(new NullProgressMonitor());
        assertEquals(1, members.size());
        assertEquals( "TestName", members.get(0).resolve(FeatureSource.class, new NullProgressMonitor()).getSchema().getName().getLocalPart()); //$NON-NLS-1$
    }

    @Test
    public void testCreateTypeOnIllegalDS() throws Exception {
        URL id=new URL("http://localhost:12763/geoserver/wfs"); //$NON-NLS-1$
        CatalogTestUtils.assumeNoConnectionException(id, 3000);
        
        WFSServiceExtension ext=new WFSServiceExtension();
        Map<String, Serializable> params = ext.createParams(id);
        
        IService service = ext.createService(id, params);
        
        NewFeatureTypeOp op=new NewFeatureTypeOp();
        op.testingSetTesting(true);
        op.op(Display.getCurrent(), service, new NullProgressMonitor());
        
        UDIGTestUtil.inDisplayThreadWait(500, WaitCondition.FALSE_CONDITION, false);
        
        assertTrue(op.testingIsError() );
    }

}
