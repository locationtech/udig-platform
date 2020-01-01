/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureEvent.Type;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.ITransientResolve;
import org.locationtech.udig.catalog.tests.CatalogTests;
import org.locationtech.udig.project.IResourceInterceptor;
import org.locationtech.udig.project.internal.impl.LayerImpl;
import org.locationtech.udig.project.internal.impl.LayerResource;
import org.locationtech.udig.project.internal.interceptor.ResourceCacheInterceptor;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.tests.TestInterceptorCaching;
import org.locationtech.udig.project.tests.TestInterceptorPost;
import org.locationtech.udig.project.tests.TestInterceptorPre;
import org.locationtech.udig.project.tests.support.MapTests;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Tests LayerImpl
 * @author Jesse
 * @since 1.1.0
 */
public class LayerImplTest {

    private Map map;
    private LayerImpl layer;

    @Before
    public void setUp() throws Exception {
        map=MapTests.createDefaultMap("typename1", 3, true, null); //$NON-NLS-1$
        layer=(LayerImpl) map.getMapLayers().get(0);
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#getGeoResources()}.
     */
    @Test
    public void testGetGeoResources() {
        assertEquals(1, layer.getGeoResources().size());
        assertTrue("Was :"+layer.getGeoResources().get(0).getClass(),layer.getGeoResources().get(0) instanceof LayerResource); //$NON-NLS-1$
    }

    /**
     * Testing that the SetTransactionInterceptor is being ran correctly.
     * Testing that the CachingInterceptor is being ran correctly
     * Testing that the TestInterceptor is being ran correctly.
     */
    @Test
    public void testGetGeoResourcesInterceptors() throws IOException {
        NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        FeatureStore<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureStore.class, nullProgressMonitor);
        Transaction transaction = resource.getTransaction();
        assertNotNull(transaction);
        assertTrue(transaction==Transaction.AUTO_COMMIT);
        assertSame(transaction, resource.getTransaction());
        assertSame(resource, layer.getResource(FeatureStore.class, nullProgressMonitor));
        assertSame(resource, layer.getResource(FeatureSource.class, nullProgressMonitor));
        
        assertTrue(TestInterceptorPre.runs>0);
        assertFalse(TestInterceptorCaching.cached);
        assertFalse(TestInterceptorCaching.obtained);
        assertTrue(TestInterceptorPost.runs>0);
        
        ProjectPlugin.getPlugin().getPreferenceStore().setValue(PreferenceConstants.P_LAYER_RESOURCE_CACHING_STRATEGY, "org.locationtech.udig.project.tests.org.locationtech.udig.project.tests.interceptor2"); //$NON-NLS-1$
        try{
            TestInterceptorPre.runs=0;
            TestInterceptorPost.runs=0;
            
            assertNotSame(resource, layer.getResource(FeatureStore.class, nullProgressMonitor));
    
            assertEquals(1, TestInterceptorPre.runs);
            assertTrue(TestInterceptorCaching.cached);
            assertFalse(TestInterceptorCaching.obtained);
            assertEquals(1,TestInterceptorPost.runs);
            
            layer.getResource(FeatureStore.class, nullProgressMonitor);
            
            assertEquals(1, TestInterceptorPre.runs);
            assertTrue(TestInterceptorCaching.cached);
            assertTrue(TestInterceptorCaching.obtained);
            assertEquals(2, TestInterceptorPost.runs);
            
            TestInterceptorPre.runs=0;
            TestInterceptorPost.runs=0;
    
            layer.getResource(ITransientResolve.class, nullProgressMonitor);
            
            assertEquals(0, TestInterceptorPre.runs);
            assertEquals(1, TestInterceptorPost.runs);
        }finally{
            ProjectPlugin.getPlugin().getPreferenceStore().setValue(PreferenceConstants.P_LAYER_RESOURCE_CACHING_STRATEGY, ResourceCacheInterceptor.ID);
        }
    }
    
    /**
     * Tests the case where a interceptor interfers with a later interceptor that is run.
     *
     * @throws Exception
     */
    @Test
    public void testRunCoDependentInterceptors() throws Exception {
        LayerResource resource=(LayerResource) layer.getGeoResources().get(0);
        
        
        try{
            resource.testingOnly_sort(new Comparator<IResourceInterceptor<? extends Object>>(){
                
                public int compare( IResourceInterceptor< ? extends Object> o1, IResourceInterceptor< ? extends Object> o2 ) {
                    if( o1 instanceof TestInterceptorPost){
                        return -1;
                    }
                    if( o2 instanceof TestInterceptorPost){
                        return 1;
                    } 
                    return 0;
                }
                
            }, false);
            TestInterceptorPost.changeType=true;
            NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
            FeatureSource<SimpleFeatureType, SimpleFeature> resolve = resource.resolve(FeatureSource.class, nullProgressMonitor);
            assertNotNull(resolve);
        }finally{
            TestInterceptorPost.changeType=false;     
            resource.testingOnly_sort(null, false);

        }
    }
    
    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#getResource(java.lang.Class, org.eclipse.core.runtime.IProgressMonitor)}.
     */
    @Test
    public void testGetResource() throws IOException {
        assertNotNull(layer.getResource(FeatureStore.class, new NullProgressMonitor()));
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#findGeoResource(java.lang.Class)}.
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testFindGeoResource() throws Exception{
        String string = "value"; //$NON-NLS-1$
        layer = MapTests.createLayer(new URL("http://testresourcefindGeoResource.org"), string , null); //$NON-NLS-1$
        List resolveTos = layer.getResource(List.class, null);
        Integer integer = Integer.valueOf(2);
        resolveTos.add(integer);
        Float floatValue = Float.valueOf(2.0f);
        resolveTos.add(floatValue);
        
        assertNotNull(layer.findGeoResource(String.class));
        assertNotNull(layer.findGeoResource(Float.class));
        assertNotNull(layer.findGeoResource(Integer.class));
        assertNull(layer.findGeoResource(Double.class));
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#isType(java.lang.Class)}.
     */
    @Test
    public void testHasResource() {
        assertTrue(layer.hasResource(FeatureStore.class));
        assertTrue(layer.hasResource(FeatureSource.class));
        assertFalse(layer.hasResource(Integer.class));
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#getCRS()}.
     */
    @Test
    public void testGetCRS() {
        //TODO implement test
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#getDefaultColor()}.
     */
    @Test
    public void testGetDefaultColor() {
        //TODO implement test 
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#getMinScaleDenominator()}.
     */
    @Test
    public void testGetMinScaleDenominator() {
        //TODO implement test 
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#getMaxScaleDenominator()}.
     */
    @Test
    public void testGetMaxScaleDenominator() {
        //TODO implement test 
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#refresh(org.locationtech.jts.geom.Envelope)}.
     */
    @Test
    public void testRefresh() {
        //TODO implement test 
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#layerToMapTransform()}.
     */
    @Test
    public void testLayerToMapTransform() {
        //TODO implement test 
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#mapToLayerTransform()}.
     */
    @Test
    public void testMapToLayerTransform() {
        //TODO implement test 
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#createBBoxFilter(org.locationtech.jts.geom.Envelope, org.eclipse.core.runtime.IProgressMonitor)}.
     */
    @Test
    public void testCreateBBoxFilter() {
        //TODO implement test 
    }

    /**
     * Test method for {@link org.locationtech.udig.project.internal.impl.LayerImpl#changed(org.locationtech.udig.catalog.IResolveChangeEvent)}.
     */
    @Test
    public void testChanged() {
        //TODO implement test 
    }
    
    @Test
    public void testSetZOrder() throws Exception {
        IGeoResource resource = CatalogTests.createGeoResource("resource", 3, false); //$NON-NLS-1$
        Layer createLayer = map.getLayerFactory().createLayer(resource);
        createLayer.setName("layer1"); //$NON-NLS-1$
        map.getLayersInternal().add(createLayer);

        createLayer = map.getLayerFactory().createLayer(resource);
        createLayer.setName("layer2"); //$NON-NLS-1$
        map.getLayersInternal().add(createLayer);

        createLayer = map.getLayerFactory().createLayer(resource);
        createLayer.setName("layer3"); //$NON-NLS-1$
        map.getLayersInternal().add(createLayer);

        createLayer = map.getLayerFactory().createLayer(resource);
        createLayer.setName("layer4"); //$NON-NLS-1$
        map.getLayersInternal().add(createLayer);
        
        layer.setZorder(2);
        assertEquals(2, layer.getZorder());
        assertEquals(2, map.getLayersInternal().indexOf(layer));

        layer.setZorder(0);
        assertEquals(0, layer.getZorder());
        assertEquals(0, map.getLayersInternal().indexOf(layer));

    }
    
    @SuppressWarnings("unchecked")
    @Ignore
    @Test
    public void testGetBounds() throws Exception {
        IGeoResource resource = CatalogTests.createGeoResource("resource", 0, false); //$NON-NLS-1$
        FeatureStore<SimpleFeatureType, SimpleFeature> fs = resource.resolve(FeatureStore.class, null);
        DefaultFeatureCollection collection = new DefaultFeatureCollection();
        collection.clear();
        GeometryFactory fac=new GeometryFactory();

        Object[] atts = new Object[]{
                fac.createPoint(new Coordinate(0,0)),
                "name1" //$NON-NLS-1$
        };
        SimpleFeatureType schema = fs.getSchema();
        collection.add(SimpleFeatureBuilder.build(schema, atts, "id"));

        atts = new Object[]{
                fac.createPoint(new Coordinate(45,45)),
                "name2" //$NON-NLS-1$
        };
        collection.add(SimpleFeatureBuilder.build(schema, atts, "id"));

        fs.removeFeatures(Filter.INCLUDE);
        
        fs.addFeatures(collection);
        Layer layer = map.getLayerFactory().createLayer(resource);
        layer.setName("layer1"); //$NON-NLS-1$
        map.getLayersInternal().add(layer);

        assertEquals( new Envelope(0,45,0,45), layer.getBounds(null, layer.getCRS()) );
        
        CoordinateReferenceSystem decode = CRS.decode("EPSG:2065");
        SimpleFeature[] feature = UDIGTestUtil.createTestFeatures("test", new Point[]{null}, new String[]{"name"}, decode); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        layer = map.getLayerFactory().createLayer(CatalogTests.createGeoResource(feature, true));
        map.getLayersInternal().add( layer );
        
        ReferencedEnvelope bounds = layer.getBounds(null, decode);
        assertEquals(decode, bounds.getCoordinateReferenceSystem());
        
        // TODO
        
    }

    private void addFeatureEvents(List<FeatureEvent> featureChanges, int quantity, boolean useIndex) throws IOException {
        final NullProgressMonitor nullProgressMonitor = new NullProgressMonitor();
        final ReferencedEnvelope envelop = new ReferencedEnvelope(0, 0.1, 0, 0.1, DefaultGeographicCRS.WGS84);
        for (int i=0; i<quantity; i++) {
            FeatureEvent featureEvent = new FeatureEvent(layer.getResource(FeatureSource.class, nullProgressMonitor), Type.CHANGED, envelop);

            if (useIndex) {
                featureChanges.add(0, featureEvent);
            } else {
                featureChanges.add(featureEvent);
            }
        }
    }

    @Test
    public void testFeatureSourceCacheCleanupAddWithoutIndex() throws IOException {
        List<FeatureEvent> featureChanges = layer.getFeatureChanges();
        featureChanges.clear();
        addFeatureEvents(featureChanges, 42, false);
        assertTrue(featureChanges.size() <= 10);
    }

    @Test
    public void testFeatureSourceCacheCleanupAddWithIndex() throws IOException {
        List<FeatureEvent> featureChanges = layer.getFeatureChanges();
        featureChanges.clear();
        addFeatureEvents(featureChanges, 42, true);
        assertTrue(featureChanges.size() <= 10);
    }
}
