package net.refractions.udig.project.internal.impl;

import java.awt.Dimension;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.tests.CatalogTests;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.memory.MemoryDataStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;

import com.vividsolutions.jts.geom.Geometry;

public class EditFeatureTest extends AbstractProjectTestCase {

    private static final String MODIFIED_VALUE = "modifiedValue"; //$NON-NLS-1$
    private static final String ORIGINAL_VALUE = "secondFeature"; //$NON-NLS-1$
    MemoryDataStore ds;
    private IService service;
    private Map map;
    private Feature[] features;

    @SuppressWarnings("deprecation")
    protected void setUp() throws Exception {
        super.setUp();
        features = UDIGTestUtil.createTestFeatures("testType", new Geometry[]{}, //$NON-NLS-1$
        		new String[]{"firstValue", ORIGINAL_VALUE, "thirdValue", "fourthValue"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        IGeoResource resource=CatalogTests.createGeoResource(features,true);
        service=resource.service(null);
        ds=service.resolve(MemoryDataStore.class, null);
        map=MapTests.createNonDynamicMapAndRenderer(resource, new Dimension(512,512));

    }


    @Override
    protected void tearDown() throws Exception {
        CatalogPlugin.getDefault().getLocalCatalog().remove(service);
        super.tearDown();
    }

    public void testSetFeatureAttribute() throws Exception{
        FeatureStore store=map.getLayersInternal().get(0).getResource(FeatureStore.class, null);
        FilterFactory factory=FilterFactoryFinder.createFilterFactory();
        FeatureCollection collection=store.getFeatures(factory.createFidFilter(features[1].getID()));
        FeatureIterator iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);
        store.modifyFeatures(store.getSchema().getAttributeType(1), MODIFIED_VALUE, factory.createFidFilter(features[1].getID()) );

        //not committed so other featurestores should not get modified value
        FeatureSource dsSource= ds.getFeatureSource("testType"); //$NON-NLS-1$
        collection=dsSource.getFeatures(factory.createFidFilter(features[1].getID()));
        iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);

        //layer featureStore has transactions so should have new value
        collection=store.getFeatures(factory.createFidFilter(features[1].getID()));
        iter = collection.features();
        assertEquals(MODIFIED_VALUE, iter.next().getAttribute(1));
        collection.close(iter);

        //Create and send a commit command
        map.sendCommandSync(EditCommandFactory.getInstance().createCommitCommand());

        //Now is committed so all FeatureSources should have the new value
        collection=dsSource.getFeatures(factory.createFidFilter(features[1].getID()));
        iter = collection.features();
        assertEquals(MODIFIED_VALUE, iter.next().getAttribute(1));
        collection.close(iter);
    }

    public void testRollback() throws Exception{
        FeatureStore store=map.getLayersInternal().get(0).getResource(FeatureStore.class, null);
        FilterFactory factory=FilterFactoryFinder.createFilterFactory();
        String id = features[1].getID();
        FeatureCollection collection=store.getFeatures(factory.createFidFilter(id));
        FeatureIterator iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);
        store.modifyFeatures(store.getSchema().getAttributeType(1), MODIFIED_VALUE, factory.createFidFilter(id) );

        //not committed so other featurestores should not get modified value
        FeatureSource dsSource= ds.getFeatureSource("testType"); //$NON-NLS-1$
        collection=dsSource.getFeatures(factory.createFidFilter(id));
        iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);

        //layer featureStore has transactions so should have new value
        collection=store.getFeatures(factory.createFidFilter(id));
        iter = collection.features();
        assertEquals(MODIFIED_VALUE, iter.next().getAttribute(1));
        collection.close(iter);

        //Create and send a commit command
        map.sendCommandSync(EditCommandFactory.getInstance().createRollbackCommand());

        //Now is committed so all FeatureSources should have the new value
        collection=store.getFeatures(factory.createFidFilter(id));
        iter = collection.features();
        assertEquals(ORIGINAL_VALUE, iter.next().getAttribute(1));
        collection.close(iter);
    }

}
