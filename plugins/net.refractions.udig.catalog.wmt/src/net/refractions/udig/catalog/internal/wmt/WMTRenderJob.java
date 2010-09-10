package net.refractions.udig.catalog.internal.wmt;

import net.refractions.udig.catalog.internal.wmt.ui.preferences.WMTPreferenceConstants;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * This class wraps together the information needed to 
 * process a render call:
 *  - the map extent
 *  - CRS's
 *  - transformations between the CRS's
 *  - the scale
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WMTRenderJob {

    /** the CRS the tiles were projected in (TilesProjectedCrs) */
    private CoordinateReferenceSystem crsTilesProjected; 
           
    /** Transformation: TileCrs (mostly WGS_84) -> TilesProjectedCrs (mostly Google's Mercator) */
    private MathTransform transformTileCrsToTilesProjected;                
    /** Transformation: TilesProjectedCrs (mostly Google's Mercator) -> MapCrs */
    private MathTransform transformTilesProjectedToMap; 
    
    private WMTScaleZoomLevelMatcher zoomLevelMatcher;
    
    
    public WMTRenderJob(
            CoordinateReferenceSystem crsTilesProjected, 
            MathTransform transformTileCrsToTilesProjected,
            MathTransform transformTilesProjectedToMap, 
            WMTScaleZoomLevelMatcher zoomLevelMatcher) {
        this.zoomLevelMatcher = zoomLevelMatcher;
        
        this.crsTilesProjected = crsTilesProjected;
        this.transformTileCrsToTilesProjected = transformTileCrsToTilesProjected;
        this.transformTilesProjectedToMap = transformTilesProjectedToMap;
    }
       
    public static WMTRenderJob createRenderJob(ReferencedEnvelope mapExtentMapCrs, 
            double scale, WMTSource wmtSource) throws Exception {
        WMTScaleZoomLevelMatcher zoomLevelMatcher = 
            WMTScaleZoomLevelMatcher.createMatcher(mapExtentMapCrs, scale, wmtSource);
  
        CoordinateReferenceSystem crsTilesProjected = wmtSource.getProjectedTileCrs(); // the CRS the tiles were projected in
        
        // Transformation: TileCrs (mostly WGS_84) -> TilesProjectedCrs (mostly Google's Mercator)
        MathTransform transformTileCrsToTilesProjected = 
            getTransformation(zoomLevelMatcher.getCrsTiles(), crsTilesProjected);
                    
        // Transformation: TilesProjectedCrs (mostly Google's Mercator) -> MapCrs
        MathTransform transformTilesProjectedToMap = 
            getTransformation(crsTilesProjected, zoomLevelMatcher.getCrsMap());           
        
        return new WMTRenderJob(
                crsTilesProjected, 
                transformTileCrsToTilesProjected,
                transformTilesProjectedToMap,
                zoomLevelMatcher);
    }
    
    public static int getScaleFactor() {
        return WMTPlugin.getDefault().
            getPreferenceStore().getInt(WMTPreferenceConstants.P_WMT_SCALEFACTOR);
    }
    
    public static int getTileLimitWarning() {
        return WMTPlugin.getDefault().
            getPreferenceStore().getInt(WMTPreferenceConstants.P_WMT_TILELIMIT_WARNING);
    }
    
    public static int getTileLimitError() {
        return WMTPlugin.getDefault().
            getPreferenceStore().getInt(WMTPreferenceConstants.P_WMT_TILELIMIT_ERROR);
    }
    
    public WMTScaleZoomLevelMatcher getZoomLevelMatcher() {
        return zoomLevelMatcher;
    }

    public ReferencedEnvelope getMapExtentTileCrs() {
        return zoomLevelMatcher.getMapExtentTileCrs();
    }    
            
    public CoordinateReferenceSystem getCrsTilesProjected() {
        return crsTilesProjected;
    }   
            
    public CoordinateReferenceSystem getCrsMap() {
        return zoomLevelMatcher.getCrsMap();
    }

    public ReferencedEnvelope projectTileToMapCrs(ReferencedEnvelope boundsInTileCrs) throws Exception {
        return zoomLevelMatcher.projectTileToMapCrs(boundsInTileCrs);
    } 
    
    public ReferencedEnvelope projectMapToTileCrs(ReferencedEnvelope boundsInMapCrs) throws Exception {
        return zoomLevelMatcher.projectMapToTileCrs(boundsInMapCrs);
    }
    
    public ReferencedEnvelope projectTileToTileProjectedCrs(ReferencedEnvelope boundsInTileCrs) throws Exception {
        return getProjectedEnvelope(boundsInTileCrs, crsTilesProjected, transformTileCrsToTilesProjected);
    }
    
    public ReferencedEnvelope projectTileProjectedToMapCrs(ReferencedEnvelope boundsInTilesProjectedCrs) throws Exception {
        return getProjectedEnvelope(boundsInTilesProjectedCrs, getCrsMap(), transformTilesProjectedToMap);
    }
    
    /**
     * Re-Projects the given envelope to destinationCRS
     * using transformation.
     *
     * @param envelope
     * @param destinationCRS
     * @param transformation
     * @return
     * @throws Exception
     */
    public static ReferencedEnvelope getProjectedEnvelope(
            ReferencedEnvelope envelope, 
            CoordinateReferenceSystem destinationCRS, 
            MathTransform transformation) throws Exception {
        CoordinateReferenceSystem sourceCRS = envelope.getCoordinateReferenceSystem();
        
        if(sourceCRS.equals(destinationCRS)) {
            // no need to reproject
            
            return envelope;
        } else {         
            // Reproject envelope: first try JTS.transform, if that fails use ReferencedEnvelope.transform
            try {         
                
                return new ReferencedEnvelope(JTS.transform(envelope, transformation), destinationCRS);                
            } catch(Exception exc) {   
                
                    return envelope.transform(destinationCRS, false);
            }
        }
    }
    
    /**
     * Returns the transformation to convert between these two CRS's.
     *
     * @param fromCRS
     * @param toCRS
     * @return
     * @throws Exception
     */
    public static MathTransform getTransformation(CoordinateReferenceSystem fromCRS, 
            CoordinateReferenceSystem toCRS) throws Exception {
        if(!fromCRS.equals(toCRS)) {
            
            return CRS.findMathTransform(fromCRS, toCRS);            
        }
        
        return null;
    }
}

