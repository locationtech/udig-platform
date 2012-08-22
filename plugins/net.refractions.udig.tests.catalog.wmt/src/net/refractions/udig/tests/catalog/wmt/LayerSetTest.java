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
