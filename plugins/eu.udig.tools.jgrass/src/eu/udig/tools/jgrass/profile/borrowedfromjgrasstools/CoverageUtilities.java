/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.tools.jgrass.profile.borrowedfromjgrasstools;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.operation.matrix.XAffineTransform;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;

/**
 * <p>
 * A class of utilities bound to raster analysis
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 0.1
 */
@SuppressWarnings("deprecation")
public class CoverageUtilities {
    public static final String NORTH = "NORTH"; //$NON-NLS-1$
    public static final String SOUTH = "SOUTH"; //$NON-NLS-1$
    public static final String WEST = "WEST"; //$NON-NLS-1$
    public static final String EAST = "EAST"; //$NON-NLS-1$
    public static final String XRES = "XRES"; //$NON-NLS-1$
    public static final String YRES = "YRES"; //$NON-NLS-1$
    public static final String ROWS = "ROWS"; //$NON-NLS-1$
    public static final String COLS = "COLS"; //$NON-NLS-1$

    /**
     * Get the parameters of the region covered by the {@link GridCoverage2D coverage}. 
     * 
     * @param gridCoverage the coverage.
     * @return the {@link HashMap map} of parameters. ( {@link #NORTH} and the 
     *          other static vars can be used to retrieve them.
     */
    public static HashMap<String, Double> getRegionParamsFromGridCoverage( GridCoverage2D gridCoverage ) {
        HashMap<String, Double> envelopeParams = new HashMap<String, Double>();

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
     * Calculates the profile of a raster map between two given {@link Coordinate coordinates}.
     * 
     * @param start the first coordinate.
     * @param end the last coordinate.
     * @param coverage the coverage from which to extract the profile.
     * @return the list of {@link ProfilePoint}s.
     * @throws Exception
     */
    public static List<ProfilePoint> doProfile( Coordinate start, Coordinate end, GridCoverage2D coverage ) throws Exception {
        HashMap<String, Double> regionMap = CoverageUtilities.getRegionParamsFromGridCoverage(coverage);
        double xres = regionMap.get(CoverageUtilities.XRES);

        LineSegment pline = new LineSegment(start, end);
        double lenght = pline.getLength();

        List<ProfilePoint> profilePointsList = new ArrayList<ProfilePoint>();
        double progressive = 0.0;

        int samDim = coverage.getSampleDimensions().length;
        GridGeometry2D gridGeometry = coverage.getGridGeometry();
        Envelope2D envelope2d = gridGeometry.getEnvelope2D();
        final double[] evaluated = new double[samDim];

        // ad the first point
        final Point2D.Double point = new Point2D.Double(start.x, start.y);
        double value = Double.NaN;
        if (envelope2d.contains(point)) {
            coverage.evaluate(point, evaluated);
            value = evaluated[0];
        }

        ProfilePoint profilePoint = new ProfilePoint(0.0, value, start.x, start.y);
        profilePointsList.add(profilePoint);
        progressive = progressive + xres;

        while( progressive < lenght ) {
            Coordinate c = pline.pointAlong(progressive / lenght);
            value = Double.NaN;
            point.setLocation(c.x, c.y);
            if (envelope2d.contains(point)) {
                coverage.evaluate(point, evaluated);
                value = evaluated[0];
            }
            profilePoint = new ProfilePoint(progressive, value, c.x, c.y);
            profilePointsList.add(profilePoint);
            progressive = progressive + xres;
        }

        // add the last point
        value = Double.NaN;
        point.setLocation(end.x, end.y);
        if (envelope2d.contains(point)) {
            coverage.evaluate(point, evaluated);
            value = evaluated[0];
        }
        profilePoint = new ProfilePoint(lenght, value, end.x, end.y);
        profilePointsList.add(profilePoint);

        return profilePointsList;
    }

}
