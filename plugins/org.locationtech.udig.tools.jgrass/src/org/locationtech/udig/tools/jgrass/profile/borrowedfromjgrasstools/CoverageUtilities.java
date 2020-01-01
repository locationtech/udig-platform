/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.profile.borrowedfromjgrasstools;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.matrix.XAffineTransform;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.linearref.LengthIndexedLine;

/**
 * <p>
 * A class of utilities bound to raster analysis
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @author Frank Gasdorf
 * 
 * @since 0.1
 */
public class CoverageUtilities {
    public static final String NORTH = "NORTH"; //$NON-NLS-1$
    public static final String SOUTH = "SOUTH"; //$NON-NLS-1$
    public static final String WEST = "WEST"; //$NON-NLS-1$
    public static final String EAST = "EAST"; //$NON-NLS-1$
    public static final String XRES = "XRES"; //$NON-NLS-1$
    public static final String YRES = "YRES"; //$NON-NLS-1$
    public static final String ROWS = "ROWS"; //$NON-NLS-1$
    public static final String COLS = "COLS"; //$NON-NLS-1$

    private static GeometryFactory gf = new GeometryFactory();

    /**
     * Get the parameters of the region covered by the {@link GridCoverage2D coverage}. 
     * 
     * @param gridCoverage the coverage.
     * @return the {@link HashMap map} of parameters. ( {@link #NORTH} and the 
     *          other static vars can be used to retrieve them.
     */
    public static RegionMap getRegionParamsFromGridCoverage( GridCoverage2D gridCoverage ) {
        RegionMap envelopeParams = new RegionMap();

        Envelope envelope = gridCoverage.getEnvelope();

        DirectPosition lowerCorner = envelope.getLowerCorner();
        double[] westSouth = lowerCorner.getCoordinate();
        DirectPosition upperCorner = envelope.getUpperCorner();
        double[] eastNorth = upperCorner.getCoordinate();

        GridGeometry2D gridGeometry = gridCoverage.getGridGeometry();
        GridEnvelope2D gridRange = gridGeometry.getGridRange2D();
        int height = gridRange.height;
        int width = gridRange.width;

        AffineTransform gridToCRS = (AffineTransform) gridGeometry.getGridToCRS();
        double xRes = XAffineTransform.getScaleX0(gridToCRS);
        double yRes = XAffineTransform.getScaleY0(gridToCRS);

        envelopeParams.put(NORTH, eastNorth[1]);
        envelopeParams.put(SOUTH, westSouth[1]);
        envelopeParams.put(WEST, westSouth[0]);
        envelopeParams.put(EAST, eastNorth[0]);
        envelopeParams.put(XRES, xRes);
        envelopeParams.put(YRES, yRes);
        envelopeParams.put(ROWS, (double) height);
        envelopeParams.put(COLS, (double) width);

        return envelopeParams;
    }

    /**
     * Calculates the profile of a raster map between given {@link Coordinate coordinates}.
     * 
     * @param coverage the coverage from which to extract the profile.
     * @param coordinates the coordinates to use to trace the profile.
     * @return the list of {@link ProfilePoint}s.
     * @throws Exception
     */
    public static List<ProfilePoint> doProfile(CoordinateReferenceSystem mapCRS, GridCoverage2D coverage, double step, Coordinate... coordinates )
            throws Exception {
        List<ProfilePoint> profilePointsList = new ArrayList<ProfilePoint>();

        LineString line = gf.createLineString(coordinates);
        double lineLength = line.getLength();
        LengthIndexedLine indexedLine = new LengthIndexedLine(line);

        double progressive = 0.0;
        Point2D point = new Point2D.Double();
        
        CoordinateReferenceSystem coverageCRS = coverage.getCoordinateReferenceSystem();
        MathTransform mathTransform = null;
        if (mapCRS != coverageCRS) {
            mathTransform = CRS.findMathTransform(mapCRS, coverageCRS, true);
        }

        while( progressive < lineLength + step ) { // run over by a step to make sure we get the
                                                   // last coord back from the extractor
            Coordinate c = indexedLine.extractPoint(progressive);
            Coordinate current = null;
            if (mathTransform != null) {
                current = JTS.transform(c, null, mathTransform);
            } else {
                current = c;
            }
            
            double value = JGTConstants.doubleNovalue;
            try {
                point.setLocation(current.x, current.y);
                double[] evaluated = coverage.evaluate(point, (double[]) null);
                value = evaluated[0];
            } catch (Exception e) {
                // ignore problematic points (outside etc)
            }
            if (value != JGTConstants.doubleNovalue) {
                ProfilePoint profilePoint = new ProfilePoint(progressive, value, c.x, c.y);
                profilePointsList.add(profilePoint);
            }
            progressive = progressive + step;
        }
        return profilePointsList;
    }

}
