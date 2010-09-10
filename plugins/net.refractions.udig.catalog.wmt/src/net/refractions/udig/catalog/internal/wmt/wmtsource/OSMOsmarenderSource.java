package net.refractions.udig.catalog.internal.wmt.wmtsource;

import java.util.Arrays;


public class OSMOsmarenderSource extends OSMSource {
    public static String NAME = "Osmarender"; //$NON-NLS-1$
    
    protected OSMOsmarenderSource() {
        setName(NAME); 
    }

    @Override
    public String getBaseUrl() {
        return "http://tah.openstreetmap.org/Tiles/tile/"; //$NON-NLS-1$
    }

    //region Zoom-level
    // zoom-level range for Osmarender
    private static double[] scaleList = Arrays.copyOfRange(OSMSource.scaleList, 0, 18);
    
    /**
     * As Osmarender has only tiles from zoom-level 0-17, 
     * we have to return a stripped-down mapping list.
     * 
     * see: http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Tiles
     *
     * @return mapping between OSM zoom-levels and map scale
     */
    @Override
    public double[] getScaleList() {
        return OSMOsmarenderSource.scaleList;
    }
    //endregion
    

}
