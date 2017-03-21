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
package org.locationtech.udig.project.internal.render;

import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.udig.core.testsupport.FeatureCreationTestUtil;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.testsupport.AbstractProjectTestCase;
import org.locationtech.udig.project.testsupport.MapTests;
import org.opengis.feature.simple.SimpleFeature;

public class SelectionLayerTest extends AbstractProjectTestCase {
    
    IRenderContext context;
    SelectionLayer selectionLayer;
    Layer layer;

    @Before
    public void setUp() throws Exception {
        SimpleFeature[] features=FeatureCreationTestUtil.createDefaultTestFeatures("testType", 3); //$NON-NLS-1$
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
