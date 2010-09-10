package net.refractions.udig.catalog.internal.wmt.tile;

import java.net.URL;

import net.refractions.udig.catalog.internal.wmt.Trace;
import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.tile.MQTile.MQTileName.MQZoomLevel;
import net.refractions.udig.catalog.internal.wmt.wmtsource.MQSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.core.internal.CorePlugin;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.mapquest.apiwrapper.MQAPIWrapper;

public class MQTile extends WMTTile {
    private MQTileName tileName;
    private MQSource mqSource;
    
    public MQTile(int x, int y, MQZoomLevel zoomLevel, MQSource mqSource) {
        this(new MQTileName(x, y, zoomLevel, mqSource), mqSource);
    }
    
    public MQTile(MQTileName tileName, MQSource mqSource){
        super(MQTile.getExtentFromTileName(tileName), tileName);
        
        this.tileName = tileName;
        this.mqSource = mqSource;
    }
    //region Get extent from tile-name        
    /**
     * Returns the bounding box of a tile by the given tile name.
     * 
     * The lower left corner of tile 0/0 is at -90,-180.
     * 
     * see:
     * http://developer.mapquest.com/content/documentation/ApiDocumentation/53/JavaScript/JS_DeveloperGuide_v5.3.0.1.htm#styler-id1.17
     * 
     * @param tileName
     * @return BoundingBox for a tile
     */
    public static ReferencedEnvelope getExtentFromTileName(MQTileName tileName) {
        int scale = (int) MQSource.scaleList[tileName.zoomLevel.getZoomLevel()];
        
        ReferencedEnvelope extent = new ReferencedEnvelope(
                tile2lon(tileName.getX(), scale), 
                tile2lon(tileName.getX() + 1, scale), 
                tile2lat(tileName.getY() + 1, scale), 
                tile2lat(tileName.getY(), scale), 
                DefaultGeographicCRS.WGS84);
        
        return extent;
    }
    
    public static double tile2lat(double row, int scale) {
            return pixel2lat(MQSource.TILESIZE_HEIGHT * row, scale);            
    }
    
    public static double tile2lon(double col, int scale) {        
            return pixel2lon(MQSource.TILESIZE_WIDTH * col, scale);
    }
    
    public static double pixel2lat(double y, int scale) {
        return (y / (MQSource.PIXELSPERLATDEGREE / scale)) - 90;
    }
    
    public static double pixel2lon(double x, int scale) {
        return (x / (MQSource.PIXELSPERLNGDEGREE / scale)) - 180;
    }
    //endregion
    
    @Override
    public MQTile getLowerNeighbour() {
        return new MQTile(tileName.getLowerNeighbour(), mqSource);
    }

    @Override
    public MQTile getRightNeighbour() {
        return new MQTile(tileName.getRightNeighbour(), mqSource);
    }


    public static class MQTileFactory extends WMTTileFactory {

        //region Get tile from coordinate
        /**
         * Finds out the tile which contains the coordinate at a given zoom level.
         * 
         * see:
         * http://developer.mapquest.com/content/documentation/ApiDocumentation/53/JavaScript/JS_DeveloperGuide_v5.3.0.1.htm#styler-id1.17
         * 
         * @param lat y
         * @param lon x
         * @param zoomLevel
         * @param wmtSource
         * @return
         */
        public MQTile getTileFromCoordinate(double lat, double lon, 
                WMTZoomLevel zoomLevel, WMTSource wmtSource) {
            // normalize latitude and longitude
            lat = WMTTileFactory.normalizeDegreeValue(lat, 90);
            lon = WMTTileFactory.normalizeDegreeValue(lon, 180);
            
            double y = (lat + 90.0) * (MQSource.PIXELSPERLATDEGREE / 
                    MQSource.scaleList[zoomLevel.getZoomLevel()]);
            int row = (int) (y / MQSource.TILESIZE_HEIGHT);
            
            double x = (lon + 180.0) * (MQSource.PIXELSPERLNGDEGREE / 
                    MQSource.scaleList[zoomLevel.getZoomLevel()]);
            int col = (int) (x / MQSource.TILESIZE_WIDTH);
            
            WMTPlugin.debug("[MQTile.getTileFromCoordinate] " + zoomLevel.getZoomLevel() + //$NON-NLS-1$
                    "/" + col +  "/" + row + " lon: " + lon + " lat: " + lat, Trace.MQ);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            
            
            return new MQTile(col, row, (MQZoomLevel) zoomLevel, (MQSource) wmtSource);
        }
        

        //endregion

        public WMTZoomLevel getZoomLevel(int zoomLevel, WMTSource wmtSource) {
            return new MQTileName.MQZoomLevel(zoomLevel);
        }
        
    }
    
    /**
     * A small helper class which stores the tile-name.
     * 
     * @author to.srwn
     * @since 1.1.0
     */
    public static class MQTileName extends WMTTileName{
        private MQZoomLevel zoomLevel;
        private MQSource mqSource;
                
        public MQTileName(int x, int y, MQZoomLevel zoomLevel, MQSource source) {
            super(zoomLevel, x, y, source);
            this.zoomLevel = zoomLevel;
            this.mqSource = source;
        }
        
        /**
         * Asks the MapQuest API to generate the map-image url.
         * 
         * @return
         */
        public URL getTileUrl() {
            try {
                int scale = (int) MQSource.scaleList[zoomLevel.getZoomLevel()];
                
                // the map is generated by the center coordinate
                double lon = getCenterLon(scale);
                double lat = getCenterLat(scale);

                MQAPIWrapper apiWrapper = mqSource.getApiWrapper();
                
                String mapImageUrl = apiWrapper.getUrl(scale, lon, lat, 
                        MQSource.TILESIZE_WIDTH, MQSource.TILESIZE_HEIGHT);
                
                return new URL(null, mapImageUrl, CorePlugin.RELAXED_HANDLER);  
                
            } catch (Exception e) {
                WMTPlugin.log("[MQTile] Could not create the url for tile (Zoom: " + zoomLevel.getZoomLevel() + //$NON-NLS-1$
                        ", X: " + getX() + ", " + getY(), e); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
            return null;
        }
       
        public double getCenterLon(int scale) {
            return MQTile.tile2lon(getX() + 0.5, scale);
        }
       
        public double getCenterLat(int scale) {
            return MQTile.tile2lat(getY() + 0.5, scale);
        }
        
        public MQTileName getRightNeighbour() {
            return new MQTileName( 
                        WMTTileName.arithmeticMod((getX()+1), zoomLevel.getMaxTilePerRowNumber()),
                        getY(),
                        zoomLevel,
                        mqSource);
        }
        
        public MQTileName getLowerNeighbour() {
            return new MQTileName( 
                        getX(),
                        WMTTileName.arithmeticMod((getY()-1), zoomLevel.getMaxTilePerColNumber()),
                        zoomLevel,
                        mqSource);
        }
        
        public String toString() {
            return zoomLevel.getZoomLevel() + "/" + getX() + "/" + getY(); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof MQTileName)) return false;
            
            MQTileName other = (MQTileName) obj;
            
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
        public static class MQZoomLevel extends WMTZoomLevel{
           
            public MQZoomLevel(int zoomLevel) {
                super(zoomLevel);
            }
            
            /**
             * Maximum tile-number for each each zoom-level
             * 
             * (similar to the MapQuest AJAX API)
             */
            public static final int[] maxTileNumbers = new int[] {
                2,
                6,
                18,
                50,
                117,
                251,
                542,
                1136,
                2347,
                4889,
                9779,
                19558,
                37450,
                70409,
                117349,
                176024
            };
            
            /**
             * The maximum tile-number:
             * 
             * For example at zoom-level 0, the tilenames are in the following range:
             * 0..1
             */
            @Override
            public int calculateMaxTilePerColNumber(int zoomLevel) {
                return MQZoomLevel.maxTileNumbers[zoomLevel];  
            }

            @Override
            public int calculateMaxTilePerRowNumber(int zoomLevel) {
                return calculateMaxTilePerColNumber(zoomLevel);
            }   
        }
        
    }
}
