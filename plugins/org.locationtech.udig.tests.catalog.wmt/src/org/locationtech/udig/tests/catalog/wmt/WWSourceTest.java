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

import java.util.Map;

import org.locationtech.udig.catalog.internal.wmt.WMTRenderJob;
import org.locationtech.udig.catalog.internal.wmt.tile.WWTile;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WWSource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.ww.QuadTileSet;
import org.locationtech.udig.catalog.wmsc.server.Tile;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jdom2.Element;
import org.junit.Test;

public class WWSourceTest {
    
    @Test
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
                true, null, 1000);
        assertEquals(true, tiles1.isEmpty());
        
        // bounds of the QuadTileSet are inside the extent
        WMTRenderJob renderJob2 = WMTRenderJob.createRenderJob(
                new ReferencedEnvelope(18.34, 18.4, -33.85, -33.5, DefaultGeographicCRS.WGS84), 
                20000000, 
                wwSource);
        Map<String, Tile> tiles2 = wwSource.cutExtentIntoTiles(renderJob2, 
                50, 
                true, null, 1000);
        assertEquals(1, tiles2.size());
    }
    
    @Test
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
