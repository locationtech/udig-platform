package net.refractions.udig.project.internal.impl;

import static org.junit.Assert.assertNotNull;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class LayerResourceTest extends AbstractProjectTestCase {
   
    private Map map;
    private Layer layer;
    private IGeoResource resource;

    @Before
    public void setUp() throws Exception {
        resource=MapTests.createGeoResource("test", 3, true); //$NON-NLS-1$
        map = MapTests.createNonDynamicMapAndRenderer(resource, null);
        layer=map.getLayersInternal().get(0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFrameworkTransactionProtection() throws Exception{
        FeatureSource<SimpleFeatureType, SimpleFeature> source =layer.getResource(FeatureSource.class, null);
        assertNotNull(source);
        FeatureStore<SimpleFeatureType, SimpleFeature> store=(FeatureStore<SimpleFeatureType, SimpleFeature>) source;
        store.setTransaction(Transaction.AUTO_COMMIT);
    }
}
