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
package net.refractions.udig.project.internal.render;

import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.tests.support.AbstractProjectTestCase;
import net.refractions.udig.project.tests.support.MapTests;
import net.refractions.udig.ui.tests.support.UDIGTestUtil;

import org.junit.Before;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

public class SelectionLayerTest extends AbstractProjectTestCase {
    
    IRenderContext context;
    SelectionLayer selectionLayer;
    Layer layer;

    @Before
    public void setUp() throws Exception {
        SimpleFeature[] features=UDIGTestUtil.createDefaultTestFeatures("testType", 3); //$NON-NLS-1$
        Map map=MapTests.createNonDynamicMapAndRenderer(MapTests.createGeoResource(features,true), new Dimension(512,512));
        context = map.getRenderManagerInternal().getRenderExecutor().getContext();
        layer = map.getLayersInternal().get(0);
//        layer.setZorder(5);
        selectionLayer = new SelectionLayer(layer);
    }

    @Test
    public void testCompareTo() {
        assertTrue(layer.compareTo(selectionLayer) < 0);
        assertTrue(selectionLayer.compareTo(layer) > 0);
    }
}
