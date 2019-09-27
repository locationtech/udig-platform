/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 * (C) 2006, Axios Engineering S.L. (Axios)
 * (C) 2006, County Council of Gipuzkoa, Department of Environment and Planning
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package org.locationtech.udig.tools.geometry.split;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Location;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geomgraph.PlanarGraph;
import org.locationtech.jts.geomgraph.Position;

import org.locationtech.udig.tools.geometry.internal.util.GeometryList;

/**
 * A {@link PlanarGraph} that builds itself from a {@link Polygon} and a {@link LineString split
 * line}.
 * <p>
 * The resulting graph will have the following characteristics:
 * <ul>
 * <li>It will contain as many edges as lineStrings in the boundary of the intersection geometry
 * between the polygon and the splitting line string.
 * <li>All edges will be labeled {@link Location#BOUNDARY} at {@link Position#ON}</li>
 * <li>The edges from the polygon's exterior ring will be labeled {@link Location#EXTERIOR} at the
 * {@link Position#LEFT}, {@link Location#INTERIOR} at {@link Position#RIGHT}</li>
 * <li>The edges from the polygon's holes will be labeled {@link Location#INTERIOR} at the
 * {@link Position#LEFT}, {@link Location#EXTERIOR} at {@link Position#RIGHT}</li>
 * </ul>
 * <p>
 * Note the provided polygon may result modified as the result of {@link Polygon#normalize()}, which
 * is called in order to ensure proper orientation of the shell and holes.
 * </p>
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
class SplitGraphBuilder {

    private static final Logger LOGGER = Logger.getLogger(SplitGraphBuilder.class.getName());

    private final Polygon polygon;

    final private UsefulSplitLineBuilder usefulSplitLineBuilder;

    private Graph graph = new Graph();

    public String toString() {

        return this.usefulSplitLineBuilder.getClass().getName()
                + ":" + this.usefulSplitLineBuilder.toString(); //$NON-NLS-1$
    }

    /**
     * Constructor for split graph.
     * 
     * @param polygon The polygon to analyze.
     * @param splitter The split line.
     */
    public SplitGraphBuilder(final Polygon polygon, final UsefulSplitLineBuilder splitLine) {

        this.polygon = polygon;

        LOGGER.fine("Input polygon: " + polygon.toText()); //$NON-NLS-1$
        LOGGER.fine("Input split line: " + splitLine.getOriginalSplitLine().toText()); //$NON-NLS-1$

        this.usefulSplitLineBuilder = splitLine;
    }

    /**
     * Build the graph using the given polygon and the split line.
     * 
     * @return this builder
     */
    public SplitGraphBuilder build() {

        // after normalize() we know the shell is oriented CW and the holes CCW
        this.polygon.normalize();

        this.usefulSplitLineBuilder.build(this.polygon);
        Geometry utilSplitLine = this.usefulSplitLineBuilder.getResultSplitLine();
        AdaptedPolygon adaptedPolygon = this.usefulSplitLineBuilder.getAdaptedPolygon();

        LOGGER.fine("Adapted Polygon: " + adaptedPolygon.asPolygon().toText()); //$NON-NLS-1$
        LOGGER.fine("Util split line: " + utilSplitLine.toText()); //$NON-NLS-1$

        buildGraph(utilSplitLine, adaptedPolygon.asPolygon());

        return this;

    }

    /**
     * <pre>
     * Build the graph using the given polygon and the split line.
     * &lt;code&gt;
     *                             
     *                  +----------o-----------+
     *                  |          |           |
     *                  |          |           |
     *                  |    +-----------+     |
     *                  |    |     |     |     |
     *                  |    |     |     |     |
     *                  |    |     |     |     |
     *                  |    o__\__o_____|     |
     *                  |       /  |           |
     *                 /|\        /|\          |
     *                  o__________o___________| 
     *                                        
     *                             
     * &lt;/code&gt;
     * </pre>
     * 
     * @param utilSplitLine the part of split line that can be used to split the polygon
     * @param polygon the polygon to be split
     * 
     */
    private void buildGraph(final Geometry utilSplitLine, final Polygon polygon) {

        List<Geometry> shellList = makeShellGeometryList(polygon, utilSplitLine);
        addShellInCW(shellList);

        Set<LinearRing> nonSplitRings = this.usefulSplitLineBuilder.getNonSplitRings();
        List<LineString> splitHoleList = makeValidSplitHoles(polygon, nonSplitRings);
        if (splitHoleList != null && !splitHoleList.isEmpty()) {
            List<Geometry> holesList = makeHoleGeometryList(splitHoleList, utilSplitLine);
            if (holesList != null && !holesList.isEmpty()) {
                addHolesInCCW(holesList);
            }
        }

        addSplitLineIntoGraph(utilSplitLine, polygon, splitHoleList);
    }

    /**
     * split intersection segments have interior location at both left and right
     * 
     * @param utilSplitLine
     * @param polygon
     * @param holesList
     */
    private void addSplitLineIntoGraph(final Geometry utilSplitLine, final Polygon polygon,
            List<LineString> holesList) {

        // split intersection segments have interior location at both left
        // and right
        Geometry intersectingLineStrings = utilSplitLine.intersection(polygon);

        if (intersectingLineStrings.getNumGeometries() > 1) {
            // If points exist, then remove them.
            intersectingLineStrings = filterLineString(intersectingLineStrings);
        }

        // use the same input used to create hole edges
        Geometry holeCollection = intersectingLineStrings.getFactory().createMultiLineString(
                holesList.toArray(new LineString[holesList.size()]));
        Geometry holeGeometries = holeCollection.difference(utilSplitLine);
        insertEdge(intersectingLineStrings, holeGeometries, Location.BOUNDARY, Location.INTERIOR,
                Location.INTERIOR);
    }

    /**
     * Only return the lines contained on the given geometry, the non lines geometry are rejected.
     * 
     * @param geometry Intersection geometry between split line and source geometry.
     * @return The valid geometries needed for the graph, those are lines and multiLines.
     */
    private Geometry filterLineString(Geometry geometry) {

        List<Geometry> filteredLines = new ArrayList<Geometry>();
        for (int i = 0; i < geometry.getNumGeometries(); i++) {

            Geometry possibleLine = geometry.getGeometryN(i);

            // if there are point geometries, discard it.
            if (possibleLine instanceof LineString || possibleLine instanceof MultiLineString) {

                // also remove very very short liens.
                if (possibleLine.getLength() > UsefulSplitLineBuilder.DEPRECIATE_VALUE) {

                    filteredLines.add(possibleLine);
                }
            }
        }
        GeometryFactory gf = geometry.getFactory();

        return gf.buildGeometry(filteredLines);
    }

    /**
     * hole segments oriented CCW means interior at the left, exterior at the right
     * 
     * @param holesList
     */
    private void addHolesInCCW(List<Geometry> holesList) {
        this.graph.addEdges(holesList, Location.BOUNDARY, Location.INTERIOR, Location.EXTERIOR);

    }

    /**
     * shell segments oriented CW means exterior at the left, interior at the right;
     * 
     * @param shellList
     */
    private void addShellInCW(List<Geometry> shellList) {
        this.graph.addEdges(shellList, Location.BOUNDARY, Location.EXTERIOR, Location.INTERIOR);
    }

    /**
     * Makes a List using the polygon shell (hull) and the lineString that are common to polygon
     * shell and intersection edges.
     * 
     * @param polygon
     * @param utilSplitLine
     * @return a list with the shell and the common edges
     */
    private List<Geometry> makeShellGeometryList(final Polygon polygon, final Geometry utilSplitLine) {

        Geometry shellDiff = polygon.getExteriorRing().difference(utilSplitLine);

        // Geometries that belong to shell
        List<Geometry> geometriesBelongShell = new ArrayList<Geometry>();
        geometriesBelongShell.add(shellDiff);

        // add the lineString that is common with the polygon shell and
        // intersection
        // edges.
        Geometry intersectResult = polygon.getExteriorRing().intersection(utilSplitLine);

        geometriesBelongShell = addLinesInCommon(intersectResult, geometriesBelongShell);

        return geometriesBelongShell;
    }

    /**
     * Adds the geometries present in the intersection result if they are {@link LineString} into
     * the geometry list
     * 
     * @param intersectResult
     * @param geometryList
     * 
     * @return the geometry list updated with the geometries intersection
     */
    private List<Geometry> addLinesInCommon(Geometry intersectResult, List<Geometry> geometryList) {

        if (!intersectResult.isEmpty()) {
            if (intersectResult instanceof GeometryCollection) {
                // get the lineString or multiLineString instances
                for (int i = 0; i < intersectResult.getNumGeometries(); i++) {

                    Geometry part = intersectResult.getGeometryN(i);
                    if (part instanceof LineString || part instanceof MultiLineString) {
                        geometryList.add(part);
                    }
                }
            } else if (intersectResult instanceof LineString
                    || intersectResult instanceof MultiLineString) {
                geometryList.add(intersectResult);
            }
        }
        return geometryList;
    }

    /**
     * Makes a list of polygon holes that intersect with the split line.
     * 
     * @param polygonHolesArray polygon holes
     * @param utilSplitLine Line or MultiLine
     * @return a List of holes
     */
    private List<Geometry> makeHoleGeometryList(final List<LineString> polygonHolesArray,
            final Geometry utilSplitLine) {

        GeometryFactory factory = this.polygon.getFactory();
        Geometry holeCollection = factory.createMultiLineString(polygonHolesArray
                .toArray(new LineString[polygonHolesArray.size()]));
        Geometry nodedHoles = holeCollection.difference(utilSplitLine);
        List<Geometry> geometriesBelongHole = new ArrayList<Geometry>();
        geometriesBelongHole.add(nodedHoles);

        // add the lineString that are common to hole and intersection edges.
        Geometry intersectionResult = holeCollection.intersection(utilSplitLine);
        geometriesBelongHole = addLinesInCommon(intersectionResult, geometriesBelongHole);

        return geometriesBelongHole;
    }

    /**
     * Make a list of holes involved in the split operation
     * 
     * @param polygon
     * @param nonSplitRings
     * @return the holes involved in the split operation
     */
    private List<LineString> makeValidSplitHoles(final Polygon polygon,
            Set<LinearRing> nonSplitRings) {

        List<LineString> holesArray = new GeometryList<LineString>();

        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {

            LineString hole = polygon.getInteriorRingN(i);
            // if the hole isn't one of the non-split ring, add them because
            // this hole will suffer split.
            if (!nonSplitRings.contains(hole)) {
                holesArray.add(hole);
            }
        }
        return holesArray;
    }

    /**
     * Each edge will be built with 2 coordinates.
     * 
     * @param intersectingLineStrings The geometry which edges will be based on.
     * @param onLoc position for ON.
     * @param leftLoc position for LEFT.
     * @param rightLoc position for RIGHT.
     */
    private void insertEdge(final Geometry intersectingLineStrings, final Geometry holeGeometries,
            final int onLoc, final int leftLoc, final int rightLoc) {

        for (int i = 0; i < intersectingLineStrings.getNumGeometries(); i++) {

            Geometry intersectingSegment = intersectingLineStrings.getGeometryN(i);

            if ((intersectingSegment.getNumPoints() == 2) && !holeGeometries.isEmpty()) {
                // special case, when the line has 2 coordinates and
                // its orientation can't be calculated because it hasn't.
                intersectingSegment = adjustSegmentToHoleDirection(intersectingSegment,
                        holeGeometries);
            }

            Coordinate[] coords = intersectingSegment.getCoordinates();
            for (int j = 0; j < coords.length - 1; j++) {

                final SplitEdge edge = SplitEdge.newInstance(coords[j], coords[j + 1], onLoc,
                        leftLoc, rightLoc);

                // add the list that only contains one edge because it will
                // create 2 directedEdge.
                this.graph.addEdge(edge);
            }
        }
    }

    /**
     * Checks if the intersecting segment intersects with a hole in two points. In that case, the
     * segment might be adjusted following the orientation of the intersected hole.
     * 
     * @param intersectingSegment this segment that could intersect with a hole
     * @param holeGeometries the polygon hole list
     */
    private LineString adjustSegmentToHoleDirection(final Geometry intersectingSegment,
            final Geometry holeGeometries) {

        LineString intersectedHole = intersectionHole(intersectingSegment, holeGeometries);

        if (intersectedHole == null) {
            return (LineString) intersectingSegment;// it does not require
            // adjust orientation
        }

        // Traverses the hole-segments until the second intersection with the
        // intersectingSegment is found
        // a ring will be created with those segments between first intersection
        // and second intersection.
        Coordinate secondIntersection = null;
        Coordinate firstIntersection = null;
        int j = -1;
        Coordinate[] holeCoords = intersectedHole.getCoordinates();
        List<Coordinate> ring = new LinkedList<Coordinate>();
        for (int i = 0; i < holeCoords.length - 1; i++) {

            Geometry intersection = intersectionWithSegment(holeCoords, i, intersectingSegment);
            if (intersection instanceof Point) {

                // store first and second coordinates
                if (firstIntersection == null) {

                    firstIntersection = intersection.getCoordinate();
                    ring.add(firstIntersection);
                    ring.add(holeCoords[i + 1]);
                    j = i + 1;
                    break;
                }
                // Adds the rest of segments in the ring until found a second
                // intersection
            }
        }
        assert firstIntersection != null && j != -1;

        while (true) {
            Geometry intersection = intersectionWithSegment(holeCoords, j, intersectingSegment);

            if (intersection instanceof Point
                    && !intersection.getCoordinate().equals2D(firstIntersection)) {
                secondIntersection = intersection.getCoordinate();
                ring.add(secondIntersection);
                // close the ring
                ring.add(firstIntersection);
                break;

            } else {
                ring.add(holeCoords[j + 1]);
            }
            j++;
        }

        assert secondIntersection != null;

        // Creates the adjusted line following this rules:
        // - if the ring is CW then the result line must be this: first
        // intersection coordinate--> second intersection coordinate.
        // - if the ring is CCW the the result line must be this: second
        // intersection coordinate --> first intersection coordinate.
        GeometryFactory factory = intersectingSegment.getFactory();
        LinearRing linearRing = factory.createLinearRing(ring.toArray(new Coordinate[ring.size()]));

        LineString adjustedSegment = null;
        if (isCW(linearRing)) {

            adjustedSegment = createAdjustedSegment(firstIntersection, secondIntersection, factory);

        } else {

            adjustedSegment = createAdjustedSegment(secondIntersection, firstIntersection, factory);

        }
        return adjustedSegment;
    }

    /**
     * Finds the hole that intersects in two points with the the segment.
     * 
     * @param splitLineSegment
     * @param holeGeometries
     * 
     * @return the hole that intersect with the segment, null in other case
     */
    private LineString intersectionHole(final Geometry splitLineSegment,
            final Geometry holeGeometries) {

        List<LineString> holeList = convertHolesGeometriesToHoleList(holeGeometries);

        LineString intersectedHole = null;

        for (LineString hole : holeList) {
            // Seeks if the segment intersect with the line
            Geometry intersectionWithHole = splitLineSegment.intersection(hole);
            if (intersectionWithHole.getNumGeometries() == 2) {
                intersectedHole = hole;
            }
        }

        return intersectedHole;
    }

    /**
     * converts the collection of holes (LineString) to a List of String
     * 
     * @param holeGeometries
     * @return List of holes as LineString
     */
    private List<LineString> convertHolesGeometriesToHoleList(final Geometry holeGeometries) {

        assert holeGeometries != null;

        List<LineString> holeList = new ArrayList<LineString>(holeGeometries.getNumGeometries());

        for (int i = 0; i < holeGeometries.getNumGeometries(); i++) {

            LineString hole = (LineString) holeGeometries.getGeometryN(i);
            holeList.add(hole);
        }
        return holeList;
    }

    private LineString createAdjustedSegment(final Coordinate firstIntersection,
            final Coordinate secondIntersection, final GeometryFactory factory) {

        Coordinate[] adjustedSegmentCoords = new Coordinate[] { firstIntersection,
                secondIntersection };

        return factory.createLineString(adjustedSegmentCoords);
    }

    /**
     * Check if it's CW.
     * 
     * @param linearRing
     * @return true if the ring has a clock wise orientation
     */
    private boolean isCW(final LinearRing linearRing) {

        Coordinate[] ringCoord = linearRing.getCoordinates();

        return !CGAlgorithms.isCCW(ringCoord);
    }

    private Geometry intersectionWithSegment(Coordinate[] holeCoords, int i,
            Geometry intersectingSegment) {

        Coordinate[] holeSegmentCoord = new Coordinate[] { holeCoords[i], holeCoords[i + 1] };

        LineString holeSegment;
        GeometryFactory geomFact = intersectingSegment.getFactory();
        holeSegment = geomFact.createLineString(holeSegmentCoord);
        Geometry intersection = holeSegment.intersection(intersectingSegment);

        return intersection;
    }

    /**
     * The set of rings that have not suffered split
     * 
     * @return a list of rings
     */
    public Set<LinearRing> getNonSplitRings() {
        return this.usefulSplitLineBuilder.getNonSplitRings();
    }

    /**
     * The resultant of {@link #build()} method.
     * 
     * @return The built graph
     */
    public Graph getResultantGraph() {
        return this.graph;
    }

}
