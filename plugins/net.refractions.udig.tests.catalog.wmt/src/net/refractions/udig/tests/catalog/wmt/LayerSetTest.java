package net.refractions.udig.tests.catalog.wmt;

import junit.framework.TestCase;
import net.refractions.udig.catalog.internal.wmt.wmtsource.ww.LayerSet;

import org.jdom.Element;

public class LayerSetTest extends TestCase {
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
