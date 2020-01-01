/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.processingregion;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.Envelope2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

/**
 * Represents the processing region.
 * 
 * <p>
 * Calculations always work against a particular geographic region, which
 * contains the boundaries of the region as well as the information of the
 * region's resolution and the number of rows and cols of the region.
 * </p>
 * <p>
 * <b>Warning</b>: since the rows and cols have to be integers, the resolution
 * is may be recalculated to fulfill this constraint. Users should not wonder if
 * the asked resolution is not available in the supplied boundaries.
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.2.1
 * 
 */
public class ProcessingRegion {

    /**
     * The northern boundary of the region.
     */
    private double north = Double.NaN;

    /**
     * The southern boundary of the region.
     */
    private double south = Double.NaN;

    /**
     * The western boundary of the region.
     */
    private double west = Double.NaN;

    /**
     * The eastern boundary of the region.
     */
    private double east = Double.NaN;

    /**
     * The north-south resolution of the region.
     */
    private double ns_res = Double.NaN;

    /**
     * The east-west resolution of the region.
     */
    private double we_res = Double.NaN;

    /**
     * The number of rows of the region.
     */
    private int rows = 0;

    /**
     * The number of columns of the region.
     */
    private int cols = 0;

    /**
     * Creates a new instance of {@link ProcessingRegion}.
     * 
     * <p>
     * This constructor may be used when boundaries and number of rows and
     * columns are available.
     * </p>
     * 
     * @param west
     *            the western boundary.
     * @param east
     *            the eastern boundary.
     * @param south
     *            the southern boundary.
     * @param north
     *            the nothern boundary.
     * @param rows
     *            the number of rows.
     * @param cols
     *            the number of cols.
     */
    public ProcessingRegion( double west, double east, double south, double north, int rows, int cols ) {
        this.west = west;
        this.east = east;
        this.south = south;
        this.north = north;
        this.rows = rows;
        this.cols = cols;
        fixResolution();
    }

    /**
     * Creates a new instance of {@link ProcessingRegion}.
     * 
     * <p>
     * This constructor may be used when boundaries and the resolution is
     * available.
     * </p>
     * 
     * @param west
     *            the western boundary.
     * @param east
     *            the eastern boundary.
     * @param south
     *            the southern boundary.
     * @param north
     *            the northern boundary.
     * @param weres
     *            the east-west resolution.
     * @param nsres
     *            the north -south resolution.
     */
    public ProcessingRegion( double west, double east, double south, double north, double weres, double nsres ) {
        this.west = west;
        this.east = east;
        this.south = south;
        this.north = north;
        we_res = weres;
        ns_res = nsres;

        fixRowsAndCols();
        fixResolution();
    }

    /**
     * Creates a new instance of {@link ProcessingRegion} by duplicating an existing
     * region.
     * 
     * @param region
     *            a region from which to take the setting from.
     */
    public ProcessingRegion( ProcessingRegion region ) {
        west = region.getWest();
        east = region.getEast();
        south = region.getSouth();
        north = region.getNorth();
        rows = region.getRows();
        cols = region.getCols();
        fixResolution();
    }

    /**
     * Creates a new instance of {@link ProcessingRegion} from an {@link Envelope2D}
     * .
     * 
     * @param envelope2D
     *            the envelope2D from which to take the setting from.
     */
    public ProcessingRegion( Envelope2D envelope2D ) {
        west = envelope2D.getMinX();
        east = envelope2D.getMaxX();
        south = envelope2D.getMinY();
        north = envelope2D.getMaxY();
        we_res = envelope2D.getHeight();
        ns_res = envelope2D.getWidth();

        fixRowsAndCols();
        fixResolution();
    }

    /**
     * Creates a new instance of {@link ProcessingRegion} from given strings.
     * 
     * @param west
     *            the western boundary string.
     * @param east
     *            the eastern boundary string.
     * @param south
     *            the southern boundary string.
     * @param north
     *            the nothern boundary string.
     * @param ewres the x resolution string.
     * @param nsres the y resolution string.
     */
    public ProcessingRegion( String west, String east, String south, String north, String ewres, String nsres ) {

        double[] nsew = nsewStringsToNumbers(north, south, east, west);
        double[] xyRes = xyResStringToNumbers(ewres, nsres);
        double no = nsew[0];
        double so = nsew[1];
        double ea = nsew[2];
        double we = nsew[3];
        double xres = xyRes[0];
        double yres = xyRes[1];

        ProcessingRegion tmp = new ProcessingRegion(we, ea, so, no, xres, yres);
        setExtent(tmp);

    }

    /**
     * Creates a new instance of {@link ProcessingRegion} from given strings.
     * 
     * @param west
     *            the western boundary string.
     * @param east
     *            the eastern boundary string.
     * @param south
     *            the southern boundary string.
     * @param north
     *            the nothern boundary string.
     * @param rows
     *            the string of rows.
     * @param cols
     *            the string of cols.
     */
    public ProcessingRegion( String west, String east, String south, String north, int rows, int cols ) {
        double[] nsew = nsewStringsToNumbers(north, south, east, west);
        double no = nsew[0];
        double so = nsew[1];
        double ea = nsew[2];
        double we = nsew[3];
        ProcessingRegion tmp = new ProcessingRegion(we, ea, so, no, rows, cols);
        setExtent(tmp);
    }

    /**
     * Sets the extent of this window using another window.
     * 
     * @param win another window object
     */
    public void setExtent( ProcessingRegion region ) {
        west = region.getWest();
        east = region.getEast();
        south = region.getSouth();
        north = region.getNorth();
        rows = region.getRows();
        cols = region.getCols();
        fixResolution();
        fixRowsAndCols();
    }

    /**
     * Creates JTS envelope from the current region.
     * 
     * @return the JTS envelope wrapping the current region.
     */
    public Envelope getEnvelope() {
        return new Envelope(new Coordinate(west, north), new Coordinate(east, south));
    }

    /**
     * Creates a {@linkplain Rectangle2D.Double rectangle} from the current
     * region.
     * 
     * <p>
     * Note that the rectangle width and height are world coordinates.
     * </p>
     * 
     * @return the rectangle wrapping the current region.
     */
    public Rectangle2D.Double getRectangle() {
        return new Rectangle2D.Double(west, south, east - west, north - south);
    }

    @SuppressWarnings("nls")
    public String toString() {
        return ("region:\nwest=" + west + "\neast=" + east + "\nsouth=" + south + "\nnorth=" + north + "\nwe_res=" + we_res
                + "\nns_res=" + ns_res + "\nrows=" + rows + "\ncols=" + cols);
    }

    /**
     * Reprojects a {@link ProcessingRegion region}.
     * 
     * @param sourceCRS
     *            the original {@link CoordinateReferenceSystem crs} of the
     *            region.
     * @param targetCRS
     *            the target {@link CoordinateReferenceSystem crs} of the
     *            region.
     * @param lenient
     *            defines whether to apply a lenient transformation or not.
     * @return a new {@link ProcessingRegion region}.
     * @throws Exception
     *             exception that may be thrown when applying the
     *             transformation.
     */
    public ProcessingRegion reproject( CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS, boolean lenient )
            throws Exception {

        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, lenient);
        Envelope envelope = getEnvelope();
        Envelope targetEnvelope = JTS.transform(envelope, transform);

        return new ProcessingRegion(targetEnvelope.getMinX(), targetEnvelope.getMaxX(), targetEnvelope.getMinY(),
                targetEnvelope.getMaxY(), getRows(), getCols());

    }

    /**
     * calculates the resolution from the boundaries of the region and the rows
     * and cols.
     */
    private void fixResolution() {
        we_res = (east - west) / cols;
        ns_res = (north - south) / rows;
    }

    /**
     * calculates rows and cols from the region and its resolution.
     * 
     * <p>
     * Rows and cols have to be integers, rounding is applied if required.
     * </p>
     */
    private void fixRowsAndCols() {
        rows = (int) Math.round((north - south) / ns_res);
        if (rows < 1)
            rows = 1;
        cols = (int) Math.round((east - west) / we_res);
        if (cols < 1)
            cols = 1;
    }

    /**
     * Snaps a geographic point to be on the region grid.
     * 
     * <p>
     * Moves the point given by X and Y to be on the grid of the supplied
     * region.
     * </p>
     * 
     * @param x
     *            the easting of the arbitrary point.
     * @param y
     *            the northing of the arbitrary point.
     * @param region
     *            the active window from which to take the grid.
     * @return the snapped coordinate.
     */
    public static Coordinate snapToNextHigherInRegionResolution( double x, double y, ProcessingRegion region ) {

        double minx = region.getRectangle().getBounds2D().getMinX();
        double ewres = region.getWEResolution();
        double xsnap = minx + (Math.ceil((x - minx) / ewres) * ewres);

        double miny = region.getRectangle().getBounds2D().getMinY();
        double nsres = region.getNSResolution();
        double ysnap = miny + (Math.ceil((y - miny) / nsres) * nsres);

        return new Coordinate(xsnap, ysnap);

    }

    /**
     * Creates a region from envelope bounds snapped to a region grid.
     * 
     * <p>
     * This takes an envelope and a JGrass region and creates a new region to
     * match the bounds of the envelope, but the grid of the region. This is
     * important if the region has to match some feature layer.
     * </p>
     * <p>
     * The bounds of the new region contain completely the envelope.
     * </p>
     * 
     * @param sourceEnvelope
     *            the envelope to adapt.
     * @param sourceRegion
     *            the region from which to take the grid to be snapped.
     * @return a new region, created from the envelope bounds snapped to the
     *         region grid.
     */
    public static ProcessingRegion adaptActiveRegionToEnvelope( Envelope sourceEnvelope, ProcessingRegion sourceRegion ) {

        double originalXres = sourceRegion.getNSResolution();
        double originalYres = sourceRegion.getWEResolution();
        double originalWest = sourceRegion.getWest();
        double originalSouth = sourceRegion.getSouth();

        double envWest = sourceEnvelope.getMinX();
        double deltaX = (envWest - originalWest) % originalXres;
        double newWest = envWest - deltaX;

        double envSouth = sourceEnvelope.getMinY();
        double deltaY = (envSouth - originalSouth) % originalYres;
        double newSouth = envSouth - deltaY;

        double newWidth = sourceEnvelope.getWidth();
        double deltaW = newWidth % originalXres;
        newWidth = newWidth - deltaW + originalXres;

        double newHeight = sourceEnvelope.getHeight();
        double deltaH = newHeight % originalYres;
        newHeight = newHeight - deltaH + originalYres;

        ProcessingRegion newRegion = new ProcessingRegion(newWest, newWest + newWidth, newSouth, newSouth + newHeight,
                originalXres, originalYres);

        return newRegion;
    }

    /**
     * @param subregionsNum
     * @return
     */
    public List<ProcessingRegion> toSubRegions( int subregionsNum ) {
        int tmpR = getRows();
        int tmpC = getCols();

        double tmpWest = getWest();
        double tmpSouth = getSouth();
        double tmpWERes = getWEResolution();
        double tmpNSRes = getNSResolution();

        if (subregionsNum > tmpR || subregionsNum > tmpC) {
            throw new IllegalArgumentException("The number of subregions has to be smaller than the number of rows and columns.");
        }

        int subregRows = (int) Math.floor(tmpR / (double) subregionsNum);
        int subregCols = (int) Math.floor(tmpC / (double) subregionsNum);

        List<ProcessingRegion> regions = new ArrayList<ProcessingRegion>();

        double runningEasting = tmpWest;
        double runningNorthing = tmpSouth;
        for( int i = 0; i < subregionsNum; i++ ) {
            double n = runningNorthing + subregRows * tmpNSRes;
            double s = runningNorthing;
            for( int j = 0; j < subregionsNum; j++ ) {
                double w = runningEasting;
                double e = runningEasting + subregCols * tmpWERes;

                if (e > getEast()) {
                    e = getEast();
                }
                if (n > getNorth()) {
                    n = getNorth();
                }

                ProcessingRegion r = new ProcessingRegion(w, e, s, n, tmpWERes, tmpNSRes);
                if (r.getWEResolution() == 0 || r.getNSResolution() == 0) {
                    continue;
                }
                regions.add(r);

                runningEasting = e;
            }
            runningEasting = tmpWest;
            runningNorthing = n;
        }

        return regions;
    }
    /**
     * Reads a region file and sets a given region to the supplied region file.
     * 
     * @param filePath
     *            the path to the region file.
     * @param region
     *            the region to be set to the region file informations.
     */
    @SuppressWarnings("nls")
    /**
     * Transforms degree string into the decimal value.
     * 
     * @param value the string in degrees.
     * @return the translated value.
     */
    private double degreeToNumber( String value ) {
        double number = -1;

        String[] valueSplit = value.trim().split(":"); //$NON-NLS-1$
        if (valueSplit.length == 3) {
            // deg:min:sec.ss
            double deg = Double.parseDouble(valueSplit[0]);
            double min = Double.parseDouble(valueSplit[1]);
            double sec = Double.parseDouble(valueSplit[2]);
            number = deg + min / 60.0 + sec / 60.0 / 60.0;
        } else if (valueSplit.length == 2) {
            // deg:min
            double deg = Double.parseDouble(valueSplit[0]);
            double min = Double.parseDouble(valueSplit[1]);
            number = deg + min / 60.0;
        } else if (valueSplit.length == 1) {
            // deg
            number = Double.parseDouble(valueSplit[0]);
        }
        return number;
    }

    /**
     * Transforms a GRASS resolution string in metric or degree to decimal.
     * 
     * @param ewres the x resolution string.
     * @param nsres the y resolution string.
     * @return the array of x and y resolution doubles.
     */
    private double[] xyResStringToNumbers( String ewres, String nsres ) {
        double xres = -1.0;
        double yres = -1.0;
        if (ewres.indexOf(':') != -1) {
            xres = degreeToNumber(ewres);
        } else {
            xres = Double.parseDouble(ewres);
        }
        if (nsres.indexOf(':') != -1) {
            yres = degreeToNumber(nsres);
        } else {
            yres = Double.parseDouble(nsres);
        }

        return new double[]{xres, yres};
    }

    /**
     * Transforms the GRASS bounds strings in metric or degree to decimal.
     * 
     * @param north the north string.
     * @param south the south string.
     * @param east the east string.
     * @param west the west string.
     * @return the array of the bounds in doubles.
     */
    @SuppressWarnings("nls")
    private double[] nsewStringsToNumbers( String north, String south, String east, String west ) {

        double no = -1.0;
        double so = -1.0;
        double ea = -1.0;
        double we = -1.0;

        if (north.indexOf("N") != -1 || north.indexOf("n") != -1) {
            north = north.substring(0, north.length() - 1);
            no = degreeToNumber(north);
        } else if (north.indexOf("S") != -1 || north.indexOf("s") != -1) {
            north = north.substring(0, north.length() - 1);
            no = -degreeToNumber(north);
        } else {
            no = Double.parseDouble(north);
        }
        if (south.indexOf("N") != -1 || south.indexOf("n") != -1) {
            south = south.substring(0, south.length() - 1);
            so = degreeToNumber(south);
        } else if (south.indexOf("S") != -1 || south.indexOf("s") != -1) {
            south = south.substring(0, south.length() - 1);
            so = -degreeToNumber(south);
        } else {
            so = Double.parseDouble(south);
        }
        if (west.indexOf("E") != -1 || west.indexOf("e") != -1) {
            west = west.substring(0, west.length() - 1);
            we = degreeToNumber(west);
        } else if (west.indexOf("W") != -1 || west.indexOf("w") != -1) {
            west = west.substring(0, west.length() - 1);
            we = -degreeToNumber(west);
        } else {
            we = Double.parseDouble(west);
        }
        if (east.indexOf("E") != -1 || east.indexOf("e") != -1) {
            east = east.substring(0, east.length() - 1);
            ea = degreeToNumber(east);
        } else if (east.indexOf("W") != -1 || east.indexOf("w") != -1) {
            east = east.substring(0, east.length() - 1);
            ea = -degreeToNumber(east);
        } else {
            ea = Double.parseDouble(east);
        }

        return new double[]{no, so, ea, we};
    }

    /**
     * Getter for north
     * 
     * @return the north
     */
    public double getNorth() {
        return north;
    }

    /**
     * Setter for north
     * 
     * @param north
     *            the north to set
     */
    public void setNorth( double north ) {
        this.north = north;
    }

    /**
     * Getter for south
     * 
     * @return the south
     */
    public double getSouth() {
        return south;
    }

    /**
     * Setter for south
     * 
     * @param south
     *            the south to set
     */
    public void setSouth( double south ) {
        this.south = south;
    }

    /**
     * Getter for west
     * 
     * @return the west
     */
    public double getWest() {
        return west;
    }

    /**
     * Setter for west
     * 
     * @param west
     *            the west to set
     */
    public void setWest( double west ) {
        this.west = west;
    }

    /**
     * Getter for east
     * 
     * @return the east
     */
    public double getEast() {
        return east;
    }

    /**
     * Setter for east
     * 
     * @param east
     *            the east to set
     */
    public void setEast( double east ) {
        this.east = east;
    }

    /**
     * Getter for ns_res
     * 
     * @return the ns_res
     */
    public double getNSResolution() {
        return ns_res;
    }

    /**
     * Setter for ns_res
     * 
     * @param ns_res
     *            the ns_res to set
     */
    public void setNSResolution( double ns_res ) {
        this.ns_res = ns_res;
        fixRowsAndCols();
        fixResolution();
    }

    /**
     * Getter for we_res
     * 
     * @return the we_res
     */
    public double getWEResolution() {
        return we_res;
    }

    /**
     * Setter for we_res
     * 
     * @param we_res
     *            the we_res to set
     */
    public void setWEResolution( double we_res ) {
        this.we_res = we_res;
        fixRowsAndCols();
        fixResolution();
    }

    /**
     * Getter for rows
     * 
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Setter for rows
     * 
     * @param rows
     *            the rows to set
     */
    public void setRows( int rows ) {
        this.rows = rows;
        fixResolution();
    }

    /**
     * Getter for cols.
     * 
     * @return the cols.
     */
    public int getCols() {
        return cols;
    }

    /**
     * Setter for cols.
     * 
     * @param cols
     *            the cols to set.
     */
    public void setCols( int cols ) {
        this.cols = cols;
        fixResolution();
    }

}
