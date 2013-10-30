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
import org.locationtech.udig.catalog.internal.wmt.tile.NASATile;
import org.locationtech.udig.catalog.internal.wmt.tile.NASATile.NASATileName;
import org.locationtech.udig.catalog.internal.wmt.tile.NASATile.NASATileName.NASAZoomLevel;
import org.locationtech.udig.catalog.internal.wmt.tile.WMTTile.WMTTileFactory;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.NASASource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSourceFactory;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.Before;
import org.junit.Test;


public class NASATileTest {
    
    private NASASource source;
    private NASASource sourceUSA;
    
    @Before
    public void setUp() throws Exception {
        
        String resourceId1 = "Global Mosaic, pan sharpened visual";        
        source = (NASASource) WMTSourceFactory.createSource(null, WMTSource.getRelatedServiceUrl(NASASource.class), resourceId1, true);
        
        String resourceId2 =  "Continental US Elevation";        
        sourceUSA = (NASASource) WMTSourceFactory.createSource(null, WMTSource.getRelatedServiceUrl(NASASource.class), resourceId2, true); 
    }

    @Test
    public void testZoomLevel() {
        
        NASAZoomLevel zoomLevel = source.getZoomLevel(0);


        ReferencedEnvelope boundsFirstTile = new ReferencedEnvelope(-180, -116, -26, 90, DefaultGeographicCRS.WGS84);
        assertEquals("http://wms.jpl.nasa.gov/wms.cgi?request=GetMap&layers=global_mosaic&srs=EPSG:4326&format=image/jpeg&styles=visual&width=512&height=512&bbox=-180,-26,-116,90", 
                zoomLevel.getTileUrl(boundsFirstTile));
                
        assertEquals(6, zoomLevel.calculateMaxTilePerRowNumber(0));
        assertEquals(3, zoomLevel.calculateMaxTilePerColNumber(0));
    }
    
    @Test
    public void testGetTileFromCoordinate() {
        
        NASAZoomLevel zoomLevel = source.getZoomLevel(0);
        WMTTileFactory tileFactory = source.getTileFactory();
        
        NASATile tile = (NASATile) tileFactory.getTileFromCoordinate(90, -180, zoomLevel, source);        
        assertEquals("Global Mosaic, pan sharpened visual_0_0_0", tile.getId());
        
        NASATile tile2 = (NASATile) tileFactory.getTileFromCoordinate(-90, -180, zoomLevel, source);        
        assertEquals("Global Mosaic, pan sharpened visual_0_0_2", tile2.getId());
        
        NASATile tile3 = (NASATile) tileFactory.getTileFromCoordinate(-90, -115, zoomLevel, source);        
        assertEquals("Global Mosaic, pan sharpened visual_0_1_2", tile3.getId());
    }
    
    @Test
    public void testGetTileFromCoordinateUSA() {
        
        NASAZoomLevel zoomLevel = sourceUSA.getZoomLevel(3);
        WMTTileFactory tileFactory = sourceUSA.getTileFactory();
        
        NASATile tile = (NASATile) tileFactory.getTileFromCoordinate(50, -125, zoomLevel, sourceUSA);        
        assertEquals("Continental US Elevation_3_0_0", tile.getId());
        
        NASATile tile2 = (NASATile) tileFactory.getTileFromCoordinate(39, -125, zoomLevel, sourceUSA);        
        assertEquals("Continental US Elevation_3_0_1", tile2.getId());
        
        NASATile tile3 = (NASATile) tileFactory.getTileFromCoordinate(39, -114, zoomLevel, sourceUSA);        
        assertEquals("Continental US Elevation_3_1_1", tile3.getId());
    }
    
    @Test
    public void testGetExtentFromTileName() {
        NASAZoomLevel zoomLevel = source.getZoomLevel(0);
        
        NASATileName tileName1 = new NASATileName(0, 0, zoomLevel, source);
        
        assertEquals("ReferencedEnvelope[-180.0 : -116.0, 26.0 : 90.0]", 
                NASATile.getExtentFromTileName(tileName1).toString());
        
        assertEquals("ReferencedEnvelope[-180.0 : -116.0, -38.0 : 26.0]", 
                NASATile.getExtentFromTileName(tileName1.getLowerNeighbour()).toString());
        
        assertEquals("ReferencedEnvelope[-116.0 : -52.0, 26.0 : 90.0]", 
                NASATile.getExtentFromTileName(tileName1.getRightNeighbour()).toString());
    }
    
    @Test
    public void testGetExtentFromTileNameUSA() {
        NASAZoomLevel zoomLevel = sourceUSA.getZoomLevel(3);
        
        NASATileName tileName1 = new NASATileName(0, 0, zoomLevel, sourceUSA);
        
        equals(NASATile.getExtentFromTileName(tileName1), 
                -125.0, -114.33333333, 39.33333333, 50.0); 

        equals(NASATile.getExtentFromTileName(tileName1.getLowerNeighbour()), 
                -125.0, -114.33333333, 28.66666666, 39.33333333); 

        equals(NASATile.getExtentFromTileName(tileName1.getRightNeighbour()), 
                -114.33333333,-103.66666666, 39.33333333, 50.0);         
    }
    
    private void equals(ReferencedEnvelope env, double xmin, double xmax, double ymin, double ymax) {
        double delta = 0.0000001;
        
        assertEquals(xmax, env.getMaxX(), delta);
        assertEquals(xmin, env.getMinX(), delta);
        assertEquals(ymax, env.getMaxY(), delta);
        assertEquals(ymin, env.getMinY(), delta);
    }
    
}
