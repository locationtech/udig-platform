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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Polygon;

import org.locationtech.udig.tools.geometry.internal.util.GeometryList;
import org.locationtech.udig.tools.geometry.internal.util.GeometrySet;

/**
 * <p>
 * Builds the useful split line. The useful split line is composed of split line fragments that intersect with the
 * polygon to split. Thus, the result of build will be presented as a {@link MultiLineString}, where each line intersect with
 * the polygon.
 * </p>
 * <p>
 * As subproduct, the build process provides the set of "Non Split Rings" and the {@link AdaptedPolygon}.
 * Non split rings are that rings which are not affected by the split line. The {@link AdaptedPolygon}
 * contains those additional vertexes resultant of the intersection of the polygon's boundaries with 
 * the split line. 
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
class UsefulSplitLineBuilder {
    
    
    /** Lines with less length than this, will be depreciated. */
    public static final double         DEPRECIATE_VALUE    = 1.0E-4;
    
    /** The position of the initial point respect the polygon. */
    private static final int            OUTSIDE             = 1;
    private static final int            INSIDE              = -1;
    private static final int            NONE                = 0;

    /** preserves the original split line */
    private final LineString originalSplitLine;
    
    /** Store the lineStrings that fully intersect with the polygons and will form new features. */
    private List<Geometry>              usefulSplitLineSegments        = new GeometryList<Geometry>();
    

    /** The polygon with the start intersection coordinates added on it. */
    private AdaptedPolygon                      adaptedPolygon = null;

    private final GeometryFactory       geomFactory;
    
    /**
     * Will store the rings that won't be modified by the split operation.
     */
    private Set<LinearRing>             nonSplitRings       = new GeometrySet<LinearRing>();

    /** Maintin the final result, this is the useful split line. */ 
    private Geometry                    resultSplitLine = null;


    /**
     * @return rings that won't be modified by the split operation.
     */
    public Set<LinearRing> getNonSplitRings() {
        return this.nonSplitRings;
    }

    /**
     * @return the original split line
     */
    public LineString getOriginalSplitLine() {
        return this.originalSplitLine;
    }

    /**
     * @return the useful split line
     */
    public Geometry getResultSplitLine() {
        return resultSplitLine;
    }

    /**
     * Assure the first coordinate lies outside the polygon.
     * 
     * @param modifiedCoords
     *            The split line coordinates.
     * @param extBoundary
     *            The exterior of the polygon.
     * @return An array of coordinates which first coordinate start inside the
     *         ring.
     */
    private Coordinate[] beginOutside(Coordinate[] modifiedCoords,  Geometry extBoundary) {

        // get the first position inside the are of the ring.
        Geometry polygonArea = this.geomFactory.createPolygon((LinearRing) extBoundary, null);

        int outsidePos = -1;

        // find the first point that lie inside the area.
        for (int a = 0; a < modifiedCoords.length; a++) {

            Coordinate point = modifiedCoords[a];

            if (!polygonArea.contains(this.geomFactory.createPoint(point))) {
                // thats the first point.
                // from that coordinate seek if there are 2
                // intersections with the interior boundary.
                outsidePos = a;
                break;
            }
        }
        // get the first intersection.
        IntersectionStrategy interStrategy = new IntersectionStrategy();
        interStrategy.findFirstIntersection(modifiedCoords, extBoundary);
        Coordinate firstIntersectionCoord = interStrategy.getIntersection();
        int firstIntersectionPosition = interStrategy.getIntersectionPosition();

        // if there isn't any coordinate outside, and there isn't any
        // intersection with the exterior boundary, return.
        if (firstIntersectionCoord == null || outsidePos == -1 && ! interStrategy.foundIntersection()) {
            return modifiedCoords;
        }
        int posFirstCoord;
        // calculate which one go first, and start from that coordinate.
        if (outsidePos > firstIntersectionPosition || outsidePos == -1) {
            // this coordinate must be eliminated from the array.
            modifiedCoords[firstIntersectionPosition] = firstIntersectionCoord;
            posFirstCoord = firstIntersectionPosition;
        } else {
            posFirstCoord = outsidePos;
        }

        modifiedCoords = SplitUtil.createCoordinateArrayWithoutDuplicated(posFirstCoord, modifiedCoords.length - 1, modifiedCoords);

        return modifiedCoords;
    }
    
    /**
     * @see {{@link #newInstance()}
     */
    private UsefulSplitLineBuilder(final LineString splitLine){
        this.originalSplitLine = splitLine;
        this.geomFactory = splitLine.getFactory();
    }
    
    /**
     * 
     * @return New instance of {@link UsefulSplitLineBuilder}
     */
    public static final UsefulSplitLineBuilder newInstance(final LineString splitLine){
        
        UsefulSplitLineBuilder splitWrapper = new UsefulSplitLineBuilder(splitLine);
        
        return splitWrapper;
    }
    

    /**
     * This method will create a new lineString. This lineString will be the
     * useful part to be used when creating the graph.
     * 
     * @param polygon
     * @return The useful part of the lineString, that means, the part of the
     *         line that intersects with the feature and its interior. Null if there
     *         is not intersection.
     */
    public void build(final Polygon polygon) {

        this.adaptedPolygon = new AdaptedPolygon(polygon);
        Geometry usefulSplitLines = createUsefulSplitLine(this.originalSplitLine, this.adaptedPolygon);
        assert usefulSplitLines != null:  "must be at least one split line."; //$NON-NLS-1$

        this.resultSplitLine  = null;
        if (usefulSplitLines instanceof LineString) {

            List<Geometry> ccwLines = new ArrayList<Geometry>();

            for (int i = 0; i < usefulSplitLines.getNumGeometries(); i++) {

                // get the line and check if it is CCW oriented, if not, try to
                // orient it. If its colinear should be added without the isCCW
                // analysis.

                Geometry actual = usefulSplitLines.getGeometryN(i);
                Coordinate[] lineCoord = actual.getCoordinates();

                if (lineCoord.length > 2 && !SplitUtil.isColinear(lineCoord)) {

                    // close the line to obtain a ring and check the CCW.
                    Coordinate[] closed = RingUtil.builRing(lineCoord);
                    if (!CGAlgorithms.isCCW(closed)) {

                        actual = reverseLineString(closed);

                        assert CGAlgorithms.isCCW(actual.getCoordinates()) : "It should be CCW. Actual SplitLine: " + actual + ". Geometry to split: " + polygon; //$NON-NLS-1$ //$NON-NLS-2$

                        actual = reverseLineString(lineCoord);
                    }
                }

                ccwLines.add(actual);
            }
            this.resultSplitLine = usefulSplitLines.getFactory().buildGeometry(ccwLines);
        } else {
            // merge the pieces of lines if its possible.
            this.resultSplitLine = SplitUtil.buildLineUnion(usefulSplitLines);
        }
    }

    /**
     * Reverse the given coordinates, and return the resultant lineString.
     * 
     * @param intersectionCoordinates
     *            Coordinates from a lineString.
     * @param geomFactory
     *            Geometry factory.
     * @return The geometry built.
     */
    private Geometry reverseLineString(Coordinate[] intersectionCoordinates) {

        intersectionCoordinates = CoordinateArrays.copyDeep(intersectionCoordinates);
        CoordinateArrays.reverse(intersectionCoordinates);

        return geomFactory.createLineString(intersectionCoordinates);
    }
    

    /**
     * <pre>
     * First step:
     * With the first step, the line is reduced, discarding the points 
     * that won't do anything.
     * 
     * -Seek the first coordinate outside the boundary. 
     * -Seek the first intersection with the boundary. 
     * -If the first intersection is before the first coordinate outside the 
     * line, that coordinate will be the beginning of the line.
     * 
     * Second step:
     * 
     * Finding out where the first coordinate lies.
     * 
     * -If it lies in the exterior of the polygon, go through the line finding
     * 2 intersections with the exterior boundary, get the lineString that is formed between those intersections
     * and reduce the line. Do this until it doesn't found 2 intersections.
     * After that, with the remaining line do the same but intersection with the interior.
     * 
     * -If it lies inside a ring, first, sort the rings in the order the split line 
     * is intersection them, then go through the line finding
     * 2 intersections with the interior boundary, get the lineString that is formed between those intersections
     * and reduce the line. Do this until it doesn't found 2 intersections.
     * After that, with the remaining line do the same but intersection with the exterior.
     * </pre>
     * 
     * @param splitLine The lineString that would be checked.
     * @param adaptedPolygon (i/o) The polygon for checking.
     * 
     * @return The collection of pieces of split line. Null if there is not an useful split line part
     */
    public Geometry createUsefulSplitLine( final LineString splitLine, final AdaptedPolygon adaptedPolygon) {
   
        // does a defensive copy of coordinate
        final Coordinate[] clonedSplitLineCoord = cloneCoordinate(splitLine.getCoordinates());

        // The line is reduced, discarding the points that won't split the polygon.
        Coordinate[] adaptedSplitLine = discardSplitLineSegmentAdaptingPolygon(clonedSplitLineCoord, splitLine , adaptedPolygon);
        
        UsefulSplitLine useful = new UsefulSplitLine(this.usefulSplitLineSegments, adaptedSplitLine);

        // finds where the first coordinate lies. It could be outside the
        // feature, or if it has rings, it could lie inside a ring. 
        int location = whereIsFirstSplitLineCoord(adaptedSplitLine, adaptedPolygon);

        if (location == OUTSIDE) {
            // start from the outside of the polygon and go through the line.
            useful = makeUsefulSplitLineFromOutsidePolygon(adaptedPolygon, useful);
            
            // once it done, if still are coordinates for going through
            // them, check if there are some holes intersection.
            if (useful.getRemaininSplitLine().length > 0 && adaptedPolygon.asPolygon().getNumInteriorRing() > 0) {

                useful =makeUsefulSplitLineFromInsidePolygon(adaptedPolygon, useful);
            }
            this.usefulSplitLineSegments = useful.getUsefulSplitLineFragments();
            
            this.nonSplitRings = extractNonSplitHolesFromPolygon(adaptedPolygon, this.usefulSplitLineSegments, this.nonSplitRings );
            
        } else {

            // start from the inside of one of the rings.
            useful = makeUsefulSplitLineFromInsidePolygon(adaptedPolygon, useful);
            this.usefulSplitLineSegments = useful.getUsefulSplitLineFragments();
            adaptedSplitLine = useful.getRemaininSplitLine();
            if (adaptedSplitLine.length > 1) {
                useful = makeUsefulSplitLineFromOutsidePolygon(adaptedPolygon, useful);
                this.usefulSplitLineSegments = useful.getUsefulSplitLineFragments();
            }
        }
        if( this.usefulSplitLineSegments.size() > 0 ){
            return geomFactory.buildGeometry(this.usefulSplitLineSegments);
        } else {
            return null;
        }
    }


    /**
     * Will get a ring, and check if that ring is intersected with any of the
     * lines. If it doesn't intersect, this ring will be a non-split ring.
     * 
     * @param adapted
     *            The original polygon.
     * @param splitLineSegments
     *            Geometry factory.
     * @param nonSplitRings 
     */
    private Set<LinearRing> extractNonSplitHolesFromPolygon(
            final AdaptedPolygon adapted, 
            final List<Geometry> splitLineSegments, 
            Set<LinearRing> nonSplitRings) {

        Polygon polygon = adapted.asPolygon();
        // get the hole rings, and check if any ring is never intersected by those
        // lines.
        for (int j = 0; j < polygon.getNumInteriorRing(); j++) {

            LinearRing holeRing = (LinearRing) polygon.getInteriorRingN(j);
            // create a polygonRing with the purpose off testing touch between
            // polygon-line.
            Polygon polygonRing = this.geomFactory.createPolygon((LinearRing) holeRing, null);
            boolean intersects = false;
            // check this ring with all the lines finding if they intersect.
            for (int i = 0; i < splitLineSegments.size(); i++) {

                Geometry eachLine = splitLineSegments.get(i);
                // the intersection between the line and the boundary
                // must be a point or multiPoint, and not a LineString
                Geometry intersection = eachLine.intersection(holeRing);
                if (eachLine.intersects(holeRing)
                            && !(intersection instanceof LineString || intersection instanceof MultiLineString)
                            && !eachLine.touches(polygonRing)) {

                    intersects = true;
                    break;
                }
            }
            // if this ring isn't intersected by any line, add to the nonSplit
            // lines.
            if (!intersects) {
                nonSplitRings.add(holeRing);
            }
        }
        return nonSplitRings;
    }

    
    /**
     * <pre>
     * 
     * Sort the interior rings. The first ring that intersects with the split
     * line and is closest to the beginning of the split line, that will be the
     * first ring.
     * 
     * Go through the line, intersecting it with the interior rings of the
     * polygon. Start inside a ring, find 2 intersection, and the
     * coordinates between that 2 intersections will form a piece of line. Do
     * this until the line hasn't more coordinates, or there aren't 2
     * intersections or more.
     * 
     * The rings that aren't modified by the split line, will be added 
     * to the non-split rings list.
     * 
     * </pre>
     * 
     * @param adapt
     *            (i/o) The polygon feature.
     * @param usefulSplitLine
     *            (i/o) useful Split line.
     * @return The list of useful split line.
     */
    private UsefulSplitLine makeUsefulSplitLineFromInsidePolygon(AdaptedPolygon adapt, UsefulSplitLine usefulSplitLine) {
        
        // go through the line finding intersections with rings and sorting
        Polygon polygon = adapt.asPolygon();
        Coordinate[] splitLineCoords = usefulSplitLine.getRemaininSplitLine();
        Set<LinearRing> sortedRings = sortRing(splitLineCoords, polygon);

        // now that rings are ordered, start filtering the line. If a piece
        // of line with a ring will form a valid new geometry, separate that
        // piece of line.

        for (LinearRing hole : sortedRings) {

            if (splitLineCoords.length > 1) {
                // the coordinates will begin inside the ring.
                splitLineCoords = beginInsideRing(splitLineCoords,  hole);
                // a valid hole must be intersected by the line twice.
                // find interior intersections.

                int numIntIntersection = SplitUtil.countIntersectionsFromPosition(0, splitLineCoords, hole);

                if (numIntIntersection >= 2) {

                    usefulSplitLine = extractUsefulSplitLine(hole, usefulSplitLine, adapt);
                    splitLineCoords = usefulSplitLine.getRemaininSplitLine();
                    
                } else {
                    // not valid ring. Add to the list.
                    this.nonSplitRings.add(hole);
                }
            } else {
                this.nonSplitRings.add(hole);
            }
        }
        
        // There are some rings that had been added to nonSplitRings
        // because the line was entirely covered, but those rings, intersect
        // with the line and the haven't got a chance of been tested.

        // Check if they intersect with any of the rings from the nonSplitRings.
        List<Geometry> usefulSplitLineSegments = usefulSplitLine.getUsefulSplitLineFragments();
        List<LinearRing> correctSplitRings = new ArrayList<LinearRing>();
        for (Geometry piece : usefulSplitLineSegments) {
            for (LinearRing hole : this.nonSplitRings) {
                // create a polygonRing with the purpose off testing touch
                // between polygon-line.
                Polygon polygonRing = geomFactory.createPolygon((LinearRing) hole, null);
                if (piece.intersects(hole) && !piece.touches(polygonRing)) {
                    // remove that hole from the list, it is OK.
                    correctSplitRings.add(hole);
                }
            }
        }
        this.nonSplitRings.removeAll((Collection< ? >) correctSplitRings);
        
        usefulSplitLine.setRemaininSplitLine(splitLineCoords);
        
        usefulSplitLine.setUsefulSplitLineFragments(usefulSplitLineSegments);
        
        return usefulSplitLine;
    }


    /**
     * <pre>
     * Sort the interior rings.
     * 
     * The first ring will be the one that intersects first and is nearest 
     * to the beginning of the split line, that in this case is the shortCoords[0].
     * 
     * </pre>
     * 
     * @param shortCoords
     * @param polygon
     * @return The interior rings sorted.
     */
    private Set<LinearRing> sortRing(Coordinate[] shortCoords,  Polygon polygon) {

        Set<LinearRing> sortedRings = new LinkedHashSet<LinearRing>();

        for (int i = 0; i < shortCoords.length - 1; i++) {

            Coordinate[] lineSegment = new Coordinate[2];
            lineSegment[0] = shortCoords[i];
            lineSegment[1] = shortCoords[i + 1];
            LineString segment = this.geomFactory.createLineString(lineSegment);

            // intersects this segment with any of the rings, if intersects,
            // thats ring goes first.
            List<LinearRing> rings = SplitUtil.addRingThatIntersects(segment, polygon);

            // go through the rings, and for each one, get the intersection, and
            // calculate which one is the nearest respect the beginning of the
            // segment.
            Map<LinearRing, Double> ringsDistance = RingUtil.setRingWithClosestIntersectionDistance(rings, segment, lineSegment);

            sortedRings = addClosestRings(sortedRings, ringsDistance);

        }

        return sortedRings;
    }
    
    /**
     * <pre>
     * Recursive function.
     * Go through ringsDistance map getting each ring-distance and calculating
     * which is the lowest distance. At the end, add the closest ring to the sortedRing set, 
     * remove this ring from the map, and call again the function, until the map is empty.
     * 
     * 
     * </pre>
     * 
     * @param sortedRings
     *            Set of ring sorted.
     * @param ringsDistance
     *            Map with rings linked with closest distance.
     * @return Set of rings, ordered depending the given distance.
     */
    private Set<LinearRing> addClosestRings(Set<LinearRing> sortedRings, Map<LinearRing, Double> ringsDistance) {

        if (ringsDistance.size() > 0) {
            // calculate which one has the closest distance
            Set<Entry<LinearRing, Double>> entrySet = ringsDistance.entrySet();
            Iterator<Entry<LinearRing, Double>> iterator = entrySet.iterator();

            double minDistance = Double.MAX_VALUE;
            LinearRing candidateRing = null;

            while (iterator.hasNext()) {

                Entry<LinearRing, Double> eachEntry = iterator.next();
                Double distance = eachEntry.getValue();

                if (Math.abs(distance) < minDistance) {
                    minDistance = Math.abs(distance);
                    candidateRing = eachEntry.getKey();
                }
            }
            sortedRings.add( candidateRing);
            ringsDistance.remove(candidateRing);

            sortedRings = addClosestRings(sortedRings, ringsDistance);
        }

        return sortedRings;
    }
        
    /**
     * Assures the first coordinate lies inside any of the polygon rings.
     * 
     * @param modifiedCoords
     *            The split line coordinates.
     * @param gf
     *            Geometry factory.
     * @param ring
     *            An interior ring.
     * @return An array of coordinates which first coordinate start inside the
     *         ring.
     */
    private Coordinate[] beginInsideRing(Coordinate[] modifiedCoords,  Geometry ring) {

        // get the first position inside the are of the ring.
        Polygon ringArea = this.geomFactory.createPolygon((LinearRing) ring, null);
        int outsidePos = -1;
        // find the first point that lie inside the area or on the boundary.
        for (int a = 0; a < modifiedCoords.length; a++) {

            Coordinate point = modifiedCoords[a];

            if (ringArea.contains(this.geomFactory.createPoint(point)) || ringArea.getExteriorRing().contains(geomFactory.createPoint(point))) {
                // thats the first point.
                // from that coordinate seek if there are 2
                // intersections with the interior boundary.
                outsidePos = a;
                break;
            }
        }
        // get the first intersection.
        IntersectionStrategy interStrategy = new IntersectionStrategy();
        interStrategy.findFirstIntersection(modifiedCoords, ring);
        Coordinate firstIntersectionCoord = interStrategy.getIntersection();
        int firstIntersectionPosition = interStrategy.getIntersectionPosition();
        
        // if there isn't any coordinate inside, and there isn't any
        // intersection with this interior ring, return.
        if (outsidePos == -1 && !interStrategy.foundIntersection()) {
            return modifiedCoords;
        }

        int posFirstCoord;
        // calculate which one go first, and start from that coordinate.
        if (outsidePos > firstIntersectionPosition || outsidePos == -1) {
            // this coordinate must be eliminated from the array.
            modifiedCoords[firstIntersectionPosition] = firstIntersectionCoord;
            posFirstCoord = firstIntersectionPosition;
        } else {
            posFirstCoord = outsidePos;
        }

        modifiedCoords = SplitUtil.createCoordinateArrayWithoutDuplicated(posFirstCoord, modifiedCoords.length - 1, modifiedCoords);
        return modifiedCoords;
    }
    
        


    /**
     * <pre>
     * 
     * Go through the line, intersecting it with the exterior of the polygon.
     * Start outside the polygon, find 2 intersection, and the coordinates
     * between that 2 intersections will form a piece of line. Do this until the
     * line hasn't more coordinates, or there aren't 2 intersections or more.
     * Finally, the remaining coordinates of the split line are returned.
     * 
     * </pre>
     * 
     * @param adapted
     *             (i/o)The polygon feature.
     * @param remainingCoords
     *            (i/o) The split line coordinates.
     *            
     * @return The remaining coordinates of the split line.
     */
    private UsefulSplitLine makeUsefulSplitLineFromOutsidePolygon( 
            AdaptedPolygon  adapted,
            UsefulSplitLine useful) {

        Coordinate[] remainingCoords = useful.getRemaininSplitLine();
        
        Polygon polygon = adapted.asPolygon();
        LineString extBoundary = polygon.getExteriorRing();
        // start from the outside of the geometry.
        remainingCoords = beginOutside(remainingCoords,  extBoundary);
        int numIntIntersection = SplitUtil.countIntersectionsFromPosition(0, remainingCoords,  extBoundary);
        if (numIntIntersection >= 2) {
            useful.setRemaininSplitLine(remainingCoords);
            useful = extractUsefulSplitLine((LinearRing) extBoundary, useful, adapted);
        }

        return useful;
    }
       
    
    /**
     * Discards those segments of the line which won't affect the polygon.
     * 
     * @param pieceOfBoundary           Boundary of the polygon. It could be the exterior ring or internal ring
     * @param usefulSplitLine             
     * @param adaptedPolygon            (i/o) additional vertex are added as result of this method. 
     * 
     * 
     * @return The list of useful split line.
     */
    private UsefulSplitLine  extractUsefulSplitLine( 
            final LinearRing pieceOfBoundary,
            final UsefulSplitLine usefulSplitLine,
            AdaptedPolygon adaptedPolygon) {

        Coordinate[] remainingSplitLine = usefulSplitLine.getRemaininSplitLine();
        List<Geometry> usefulSplitLineFragmentList = usefulSplitLine.getUsefulSplitLineFragments();
        
        // does a defensive copy of adapted polygon. The adapted polygon will be modified only if 
        // the piece of boundary intersect with a fragment of useful split line (that intersect with the polygon).
        AdaptedPolygon  clonedPolygon = (AdaptedPolygon) adaptedPolygon.clone();
        Polygon polygon = clonedPolygon.asPolygon();
        
        // Search the pieces of split line that are between the first and second intersection.
        // If that piece of split line intersect with the polygon it is an useful line part.
        int secondSegmentPosition = 0; 
        Coordinate secondIntersection= null;

        LineBoundaryIntersectionAssociation interSegmentList = new LineBoundaryIntersectionAssociation(remainingSplitLine.clone(), pieceOfBoundary);
        while(  ((interSegmentList.countIntersections() - interSegmentList.getVisitedIntersection() ) >= 2) ){
            
            // search the next two intersections.
            interSegmentList.moveNextIntersection();
            int firstSegmentPosition = interSegmentList.getIntersectionSegmentPosition();
            final Coordinate firstIntersection = interSegmentList.getIntersection();
            final LineString firstRingSegment = interSegmentList.getRingSegment();

            interSegmentList.moveNextIntersection();
            secondSegmentPosition =  interSegmentList.getIntersectionSegmentPosition(); 
            secondIntersection = interSegmentList.getIntersection();
            final LineString secondRingSegment = interSegmentList.getRingSegment();

            // adapt the polygon inserting the intersection points
            clonedPolygon.insertVertex(pieceOfBoundary, firstRingSegment, firstIntersection );
            clonedPolygon.insertVertex(pieceOfBoundary, secondRingSegment, secondIntersection );
            polygon = clonedPolygon.asPolygon();

            // build the candidate split line fragment
            LineString candidateSplitLineFragment = interSegmentList.buildLineBetweenIntersectionPoints(firstSegmentPosition, firstIntersection, secondSegmentPosition, secondIntersection);

            // if the pieceCoords intersects with the polygon, then add in the extractedSegmentLines list
            if( segmentsIntersectsPolygon(candidateSplitLineFragment, polygon) ){ 
                    
                usefulSplitLineFragmentList.add(candidateSplitLineFragment);
                
                // update the resultant adapted polygon with those intersection point that below to the useful segment
                adaptedPolygon.insertVertex(pieceOfBoundary, firstRingSegment, firstIntersection );
                adaptedPolygon.insertVertex(pieceOfBoundary, secondRingSegment, secondIntersection );
            }
            // move to previous intersection in order to analyze the next
            interSegmentList.moveBackIntersection(); 
        } // end while
        // remove the processed split line fragment from remaining split line
        remainingSplitLine = removeProcessedSegment(interSegmentList, secondSegmentPosition, secondIntersection, remainingSplitLine);
        
        // set the resultant split line fragments and the rest of line
        usefulSplitLine.setUsefulSplitLineFragments(usefulSplitLineFragmentList);
        usefulSplitLine.setRemaininSplitLine(remainingSplitLine);
        
        return usefulSplitLine;
    }

    /**
     * Remove the part of split in the position indeed from the intersection point.
     * 
     * @param interSegmentList
     * @param segmentPosition
     * @param intersection
     * @param remainingSplitLineCoords
     * @return the remaining split line
     */
    private Coordinate[] removeProcessedSegment( 
            final LineBoundaryIntersectionAssociation   interSegmentList,
            final int                                   segmentPosition, 
            final Coordinate                            intersection, 
            Coordinate[]                                remainingSplitLineCoords ) {

        // remove the headers coordinates until the current segment (included) from the remaining split line
        LineString splitLineFragment = interSegmentList.getSplitLineSegment( segmentPosition );
        remainingSplitLineCoords = cutRemaininigSplitLine(splitLineFragment , remainingSplitLineCoords);
        if( remainingSplitLineCoords.length > 0 ){
            // the segment is reduced using the second intersection point 
            Coordinate[] reducedSplitLineFragment =  SplitUtil.replaceCoordinate(0, intersection, splitLineFragment.getCoordinates());
            reducedSplitLineFragment = SplitUtil.createCoordinateArrayWithoutDuplicated(0, reducedSplitLineFragment.length - 1 , reducedSplitLineFragment);
            remainingSplitLineCoords = SplitUtil.mergeCoordinate( reducedSplitLineFragment, remainingSplitLineCoords);
        }
        return remainingSplitLineCoords;
    }

    /**
     * Creates a new line, as a coordinate array, using the position of segment.
     * 
     * @param splitLineFragment
     * @param remainingSplitLine
     * 
     * @return a new coordinate array 
     */
    private Coordinate[] cutRemaininigSplitLine( 
            final LineString splitLineFragment,
            final Coordinate[] remainingSplitLine ) {
        
        // search the coordinates of the segment in the position indeed
        int lastCoord = splitLineFragment.getNumPoints()-1;
        int lastCoordPosition =  CoordinateArrays.indexOf(splitLineFragment.getCoordinateN(lastCoord), remainingSplitLine);

        // copy the coordinates which follow the last coordinate of split line fragment
        Coordinate[] newRemainingSplitLine; 
        
        if(lastCoordPosition < (remainingSplitLine.length - 1) ){
            // copy the rest of coordinates from the first coordinate of referenced segment.
            newRemainingSplitLine = SplitUtil.createCoordinateArrayWithoutDuplicated(lastCoordPosition, remainingSplitLine.length -1 , remainingSplitLine);
        } else {
            newRemainingSplitLine = new Coordinate[0];
        }
        return newRemainingSplitLine;
    }
    private boolean segmentsIntersectsPolygon(final  LineString usefulSegment, final Polygon polygon ) {
        
        Geometry intersection = polygon.intersection(usefulSegment);

        return SplitUtil.containsLineString(intersection);
    }


    /**
     * Checks where lies the first coordinate, it could be outside the polygon or
     * inside any of the ring. If the first doesn't lie in any of the described
     * places, check the next coordinate.
     * 
     * TODO refactoring: improve the legibility structuring this method.
     *  
     * @param shortCoords
     * @param adaptedPolygon
     * 
     * @return Where it lies, it could be, outside, inside, of none of them.
     */
    private int whereIsFirstSplitLineCoord(final Coordinate[] shortCoords, final AdaptedPolygon adaptedPolygon) {

        Polygon polygon = adaptedPolygon.asPolygon();
        
        Geometry polygonArea = this.geomFactory.createPolygon((LinearRing) polygon.getExteriorRing(), null);

        for (int i = 0; i < shortCoords.length - 2; i++) {

            if (!polygonArea.contains(this.geomFactory.createPoint(shortCoords[i]))) {

                // It lies outside the polygon.
                // Start with the exterior of the polygon.
                return OUTSIDE;
            }
            // check it lies inside any holes.
            for (int j = 0; j < polygon.getNumInteriorRing(); j++) {

                Geometry ringArea = this.geomFactory.createPolygon((LinearRing) polygon.getInteriorRingN(j), null);
                if (ringArea.contains(this.geomFactory.createPoint(shortCoords[i]))) {

                    // lies inside a ring.
                    return INSIDE;
                }
            }
            // create a line segment between i and i + 1.
            Coordinate[] segCoords = new Coordinate[2];
            segCoords[0] = shortCoords[i];
            segCoords[1] = shortCoords[i + 1];
            LineString segment = this.geomFactory.createLineString(segCoords);
            // calculate the distance with the exterior ring, if intersects.
            double minExtDistance = Double.MAX_VALUE;
            double distance;
            Geometry intersection = polygon.getExteriorRing().intersection(segment);
            if (!intersection.isEmpty()) {
                // find the closest one to shortCoords[i]
                for (int j = 0; j < intersection.getNumGeometries(); j++) {
    
                    Coordinate pointToTest = intersection.getGeometryN(j).getCoordinate();
    
                    distance = SplitUtil.calculateDistanceFromFirst(pointToTest, segCoords[0], segCoords[1]);
    
                    if (Math.abs(distance) < minExtDistance) {
                        minExtDistance = Math.abs(distance);
                    }
                }
            }

            double minRingDistance = Double.MAX_VALUE;

            for (int j = 0; j < polygon.getNumInteriorRing(); j++) {

                Geometry ring = polygon.getInteriorRingN(j);

                intersection = ring.intersection(segment);
                // find the closest one to shortCoords[i]
                for (int h = 0; h < intersection.getNumGeometries(); h++) {

                    Coordinate pointToTest = intersection.getGeometryN(h).getCoordinate();

                    if (pointToTest != null) {
                        distance = SplitUtil.calculateDistanceFromFirst(pointToTest, segCoords[0], segCoords[1]);

                        if (Math.abs(distance) < minRingDistance) {
                            minRingDistance = Math.abs(distance);
                        }
                    }
                }
            }

            if (minExtDistance < minRingDistance) {
                return OUTSIDE;
            } else if (minRingDistance < minExtDistance) {
                return INSIDE;
            }
        }

        // calculate the last coordinate where it lies.
        if (!polygonArea.contains(this.geomFactory.createPoint(shortCoords[shortCoords.length - 1]))) {

            // It lies outside the polygon.
            // Start with the exterior of the polygon.
            return OUTSIDE;
        }
        // check it lies inside any holes.
        for (int j = 0; j < polygon.getNumInteriorRing(); j++) {

            Geometry ringArea = this.geomFactory.createPolygon((LinearRing) polygon.getInteriorRingN(j), null);
            if (ringArea.contains(this.geomFactory.createPoint(shortCoords[shortCoords.length - 1]))) {

                // lies inside a ring.
                return INSIDE;
            }
        }
        return NONE;
    }

    


    /**
     * Make a copy of the coordinates.
     * 
     * @param coords
     * @return A copy of the original coordinates.
     */
    private Coordinate[] cloneCoordinate(Coordinate[] coords) {

        Coordinate[] cloneCoordinates = new Coordinate[coords.length];
        for (int i = 0; i < coords.length; i++) {
            cloneCoordinates[i] = (Coordinate) coords[i].clone();
        }
        return cloneCoordinates;
    }
    

    /**
     * <pre>
     * The useful part will be the next: 
     * Seek the first coordinate outside the boundary. 
     * Seek the first intersection with the boundary. 
     * If the first intersection is before the first coordinate outside the 
     * line, that coordinate will be the beginning of the line.
     * 
     * Do the same with the last coordinate outside and the last intersection.
     * 
     * </pre>
     * 
     * @param lineCoords
     * @param splitLine
     * @param adaptedPolygon (i/o)
     * @return The piece of lineString that starts and ends outside the
     *         boundary.
     */
    private Coordinate[] discardSplitLineSegmentAdaptingPolygon(Coordinate[] lineCoords, LineString splitLine, AdaptedPolygon adaptedPolygon) {

        Polygon polygon = adaptedPolygon.asPolygon();
        
        int posFirstCoord = 0;
        int posLastCoord = lineCoords.length - 1;
        
        Geometry boundary = polygon.getBoundary();

        int firstOutsidePosition = SplitUtil.findPositionOfFirstCoordinateOutside(splitLine, polygon);

        // get the first intersection.
        IntersectionStrategy interStrategy = new IntersectionStrategy();
        interStrategy.findFirstIntersection(lineCoords,  boundary);
        final Coordinate firstIntersectionCoord = interStrategy.getIntersection();
        final int firstIntersectionPosition = interStrategy.getIntersectionPosition();

        // calculate which one go first, and start from that coordinate.
        if ((firstOutsidePosition > firstIntersectionPosition) || (firstOutsidePosition == -1)) {
            // this coordinate must be eliminated from the array.
            lineCoords[firstIntersectionPosition] = firstIntersectionCoord;
            posFirstCoord = firstIntersectionPosition;
            // insert the intersection coordinate into the polygon.
            adaptedPolygon = insertCoordinateIntoPolygon(firstIntersectionCoord, adaptedPolygon, splitLine);
        } else {
            posFirstCoord = firstOutsidePosition;
        }

        int lastOutsidePosition = SplitUtil.findLastCoordinateOutside(splitLine, polygon);

        // get the last intersection.
        interStrategy.findLastIntersection(lineCoords, boundary);
        final Coordinate lastCoord = interStrategy.getIntersection();
        final int lastPosition = interStrategy.getIntersectionPosition();

        if (lastOutsidePosition < lastPosition || lastOutsidePosition == -1) {
            // this coordinate must be eliminated from the array.
            if (lastCoord != null) {
                lineCoords[lastPosition] = lastCoord;
                posLastCoord = lastPosition;
                // insert the intersection coordinate into the polygon.
                adaptedPolygon = insertCoordinateIntoPolygon(lastCoord, adaptedPolygon, splitLine);
            }
        } else {
            posLastCoord = lastOutsidePosition;
        }

        // build a new lineString starting form the first intersection between
        // the line and the polygon to the last intersection between the line
        // and the polygon, with this, we assure all the useful part of the
        // line, the one that intersects with the polygon is being used.
        Coordinate[] newCoords = SplitUtil.createCoordinateArrayWithoutDuplicated(posFirstCoord, posLastCoord, lineCoords);
        return newCoords;
    }

   /**
    * A coordinate will be inserted in the adaptedPolygon. The coordinate is
    * the result of doing an intersection operation between a line and this
    * polygon, so this methods know that the coordinate belong to the polygon.
    * 
    * Find the segment where the coordinate will lie. Find using the tangent
    * operation.
    * 
    * TODO refactory: this method could be simplified using the AdaptedPolygon object
    * 
    * @param coorToInsert
    *            Coordinate to insert into polygon.
    * @param adaptedPolygon
    *            Receiver polygon.
    * @return The adaptedPolygon, which will have the coordinate inserted on
    *         it.
    */
   private AdaptedPolygon insertCoordinateIntoPolygon(final Coordinate coorToInsert, final AdaptedPolygon adaptedPolygon1, LineString splitLine) {

       // get each coordinate, and calculate the tangent with coord(X) -
       // coorToInsert - coord(X+1). A coordiante will be added on the segment
       // which scores the lowest value.

       Polygon adaptedPolygon = adaptedPolygon1.asPolygon();
       Coordinate[] boundaryCoord = adaptedPolygon.getExteriorRing().getCoordinates();
       int positionToInsert = -1;
       int positionToInsertOnRing = -1;
       int nRing = -1;
       // check if the coordinate belongs to the exterior
       for (int i = 0; i < boundaryCoord.length - 1; i++) {

           Coordinate start = boundaryCoord[i];
           Coordinate end = boundaryCoord[i + 1];
           // calculate tangent between start - coorToInsert - end
           if (isSameTangent(start, coorToInsert, end) && isPointContainedInSegment(start, coorToInsert, end)) {

               assert positionToInsert == -1 : "position to insert modified twice."; //$NON-NLS-1$
               positionToInsert = i;
               break;
           }
       }
       List<Coordinate> updatedShell = new ArrayList<Coordinate>();
       Coordinate[] shellCoords;
       if (positionToInsert != -1) {
           for (int i = 0; i < boundaryCoord.length; i++) {

               updatedShell.add(boundaryCoord[i]);
               if (i == positionToInsert) {
                   updatedShell.add(coorToInsert);
               }
           }
           shellCoords = updatedShell.toArray(new Coordinate[updatedShell.size()]);
       } else {
           shellCoords = boundaryCoord;
       }
       GeometryFactory gf = adaptedPolygon.getFactory();
       LinearRing[] rings = null;
       // check if the coordinate belong to any of the interior rings in case
       // that there are rings.
       if (adaptedPolygon.getNumInteriorRing() != 0) {

           rings = new LinearRing[adaptedPolygon.getNumInteriorRing()];
           for (int rIndex = 0; rIndex < adaptedPolygon.getNumInteriorRing(); rIndex++) {

               // add the rings without modify them.
               rings[rIndex] = (LinearRing) adaptedPolygon.getInteriorRingN(rIndex);
               // for each ring
               Coordinate[] ringCoords = rings[rIndex].getCoordinates();
               for (int i = 0; i < ringCoords.length - 1 && positionToInsertOnRing == -1; i++) {

                   Coordinate start = ringCoords[i];
                   Coordinate end = ringCoords[i + 1];
                   // calculate tangent between start - coorToInsert - end
                   if (isSameTangent(start, coorToInsert, end) && isPointContainedInSegment(start, coorToInsert, end)) {

                       assert positionToInsertOnRing == -1 : "position to insert modified twice."; //$NON-NLS-1$
                       positionToInsertOnRing = i;
                       nRing = rIndex;
                       break;
                   }
               }
           }
           if (positionToInsertOnRing != -1) {
               // ring to be modified.
               assert nRing != -1 : "there should be a ring index."; //$NON-NLS-1$
               // Get the ring which need to be modified, insert the
               // coordinate and at the end, build the polygon.

               Coordinate[] originalRing = rings[nRing].getCoordinates();
               List<Coordinate> updatedHole = new ArrayList<Coordinate>();
               for (int i = 0; i < originalRing.length; i++) {

                   updatedHole.add(originalRing[i]);
                   if (i == positionToInsertOnRing) {
                       updatedHole.add(coorToInsert);
                   }
               }
               Coordinate[] modifiedRingCoords = updatedHole.toArray(new Coordinate[updatedHole.size()]);
               LinearRing modifiedRing = gf.createLinearRing(modifiedRingCoords);
               rings[nRing] = modifiedRing;
           }
       }

       // create the polygon.
       assert shellCoords != null : "there should be at least shell coordinates to build a polygon."; //$NON-NLS-1$
       LinearRing shellRing = gf.createLinearRing(shellCoords);
       Polygon newPolygon = gf.createPolygon(shellRing, rings);
       
       AdaptedPolygon rebuildedPolygon = new AdaptedPolygon(newPolygon);

       return rebuildedPolygon;
   }
        
    /**
     * Check if the provided point is contained in the range of that segment.
     * 
     * @param start Coordinate where the segment starts.
     * @param point Provided point.
     * @param end Coordinate where the segment ends.
     * @return True if the point is between the start and end coordinate.
     */
    private boolean isPointContainedInSegment( Coordinate start, Coordinate point, Coordinate end ) {

        // check if the points is contained in that line segment formed by the
        // start coordinate and the end coordinate.
        if (start.x > point.x && point.x > end.x) {
            // x are in order, now lets see if 'y' are in order too.
            if ((start.y > point.y && point.y > end.y) || (start.y < point.y && point.y < end.y)) {
                return true;
            }
        } else if (start.x < point.x && point.x < end.x) {
            if ((start.y > point.y && point.y > end.y) || (start.y < point.y && point.y < end.y)) {
                return true;
            }
        }

        return false;
    }

    /**
     * <p>
     * 
     * <pre>
     * Check if coordToTest belongs to the segment formed by the start
     * coordinate and end coordinate.
     * 
     * Calculate the tangent between the segment formed by start coordinate and
     * coordToTest. Then it does the same but this time for the segment formed
     * by coordToTest and end coordinate. If both tangent have the same value or
     * difference between them is near to zero, we could say that coordToTest
     * belongs to the segment formed by the start and end coordinates.
     * </pre>
     * 
     * </p>
     * 
     * @param start Coordinate where the segment starts.
     * @param coordToTest Coordinate test if it belongs to the line formed by the start and end
     *        coordinates.
     * @param end Coordinate where the segment ends.
     * @return True if the have the same tangent.
     */
    private boolean isSameTangent( Coordinate start, Coordinate coordToTest, Coordinate end ) {

        // check if the X or the Y have the same value.

        double firstX = Math.abs(start.x - coordToTest.x);
        double secondX = Math.abs(coordToTest.x - end.x);
        if (firstX < DEPRECIATE_VALUE && secondX < DEPRECIATE_VALUE) {
            return true;
        }

        double firstY = Math.abs(start.y - coordToTest.y);
        double secondY = Math.abs(coordToTest.y - end.y);
        if (firstY < DEPRECIATE_VALUE && secondY < DEPRECIATE_VALUE) {
            return true;
        }

        Coordinate[] newCoor;
        // calculate the tangent of the first segment. (i and i+1)
        newCoor = new Coordinate[2];
        newCoor[0] = start;
        newCoor[1] = coordToTest;

        double dx1 = newCoor[1].x - newCoor[0].x;
        double dy1 = newCoor[1].y - newCoor[0].y;
        double tangent = dy1 / dx1;

        // calculate the tangent of the second segment (i+1 and i+2)
        newCoor = new Coordinate[2];
        newCoor[0] = coordToTest;
        newCoor[1] = end;

        double dx2 = newCoor[1].x - newCoor[0].x;
        double dy2 = newCoor[1].y - newCoor[0].y;
        double tangentToCompare = dy2 / dx2;

        double diff = Math.abs(tangent - tangentToCompare);

        return (diff < DEPRECIATE_VALUE) ? true : false;
    }

    /** 
     * The adapted polygon is the result of modifying the original polygon with the intersection vertex 
     * with the split line.
     * 
     * @return the polygon adapted to the split line 
     */
    public AdaptedPolygon getAdaptedPolygon() {
        return this.adaptedPolygon;
    }

}
