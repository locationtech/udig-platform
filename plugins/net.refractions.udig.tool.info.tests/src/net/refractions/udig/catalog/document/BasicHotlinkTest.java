package net.refractions.udig.catalog.document;

import static org.junit.Assert.*;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IDocument;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IHotlink;
import net.refractions.udig.catalog.IDocument.Type;
import net.refractions.udig.catalog.IHotlink.HotlinkDescriptor;
import net.refractions.udig.catalog.IResolveAdapterFactory;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.internal.shp.ShpServiceExtension;
import net.refractions.udig.tool.info.tests.Activator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.osgi.framework.Bundle;

public class BasicHotlinkTest {

    private IService service;
    private IGeoResource resource;

    @Before
    public void setUp() throws Exception {
        assertNotNull( "Please run as Plugin Test",Activator.getDefault() );
        
        Bundle bundle = Activator.getDefault().getBundle();
        File directory = FileLocator.getBundleFile(bundle);
        assertTrue( "Test Directory", directory.isDirectory() );
        
        File shapefile = new File(new File( directory, "internal"), "australia.shp");
        assertTrue("Sample File", shapefile.isFile() );
        
        ServiceExtension factory = new ShpServiceExtension();
        Map<String, Serializable> params = factory.createParams( shapefile.toURI().toURL() );
        service = factory.createService( null, params );
        
        List<IGeoResource> members = (List<IGeoResource>) service.resources(new NullProgressMonitor() );
        resource = members.get(0);
        
        resource.getPersistentProperties().put( BasicHotlink.HOTLINK, "FILE:FILE,LINK:WEB");
    }
    
    @After 
    public void tearDown() throws Exception {
        if( service != null ){
            service.dispose( new NullProgressMonitor() );
            service = null;
        }
    }
    @Test
    public void testBasicResolveAdaptorFactory() throws Exception {
        IResolveAdapterFactory adaptorFactory = new BasicHotlinkResolveFactory();
        assertTrue( adaptorFactory.canAdapt( resource, IHotlink.class ) );
        
        IHotlink hotlink = (IHotlink) adaptorFactory.adapt(resource,  IHotlink.class, new NullProgressMonitor() );
        assertNotNull( hotlink );
        
        List<HotlinkDescriptor> list = hotlink.getHotlinkDescriptors();
        assertEquals("descriptors found", 2, list.size() );
        
        SimpleFeatureStore featureStore = resource.resolve(SimpleFeatureStore.class, new NullProgressMonitor() );
        SimpleFeatureType schema = featureStore.getSchema();
        for( HotlinkDescriptor hotlinkDescriptor : list ){
            AttributeDescriptor attributeDescriptor = schema.getDescriptor( hotlinkDescriptor.getAttributeName() );
            assertNotNull( "confirm attribute name matches", attributeDescriptor );
            assertTrue( "Confirm String", String.class.isAssignableFrom( attributeDescriptor.getType().getBinding() ) );
            
            assertNotNull( hotlinkDescriptor.getType() != null );
        }
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        SimpleFeatureCollection features = featureStore.getFeatures( ff.equals(ff.property("STATE"), ff.literal("Tasmania")) );
        SimpleFeatureIterator iterator = features.features();
        assertTrue( "Tasmania found", iterator.hasNext() );
        SimpleFeature tasmania = iterator.next();
        
        List<IDocument> documents = hotlink.getDocuments( tasmania );
        assertEquals( "image document found", 2, documents.size() );
        
        IDocument imageDocument = documents.get(0);
        assertEquals( Type.FILE, imageDocument.getType() );
        assertEquals( "tasmania.png", imageDocument.getName() );
        
        IDocument webLink = documents.get(1);
        assertEquals( Type.WEB, webLink.getType() );
        assertTrue( webLink.getName().contains("Tasmania") );
    }

}
