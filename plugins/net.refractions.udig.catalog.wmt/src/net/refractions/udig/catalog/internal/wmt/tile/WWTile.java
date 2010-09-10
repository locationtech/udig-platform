package net.refractions.udig.catalog.internal.wmt.tile;

import java.net.HttpURLConnection;
import java.net.URL;

import net.refractions.udig.catalog.internal.wmt.Trace;
import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.tile.WWTile.WWTileName.WWZoomLevel;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WWSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.ww.ImageAccessor;
import net.refractions.udig.core.internal.CorePlugin;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

public class WWTile extends WMTTile {

    private WWTileName tileName;
    private WWSource wwSource;
    
    public WWTile(int x, int y, WWZoomLevel zoomLevel, WWSource wwSource) {
        this(new WWTileName(x, y, zoomLevel, wwSource), wwSource);
    }
    
    public WWTile(WWTileName tileName, WWSource wwSource){
        super(WWTile.getExtentFromTileName(tileName), tileName);
        
        this.tileName = tileName;
        this.wwSource = wwSource;
    }
    
    public WWTileName getTileName() {
        return tileName;
    }
    
    //region Get extent from tile-name        
    /**
     * Returns the bounding box of a tile by the given tile name.
     * 
     * The lower left corner of tile 0/0 is at -90,-180.
     * see: http://worldwindcentral.com/wiki/Image:Worldwindtilesystemtmr0.png
     *  
     * @param tileName
     * @return BoundingBox for a tile
     */
    public static ReferencedEnvelope getExtentFromTileName(WWTileName tileName) {
        ReferencedEnvelope extent = new ReferencedEnvelope(
                tile2lon(tileName.getX(), tileName), 
                tile2lon(tileName.getX() + 1, tileName), 
                tile2lat(tileName.getY(), tileName), 
                tile2lat(tileName.getY() + 1, tileName), 
                DefaultGeographicCRS.WGS84);
        
        return extent;
    }
    
    public static double tile2lat(int row, WWTileName tileName) {       
        return WWSource.WORLD_BOUNDS.getMinY() + row * tileName.getHeightInWorldUnits();     
    }
    
    public static double tile2lon(int col, WWTileName tileName) { 
        return WWSource.WORLD_BOUNDS.getMinX() + col * tileName.getWidthInWorldUnits();            
    }

    //endregion
    
    @Override
    public WWTile getLowerNeighbour() {
        return new WWTile(tileName.getLowerNeighbour(), wwSource);
    }

    @Override
    public WWTile getRightNeighbour() {
        return new WWTile(tileName.getRightNeighbour(), wwSource);
    }
    
    /**
     * Put on the "WorldWind-hat":
     * http://worldwindcentral.com/wiki/User-Agent
     */
    protected void setConnectionParams(HttpURLConnection connection) {
        connection.setRequestProperty("User-Agent", "World Wind v1.4.0.0 (Microsoft Windows NT 5.1.2600.0, en-US)"); //$NON-NLS-1$ //$NON-NLS-2$
    } 
    
    public static class WWTileFactory extends WMTTileFactory {

        //region Get tile from coordinate
        /**
         * Finds out the tile which contains the coordinate at a given zoom level.
         * 
         * @param lat y
         * @param lon x
         * @param zoomLevel
         * @param wmtSource
         * @return
         */
        public WWTile getTileFromCoordinate(double lat, double lon, 
                WMTZoomLevel zoomLevel, WMTSource wmtSource) {
            WWSource wwSource = (WWSource) wmtSource;
            
            // normalize latitude and longitude
            lat = WMTTileFactory.normalizeDegreeValue(lat, 90);
            lon = WMTTileFactory.normalizeDegreeValue(lon, 180);

            lat = WMTTileFactory.moveInRange(lat, 
                    wwSource.getBounds().getMinY(), wwSource.getBounds().getMaxY());
            lon = WMTTileFactory.moveInRange(lon, 
                    wwSource.getBounds().getMinX(), wwSource.getBounds().getMaxX());
            
            WWZoomLevel wwZoomLevel = (WWZoomLevel) zoomLevel;
            int row = WMTTileName.arithmeticMod(
                        (int) Math.ceil(Math.abs((lat - WWSource.WORLD_BOUNDS.getMinY())  / wwZoomLevel.getHeightInWorldUnits())) - 1,
                        zoomLevel.getMaxTilePerColNumber());
            int col = WMTTileName.arithmeticMod(
                        (int) Math.abs((lon - WWSource.WORLD_BOUNDS.getMinX()) / wwZoomLevel.getWidthInWorldUnits()),
                        zoomLevel.getMaxTilePerRowNumber());
            
            WMTPlugin.debug("[WWTile.getTileFromCoordinate] " + zoomLevel.getZoomLevel() + //$NON-NLS-1$
                    "/" + col +  "/" + row + " lon: " + lon + " lat: " + lat, Trace.WW);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            
            return new WWTile(col, row, wwZoomLevel, (WWSource) wmtSource);
        }
        //endregion

        public WMTZoomLevel getZoomLevel(int zoomLevel, WMTSource wmtSource) {
            WWSource wwSource = (WWSource) wmtSource;
            
            return wwSource.getZoomLevel(zoomLevel);
        }
        
    }
    
    /**
     * A small helper class which stores the tile-name.
     * 
     * @author to.srwn
     * @since 1.1.0
     */
    public static class WWTileName extends WMTTileName{
        private WWZoomLevel zoomLevel;
        private WWSource wwSource;
                
        public WWTileName(int x, int y, WWZoomLevel zoomLevel, WWSource source) {
            super(zoomLevel, x, y, source);
            this.zoomLevel = zoomLevel;
            this.wwSource = source;
        }
        
        /**
         * 
         * 
         * @return
         */
        public URL getTileUrl() {
            try {
                String tileUrl = zoomLevel.getTileUrl(this);
                
                return new URL(null, tileUrl, CorePlugin.RELAXED_HANDLER);  
                
            } catch (Exception e) {
                WMTPlugin.log("[WWTile] Could not create the url for tile (Zoom: " + zoomLevel.getZoomLevel() + //$NON-NLS-1$
                        ", X: " + getX() + ", " + getY(), e); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            return null;
        }
       

        
        public WWTileName getRightNeighbour() {
            return new WWTileName( 
                        WMTTileName.arithmeticMod((getX()+1), zoomLevel.getMaxTilePerRowNumber()),
                        getY(),
                        zoomLevel,
                        wwSource);
        }
        
        public WWTileName getLowerNeighbour() {
            return new WWTileName( 
                        getX(),
                        WMTTileName.arithmeticMod((getY()-1), zoomLevel.getMaxTilePerColNumber()),
                        zoomLevel,
                        wwSource);
        }
        
        public double getWidthInWorldUnits() {
            return zoomLevel.getWidthInWorldUnits();
        }
        
        public double getHeightInWorldUnits() {
            return zoomLevel.getHeightInWorldUnits();
        }
        
        public WWSource getWwSource() {
            return wwSource;
        }
        
        public String toString() {
            return zoomLevel.getZoomLevel() + "/" + getX() + "/" + getY(); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof WWTileName)) return false;
            
            WWTileName other = (WWTileName) obj;
            
            return (getX() == other.getX()) && (getY() == other.getY()) && zoomLevel.equals(other.zoomLevel);
        }

        /**
         * Small helper class which wraps the zoom-level and
         * the maximum tile-number for x and y in this zoom-level. 
         * 
         * 
         * @author to.srwn
         * @since 1.1.0
         */
        public static class WWZoomLevel extends WMTZoomLevel implements Comparable<WWZoomLevel>{
                private ReferencedEnvelope boundsOfFirstTile;                
                private double scale;
                
                private ImageAccessor imageAccessor;
                
            public WWZoomLevel(int zoomLevel, ReferencedEnvelope boundsOfFirstTile, 
                    double scale, ImageAccessor imageAccessor) {
                super(zoomLevel);
                
                this.boundsOfFirstTile = boundsOfFirstTile;
                this.imageAccessor = imageAccessor;
                this.scale = scale;
      
            }
    
            public String getTileUrl(WWTileName tileName) {
                return imageAccessor.getTileUrl(tileName);
            }

            @Override
            public int calculateMaxTilePerColNumber(int zoomLevel) {
                if (boundsOfFirstTile == null) return 0;
                
                return (int) Math.ceil(WWSource.WORLD_BOUNDS.getHeight() / boundsOfFirstTile.getHeight());
            }

            @Override
            public int calculateMaxTilePerRowNumber(int zoomLevel) {
                if (boundsOfFirstTile == null) return 0;
                
                return (int) Math.ceil(WWSource.WORLD_BOUNDS.getWidth() / boundsOfFirstTile.getWidth());
            } 

            public double getScale() {
                return scale;
            }
            
            public double getWidthInWorldUnits() {
                return boundsOfFirstTile.getWidth();
            }
            
            public double getHeightInWorldUnits() {
                return boundsOfFirstTile.getHeight();
            }
            
            public int compareTo(WWZoomLevel other) {
                return Double.compare(scale, other.scale);
            }
        }
        
    }
}
