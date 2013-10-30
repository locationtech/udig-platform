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
import org.locationtech.udig.catalog.internal.wmt.tile.OSMTile;
import org.locationtech.udig.catalog.internal.wmt.tile.OSMTile.OSMTileName;
import org.locationtech.udig.catalog.internal.wmt.tile.OSMTile.OSMTileName.OSMZoomLevel;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.OSMMapnikSource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.OSMSource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSourceFactory;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;


public class OSMTileTest {

    @Test
    public void testGetTileFromCoordinate() {
        OSMSource osmSource = (OSMMapnikSource) WMTSourceFactory.createSource(null, WMTSource.getRelatedServiceUrl(OSMMapnikSource.class), null, true);
        
        OSMTile tile = (OSMTile) osmSource.getTileFactory().getTileFromCoordinate(49.38052, 6.55268, new OSMZoomLevel(6), osmSource);
        
        assertEquals("http://tile.openstreetmap.org/6/33/21.png", tile.getUrl().toString()); //$NON-NLS-1$
    }
    
    @Test
    public void testGetTileFromCoordinateLatitude() {
        OSMSource osmSource = (OSMMapnikSource) WMTSourceFactory.createSource(null, WMTSource.getRelatedServiceUrl(OSMMapnikSource.class), null, true);
        
        OSMTile tile = (OSMTile) osmSource.getTileFactory().getTileFromCoordinate(85.33499182341461, -103.68507972493057, new OSMZoomLevel(0), osmSource);
        
        assertEquals("http://tile.openstreetmap.org/0/0/0.png", tile.getUrl().toString()); //$NON-NLS-1$
    }
    
    @Test
    public void testGetExtentFromTileName() {
        OSMTileName tileName = new OSMTileName(33, 21, new OSMZoomLevel(6), null);
        
        ReferencedEnvelope extent = OSMTile.getExtentFromTileName(tileName);
        
        assertEquals(5.625, extent.getMinX(), 0.01);
        assertEquals(11.25, extent.getMaxX(), 0.01);
        assertEquals(48.92249, extent.getMinY(), 0.01);
        assertEquals(52.48278, extent.getMaxY(), 0.01);
        
        System.out.println("Min-X: " + extent.getMinX()); //$NON-NLS-1$
        System.out.println("Max-X: " + extent.getMaxX()); //$NON-NLS-1$
        System.out.println("Min-Y: " + extent.getMinY()); //$NON-NLS-1$
        System.out.println("Max-Y: " + extent.getMaxY()); //$NON-NLS-1$        
    }
    
    @Test
    public void testNeighbourCalculation() {
        OSMZoomLevel zoomLevel = new OSMZoomLevel(1);        
        OSMTileName tileName = new OSMTileName(1, 0, zoomLevel, null);
        
        assertEquals(new OSMTileName(0, 0, zoomLevel, null), tileName.getRightNeighbour());  
        //assertEquals(new OSMTileName(1, 1, zoomLevel, null), tileName.getUpperNeighbour());     
    }
    
    @Test
    public void testNeighbourCalculation2() {
        OSMZoomLevel zoomLevel = new OSMZoomLevel(2);        
        OSMTileName tileName = new OSMTileName(2, 2, zoomLevel, null);
        
        assertEquals(new OSMTileName(3, 2, zoomLevel, null), tileName.getRightNeighbour());     
    }
    
    @Test
    public void testGetExtentFromTileName2() {
        // actually not a test, just printing out the extent of the whole map
        OSMTileName tileName = new OSMTileName(0, 0, new OSMZoomLevel(0), null);
        ReferencedEnvelope extent = OSMTile.getExtentFromTileName(tileName);
        
        System.out.println("Min-X: " + extent.getMinX()); //$NON-NLS-1$
        System.out.println("Max-X: " + extent.getMaxX()); //$NON-NLS-1$
        System.out.println("Min-Y: " + extent.getMinY()); //$NON-NLS-1$
        System.out.println("Max-Y: " + extent.getMaxY()); //$NON-NLS-1$     
        
        assertEquals(true, true);
    }
 
}
