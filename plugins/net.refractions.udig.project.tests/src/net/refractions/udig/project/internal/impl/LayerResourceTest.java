package net.refractions.udig.project.internal.impl;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;

import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class LayerResourceTest extends AbstractProjectTestCase {
   
    private Map map;
    private Layer layer;
    private IGeoResource resource;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        resource=MapTests.createGeoResource("test", 3, true); //$NON-NLS-1$
        map = MapTests.createNonDynamicMapAndRenderer(resource, null);
        layer=map.getLayersInternal().get(0);
    }
    
    public void testFrameworkTransactionProtection() throws Exception{
  
        FeatureSource<SimpleFeatureType, SimpleFeature> source =layer.getResource(FeatureSource.class, null);
        assert(source!=null);
        FeatureStore<SimpleFeatureType, SimpleFeature> store=(FeatureStore<SimpleFeatureType, SimpleFeature>) source;
        try{
            store.setTransaction(Transaction.AUTO_COMMIT);
            assertFalse("Should not be permitted to change the transaction", true); //$NON-NLS-1$
        }catch (IllegalArgumentException e) {
            //correct behavior
        }
    }
}
