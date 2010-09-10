package net.refractions.udig.catalog.internal.wmt.wmtsource;

import java.util.Arrays;

import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.wmtsource.ww.LayerSet;

/**
 * This class allows you to use your "Custom Server", which 
 * follows the OSM tiling-scheme
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class CSSource extends OSMSource{
    public static String NAME = "Custom Server"; //$NON-NLS-1$
    
    private String templateUrl;
    private double[] scales;
    private String id;
    
    public static final String TAG_ZOOM = "{z}"; //$NON-NLS-1$
    public static final String TAG_X = "{x}"; //$NON-NLS-1$
    public static final String TAG_Y = "{y}"; //$NON-NLS-1$
    
    protected CSSource() {
        setName(NAME);
    }

    @Override
    protected void init(String resourceId) throws Exception {
        // parse zoomMin, zoomMax and the template-url from the resource-id
        String zoomMinValue, zoomMaxValue;
        
        int lastPos = resourceId.lastIndexOf('/');
        zoomMaxValue = resourceId.substring(lastPos + 1);
        
        int nextToLastPos = resourceId.substring(0, lastPos).lastIndexOf('/');
        zoomMinValue = resourceId.substring(nextToLastPos + 1, lastPos);
        
        this.templateUrl = "http://" + resourceId.substring(0, nextToLastPos); //$NON-NLS-1$
        this.id = generateId();
        
        int zoomMin, zoomMax;        
        try{
            zoomMin = Integer.parseInt(zoomMinValue);
            zoomMax = Integer.parseInt(zoomMaxValue);
        } catch(Exception exc) {
            WMTPlugin.log("[CSSource.init] Couldn't parse the resourceId: " + resourceId, exc); //$NON-NLS-1$
            
            // set default 
            zoomMin = 0; 
            zoomMax = 20;
        }
                        
        // set scales
        if (validZoomValues(zoomMin, zoomMax)) {
            scales = Arrays.copyOfRange(SCALE_LIST, 0, zoomMax + 1);
            
            for (int i = 0; i < zoomMin; i++) {
                scales[i] = Double.NaN;
            }
        } else {
            scales = SCALE_LIST;
        }
    }
    
    //region ID
    @Override
    public String getId() {
        return id;
    }
      
    private String generateId() {
        return LayerSet.constructId("", templateUrl); //$NON-NLS-1$
    }
    //endregion


    //region Scales
    private boolean validZoomValues(int zoomMin, int zoomMax) {
        return (zoomMin < zoomMax) && 
                (zoomMin >= 0) && (zoomMax < SCALE_LIST.length);
    }
    
    @Override
    public double[] getScaleList() {
        return scales;
    }
    
    /**
     * List of scale-factors for each zoom-level,
     * see: http://msdn.microsoft.com/en-us/library/bb259689.aspx     * 
     */
    public static double[] SCALE_LIST = {
        591658711,
        295829355,    
        147914381,
        73957190,
        36978595,
        18489297,
        9244648,
        4622324,
        2311162,
        1155581,
        577790,
        288895,
        144447,
        72223,
        36111,
        18055,
        9027,
        4513,
        2256,
        1128,
        564,
        282,
        141
    };
    //endregion
    
    /**
     * Creates the tile-url by replacing the tags inside the template-url.
     */
    @Override
    public String getTileUrl(int zoomLevel, int x, int y) {
        String url = templateUrl.replace(TAG_ZOOM, Integer.toString(zoomLevel));
        url = url.replace(TAG_X, Integer.toString(x));
        url = url.replace(TAG_Y, Integer.toString(y));
        
        return url;
    }

    @Override
    public String getBaseUrl() {
        return null; // not in use
    }

}
