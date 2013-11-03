/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.wmtsource;

import java.io.File;
import java.util.Arrays;

import org.locationtech.udig.catalog.internal.wmt.WMTPlugin;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.ww.LayerSet;

/**
 * This class allows you to use your "Custom Server", which 
 * follows the OSM tiling-scheme
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class CSSource extends OSMSource {
    public static String NAME = "Custom Server"; //$NON-NLS-1$

    private String templateUrl;
    private double[] scales;
    private String id;

    public static final String TAG_ZOOM = "{z}"; //$NON-NLS-1$
    public static final String TAG_X = "{x}"; //$NON-NLS-1$
    public static final String TAG_Y = "{y}"; //$NON-NLS-1$

    private boolean isTms = false;

    protected CSSource() {
        setName(NAME);
    }

    @Override
    protected void init( String resourceId ) throws Exception {
        // parse zoomMin, zoomMax and the template-url from the resource-id
        String zoomMinValue, zoomMaxValue;

        int lastPos = resourceId.lastIndexOf('/');
        String lastStr = resourceId.substring(lastPos + 1).trim();
        int splitPoint = 0;
        if (lastStr.toLowerCase().equals("tms")) {
            isTms = true;
            int nextToLastPos = resourceId.substring(0, lastPos).lastIndexOf('/');
            zoomMaxValue = resourceId.substring(nextToLastPos + 1, lastPos);
            int nextToLastPos2 = resourceId.substring(0, nextToLastPos).lastIndexOf('/');
            zoomMinValue = resourceId.substring(nextToLastPos2 + 1, nextToLastPos);
            splitPoint = nextToLastPos2;
        }else{
            zoomMaxValue = lastStr;
            int nextToLastPos = resourceId.substring(0, lastPos).lastIndexOf('/');
            zoomMinValue = resourceId.substring(nextToLastPos + 1, lastPos);
            splitPoint = nextToLastPos;
        }

        String tmpUrlPath = resourceId.substring(0, splitPoint);
        String tmpUrlNoZXY = tmpUrlPath.split("\\{")[0];
        if (!new File(tmpUrlNoZXY).exists()) {
            this.templateUrl = "http://" + tmpUrlPath; //$NON-NLS-1$
        } else {
            this.templateUrl = tmpUrlPath; //$NON-NLS-1$
            String name2 = new File(tmpUrlNoZXY).getName();
            setName(name2);
        }
        this.id = generateId();

        int zoomMin, zoomMax;
        try {
            zoomMin = Integer.parseInt(zoomMinValue);
            zoomMax = Integer.parseInt(zoomMaxValue);
        } catch (Exception exc) {
            WMTPlugin.log("[CSSource.init] Couldn't parse the resourceId: " + resourceId, exc); //$NON-NLS-1$

            // set default
            zoomMin = 0;
            zoomMax = 20;
        }

        // set scales
        if (validZoomValues(zoomMin, zoomMax)) {
            scales = Arrays.copyOfRange(SCALE_LIST, 0, zoomMax + 1);

            for( int i = 0; i < zoomMin; i++ ) {
                scales[i] = Double.NaN;
            }
        } else {
            scales = SCALE_LIST;
        }
    }

    // region ID
    @Override
    public String getId() {
        return id;
    }

    private String generateId() {
        return LayerSet.constructId("", templateUrl); //$NON-NLS-1$
    }
    // endregion

    // region Scales
    private boolean validZoomValues( int zoomMin, int zoomMax ) {
        return (zoomMin < zoomMax) && (zoomMin >= 0) && (zoomMax < SCALE_LIST.length);
    }

    @Override
    public double[] getScaleList() {
        return scales;
    }

    /**
     * List of scale-factors for each zoom-level,
     * see: http://msdn.microsoft.com/en-us/library/bb259689.aspx     * 
     */
    public static double[] SCALE_LIST = {591658711, 295829355, 147914381, 73957190, 36978595, 18489297, 9244648, 4622324,
            2311162, 1155581, 577790, 288895, 144447, 72223, 36111, 18055, 9027, 4513, 2256, 1128, 564, 282, 141};
    // endregion

    /**
     * Creates the tile-url by replacing the tags inside the template-url.
     */
    @Override
    public String getTileUrl( int zoomLevel, int x, int y ) {
        String url;
        if (isTms) {
            int[] tmsTiles = googleTile2TmsTile(x, y, zoomLevel);
            x = tmsTiles[0];
            y = tmsTiles[1];
            url = templateUrl.replace(TAG_ZOOM, Integer.toString(zoomLevel));
            url = url.replace(TAG_X, Integer.toString(x));
            url = url.replace(TAG_Y, Integer.toString(y));
        }else{
            url = templateUrl.replace(TAG_ZOOM, Integer.toString(zoomLevel));
            url = url.replace(TAG_X, Integer.toString(x));
            url = url.replace(TAG_Y, Integer.toString(y));
        }
        return url;
    }

    @Override
    public String getBaseUrl() {
        return null; // not in use
    }

    /**
     * Converts Google tile coordinates to TMS Tile coordinates.
     * <p>
     * Calculation of number of rows for provided zoom level described by open street maps:
     * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames
     * 
     * <p>
     * Google maps requires the opposite vertical orientation.
     * 
     * @param xTile the x tile number.
     * @param yTile the y tile number.
     * @param zoom the current zoom level.
     * @return the converted values.
     */
    public static int[] googleTile2TmsTile( int xTile, int yTile, int zoom ) {
        final int n = ((int) Math.pow(2, zoom)) -1;
        int swappedRow = n - yTile;
        return new int[]{xTile, swappedRow};
    }

}
