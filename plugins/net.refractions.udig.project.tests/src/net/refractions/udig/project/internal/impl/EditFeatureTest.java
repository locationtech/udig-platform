package net.refractions.udig.project.internal.impl;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;

import com.vividsolutions.jts.geom.Geometry;

public class EditFeatureTest extends AbstractProjectTestCase {

    private static final String MODIFIED_VALUE = "modifiedValue"; //$NON-NLS-1$
    private static final String ORIGINAL_VALUE = "secondFeature"; //$NON-NLS-1$
    MemoryDataStore ds;
    private IService service;
    private Map map;
    private SimpleFeature[] features;
    
    @SuppressWarnings("deprecation")
    @Before
    public void setUp() throws Exception {
        features = UDIGTestUtil.createTestFeatures("testType", new Geometry[]{}, //$NON-NLS-1$
        		new String[]{"firstValue", ORIGINAL_VALUE, "thirdValue", "fourthValue"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        IGeoResource resource=CatalogTests.createGeoResource(features,true);
        service=resource.service(null);
        ds=service.resolve(MemoryDataStore.class, null);
        map=MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(512,512));
        
    }

    @After
    public void tearDown() throws Exception {
        CatalogPlugin.getDefault().getLocalCatalog().remove(service);
    }
    
    @Ignore
    @Test
    public void testSetFeatureAttribute() throws Exception{
        FeatureStore<SimpleFeatureType, SimpleFeature> store=map.getLayersInternal().get(0).getResource(FeatureStore.class, null);
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection=store.getFeatures(fac.id(FeatureUtils.stringToId(fac, features[1].getID())));
        FeatureIterator<SimpleFeature> iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);
        store.modifyFeatures(store.getSchema().getDescriptor(1), MODIFIED_VALUE, fac.id(FeatureUtils.stringToId(fac, features[1].getID()) )); 

        //not committed so other featurestores should not get modified value
        FeatureSource<SimpleFeatureType, SimpleFeature> dsSource= ds.getFeatureSource("testType"); //$NON-NLS-1$
        collection=dsSource.getFeatures(fac.id(FeatureUtils.stringToId(fac, features[1].getID())));
        
        iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);
        
        //layer featureStore has transactions so should have new value
        collection=store.getFeatures(fac.id(FeatureUtils.stringToId(fac, features[1].getID()))); 
        iter = collection.features();
        assertEquals(MODIFIED_VALUE, iter.next().getAttribute(1));
        collection.close(iter);

        //Create and send a commit command 
        map.sendCommandSync(EditCommandFactory.getInstance().createCommitCommand());
        
        //Now is committed so all FeatureSources should have the new value
        collection=dsSource.getFeatures(fac.id(FeatureUtils.stringToId(fac, features[1].getID())));
        iter = collection.features();
        assertEquals(MODIFIED_VALUE, iter.next().getAttribute(1));
        collection.close(iter);   
    }
    
    @Ignore
    @Test
    public void testRollback() throws Exception{
        FeatureStore<SimpleFeatureType, SimpleFeature> store=map.getLayersInternal().get(0).getResource(FeatureStore.class, null);
        FilterFactory fac=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        String id = features[1].getID();
        FeatureCollection<SimpleFeatureType, SimpleFeature> collection=store.getFeatures(fac.id(FeatureUtils.stringToId(fac, id)));
        FeatureIterator<SimpleFeature> iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);
        store.modifyFeatures(store.getSchema().getDescriptor(1), MODIFIED_VALUE, fac.id(FeatureUtils.stringToId(fac, id)) ); 

        //not committed so other featurestores should not get modified value
        FeatureSource<SimpleFeatureType, SimpleFeature> dsSource= ds.getFeatureSource("testType"); //$NON-NLS-1$
        collection=dsSource.getFeatures(fac.id(FeatureUtils.stringToId(fac, id))); 
        iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);
        
        //layer featureStore has transactions so should have new value
        collection=store.getFeatures(fac.id(FeatureUtils.stringToId(fac, id))); 
        iter = collection.features();
        assertEquals(MODIFIED_VALUE, iter.next().getAttribute(1));
        collection.close(iter);

        //Create and send a commit command 
        map.sendCommandSync(EditCommandFactory.getInstance().createRollbackCommand());
        
        //Now is committed so all FeatureSources should have the new value
        collection=store.getFeatures(fac.id(FeatureUtils.stringToId(fac, id))); 
        iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);   
    }

}
