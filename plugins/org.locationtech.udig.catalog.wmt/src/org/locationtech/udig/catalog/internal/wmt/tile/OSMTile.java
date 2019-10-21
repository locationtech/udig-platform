/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.tile;

import java.net.MalformedURLException;
import java.net.URL;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.catalog.internal.wmt.Trace;
import org.locationtech.udig.catalog.internal.wmt.WMTPlugin;
import org.locationtech.udig.catalog.internal.wmt.tile.OSMTile.OSMTileName.OSMZoomLevel;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.OSMSource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSource;
import org.locationtech.udig.core.internal.CorePlugin;


public class OSMTile extends WMTTile {
    private OSMTileName tileName;
    private OSMSource osmSource;
    
    public OSMTile(int x, int y, OSMZoomLevel zoomLevel, OSMSource osmSource) {
        this(new OSMTileName(x, y, zoomLevel, osmSource), osmSource);
    }
    
    public OSMTile(OSMTileName tileName, OSMSource osmSource){
        super(OSMTile.getExtentFromTileName(tileName), tileName);
        
        this.tileName = tileName;
        this.osmSource = osmSource;
    }
    
    public OSMTile getRightNeighbour() {
        return new OSMTile(tileName.getRightNeighbour(), osmSource);
    }
    
    public OSMTile getLowerNeighbour() {
        return new OSMTile(tileName.getLowerNeighbour(), osmSource);
    }
    
    public Coordinate getCenter() {
        return new Coordinate( 
                tile2lon(tileName.getX() + 0.5, tileName.zoomLevel.getZoomLevel()), 
                tile2lat(tileName.getY() + 0.5, tileName.zoomLevel.getZoomLevel()) 
                );
    }
    
    
    @Override
    public boolean equals( Object obj ) {
        if (!(obj instanceof OSMTile)) return false;
        
        OSMTile other = (OSMTile) obj;
        
        return tileName.equals(other.tileName);
    }
    
    //region Get extent from tile-name        
    /**
     * Returns the bounding box of a tile by the given tile name.
     * 
     * see:
     * http://wiki.openstreetmap.org/wiki/Tilenames#compute_bounding_box_for_tile_number
     * 
     * @param tileName
     * @return BoundingBox for a tile
     */
    public static ReferencedEnvelope getExtentFromTileName(OSMTileName tileName) {
        ReferencedEnvelope extent = new ReferencedEnvelope(
                tile2lon(tileName.getX(), tileName.zoomLevel.getZoomLevel()), 
                tile2lon(tileName.getX() + 1, tileName.zoomLevel.getZoomLevel()), 
                tile2lat(tileName.getY(), tileName.zoomLevel.getZoomLevel()), 
                tile2lat(tileName.getY() + 1, tileName.zoomLevel.getZoomLevel()), 
                DefaultGeographicCRS.WGS84);
        
        return extent;
    }
    
    private static double tile2lon(double x, int z) {        
        return (x / Math.pow(2.0, z) * 360.0) - 180;
    }

    private static double tile2lat(double y, int z) {
        double n = Math.PI - ((2.0 * Math.PI * y) / Math.pow(2.0, z));        
        return 180.0 / Math.PI * Math.atan(0.5 * (Math.exp(n) - Math.exp(-n)));
    }
    //endregion
    


    public static class OSMTileFactory extends WMTTileFactory {

        //region Get tile from coordinate
        /**
         * Finds out the tile which contains the coordinate at a given zoom level.
         * 
         * see:
         * http://wiki.openstreetmap.org/wiki/Tilenames#Java
         * 
         * @param lat y
         * @param lon x
         * @param zoomLevel
         * @param wmtSource
         * @return
         */
        public OSMTile getTileFromCoordinate(double lat, double lon, 
                WMTZoomLevel zoomLevel, WMTSource wmtSource) {
            // normalize latitude and longitude
            lat = WMTTileFactory.normalizeDegreeValue(lat, 90);
            lon = WMTTileFactory.normalizeDegreeValue(lon, 180);
            
            /**
             * Because the latitude is only valid in 
             * 85.0511 °N to 85.0511 °S (http://wiki.openstreetmap.org/wiki/Tilenames#X_and_Y),
             * we have to correct if necessary.
             */
            lat = WMTTileFactory.moveInRange(lat, -85.0511, 85.0511);
            
            int xTile = (int) Math.floor((lon + 180) / 360 * (1 << zoomLevel.getZoomLevel()));
            int yTile = (int) Math.floor(
                    (1 - Math.log(Math.tan(lat * Math.PI / 180) + 1
                    / Math.cos(lat * Math.PI / 180))
                    / Math.PI)
                    / 2 * (1 << zoomLevel.getZoomLevel())
                );
            
            WMTPlugin.debug("[OSMTile.getTileFromCoordinate] " + zoomLevel.getZoomLevel() + //$NON-NLS-1$
            		"/" + xTile +  "/" + yTile + " lon: " + lon + " lat: " + lat, Trace.OSM);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            
            return new OSMTile(xTile, yTile, (OSMZoomLevel) zoomLevel, (OSMSource) wmtSource);
        }   
        //endregion

        public WMTZoomLevel getZoomLevel(int zoomLevel, WMTSource wmtSource) {
            return new OSMTileName.OSMZoomLevel(zoomLevel);
        }
        
    }
    
    /**
     * A small helper class which stores the tile-name.
     * 
     * @author to.srwn
     * @since 1.1.0
     */
    public static class OSMTileName extends WMTTileName{
        private OSMZoomLevel zoomLevel;
        private OSMSource osmSource;
                
        public OSMTileName(int x, int y, OSMZoomLevel zoomLevel, OSMSource source) {
            super(zoomLevel, x, y, source);
            this.zoomLevel = zoomLevel;
            this.osmSource = source;
        }
        
        /**
         * Generates the url for a tile:
         * <pre>
         * For example:
         * http://tile.openstreetmap.org/{zoom-level}/{x}/{y}.png
         * </pre>
         *
         * @param osmSource
         * @return
         */
        public URL getTileUrl() {
            try {
                return new URL(null,
                        osmSource.getTileUrl(zoomLevel.getZoomLevel(),
                                getX(),
                                getY()),
                        CorePlugin.RELAXED_HANDLER); 
            } catch (MalformedURLException e) {
                WMTPlugin.log("[OSMTile] Could not create the url for tile (Zoom: " + zoomLevel.getZoomLevel() + //$NON-NLS-1$
                        ", X: " + getX() + ", " + getY(), e); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            return null;
        }
        
        public OSMTileName getRightNeighbour() {
            return new OSMTileName( 
                        WMTTileName.arithmeticMod((getX()+1), zoomLevel.getMaxTilePerRowNumber()),
                        getY(),
                        zoomLevel,
                        osmSource);
        }
        
        public OSMTileName getLowerNeighbour() {
            return new OSMTileName( 
                        getX(),
                        WMTTileName.arithmeticMod((getY()+1), zoomLevel.getMaxTilePerColNumber()),
                        zoomLevel,
                        osmSource);
        }
        
        public String toString() {
            return zoomLevel.getZoomLevel() + "/" + getX() + "/" + getY(); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof OSMTileName)) return false;
            
            OSMTileName other = (OSMTileName) obj;
            
            return (getX() == other.getX()) && (getY() == other.getY()) && zoomLevel.equals(other.zoomLevel);
        }

        /**
         * Small helper class which wraps the zoom-level and
         * the maximum tile-number for x and y in this zoom-level. 
         * 
         * http://tile.openstreetmap.org/{zoom-level}/{x}/{y}.png
         * 
         * @author to.srwn
         * @since 1.1.0
         */
        public static class OSMZoomLevel extends WMTZoomLevel{
           
            public OSMZoomLevel(int zoomLevel) {
                super(zoomLevel);
            }
           
            /**
             * The maximum tile-number:
             * 
             * For example at zoom-level 2, the tilenames are in the following range:
             * 2/0/0 - 2/3/3
             * 
             * (zoom-level/x/y): zoom-level/2^(zoom-level)-1/2^(zoom-level)-1)
             */
            @Override
            public int calculateMaxTilePerColNumber(int zoomLevel) {
                return (1 << zoomLevel); // 2 ^ (zoomLevel)
            }

            @Override
            public int calculateMaxTilePerRowNumber(int zoomLevel) {
                return calculateMaxTilePerColNumber(zoomLevel);
            }   
        }
        
    }

}
