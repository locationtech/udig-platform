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
package net.refractions.udig.project.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.refractions.udig.project.tests.support.MapTests;

import org.junit.Before;
import org.junit.Test;

public class LayerInterceptorsTest {

    @Before
    public void setUp() throws Exception {
        TestLayerAddedInterceptor.layerAdded=null;
        TestLayerRemovedInterceptor.layerRemoved=null;
        TestLayerCreatedInterceptor.layerCreated=null;
    }

    @Test
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
