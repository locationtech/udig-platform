package net.refractions.udig.catalog.internal.wmt.wmtsource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.internal.wmt.Trace;
import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.WMTRenderJob;
import net.refractions.udig.catalog.internal.wmt.WMTScaleZoomLevelMatcher;
import net.refractions.udig.catalog.internal.wmt.WMTService;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTile;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTile.WMTTileFactory;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTile.WMTZoomLevel;
import net.refractions.udig.catalog.internal.wmt.ui.properties.WMTLayerProperties;
import net.refractions.udig.catalog.wmsc.server.Tile;
import net.refractions.udig.core.internal.CorePlugin;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.util.ObjectCache;
import org.geotools.util.ObjectCaches;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 *
 * @author to.srwn
 * @since 1.1.0
 */
public abstract class WMTSource {   
    
    private String name;
    
    /** 
     * This WeakHashMap acts as a memory cache.
     * Because we are using SoftReference, we won't run
     * out of Memory, the GC will free space.
     **/
    private ObjectCache tiles = ObjectCaches.create("soft", 50); //$NON-NLS-1$
    
    private WMTService wmtService;
    
    protected WMTSource() {}
    
    protected void init(String resourceId) throws Exception {}
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String getId() {
        return getName();
    }
    
    public int getTileWidth() {
        return 256;
    }
    
    public int getTileHeight() {
        return 256;
    }
    
    public String getFileFormat() {
        return "png"; //$NON-NLS-1$
    }
    
    public ReferencedEnvelope getBounds() {
        return new ReferencedEnvelope(-180, 180, -85.051, 85.0511, DefaultGeographicCRS.WGS84);        
    }
    
    //region CRS
    public static final CoordinateReferenceSystem CRS_EPSG_900913;    
    static {
        CoordinateReferenceSystem crs = null;
        
        try {
            crs = CRS.decode("EPSG:900913"); //$NON-NLS-1$
        } catch (Exception exc1) {
            WMTPlugin.trace("[WMTSource] EPSG:900913 is not in the database, now it is created manually", null); //$NON-NLS-1$
            
            String wkt =
                "PROJCS[\"Google Mercator\"," + //$NON-NLS-1$
               "GEOGCS[\"WGS 84\"," + //$NON-NLS-1$
                "    DATUM[\"World Geodetic System 1984\"," + //$NON-NLS-1$
                "        SPHEROID[\"WGS 84\",6378137.0,298.257223563," + //$NON-NLS-1$
                "            AUTHORITY[\"EPSG\",\"7030\"]]," + //$NON-NLS-1$
                "        AUTHORITY[\"EPSG\",\"6326\"]]," + //$NON-NLS-1$
                "    PRIMEM[\"Greenwich\",0.0," + //$NON-NLS-1$
                "        AUTHORITY[\"EPSG\",\"8901\"]]," + //$NON-NLS-1$
                "    UNIT[\"degree\",0.017453292519943295]," + //$NON-NLS-1$
                "    AXIS[\"Geodetic latitude\",NORTH]," + //$NON-NLS-1$
                "    AXIS[\"Geodetic longitude\",EAST]," + //$NON-NLS-1$
                "    AUTHORITY[\"EPSG\",\"4326\"]]," + //$NON-NLS-1$
                "PROJECTION[\"Mercator_1SP\"]," + //$NON-NLS-1$
                "PARAMETER[\"semi_minor\",6378137.0]," + //$NON-NLS-1$
                "PARAMETER[\"latitude_of_origin\",0.0]," + //$NON-NLS-1$
                "PARAMETER[\"central_meridian\",0.0]," + //$NON-NLS-1$
                "PARAMETER[\"scale_factor\",1.0],"+ //$NON-NLS-1$
                "PARAMETER[\"false_easting\",0.0]," + //$NON-NLS-1$
                "PARAMETER[\"false_northing\",0.0]," + //$NON-NLS-1$
                "UNIT[\"m\",1.0]," + //$NON-NLS-1$
                "AXIS[\"Easting\",EAST]," + //$NON-NLS-1$
                "AXIS[\"Northing\",NORTH]," + //$NON-NLS-1$
                "AUTHORITY[\"EPSG\",\"900913\"]]"; //$NON-NLS-1$
            
            try {
                crs = CRS.parseWKT(wkt);
            } catch (Exception exc2) {
                WMTPlugin.log("[WMTSource] Could not build EPSG:900913!", exc2); //$NON-NLS-1$
                crs = DefaultGeographicCRS.WGS84;
            }
        }
        
        CRS_EPSG_900913 = crs;
    }
    
    /**
     * The projection the tiles are drawn in.
     *
     * @return
     */
    public CoordinateReferenceSystem getProjectedTileCrs() {
        return WMTSource.CRS_EPSG_900913;
    }
    
    /**
     * The CRS that is used when the extent is cut in tiles.
     *
     * @return
     */
    public CoordinateReferenceSystem getTileCrs() {
        return DefaultGeographicCRS.WGS84;
    }
    //endregion

    //region Methods to access the tile-list (cache)
    public boolean listContainsTile(String tileId) {
        return !(tiles.peek(tileId) == null || tiles.get(tileId) == null);
    }
    
    public WMTTile addTileToList(WMTTile tile) {
        if (listContainsTile(tile.getId())){
            WMTPlugin.debug("[WMTSource.addTileToList] Already in cache: " + tile.getId(), Trace.REQUEST); //$NON-NLS-1$
            return getTileFromList(tile.getId());
        } else {
            WMTPlugin.debug("[WMTSource.addTileToList] Was not in cache: " + tile.getId(), Trace.REQUEST); //$NON-NLS-1$
            tiles.put(tile.getId(), tile);
            return tile;            
        }
    }
    
    public WMTTile getTileFromList(String tileId) {
        return (WMTTile) tiles.get(tileId);
    }
    //endregion
    
    //region Methods related to the service    
    public WMTService getWmtService() {
        return wmtService;
    }

    public void setWmtService(WMTService wmtService) {
        this.wmtService = wmtService;
    }
    
    /**
     * Returns the catalog url for a given class.
     * <pre>
     * For example:
     * getRelatedServiceUrl(OSMMapnikSource.class) returns:
     * wmt://localhost/wmt/net.refractions.udig.catalog.internal.wmt.wmtsource.OSMMapnikSource
     * </pre>
     *
     * @param sourceClass
     * @return catalog url
     */
    public static URL getRelatedServiceUrl(Class<? extends WMTSource> sourceClass) {
        URL url;
        
        try {
            url = new URL(null, WMTService.ID + sourceClass.getName(), CorePlugin.RELAXED_HANDLER);
        }
        catch(MalformedURLException exc) {
            WMTPlugin.log("[WMTSource.getRelatedServiceUrl] Could not create url: " + sourceClass.getName(), exc); //$NON-NLS-1$
            url = null;
        }        
        
        return url; 
    }
    
    /**
     * Generates the URL for CloudMadeSource-Services
     *
     * @param styleId
     * @return
     */
    public static URL getCloudMadeServiceUrl(String styleId) {
        URL url = getRelatedServiceUrl(OSMCloudMadeSource.class);
        
        try {
            url = new URL(null, url.toExternalForm() + "/" + styleId, CorePlugin.RELAXED_HANDLER); //$NON-NLS-1$
        }
        catch(MalformedURLException exc) {
            WMTPlugin.log("[WMTSource.getCloudMadeServiceUrl] Could not create url: " + styleId, exc); //$NON-NLS-1$
            url = null;
        }        
        
        return url; 
    }
    
    /**
     * Generates the URL for CustomServer-Services
     *
     * @param serverUrl
     * @param zoomMin
     * @param zoomMax
     * @return
     */
    public static URL getCustomServerServiceUrl(String serverUrl, String zoomMin, String zoomMax) {
        URL url = getRelatedServiceUrl(CSSource.class);
        
        try {
            url = new URL(null, 
                    url.toExternalForm() + "/" + //$NON-NLS-1$
                    serverUrl + "/" + //$NON-NLS-1$
                    zoomMin + "/" + //$NON-NLS-1$
                    zoomMax,
                    CorePlugin.RELAXED_HANDLER); 
        }
        catch(MalformedURLException exc) {
            WMTPlugin.log("[WMTSource.getCustomServerServiceUrl] Could not create url: " + serverUrl + " " + zoomMin + " " + zoomMax, exc); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            url = null;
        }        
        
        return url; 
    }
    //endregion
    
    //region Zoom-level
    /**
     * Returns a list that represents a mapping between zoom-levels and map scale.
     * 
     * Array index: zoom-level
     * Value at index: map scale
     * 
     * High zoom-level -> more detailed map
     * Low zoom-level -> less detailed map
     * 
     * @return mapping between zoom-levels and map scale
     */
    public abstract double[] getScaleList();
    
    /**
     * Translates the map scale into a zoom-level for the map services.
     *
     * The scale-factor (0-100) decides whether the tiles will be
     * scaled down (100) or scaled up (0).
     *
     * @param renderJob Contains all the needed information
     * @param scaleFactor Scale-factor (0-100)
     * @return Zoom-level
     */
    public int getZoomLevelFromMapScale(WMTScaleZoomLevelMatcher zoomLevelMatcher, int scaleFactor) {
        // fallback scale-list
        double[] scaleList = getScaleList();
        // during the calculations this list caches already calculated scales
        double[] tempScaleList = new double[scaleList.length];
        Arrays.fill(tempScaleList, Double.NaN);
        
        assert(scaleList != null && scaleList.length > 0);
        
        int zoomLevel = zoomLevelMatcher.getZoomLevelFromScale(this, tempScaleList);
        
        // Now apply the scale-factor
        if (zoomLevel == 0) {
            return zoomLevel;
        } else {
            int upperScaleIndex = zoomLevel - 1;
            int lowerScaleIndex = zoomLevel;
            
            double deltaScale = tempScaleList[upperScaleIndex] - tempScaleList[lowerScaleIndex];
            double rangeScale = (scaleFactor / 100d) * deltaScale;
            double limitScale = tempScaleList[lowerScaleIndex] + rangeScale;
            
            if (zoomLevelMatcher.getScale() > limitScale) {
                return upperScaleIndex;
            } else {
                return lowerScaleIndex;
            }
        }
    }
    
    /**
     * Returns the zoom-level that should be used to fetch the tiles.
     *
     * @param scale
     * @param scaleFactor
     * @param useRecommended always use the calculated zoom-level, do not use the one the user selected
     * @return
     */
    public int getZoomLevelToUse(WMTScaleZoomLevelMatcher zoomLevelMatcher, int scaleFactor, boolean useRecommended,
            WMTLayerProperties layerProperties) {
        if (useRecommended) {
            return getZoomLevelFromMapScale(zoomLevelMatcher, scaleFactor);            
        }
        
        // try to load the property values
        boolean selectionAutomatic = true;
        int zoomLevel = -1;
        
        if (layerProperties.load()) {            
            selectionAutomatic = layerProperties.getSelectionAutomatic();
            zoomLevel = layerProperties.getZoomLevel();
        } else {
            selectionAutomatic = true;
        }
        
        // check if the zoom-level is valid
        if (!selectionAutomatic && 
                ((zoomLevel >= getMinZoomLevel()) && (zoomLevel <= getMaxZoomLevel()))) {
            // the zoom-level from the properties is valid, so let's take it
            return zoomLevel;
        } else {
            // No valid property values or automatic selection of the zoom-level
            return getZoomLevelFromMapScale(zoomLevelMatcher, scaleFactor);
        }
    }
    
    /**
     * Returns the lowest zoom-level number from the scaleList.
     *
     * @param scaleList
     * @return
     */
    public int getMinZoomLevel() {
        double[] scaleList = getScaleList();
        int minZoomLevel = 0;
        
        while (Double.isNaN(scaleList[minZoomLevel]) && (minZoomLevel < scaleList.length)) {
            minZoomLevel++;
        }
        
        return minZoomLevel;       
    }
    
    /**
     * Returns the highest zoom-level number from the scaleList.
     *
     * @param scaleList
     * @return
     */
    public int getMaxZoomLevel() {
        double[] scaleList = getScaleList();
        int maxZoomLevel = scaleList.length - 1;
        
        while (Double.isNaN(scaleList[maxZoomLevel]) && (maxZoomLevel >= 0)) {
            maxZoomLevel--;
        }
        
        return maxZoomLevel;
    }
    //endregion
    
    //region Tiles-Cutting  
    /**
     * Returns the TileFactory which is used to call the 
     * method getTileFromCoordinate().
     */
    public abstract WMTTileFactory getTileFactory();
   
    /**
     * The method which finds all tiles that are within the given extent,
     * used for all different map services.
     * 
     * @see WMTSource.cutExtentIntoTiles(ReferencedEnvelope extent, double scale)
     * @param extent The extent which should be cut.
     * @param scale The map scale.
     * @param scaleFactor The scale-factor (0-100): scale up or down?
     * @param recommendedZoomLevel Always use the calculated zoom-level, do not use the one the user selected
     * @return The list of found tiles.
     */
    public Map<String, Tile> cutExtentIntoTiles(WMTRenderJob renderJob, 
            int scaleFactor, boolean recommendedZoomLevel, WMTLayerProperties layerProperties) {        
        // only continue, if we have tiles that cover the requested extent
        if (!renderJob.getMapExtentTileCrs().intersects((Envelope) getBounds())) {
            return Collections.emptyMap();
        }
        
        ReferencedEnvelope extent = normalizeExtent(renderJob.getMapExtentTileCrs());
        
        WMTTileFactory tileFactory = getTileFactory();
                
        WMTZoomLevel zoomLevel = tileFactory.getZoomLevel(getZoomLevelToUse(renderJob.getZoomLevelMatcher(), 
                scaleFactor, recommendedZoomLevel, layerProperties), this);
        long maxNumberOfTiles = zoomLevel.getMaxTileNumber();
                
        Map<String, Tile> tileList = new HashMap<String, Tile>();
        
        WMTPlugin.debug("[WMTSource.cutExtentIntoTiles] Zoom-Level: " + zoomLevel.getZoomLevel() +  //$NON-NLS-1$
                " Extent: " + extent, Trace.REQUEST); //$NON-NLS-1$
        
        // Let's get the first tile which covers the upper-left corner
        WMTTile firstTile = tileFactory.getTileFromCoordinate(
                extent.getMaxY(), extent.getMinX(), zoomLevel, this);
        tileList.put(firstTile.getId(), addTileToList(firstTile));
        
        WMTTile firstTileOfRow = null;
        WMTTile movingTile = firstTileOfRow = firstTile;
        // Loop column
        do {
            // Loop row
            do {
                // get the next tile right of this one
                WMTTile rightNeighbour = movingTile.getRightNeighbour();
                
                // Check if the new tile is still part of the extent and
                // that we don't have the first tile again
                if (extent.intersects((Envelope) rightNeighbour.getExtent())
                        && !firstTileOfRow.equals(rightNeighbour)) {
                    tileList.put(rightNeighbour.getId(), addTileToList(rightNeighbour));
                    
                    WMTPlugin.debug("[WMTSource.cutExtentIntoTiles] Adding right neighbour: " +  //$NON-NLS-1$
                            rightNeighbour.getId(), Trace.REQUEST);
                    
                    movingTile = rightNeighbour;
                } else {
                    break;
                }
            } while(tileList.size() < maxNumberOfTiles);

            // get the next tile under the first one of the row
            WMTTile lowerNeighbour = firstTileOfRow.getLowerNeighbour();
            
            // Check if the new tile is still part of the extent
            if (extent.intersects((Envelope) lowerNeighbour.getExtent())
                    && !firstTile.equals(lowerNeighbour)) {
                tileList.put(lowerNeighbour.getId(), addTileToList(lowerNeighbour));
                
                WMTPlugin.debug("[WMTSource.cutExtentIntoTiles] Adding lower neighbour: " +  //$NON-NLS-1$
                        lowerNeighbour.getId(), Trace.REQUEST);
                
                firstTileOfRow = movingTile = lowerNeighbour;
            } else {
                break;
            }            
        } while(tileList.size() < maxNumberOfTiles);
        
        return tileList;
    }
    
    /**
     * The extent from the viewport may look like this:
     * MaxY: 110° (=-70°)   MinY: -110°
     * MaxX: 180°           MinX: -180°
     * 
     * But cutExtentIntoTiles(..) requires an extent that looks like this:
     * MaxY: 85° (or 90°)   MinY: -85° (or -90°)
     * MaxX: 180°           MinX: -180°
     * 
     * @param envelope
     * @return
     */
    private ReferencedEnvelope normalizeExtent(ReferencedEnvelope envelope) {
        ReferencedEnvelope bounds = getBounds();
        
        if (    envelope.getMaxY() > bounds.getMaxY() ||
                envelope.getMinY() < bounds.getMinY() ||
                envelope.getMaxX() > bounds.getMaxX() ||
                envelope.getMinX() < bounds.getMinX()   ) {
            
            
            double maxY = (envelope.getMaxY() > bounds.getMaxY()) ? bounds.getMaxY() : envelope.getMaxY();
            double minY = (envelope.getMinY() < bounds.getMinY()) ? bounds.getMinY() : envelope.getMinY(); 
            double maxX = (envelope.getMaxX() > bounds.getMaxX()) ? bounds.getMaxX() : envelope.getMaxX();
            double minX = (envelope.getMinX() < bounds.getMinX()) ? bounds.getMinX() : envelope.getMinX(); 
            
            ReferencedEnvelope newEnvelope = new ReferencedEnvelope(minX, maxX, minY, maxY, 
                    envelope.getCoordinateReferenceSystem());
            
            return newEnvelope;
        }
        
        return envelope;
    }
    //endregion
    
    @Override
    public String toString() {
        return getName();
    }
}
