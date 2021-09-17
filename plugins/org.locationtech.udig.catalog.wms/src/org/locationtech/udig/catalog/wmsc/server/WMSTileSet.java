/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.data.ows.AbstractOpenWebService;
import org.geotools.ows.wms.CRSEnvelope;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.util.ObjectCache;
import org.geotools.util.ObjectCaches;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

/**
 * Class represents a WMSC tile set.  See:
 * <p>
 * http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation#GetCapabilities_Responses
 *  </p>
 *
 * @author Emily Gouge, Graham Davis (Refractions Research, Inc.)
 * @since 1.2.0
 */
public class WMSTileSet implements TileSet {

    /** Not fear factor! The factor applied to determine what scale zoom levels should switch at */
    private static final double SCALE_FACTOR = 0.2;

    /** a unique identifies */
    private int id;

    /** the AbstractOpenWebService **/
    private AbstractOpenWebService<?,?> server;

    /** Coordinate Reference System of the Tiles */
    private CoordinateReferenceSystem crs;
    /**
     * SRSName (usually of the format "EPSG:4326")
     */
    private String epsgCode;

    /** Data bounding box **/
    private ReferencedEnvelope bboxSrs;

    /** size of tiles - in pixels (often 512) */
    private int width;

    /** size of tiles - in pixels (often 512) */
    private int height;

    /** image format - MIME type? */
    private String format;

    /** List of layers (separated by comma?) */
    private String layers;

    /** Comma separated list of resolutions in units per pixel */
    private String resolutions;

    /** Parsed out resolutions - the strict values from resolutions */
    private double[] dresolutions;

    /**
     * Parsed out resolutions - the relaxed values so we do not request more data that can be drawn
     * per pixel. (Often this amounts 1.2 real pixels per on screen pixel - see SCALE_FACTOR
     */
    private double[] mresolutions; // the scale at which we will switch zoom levels

    /** styles */
    private String styles;

    /** map of tiles
     * NOTE:  This is a WEAKHashMap because we don't want to run out of
     * memory storing all the tiles.  The garbage collector should clean
     * up less-used keys and their objects as necessary.
     **/
    ObjectCache tiles = ObjectCaches.create("soft", 50); //Tiles that are on the screen //$NON-NLS-1$

    public WMSTileSet() {
        updateID();
    }

    public void setCoorindateReferenceSystem( String epsg ) {
        this.epsgCode = epsg;
        try {
            this.crs = CRS.decode(epsg);
        } catch (Exception ex) {
            // WmsPlugin.trace("Cannot decode tile epsg code: " + epsg, ex); //$NON-NLS-1$
        }
        updateID();
    }

    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return this.crs;
    }

    public void setBoundingBox( CRSEnvelope bbox ) {
        CoordinateReferenceSystem crs = null;
        try {
            crs = CRS.decode(bbox.getEPSGCode());
        } catch (Exception ex) {
            System.out.println("Cannot decode tile epsg code: " + bbox.getEPSGCode()); //$NON-NLS-1$
        }
        bboxSrs = new ReferencedEnvelope(bbox.getMinX(), bbox.getMaxX(), bbox.getMinY(), bbox
                .getMaxY(), crs);
        updateID();
    }

    public void setWidth( int width ) {
        this.width = width;
        updateID();
    }

    public void setStyles( String styles ) {
        this.styles = styles;
        updateID();
    }

    public void setHeight( int height ) {
        this.height = height;
        updateID();
    }

    public void setFormat( String format ) {
        this.format = format;
        updateID();
    }

    public void setLayers( String layers ) {
        this.layers = layers;
        updateID();
    }

    public void setResolutions( String res ) {
        this.resolutions = res;
        String[] sres = resolutions.split(" "); //$NON-NLS-1$
        double[] dres = new double[sres.length];
        for( int i = 0; i < sres.length; i++ ) {
            dres[i] = Double.parseDouble(sres[i]);
        }
        this.dresolutions = dres;

        // compute resolutions where the zoom will switch
        mresolutions = new double[dresolutions.length - 1];
        for( int i = 0; i < dresolutions.length - 1; i++ ) {
            mresolutions[i] = ((dresolutions[i] - dresolutions[i + 1]) * SCALE_FACTOR)
                    + dresolutions[i + 1];
        }
        updateID();
    }

    public int getNumLevels() {
        return this.dresolutions.length;
    }

    /**
     * Given a scale factor for the resulting image it finds the zoom level that matches the scale
     * factor best.
     *
     * @param scale
     * @return resolution of the zoom level that best matches the scale factor
     */
    private double findAppropriateZoomLevel( double scale ) {

        if (scale > mresolutions[0]) {
            return dresolutions[0];
        }
        for( int i = 1; i < mresolutions.length; i++ ) {
            if (mresolutions[i - 1] >= scale && mresolutions[i] < scale) {
                return dresolutions[i];
            }
        }
        // maximum zoom
        return this.dresolutions[this.dresolutions.length - 1];
    }

    /**
     * Creates a wmsc query string from the given bounds.
     * <p>
     * This string is *very* carefully constructed with the assumption that all the getFormat(),
     * getEPSG(), getLayers() methods return Strings that are valid , consistent and ready to go.
     *
     * @param tile
     * @return
     */
    @SuppressWarnings("nls")
    public String createQueryString( Envelope tile ) {
        String query = "service=WMS&request=getMap&tiled=true&width="+width+"&height="+height+"&format=" + getFormat() + "&srs=" + getEPSGCode()
                + "&layers=" + getLayers() + "&bbox=" + tile.getMinX() + "," + tile.getMinY() + ","
                + tile.getMaxX() + "," + tile.getMaxY() + "&styles=" + getStyles();
        return query;
    }

    public Map<String, Tile> getTilesFromViewportScale( Envelope bounds, double viewportScale ) {
        double scale = findAppropriateZoomLevel(viewportScale);
        return getTilesFromZoom(bounds, scale);
    }

    public Map<String, Tile> getTilesFromZoom( Envelope bounds, double zoom ) {

        double xscale = width * zoom;
        double value = bounds.getMinX() - bboxSrs.getMinX();

        double minx = Math.floor(value / xscale) * xscale + bboxSrs.getMinX();
        value = bounds.getMaxX() - bboxSrs.getMinX();
        double maxx = Math.ceil(value / xscale) * xscale + bboxSrs.getMinX();

        double yscale = height * zoom;
        value = bounds.getMinY() - bboxSrs.getMinY();
        double miny = Math.floor(value / yscale) * yscale + bboxSrs.getMinY();
        value = bounds.getMaxY() - bboxSrs.getMinY();
        double maxy = Math.ceil(value / yscale) * yscale + bboxSrs.getMinY();
        Map<String, Tile> viewportTiles = new HashMap<String, Tile>();

        int xNum = (int)Math.round((maxx- minx) / xscale);
        int yNum = (int)Math.round((maxy - miny) / yscale);
        for (int x = 0; x < xNum; x++){
            double xmin = roundDouble(x * xscale + minx);
            double xmax = roundDouble((x+1) * xscale + minx);
            for (int y = 0; y < yNum; y ++){
                double ymin = roundDouble(y * yscale + miny);
                double ymax = roundDouble((y+1) * yscale + miny);
                //Envelope e = new Envelope(x*xscale+minx, (x+1) * xscale+minx, y * yscale+miny, (y +1)* yscale + miny);
                Envelope e = new Envelope(xmin, xmax, ymin, ymax);
                if (e.getMaxX() <= bboxSrs.getMinX() || e.getMinX() >= bboxSrs.getMaxX()
                        || e.getMaxY() <= bboxSrs.getMinY() || e.getMinY() >= bboxSrs.getMaxY()) {
                    // outside of bounds ignore
                } else {
                    // tile is within the bounds, create it if necessary and
                    // add it to the map
                    String tileid = WMSTile.buildId(e, zoom);
                    Tile tile;
                    if (tiles.peek(tileid) == null || tiles.get(tileid) == null) {
                        tile = new WMSTile(server, this, e, zoom);
                        tiles.put(tileid, tile);
                        // create the tile position within the tilerange grid for this scale
                        double topleft_x = bboxSrs.getMinX();
                        double topleft_y = bboxSrs.getMaxY();
                        double tileleft_x = e.getMinX();
                        double tileleft_y = e.getMaxY();

                        double spacex = tileleft_x - topleft_x; // x is left to right
                        double spacey = topleft_y - tileleft_y; // y is top to bottom

                        int posx = (int) Math.round(spacex / xscale);
                        int posy = (int) Math.round(spacey / yscale);

                        String position = posx + "_" + posy; //$NON-NLS-1$
                        tile.setPosition(position);
                    } else {
                        tile = (Tile) tiles.get(tileid);
                    }
                    viewportTiles.put(tileid, tile);
                }
            }
        }
        return viewportTiles;
    }


    /**
     * This function takes the last two digits (8 bits) of a double and 0's them.
     *
     *
     * @param number
     * @return
     */
    private static double roundDouble(double number){
        Long xBits = Double.doubleToLongBits(number);
        //zeroLowerBits
        int nBits = 8;
        long invMask = (1L << nBits) - 1L;
        long mask =~ invMask;
        xBits &= mask;
        return Double.longBitsToDouble(xBits);
    }

    /**
     *  Break up the bounds for this zoom level into a list of bounds so that no single
     *  bounds has more than 1024 tiles in it.
     *
     * @see org.locationtech.udig.catalog.wmsc.server.TileSet#getBoundsListForZoom(org.locationtech.jts.geom.Envelope, double)
     */
    public List<Envelope> getBoundsListForZoom( Envelope bounds, double zoom ) {

        int maxTilesPerBound = 1024;
        List<Envelope> boundsList = new ArrayList<Envelope>();

        double xscale = width * zoom;
        double value = bounds.getMinX() - bboxSrs.getMinX();

        double minx = Math.floor(value / xscale) * xscale + bboxSrs.getMinX();
        value = bounds.getMaxX() - bboxSrs.getMinX();
        double maxx = Math.ceil(value / xscale) * xscale + bboxSrs.getMinX();

        double yscale = height * zoom;
        value = bounds.getMinY() - bboxSrs.getMinY();
        double miny = Math.floor(value / yscale) * yscale + bboxSrs.getMinY();
        value = bounds.getMaxY() - bboxSrs.getMinY();
        double maxy = Math.ceil(value / yscale) * yscale + bboxSrs.getMinY();
        long tilesPerRow = Math.round((maxx-minx) / xscale);
        long tilesPerCol = Math.round((maxy-miny) / yscale);
        long totalTiles = tilesPerCol * tilesPerRow;

        // if there are not enough tiles to make 1024 for this zoom and bounds, then
        // return the single bounds
        if ( totalTiles <=  maxTilesPerBound ) {
            boundsList.add(bounds);
            return boundsList;
        }

        // create the size of each bounds
        double scaleDownFactor = Math.ceil(totalTiles / maxTilesPerBound);
        double boundsWidth = Math.ceil(tilesPerRow / scaleDownFactor) * xscale;
        double boundsHeight = Math.ceil(tilesPerCol / scaleDownFactor) * yscale;

        // create each bounds
        double x = minx;
        while (x <= maxx) {
            double y = miny;
            while (y <= maxy) {
                double x2 = x + boundsWidth;
                if (x2 > maxx) x2 = maxx;
                double y2 = y + boundsHeight;
                if (y2 > maxy) y2 = maxy;
                boundsList.add(new Envelope(x, x2, y, y2));
                y += boundsHeight;
            }
            x += boundsWidth;
        }

        return boundsList;
    }

    public long getTileCount( Envelope bounds, double zoom ) {
        double xscale = width * zoom;
        double value = bounds.getMinX() - bboxSrs.getMinX();

        double minx = Math.floor(value / xscale) * xscale + bboxSrs.getMinX();
        value = bounds.getMaxX() - bboxSrs.getMinX();
        double maxx = Math.ceil(value / xscale) * xscale + bboxSrs.getMinX();

        double yscale = height * zoom;
        value = bounds.getMinY() - bboxSrs.getMinY();
        double miny = Math.floor(value / yscale) * yscale + bboxSrs.getMinY();
        value = bounds.getMaxY() - bboxSrs.getMinY();
        double maxy = Math.ceil(value / yscale) * yscale + bboxSrs.getMinY();
        long tilesPerRow = Math.round((maxx-minx) / xscale);
        long tilesPerCol = Math.round((maxy-miny) / yscale);
        return tilesPerCol * tilesPerRow;
    }

    public String getLayers() {
        return this.layers;
    }

    public String getStyles() {
        return this.styles;
    }

    public String getFormat() {
        return this.format;
    }

    public String getEPSGCode() {
        return this.epsgCode;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ReferencedEnvelope getBounds() {
        return this.bboxSrs;
    }

    public int getId() {
        return this.id;
    }

    /**
     * @returns a copy of the resolutions array
     */
    public double[] getResolutions(){
        double[] d = new double[dresolutions.length];
        System.arraycopy(dresolutions, 0, d, 0, d.length);
        return d;
    }

    /**
     * Create a unique identifier for the tileset from the various strings that define the tile set.
     */
    private void updateID(){
        //compute a hashset from the attributes;
        StringBuffer sb = new StringBuffer();

        if (this.epsgCode != null)
            sb.append(epsgCode);

        if (this.format != null)
            sb.append(this.format);

        if (this.layers != null)
            sb.append(layers);

        if (this.resolutions != null)
            sb.append(this.resolutions);
        if (this.styles != null)
            sb.append(this.styles);
        if (this.bboxSrs != null){
            sb.append(this.bboxSrs.getMinX());
            sb.append(this.bboxSrs.getMaxX());
            sb.append(this.bboxSrs.getMinY());
            sb.append(this.bboxSrs.getMaxY());
            sb.append(this.bboxSrs.getMinX());
        }
        if (this.bboxSrs != null && this.bboxSrs.getCoordinateReferenceSystem() != null) {
            sb.append(this.bboxSrs.getCoordinateReferenceSystem().toString().hashCode());
        }

        sb.append(this.width);
        sb.append(this.height);

        this.id = sb.toString().hashCode();
    }

    public AbstractOpenWebService<?,?> getServer() {
        return server;
    }

    public void setServer(AbstractOpenWebService<?,?> server) {
        this.server = server;
    }
}
