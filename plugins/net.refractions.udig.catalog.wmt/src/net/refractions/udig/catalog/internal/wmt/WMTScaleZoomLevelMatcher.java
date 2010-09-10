package net.refractions.udig.catalog.internal.wmt;

import java.awt.Dimension;
import java.util.Arrays;

import net.refractions.udig.catalog.internal.wmt.tile.WMTTile;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTile.WMTTileFactory;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTile.WMTZoomLevel;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.project.internal.render.impl.ScaleUtils;

import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * This class is responsible for finding the right zoom-level
 * for a given map extent.
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WMTScaleZoomLevelMatcher {
    /** the CRS of the map (MapCrs) */
    private CoordinateReferenceSystem crsMap;
    /** the CRS used for the tile cutting (TileCrs) */
    private CoordinateReferenceSystem crsTiles;  
    
    /** Transformation: MapCrs -> TileCrs (mostly WGS_84) */
    private MathTransform transformMapToTileCrs;        
    /** Transformation: TileCrs (mostly WGS_84) -> MapCrs (needed for the blank tiles) **/
    private MathTransform transformTileCrsToMap;    
        
    /** the extent that should be drawn in TileCrs */
    private ReferencedEnvelope mapExtentTileCrs;    
    /** the extent that should be drawn in MapCrs */
    private ReferencedEnvelope mapExtentMapCrs;
    
    /** the current map-scale */
    private double scale;
    
    private static int DPI;
    static {
        try{
            DPI = Display.getDefault().getDPI().x;
        }catch(Exception exc){
            DPI = 96;
        }
    }
           
    public WMTScaleZoomLevelMatcher(
            CoordinateReferenceSystem crsMap, 
            CoordinateReferenceSystem crsTiles,
            MathTransform transformMapToTileCrs,
            MathTransform transformTileCrsToMap, 
            ReferencedEnvelope mapExtentTileCrs, 
            ReferencedEnvelope mapExtentMapCrs, 
            double scale) {
        
        this.crsMap = crsMap;
        this.crsTiles = crsTiles;
        this.transformMapToTileCrs = transformMapToTileCrs;
        this.transformTileCrsToMap = transformTileCrsToMap;
        this.mapExtentTileCrs = mapExtentTileCrs;
        this.mapExtentMapCrs = mapExtentMapCrs;
        this.scale = scale;
    }
    
    public static WMTScaleZoomLevelMatcher createMatcher(ReferencedEnvelope mapExtentMapCrs, 
            double scale, WMTSource wmtSource) throws Exception {
        CoordinateReferenceSystem crsMap = mapExtentMapCrs.getCoordinateReferenceSystem();
        CoordinateReferenceSystem crsTiles = wmtSource.getTileCrs(); // the CRS used for the tile cutting 

        // Transformation: MapCrs -> TileCrs (mostly WGS_84) 
        MathTransform transformMapToTileCrs = WMTRenderJob.getTransformation(crsMap, crsTiles);
        
        // Transformation: TileCrs (mostly WGS_84) -> MapCrs (needed for the blank tiles)
        MathTransform transformTileCrsToMap = WMTRenderJob.getTransformation(crsTiles, crsMap);
        
        // Get the mapExtent in the tiles CRS
        ReferencedEnvelope mapExtentTileCrs = WMTRenderJob.getProjectedEnvelope(mapExtentMapCrs, 
                crsTiles, transformMapToTileCrs);
        
        return new WMTScaleZoomLevelMatcher(
            crsMap, 
            crsTiles,
            transformMapToTileCrs,
            transformTileCrsToMap, 
            mapExtentTileCrs, 
            mapExtentMapCrs, 
            scale);
    }
    
    
    
    public CoordinateReferenceSystem getCrsMap() {
        return crsMap;
    }

    public CoordinateReferenceSystem getCrsTiles() {
        return crsTiles;
    }

    public ReferencedEnvelope getMapExtentTileCrs() {
        return mapExtentTileCrs;
    }

    public double getScale() {
        return scale;
    }
    
    /**
     * Finds out the best fitting zoom-level for a given map-scale.
     *
     * @param wmtSource
     * @param tempScaleList
     * @return
     */
    public int getZoomLevelFromScale(WMTSource wmtSource, double[] tempScaleList) {
        double[] scaleList = wmtSource.getScaleList();
        
        // Start with the most detailed zoom-level and search the best-fitting one
        int zoomLevel = scaleList.length - 1;
        getOptimumScaleFromZoomLevel(zoomLevel, wmtSource, tempScaleList);
        
        for (int i = scaleList.length-2; i >= 0; i--) {
            if (Double.isNaN(scaleList[i])) {
                break;
            }
            else if (getScale() < getOptimumScaleFromZoomLevel(i, wmtSource, tempScaleList)) {
                break;
            }
              
            zoomLevel = i;
            if (getScale() > getOptimumScaleFromZoomLevel(i + 1, wmtSource, tempScaleList)) {
                zoomLevel = i;
            }
        }
        
        return zoomLevel;
    }
    
    /**
     * Calculates the "best" scale for a given zoom-level by calculating 
     * the scale for a tile in the center of the map-extent and
     * by taking the mapCrs in account.  
     * 
     * "Best" scale is the scale where a 256x256 tile has also this size
     * when displayed in uDig.
     *
     * @param zoomLevel
     * @param wmtSource
     * @param tempScaleList
     * @return
     */
    public double getOptimumScaleFromZoomLevel(int zoomLevel, WMTSource wmtSource, double[] tempScaleList) {
        // check if we have calculated this already
        if (!Double.isNaN(tempScaleList[zoomLevel])) {
            return tempScaleList[zoomLevel];
        }
        
        try {
            ReferencedEnvelope centerTileBounds = getBoundsOfCenterTileInMapCrs(zoomLevel, wmtSource);
            
            double scale = ScaleUtils.calculateScaleDenominator(
                    centerTileBounds, 
                    new Dimension(wmtSource.getTileWidth(), wmtSource.getTileHeight()),
                    DPI);
            // cache the scale
            tempScaleList[zoomLevel] = scale;
            
            return scale;
        } catch (Exception exc) {
            WMTPlugin.trace("[WMTRenderJob.getOptimumScaleFromZoomLevel] Failed for: " + zoomLevel, exc); //$NON-NLS-1$
        }
        
        // in case of error, return fallback zoom-level
        return wmtSource.getScaleList()[zoomLevel];
    }
    
    public double getOptimumScaleFromZoomLevel(int zoomLevel, WMTSource wmtSource) {
        double[] tempScaleList = new double[wmtSource.getScaleList().length];
        Arrays.fill(tempScaleList, Double.NaN);
        
        return getOptimumScaleFromZoomLevel(zoomLevel, wmtSource, tempScaleList);
    }
    
    /**
     * Returns the bounds of the tile which covers the center of
     * the map extent in the CRS of the map.
     *
     * @param zoomLevel
     * @param wmtSource
     * @return
     * @throws Exception
     */
    private ReferencedEnvelope getBoundsOfCenterTileInMapCrs(int zoomLevel, WMTSource wmtSource) throws Exception {
        WMTTile centerTile = getCenterTile(zoomLevel, wmtSource);
        ReferencedEnvelope boundsInTileCrs = centerTile.getExtent();        
        ReferencedEnvelope boundsInMapCrs = projectTileToMapCrs(boundsInTileCrs);
        
        return boundsInMapCrs;
    }
    
    /**
     * Returns the tile which covers the center of
     * the map extent.
     *
     * @param zoomLevel
     * @param wmtSource
     * @return
     */
    private WMTTile getCenterTile(int zoomLevel, WMTSource wmtSource) {
        WMTTileFactory tileFactory = wmtSource.getTileFactory();
        WMTZoomLevel zoomLevelInstance = tileFactory.getZoomLevel(zoomLevel, wmtSource);
        
        // get the coordinates of the map centre (in TileCrs)
        Coordinate centerPoint = mapExtentTileCrs.centre();
        
        return tileFactory.getTileFromCoordinate(
                centerPoint.y, 
                centerPoint.x, 
                zoomLevelInstance, 
                wmtSource);
    }
    
    public ReferencedEnvelope projectTileToMapCrs(ReferencedEnvelope boundsInTileCrs) throws Exception {
        //assert(boundsInTileCrs.getCoordinateReferenceSystem().equals(crsTiles));
        return WMTRenderJob.getProjectedEnvelope(boundsInTileCrs, crsMap, transformTileCrsToMap);
    }

    public ReferencedEnvelope projectMapToTileCrs(ReferencedEnvelope boundsInMapCrs) throws Exception {
        return WMTRenderJob.getProjectedEnvelope(boundsInMapCrs, crsTiles, transformMapToTileCrs);
    }
}
