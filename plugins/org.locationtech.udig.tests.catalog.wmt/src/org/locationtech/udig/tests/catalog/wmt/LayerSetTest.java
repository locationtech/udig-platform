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
package org.locationtech.udig.tests.catalog.wmt;

import static org.junit.Assert.assertEquals;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.ww.LayerSet;

import org.jdom2.Element;
import org.junit.Test;

public class LayerSetTest {
    
    @Test
    public void testLayerSetConstruct() throws Exception {
        Element rootElement = QuadTileSetTest.getRootElement();
        
        LayerSet layerSet = new LayerSet(rootElement, "");
        
        assertEquals("ZoomIt! Data", layerSet.getName());
        assertEquals(5, layerSet.getChildLayerSets().size());
        assertEquals("GLOBAL", layerSet.getChildLayerSets().get(0).getName());
        assertEquals("-ZoomIt!%20Data-GLOBAL", layerSet.getChildLayerSets().get(0).getId());
        assertEquals("Bathymetry (30 arcsec)", 
                layerSet.getChildLayerSets().get(0).getQuadTileSets().get(0).getName());
        
    }
}
