package net.refractions.udig.catalog.internal.wmt.wmtsource;

import net.refractions.udig.catalog.internal.wmt.tile.OSMTile;
import net.refractions.udig.catalog.internal.wmt.tile.WMTTile.WMTTileFactory;

public abstract class OSMSource extends WMTSource {
    public static String NAME = "OpenStreetMap"; //$NON-NLS-1$
    private static WMTTileFactory tileFactory = new OSMTile.OSMTileFactory();
    
    protected OSMSource() {
        setName(NAME); 
    }
    
    /**
     * Returns the prefix of an tile-url, e.g.:
     * http://tile.openstreetmap.org/
     *
     * @return
     */
    protected abstract String getBaseUrl();
    
    /**
     * Constructs the url to fetch the tile from the given params.
     *
     * @param zoomLevel
     * @param x
     * @param y
     * @return
     */
    public String getTileUrl(int zoomLevel, int x, int y) {
        StringBuffer url = new StringBuffer(getBaseUrl());
        
        url.append(zoomLevel + "/"); //$NON-NLS-1$
        url.append(x + "/"); //$NON-NLS-1$
        url.append(y + ".png"); //$NON-NLS-1$
        
        return url.toString();
    }
    
    //region Zoom-level
    /**
     * A list that represents a mapping between OSM zoom-levels and map scale.
     * <pre>
     * see: 
     * http://blogs.esri.com/Support/blogs/mappingcenter/archive/2009/03/19/How-can-you-tell-what-map-scales-are-shown-for-online-maps_3F00_.aspx
     * </pre>
     */
    public static double[] scaleList = {
        Double.NaN,
        Double.NaN,    
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
        2256
    };

    
    /**
     * Returns the mapping list
     *
     * @return mapping between OSM zoom-levels and map scale
     */
    @Override
    public double[] getScaleList() { 
        return OSMSource.scaleList;
    }
    //endregion

    //region Tiles-Cutting
    @Override
    public WMTTileFactory getTileFactory() {
        return tileFactory;
    }

    //endregion

}
