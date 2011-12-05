package net.refractions.udig.project.internal;

import junit.framework.TestCase;
import net.refractions.udig.project.tests.support.MapTests;

public class LayerInterceptorsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
        TestLayerAddedInterceptor.layerAdded=null;
        TestLayerRemovedInterceptor.layerRemoved=null;
        TestLayerCreatedInterceptor.layerCreated=null;
    }

    public void testLayerInterceptors() throws Exception {
        assertNull(TestLayerAddedInterceptor.layerAdded);
        assertNull(TestLayerRemovedInterceptor.layerRemoved);
        assertNull(TestLayerCreatedInterceptor.layerCreated);
        
        
        Map map=MapTests.createDefaultMap("name", 1, true, null); //$NON-NLS-1$

        // Layer is wrapped by a TestLayer so the creation interceptor is run on the wrapped object
        // but id should still be the same.
        assertEquals(map.getMapLayers().get(0).getID(), TestLayerCreatedInterceptor.layerCreated.getID());
        assertEquals(map.getMapLayers().get(0), TestLayerAddedInterceptor.layerAdded);
        assertNull(TestLayerRemovedInterceptor.layerRemoved);
        
        TestLayerAddedInterceptor.layerAdded=null;
        TestLayerCreatedInterceptor.layerCreated=null;
        
        Layer createLayer = ProjectFactory.eINSTANCE.createLayer();
        map.getLayersInternal().add(createLayer);
        
        assertEquals(createLayer, TestLayerAddedInterceptor.layerAdded);
        assertNull(TestLayerCreatedInterceptor.layerCreated);
        assertNull(TestLayerRemovedInterceptor.layerRemoved);
        
        TestLayerAddedInterceptor.layerAdded=null;
        
        map.getLayersInternal().remove(createLayer);
        
        assertEquals(createLayer, TestLayerRemovedInterceptor.layerRemoved);
        assertNull(TestLayerAddedInterceptor.layerAdded);
        assertNull(TestLayerCreatedInterceptor.layerCreated);

    }

}
