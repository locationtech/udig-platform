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

import java.net.URL;
import java.net.URLConnection;

import org.locationtech.udig.catalog.internal.wmt.tile.WWTile;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WWSource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.ww.QuadTileSet;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import com.vividsolutions.jts.geom.Envelope;

public class QuadTileSetTest {
    
    private static Element rootElement = null;
    
    public static Element getRootElement() throws Exception {
        if (rootElement == null) {
            URL url = QuadTileSetTest.class.getResource("zoomit.xml");
            
            SAXBuilder builder = new SAXBuilder(false); 
            URLConnection connection = url.openConnection();
            
            Document dom = builder.build(connection.getInputStream());
            
            rootElement = dom.getRootElement();  
        }
        
        return rootElement;         
    }
    
    @Test
    public void testQuadTileConstruct() throws Exception {
        Element rootElement = getRootElement();
        
        QuadTileSet quadTileSet = new QuadTileSet(rootElement.getChild("ChildLayerSet").getChild("QuadTileSet"), "");
        
        assertEquals("Bathymetry (30 arcsec)", quadTileSet.getName());
        assertEquals("ReferencedEnvelope[-180.0 : 180.0, -90.0 : 90.0]", quadTileSet.getBounds().toString());
        
        assertEquals("jpg", quadTileSet.getFileFormat());
        assertEquals(512, quadTileSet.getTileSize());
        
        assertEquals(6, quadTileSet.getScaleList().length);
        
        // Lowest Zoom-Level
        assertEquals(90, quadTileSet.getZoomLevel(0).getHeightInWorldUnits(), 0.0000000001);
        assertEquals(2, quadTileSet.getZoomLevel(0).getMaxTilePerColNumber());
        assertEquals(4, quadTileSet.getZoomLevel(0).getMaxTilePerRowNumber());
        
        // Next Zoom-Level
        assertEquals(45, quadTileSet.getZoomLevel(1).getHeightInWorldUnits(), 0.0000000001);
        assertEquals(4, quadTileSet.getZoomLevel(1).getMaxTilePerColNumber());
        assertEquals(8, quadTileSet.getZoomLevel(1).getMaxTilePerRowNumber());
        
        // Url-Construction
        assertEquals("http://s2.tileservice.worldwindcentral.com/getTile?T=global.topo_bathy_30arc&firstLevel=1&L=0&X=0&Y=0", 
                quadTileSet.getZoomLevel(0).getTileUrl(
                        new WWTile.WWTileName(0, 0, quadTileSet.getZoomLevel(0), null)));
    }
    
    @Test
    public void testTiles() throws Exception {
        Element rootElement = getRootElement();        
        QuadTileSet quadTileSet = new QuadTileSet(rootElement.getChild("ChildLayerSet").getChild("QuadTileSet"), "");
        WWSource wwSource = new WWSource(quadTileSet);
        
        assertEquals(2, wwSource.getZoomLevel(0).getMaxTilePerColNumber());
        assertEquals(4, wwSource.getZoomLevel(0).getMaxTilePerRowNumber());
        
        WWTile tile1 = new WWTile(0, 0, wwSource.getZoomLevel(0), wwSource);        
        assertEquals(new Envelope(-180, -90, -90, 0), tile1.getBounds());
        
        WWTile tile2 = tile1.getRightNeighbour();        
        assertEquals(new Envelope(-90, 0, -90, 0), tile2.getBounds());
        
        WWTile tile3 = tile1.getLowerNeighbour();        
        assertEquals(new Envelope(-180, -90, 0, 90), tile3.getBounds());
        assertEquals("0/0/1", tile3.getTileName().toString());
        
        WWTile tile4 = tile3.getRightNeighbour();        
        assertEquals(new Envelope(-90, 0, 0, 90), tile4.getBounds());
        
        assertEquals(tile4, tile4.getLowerNeighbour().getLowerNeighbour());
        
    }
    

}
