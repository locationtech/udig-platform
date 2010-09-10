package net.refractions.udig.tests.catalog.wmt;

import java.util.Map;

import net.refractions.udig.catalog.internal.wmt.WMTRenderJob;
import net.refractions.udig.catalog.internal.wmt.tile.WWTile;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WWSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.ww.QuadTileSet;
import net.refractions.udig.catalog.wmsc.server.Tile;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jdom.Element;

import junit.framework.TestCase;

public class WWSourceTest extends TestCase {
    
    public void testCutExtentInTilesRobbenIsland() throws Exception {
        Element rootElement = QuadTileSetTest.getRootElement();
        
        QuadTileSet quadTileSet = new QuadTileSet(((Element) rootElement.getChildren("ChildLayerSet").get(3)).getChild("QuadTileSet"), "");
        
        assertEquals("Robben Island (0.5m)", quadTileSet.getName());
        
        WWSource wwSource = new WWSource(quadTileSet);
        
        // extent is not covered from QuadTileSet
        WMTRenderJob renderJob1 = WMTRenderJob.createRenderJob(
                new ReferencedEnvelope(1, 2, 48, 49, DefaultGeographicCRS.WGS84), 
                10000, 
                wwSource);
        
        Map<String, Tile> tiles1 = wwSource.cutExtentIntoTiles(renderJob1, 
                50, 
                true, null);
        assertEquals(true, tiles1.isEmpty());
        
        // bounds of the QuadTileSet are inside the extent
        WMTRenderJob renderJob2 = WMTRenderJob.createRenderJob(
                new ReferencedEnvelope(18.34, 18.4, -33.85, -33.5, DefaultGeographicCRS.WGS84), 
                20000000, 
                wwSource);
        Map<String, Tile> tiles2 = wwSource.cutExtentIntoTiles(renderJob2, 
                50, 
                true, null);
        assertEquals(1, tiles2.size());
    }
    

    public void testTileFromCoordinateWholeWorld() throws Exception {
        Element rootElement = QuadTileSetTest.getRootElement();
        QuadTileSet quadTileSet = new QuadTileSet(rootElement.getChild("ChildLayerSet").getChild("QuadTileSet"), "");
        
        WWSource wwSource = new WWSource(quadTileSet);
        
        assertEquals(new WWTile(0, 1, wwSource.getZoomLevel(0), wwSource).getTileName().toString(), 
                ((WWTile) wwSource.getTileFactory().getTileFromCoordinate(90, -180, wwSource.getZoomLevel(0), wwSource)).getTileName().toString());
        
        assertEquals(new WWTile(0, 1, wwSource.getZoomLevel(0), wwSource).getTileName().toString(), 
                ((WWTile) wwSource.getTileFactory().getTileFromCoordinate(89, -180, wwSource.getZoomLevel(0), wwSource)).getTileName().toString());
        
        assertEquals(new WWTile(0, 0, wwSource.getZoomLevel(0), wwSource).getTileName().toString(), 
                ((WWTile) wwSource.getTileFactory().getTileFromCoordinate(-89, -180, wwSource.getZoomLevel(0), wwSource)).getTileName().toString());
        
    
    }
}
