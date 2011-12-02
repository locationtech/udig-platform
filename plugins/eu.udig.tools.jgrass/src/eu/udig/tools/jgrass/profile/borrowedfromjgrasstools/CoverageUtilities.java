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
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.media.jai.iterator.RandomIter;
import javax.media.jai.iterator.RandomIterFactory;

import org.geotools.coverage.grid.GridCoordinates2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.Envelope2D;
import org.geotools.referencing.operation.matrix.XAffineTransform;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LengthIndexedLine;

/**
 * <p>
 * A class of utilities bound to raster analysis
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
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
    public static List<ProfilePoint> doProfile( GridCoverage2D coverage, Coordinate... coordinates ) throws Exception {
        RegionMap regionMap = CoverageUtilities.getRegionParamsFromGridCoverage(coverage);
        double xres = regionMap.getXres();
        double yres = regionMap.getYres();
        int cols = regionMap.getCols();
        int rows = regionMap.getRows();
        double step = Math.min(xres, yres);

        GridGeometry2D gridGeometry = coverage.getGridGeometry();
        RenderedImage renderedImage = coverage.getRenderedImage();
        RandomIter iter = RandomIterFactory.create(renderedImage, null);
        Envelope2D envelope2d = gridGeometry.getEnvelope2D();

        List<ProfilePoint> profilePointsList = new ArrayList<ProfilePoint>();

        GeometryFactory gf = new GeometryFactory();
        LineString line = gf.createLineString(coordinates);
        double lineLength = line.getLength();
        LengthIndexedLine indexedLine = new LengthIndexedLine(line);

        double progressive = 0.0;
        GridCoordinates2D gridCoords;
        while( progressive < lineLength + step ) { // run over by a step to make sure we get the
                                                   // last coord back from the extractor
            Coordinate c = indexedLine.extractPoint(progressive);
            gridCoords = gridGeometry.worldToGrid(new DirectPosition2D(c.x, c.y));
            double value = JGTConstants.doubleNovalue;
            if (envelope2d.contains(c.x, c.y) && isInside(cols, rows, gridCoords)) {
                value = iter.getSampleDouble(gridCoords.x, gridCoords.y, 0);
            }
            ProfilePoint profilePoint = new ProfilePoint(progressive, value, c.x, c.y);
            profilePointsList.add(profilePoint);
            progressive = progressive + step;
        }

        iter.done();
        return profilePointsList;
    }

    private static boolean isInside( int cols, int rows, GridCoordinates2D gridCoords ) {
        return gridCoords.x >= 0 && gridCoords.x <= cols && gridCoords.y >= 0 && gridCoords.y <= rows;
    }
}
