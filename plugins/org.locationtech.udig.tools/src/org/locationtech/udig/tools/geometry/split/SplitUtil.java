/* uDig - User Friendly Desktop Internet GIS
 * http://udig.refractions.net
 * (c) 2010, Vienna City
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.geometry.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.operation.polygonize.Polygonizer;

import org.locationtech.udig.tools.geometry.internal.util.GeometryList;
import org.locationtech.udig.tools.geometry.internal.util.GeometrySet;

/**
 * 
 * Useful functions for working with split, like calculate if split is possible, getting
 * intersections between split line and boundary, etc
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.2.0
 */
public final class SplitUtil {

    /**
     * Get the interior rings of the polygon, and add to the list the rings that intersects with the
     * segment.
     * 
     * @param segment line segment.
     * @param polygon the polygon to be analyzed.
     * @return A list with rings that intersects with the provided segment.
     */
    public static List<LinearRing> addRingThatIntersects(LineString segment, Polygon polygon) {

        List<LinearRing> rings = new GeometryList<LinearRing>();
        for (int j = 0; j < polygon.getNumInteriorRing(); j++) {

            // get an interior ring.
            LinearRing interiorRing = (LinearRing) polygon.getInteriorRingN(j);

            // add the rings that intersects with the segment.
            if (interiorRing.intersects(segment)) {
                rings.add(interiorRing);
            }
        }

        return rings;
    }

    /**
     * Calculate the distance of the intersectionCoord.
     * 
     * @param intersectionCoord The coordinate which will calculate the distance.
     * @param firstSegmentCoord Coordinate from the segment.
     * @param secondSegmentCoord Coordinate from the segment.
     * @return The distance between intersectionCoord and the segment.
     */
    public static double calculateDistanceFromFirst(final Coordinate intersectionCoord,
            final Coordinate firstSegmentCoord, final Coordinate secondSegmentCoord) {

        return firstSegmentCoord.distance(intersectionCoord);
    }

    /**
     * Will find the number of intersections starting from a coordinate, and return the total
     * number.
     * 
     * @param posFirstCoord Position to start with.
     * @param coordinates Line coordinates.
     * @param boundary Boundary geometry to intersect with.
     * @return The total number of intersections between split line and boundary given an start
     *         point.
     */
    public static int countIntersectionsFromPosition(final int posFirstCoord,
            final Coordinate[] coordinates, final Geometry boundary) {

        Set<Geometry> intersectionList = new GeometrySet<Geometry>();
        for (int i = posFirstCoord; i < coordinates.length - 1; i++) {

            Geometry intersection = intersection(coordinates, i, boundary, false);

            if (intersection instanceof Point || intersection instanceof MultiPoint
                    || intersection instanceof GeometryCollection) {

                for (int j = 0; j < intersection.getNumGeometries(); j++) {

                    Geometry intersectionPart = intersection.getGeometryN(j);
                    if (!intersectionList.contains(intersectionPart)) {
                        intersectionList.add(intersectionPart);
                    }
                }
            }
        }
        return intersectionList.size();
    }

    public static int countIntersectionsFromPosition(final int posFirstCoord,
            final Coordinate[] coordinates, final Geometry boundary, final Coordinate skipCoord) {

        Set<Geometry> intersectionList = new GeometrySet<Geometry>();
        for (int i = posFirstCoord; i < coordinates.length - 1; i++) {

            Geometry intersection = intersection(coordinates, i, boundary, false);

            if (intersection instanceof Point || intersection instanceof MultiPoint
                    || intersection instanceof GeometryCollection) {

                for (int j = 0; j < intersection.getNumGeometries(); j++) {

                    Geometry intersectionPart = intersection.getGeometryN(j);
                    if (!intersectionList.contains(intersectionPart)
                            && (!intersectionPart.getCoordinate().equals(skipCoord))) {
                        intersectionList.add(intersectionPart);
                    }
                }
            }
        }
        return intersectionList.size();
    }

    /**
     * Returns the intersection geometry of a segment with the given geometry.
     * 
     * @param coordinates Line coordinates.
     * @param i Position to get coordinates.
     * @param boundary Polygon boundary to intersect with.
     * @param startsFromEnd Starts creating the segment i and i-1.
     * @return The intersection geometry of a segment with the given geometry.
     */
    public static Geometry intersection(final Coordinate[] coordinates, final int i,
            final Geometry boundary, final boolean startsFromEnd) {

        // make a segment with the last coordinate of split line and the
        // following coordinate.
        Coordinate[] segmentCoordinates = new Coordinate[2];
        segmentCoordinates[0] = coordinates[i];
        segmentCoordinates[1] = (startsFromEnd) ? coordinates[i - 1] : coordinates[i + 1];

        LineString segment = boundary.getFactory().createLineString(segmentCoordinates);

        Geometry intersection = boundary.intersection(segment);

        assert intersection instanceof MultiPoint || intersection instanceof Point
                || intersection instanceof GeometryCollection || intersection instanceof LineString : "point must be point"; //$NON-NLS-1$

        return intersection;
    }

    /**
     * Check if all coordinates lies outside the polygon.
     * 
     * @param coordinates Coordinates to be checked.
     * @param polygonGeometry The polygon geometry.
     * @return True if all coordinates are outside the polygon, false otherwise.
     */
    public static boolean areAllCoordinatesOutside(final Coordinate[] coordinates,
            final Geometry polygonGeometry) {

        final GeometryFactory geomFactory = polygonGeometry.getFactory();

        boolean allOutside = true;
        for (int i = 0; i < coordinates.length; i++) {

            Point point = geomFactory.createPoint(coordinates[i]);
            // check the points is inside the polygon.
            // if some one is inside, will return.
            if (polygonGeometry.contains(point)) {
                allOutside = false;
                break;
            }
        }
        return allOutside;
    }

    /**
     * Seeks the first coordinate of split line that lies outside of polygon.
     * 
     * @param splitLine The split line.
     * @param polygonGeometry The polygon which the line will use.
     * @return the first coordinate founded outside the polygon. If it doesn't find, return -1.
     */
    public static int findPositionOfFirstCoordinateOutside(LineString splitLine,
            Geometry polygonGeometry) {

        Coordinate[] lineCoordinates = splitLine.getCoordinates();
        GeometryFactory fc = splitLine.getFactory();
        int position = -1;
        for (int i = 0; i < lineCoordinates.length; i++) {

            boolean valid = isCoordOutSidePolygon(polygonGeometry, lineCoordinates, i, fc);
            if (valid) {
                return i;
            }
        }
        return position;
    }

    /**
     * Finds the coordinates of the split line and go through it. Start from the last coordinate and
     * seeks the first coordinate of split line that lies outside of polygon.
     * 
     * @param splitLine The split line.
     * @param polygonGeometry The polygon which the line will use.
     * @return the first coordinate founded outside the polygon. If it doesn't find, return -1.
     */
    public static int findLastCoordinateOutside(LineString splitLine, Geometry polygonGeometry) {

        Coordinate[] lineCoordinates = splitLine.getCoordinates();
        GeometryFactory fc = splitLine.getFactory();
        int position = -1;
        for (int i = lineCoordinates.length - 1; 0 <= i; i--) {

            boolean valid = isCoordOutSidePolygon(polygonGeometry, lineCoordinates, i, fc);
            if (valid) {
                return i;
            }
        }
        return position;
    }

    /**
     * Return true if the lineCoord touches the boundary or intersects the hole area
     * 
     * @param polygonGeometry
     * @param lineCoords
     * @param fc
     * @return true if the segment is out side
     */
    private static boolean isCoordOutSidePolygon(final Geometry polygonGeometry,
            final Coordinate[] lineCoords, final int indexInLineCoords, final GeometryFactory fc) {

        Point point = fc.createPoint(lineCoords[indexInLineCoords]);
        if (polygonGeometry.disjoint(point)) {
            return true;
        }
        if (polygonGeometry.touches(point)) {

            if (indexInLineCoords < lineCoords.length - 1) {
                Coordinate[] firstSeg = new Coordinate[] { lineCoords[indexInLineCoords],
                        lineCoords[indexInLineCoords + 1] };
                LineString firstSegment = fc.createLineString(firstSeg);

                return containsLineString(polygonGeometry.intersection(firstSegment));
            }

        }
        return false;
    }

    /**
     * Check if the geometry has {@link LineString} or {@link MultiLineString} geometry type.
     * 
     * @param geometry
     * @return True if intersection returns a {@link LineString} or {@link MultiLineString} geometry
     *         type.
     */
    public static boolean containsLineString(Geometry geometry) {

        for (int i = 0; i < geometry.getNumGeometries(); i++) {

            Geometry geomPart = geometry.getGeometryN(i);
            if (geomPart instanceof LineString || geomPart instanceof MultiLineString) {
                return true;
            }

        }
        return false;
    }

    /**
     * Check if the given line is a closed line or if it has a self-intersections, that means it
     * will have a ring in the middle of it.
     * 
     * @param splitLine The split line.
     * @return True if it forms a ring.
     */
    public static boolean isClosedLine(LineString splitLine) {

        Geometry multiLines = splitLine.union();

        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(multiLines);
        Collection<?> polyCollection = polygonizer.getPolygons();

        return polyCollection.size() != 0;
    }

    /**
     * When the splitLines are a MultiLineString they could have many geometries and those could be
     * nodded. This algorithm will create one geometry from nodded geometries with its coordinate
     * CCW oriented. If all the geometries aren't nodded, it will return a multiGeometry with the
     * nodded ones as a single geometry and the others as they are.
     * 
     * Ex: There could be 4 line geometries, making a continuous line. Then, the algorithm will join
     * all the geometries together, change its orientation to CCW and return only 1 geometry.
     * 
     * @param splitLines A multiLineString containing the splitlines.
     * @return If all the geometries are nodded, returns a single LineString, if they aren't,
     *         returns a multiLineString.
     */
    public static Geometry buildLineUnion(Geometry splitLines) {

        // try to "merge" the lines, this means if there is multigGeometry but
        // each geometry is nodded with the next one, create a lineString or
        // multilineString with nodded lines.

        List<Geometry> resultGeoms = new ArrayList<Geometry>();
        List<Coordinate> mergedCoordinates = new ArrayList<Coordinate>();
        GeometryFactory gf = splitLines.getFactory();
        for (int i = 0; i < splitLines.getNumGeometries(); i++) {

            // get the coordinates of the actual geometry.
            Coordinate[] partCoord = splitLines.getGeometryN(i).getCoordinates();

            if (!mergedCoordinates.isEmpty()) {
                // compare the last added coordinate in mergedCoordinates with
                // the first coordinate from the part, if they are the same, it
                // means the line is nodded.
                Coordinate lastAddedCoord = mergedCoordinates.get(mergedCoordinates.size() - 1);
                if (lastAddedCoord.equals2D(partCoord[0])) {
                    // add
                    mergedCoordinates = addCoordinates(partCoord, mergedCoordinates, 1);
                } else {
                    // they are not nodded or the nodding has ended.
                    // create the geometry and start seeking again for more
                    // nodded coordinates.
                    resultGeoms = addMergedGeometry(mergedCoordinates, resultGeoms, gf);
                    mergedCoordinates.clear();
                    // add the partCoord as the first piece of line.
                    mergedCoordinates = addCoordinates(partCoord, mergedCoordinates, 0);
                }
            } else {
                // add the first piece of line.
                mergedCoordinates = addCoordinates(partCoord, mergedCoordinates, 0);
            }
        }
        // if mergedCoordinate contains any item, create the resultant geometry.
        if (mergedCoordinates.size() != 0) {
            resultGeoms = addMergedGeometry(mergedCoordinates, resultGeoms, gf);
        }

        return gf.buildGeometry(resultGeoms);
    }

    /**
     * Get the coordinates and convert CCW if needed, then create the geometry, add to the list and
     * return it.
     * 
     * @param mergedCoordinates The coordinates of the new geometry.
     * @param resultGeoms The list with the current processed geometries.
     * @param gf GeometryFactory.
     * @return The list of geometries plus this last one geometry.
     */
    private static List<Geometry> addMergedGeometry(List<Coordinate> mergedCoordinates,
            List<Geometry> resultGeoms, GeometryFactory gf) {

        Coordinate[] mergedArray = mergedCoordinates.toArray(new Coordinate[mergedCoordinates
                .size()]);

        if (mergedArray.length > 2 && !isColinear(mergedArray)) {

            // close the ring
            Coordinate[] closed = RingUtil.builRing(mergedArray);
            if (!CGAlgorithms.isCCW(closed)) {

                Geometry reverse = reverseLineString(closed, gf);

                assert CGAlgorithms.isCCW(reverse.getCoordinates()) : "It should be CCW. Actual SplitLine: " + reverse; //$NON-NLS-1$

                reverse = reverseLineString(mergedArray, gf);

                resultGeoms.add(reverse);
            } else {
                resultGeoms.add(gf.createLineString(mergedArray));
            }
        } else {
            resultGeoms.add(gf.createLineString(mergedArray));
        }

        return resultGeoms;
    }

    /**
     * Check if the given line is colinear.
     * 
     * @param lineCoord Coordinates of the splitLine.
     * @return True if it is colinear.
     */
    public static boolean isColinear(Coordinate[] lineCoord) {

        boolean isColinear = true;
        for (int i = 0; i < lineCoord.length - 2; i++) {

            if (CGAlgorithms.computeOrientation(lineCoord[i], lineCoord[i + 1], lineCoord[i + 2]) != 0) {
                isColinear = false;
                break;
            }
        }
        return isColinear;
    }

    /**
     * Add coordinates to the list.
     * 
     * @param partCoord The coordinates of the line.
     * @param mergedCoordinates The list with the nodded coordinates.
     * @param startPosition Position from where to start.
     * @return The list filled with the coordinates from the line.
     */
    private static List<Coordinate> addCoordinates(Coordinate[] partCoord,
            List<Coordinate> mergedCoordinates, int startPosition) {

        for (int j = startPosition; j < partCoord.length; j++) {
            mergedCoordinates.add(partCoord[j]);
        }

        return mergedCoordinates;
    }

    /**
     * Reverse the given coordinates, and return the resultant lineString.
     * 
     * @param intersectionCoordinates Coordinates from a lineString.
     * @param gf Geometry factory.
     * @return The geometry built.
     */
    private static Geometry reverseLineString(Coordinate[] intersectionCoordinates,
            GeometryFactory gf) {

        intersectionCoordinates = CoordinateArrays.copyDeep(intersectionCoordinates);
        CoordinateArrays.reverse(intersectionCoordinates);

        return gf.createLineString(intersectionCoordinates);
    }

    /**
     * Copies the coordinates starting form posFirstCoord to posLastCoord.
     * 
     * @param posFirstCoord Start position.
     * @param posLastCoord End position.
     * @param sourceCoords Coordinates to copy.
     * @return New array of coordinates.
     */
    public static Coordinate[] createCoordinateArrayWithoutDuplicated(int posFirstCoord,
            int posLastCoord, final Coordinate[] sourceCoords) {

        Coordinate[] newCoords = new Coordinate[posLastCoord - posFirstCoord + 1];

        newCoords = Arrays.copyOfRange(sourceCoords, posFirstCoord, posLastCoord + 1);

        newCoords = CoordinateArrays.removeRepeatedPoints(newCoords);

        return newCoords;
    }

    /**
     * Inserts the coordinate in the array of coordinates
     * 
     * @param insertPosition
     * @param newCoord
     * @param coordArray
     * 
     * @return a new array of coordinates with the newCoord in the position
     */
    public static Coordinate[] replaceCoordinate(final int insertPosition,
            final Coordinate newCoord, final Coordinate[] coordArray) {

        ArrayList<Coordinate> workingArray = new ArrayList<Coordinate>(Arrays.asList(coordArray));
        workingArray.set(insertPosition, newCoord);

        return workingArray.toArray(new Coordinate[workingArray.size()]);
    }

    /**
     * Make a new {@link LineString} which contains the specified vertex
     * 
     * @param line the original vertex
     * @param vertex the vertex to insert
     * @return a new {@link LineString} with the indeed vertex
     */
    public static LineString insertVertexInLine(LineString line, Coordinate vertex) {

        Coordinate[] lineCoords = line.getCoordinates();
        if (CoordinateArrays.indexOf(vertex, lineCoords) > 0) {
            return line;
        }
        Coordinate[] newLineCoords = insertVertexInLine(lineCoords, vertex);

        LineString newLine = line.getFactory().createLineString(newLineCoords);

        return newLine;
    }

    /**
     * Create a new coordinate array inserting in order the provided vertex. The order is determined
     * by the distance of each coordinate respect the first coordinate.
     * 
     * @param line
     * @param vertex
     * @return an new coordinate with the provided vertex
     */
    public static Coordinate[] insertVertexInLine(Coordinate[] line, Coordinate vertex) {

        Coordinate[] newLine = new Coordinate[line.length + 1];
        final double vertexDistance = line[0].distance(vertex);

        int found = -1;
        int j = -1;
        for (int i = 0; i < line.length; i++) {

            // searches the position of new coordinate
            double currentDistance = line[0].distance(line[i]);
            if (currentDistance <= vertexDistance) {
                newLine[++j] = line[i];
            } else {
                found = i;
                break;
            }
        }
        if (found != -1) {
            // add the vertex and the rest of coordinates in the new segment
            newLine[++j] = vertex;
            for (int i = found; i < line.length; i++) {
                newLine[++j] = line[i];
            }

        } else {
            // vertex is the last coordinate
            newLine[newLine.length - 1] = vertex;
        }
        return newLine;

    }

    /**
     * Merge the first and the second line in one
     * 
     * @param firstLine
     * @param secondLine
     * @return a new Coordinate list that contains the first and the second set of coordinates
     */
    public static Coordinate[] mergeCoordinate(final Coordinate[] firstLine,
            final Coordinate[] secondLine) {

        List<Coordinate> merge = new LinkedList<Coordinate>();

        for (int i = 0; i < firstLine.length; i++) {

            if (!merge.contains(firstLine[i])) {

                merge.add(firstLine[i]);
            }
        }
        for (int i = 0; i < secondLine.length; i++) {

            if (!merge.contains(secondLine[i])) {

                merge.add(secondLine[i]);
            }
        }

        return merge.toArray(new Coordinate[merge.size()]);
    }

    /**
     * Check if it is a valid intersection between the original geometry and the split line.
     * 
     * Test polygons and multiPolygons.
     * 
     * Get the boundary of the polygon geometry and intersects it with the split line. A valid split
     * operation must fulfill the next:
     * 
     * <pre>
     * 
     * -The split line must intersect the polygon boundary at least at 2
     * points and part of split line must intersect with the polygon.
     * -The line comes from outside the feature, intersects on one point a boundary (interior-exterior), 
     * the line must have at least one point inside the feature, and then intersects again
     * the same boundary(interior-exterior).
     * </pre>
     * 
     * @param geometryToSplit The possible geometry to be split. It should be LineString,
     *        MultilineString, Polygon or MultiPolygon.
     * 
     * @return True if the split operation will produce a valid resultant geometry. False in other
     *         case.
     */

    public static boolean canSplitPolygon(Geometry geometryToSplit,
            UsefulSplitLineBuilder splitLineBuilder) {

        assert geometryToSplit instanceof Polygon || geometryToSplit instanceof MultiPolygon;

        LineString originalSplitLine = splitLineBuilder.getOriginalSplitLine();
        // Get the boundary
        Geometry boundary = geometryToSplit.getBoundary();
        // check if the line is contained inside the geometry to be split.
        if (geometryToSplit.contains(originalSplitLine) && !boundary.intersects(originalSplitLine)) {

            // here we could have the sample of closed lines.
            return SplitUtil.isClosedLine(originalSplitLine);
        }

        // special case, closed split lines.
        if (SplitUtil.isClosedLine(originalSplitLine)) {

            return validateClosedLine(originalSplitLine, geometryToSplit);

        } else {

            for (int i = 0; i < boundary.getNumGeometries(); i++) {

                Geometry eachBoundary = boundary.getGeometryN(i);
                Geometry intersectionPonts = eachBoundary.intersection(originalSplitLine);
                if (intersectionPonts instanceof LineString
                        || intersectionPonts instanceof MultiLineString) {

                    continue; // the intersection is a line over the boundary, so it is not valid
                              // for split operation
                }
                assert intersectionPonts instanceof MultiPoint
                        || intersectionPonts instanceof Point
                        || intersectionPonts instanceof GeometryCollection : "the intersection with the boundary must be point or multypoint"; //$NON-NLS-1$

                if (intersectionPonts.getNumGeometries() >= 2) {
                    // checks if it is possible to build an useful split line
                    for (int j = 0; j < geometryToSplit.getNumGeometries(); j++) {

                        Polygon currentPolygon = (Polygon) geometryToSplit.getGeometryN(j);

                        AdaptedPolygon adaptedPolygon = new AdaptedPolygon(currentPolygon);
                        Geometry usefulSplitLines = splitLineBuilder.createUsefulSplitLine(
                                originalSplitLine, adaptedPolygon);

                        if (usefulSplitLines != null) {
                            return true; // there is a part of split line which is useful to cut the
                                         // polygon
                        }
                    }
                }
            }
        }
        return false;

    }

    /**
     * Validates if the given closed split line is valid for the split operation. It must fulfill
     * the next condition: the intersection between the polygons created from the rings (split line
     * ring) and the original geometry must be polygon or multiPolygon.
     * 
     * @param splitLine Closed Split line.
     * @param originalGeometry Geometry to split.
     * @return True if it is a valid input for the closed line algorithm.
     */
    private static boolean validateClosedLine(final LineString splitLine,
            final Geometry originalGeometry) {

        Geometry multiLines = splitLine.union();

        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(multiLines);
        Collection<Geometry> polyCollection = polygonizer.getPolygons();

        // if any of the pieces are inside the geometry, that means the closed
        // line will end up forming new polygons, it'll produce an output.
        for (Geometry pieces : polyCollection) {

            if ((originalGeometry.contains(pieces) || originalGeometry.intersects(pieces))
                    && ((originalGeometry.intersection(pieces) instanceof Polygon) || (originalGeometry
                            .intersection(pieces) instanceof MultiPolygon))) {
                return true;
            }
        }

        return false;
    }

}
