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
package net.refractions.udig.tests.catalog.wmt;

import static org.junit.Assert.assertEquals;
import net.refractions.udig.catalog.internal.wmt.wmtsource.ww.LayerSet;

import org.jdom.Element;
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
