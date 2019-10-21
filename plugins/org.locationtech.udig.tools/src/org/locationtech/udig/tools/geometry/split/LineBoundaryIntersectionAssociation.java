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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.operation.distance.DistanceOp;

import org.locationtech.udig.tools.geometry.internal.util.GeometryList;

/**
 * Maintains the line's segments and its intersection with the piece of boundary (exterior ring or
 * interior ring).
 * <p>
 * The list of intersection points are ordered by the distance from the intersection point to the
 * first vertex of line. The order is min distance to max distance.
 * </p>
 * 
 * <pre>
 * Thus if the segment has the following intersections in the list:
 * i1, i2, ...in
 * 
 * The list is sorted using the criteria:
 * 
 * distance(fv,i1) < distance(fv,i2)
 * 
 * where fv is the first vertex of linestring.
 * </pre>
 * 
 * </p>
 * <p>
 * The intersection association between split line segment and boundary segment is:
 * </p>
 * <p>
 * Split Line Segment (0 n)----intersect---- (0..n) Boundary Ring Segment
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.2
 */
final class LineBoundaryIntersectionAssociation {

    private class IntersectionLink {

        private final LineString splitLineSegment;
        private final LineString ringSegment;
        private final Point intersection;

        public IntersectionLink( final LineString splitSegment, final LineString ringSegment,
                final Point intersection ) {

            assert splitSegment != null && ringSegment != null && intersection != null;

            this.splitLineSegment = splitSegment;
            this.ringSegment = ringSegment;
            this.intersection = intersection;
        }

        public String toString() {

            return "IntersectionLink( SplitLine: " + this.splitLineSegment + " - RingSegment: " + this.ringSegment + " - Intersection: " + this.intersection + ")"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }

        public LineString getRingSegment() {
            return this.ringSegment;
        }

        public Point getIntersection() {
            return this.intersection;
        }
    }

    /**
     * Maintains the current intersection. It assures that the intersection between the split
     * segment and the ring segment is consistent.
     * 
     * @author Mauricio Pazos (www.axios.es)
     * @author Aritz Davila (www.axios.es)
     * @since 1.3.2
     */
    public static class IntersectCursor implements Cloneable{

        private Point intersectionPoint = null;
        private int segmentPosition = -1;
        private LineString ringSegment = null;
        private int visitedIntersection = 0;


        public static IntersectCursor NULL = new IntersectCursor();

        public IntersectCursor() {
        }

        public void setCurrentIntersection( final int i, final Point intersectionPoint,
                final LineString ringSegment ) {

            setState(i, intersectionPoint, ringSegment);
        }
        public Object clone(){
            IntersectCursor duplicated = null; 
            try {
                
                duplicated = (IntersectCursor)super.clone();
                duplicated.intersectionPoint = (Point) this.intersectionPoint.clone();
                duplicated.ringSegment = (LineString) this.ringSegment.clone();
                duplicated.segmentPosition = this.segmentPosition;
                duplicated.visitedIntersection = this.visitedIntersection;
                
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return duplicated;
        }
        
        public int getVisitedIntersection() {
            return this.visitedIntersection;
        }

        @Override
        public boolean equals( Object obj ) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            IntersectCursor other = (IntersectCursor) obj;
            if (intersectionPoint == null) {
                if (other.intersectionPoint != null)
                    return false;
            } else if (!intersectionPoint.equals(other.intersectionPoint))
                return false;
            if (ringSegment == null) {
                if (other.ringSegment != null)
                    return false;
            } else if (!ringSegment.equals(other.ringSegment))
                return false;
            if (segmentPosition != other.segmentPosition)
                return false;
            return true;
        }

        @Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((intersectionPoint == null) ? 0 : intersectionPoint
							.hashCode());
			result = prime * result
					+ ((ringSegment == null) ? 0 : ringSegment.hashCode());
			result = prime * result + segmentPosition;
			result = prime * result + visitedIntersection;
			return result;
		}


		/**
         * @return the visited intersection point
         */
        public Point getIntersectionPoint() {

            return this.intersectionPoint;
        }

        /**
         * @return the position in the segment list
         */
        public int getSegmentPosition() {
            return this.segmentPosition;
        }

        /**
         * @return the intersected ring segment by the split line segment
         */
        public LineString getIntersectedRingSegment() {
            return this.ringSegment;
        }

        private void setState( final int segmentPosition, final Point intersectionPoint,
                final LineString intersectedRingSegment ) {
            assert segmentPosition >= 0 && intersectionPoint != null
                    && intersectedRingSegment != null;

            this.segmentPosition = segmentPosition;
            this.intersectionPoint = intersectionPoint;
            this.ringSegment = intersectedRingSegment;
            
            this.visitedIntersection++;
        }

    }

    /**
     * Maintains the segment of line and intersections with the boundary
     * 
     * @author Mauricio Pazos (www.axios.es)
     * @author Aritz Davila (www.axios.es)
     * @since 1.3.0
     */
    private static class SplitLineSegmentIntersectionNode {

        /** the segment of line (split line) */
        private LineString segment = null;

        /** intersection list ordered by distance from first segment's vertex. */
        private List<Point> intersectionList = null;

        private GeometryFactory geometryFactory;

        /**
         * a new instance of SegmentNode
         * 
         * @param segment
         * @param intersectionList
         */
        public SplitLineSegmentIntersectionNode( final LineString segment,
                List<Point> intersectionList ) {

            this.geometryFactory = segment.getFactory();
            this.segment = segment;
            this.intersectionList = sortIntersectionByDistance(intersectionList);
        }

        public String toString() {

            StringBuilder str = new StringBuilder(this.segment.toText());
            str.append(" ["); //$NON-NLS-1$

            for( Point point : this.intersectionList ) {
                str.append(point.toText());
                str.append(" "); //$NON-NLS-1$
            }
            str.append("]"); //$NON-NLS-1$

            return str.toString();
        }

        public LineString getSegment() {

            return segment;
        }

        public List<Point> getIntersectionList() {

            return intersectionList;
        }

        /**
         * Sorts the intersection point from min to max distance of first segment's vertex
         * 
         * @return a sorted list
         */
        private List<Point> sortIntersectionByDistance( final List<Point> unSortedList ) {

            if (unSortedList.size() <= 1) {
                return unSortedList;
            }
            // The unsorted list has got almost two elements. Initializes the sorted list with the
            // first
            List<Point> sortedList = new GeometryList<Point>();
            sortedList.add(0, unSortedList.get(0));

            final Point firstVertex = this.geometryFactory.createPoint(this.segment
                    .getCoordinateN(0));

            for( int i = 1; i < unSortedList.size(); i++ ) {

                Point curPoint = unSortedList.get(i);
                double newDistance = DistanceOp.distance(firstVertex, curPoint);
                assert newDistance >= 0;

                boolean wasInserted = false;
                for( int j = 0; j < sortedList.size(); j++ ) {

                    Point sortedPoint = sortedList.get(j);
                    double sortedDistance = DistanceOp.distance(firstVertex, sortedPoint);
                    assert sortedDistance >= 0;
                    if (newDistance < sortedDistance) {

                        sortedList = addPointInListFilterEquals(sortedList, j, curPoint);
                        wasInserted = true;
                        break;
                    }
                }
                if (!wasInserted) {
                    // the distance is the bigger, so add it as last element
                    if( !sortedList.contains(curPoint) ){
                        sortedList.add(curPoint);
                    }
                }
            }
            return sortedList;
        }

        /**
         * Add the intersection point taking in to account that the list must not have duplicated
         * points Note: this method was rited because the List.contains method do not work for
         * geometry element list
         * 
         * @param intersectionList
         * @param insertPosition
         * @param intersectionPoint
         * @return The list with the new point (it it is not present in the list)
         */
        private List<Point> addPointInListFilterEquals( final List<Point> intersectionList,
                final int insertPosition, final Point intersectionPoint ) {

            if (intersectionList.contains(intersectionPoint)) {
                return intersectionList;
            }
            intersectionList.add(insertPosition, intersectionPoint);

            return intersectionList;
        }

        /**
         * @return the intersection nearest to the first segment's vertex.
         */
        public Point searchNearestIntersection() {

            return this.intersectionList.isEmpty() ? null : this.intersectionList.get(0);
        }

        /**
         * Searches the next intersection from the indeed point
         * 
         * @param referencePoint
         * @return the next nearest intersection point
         */
        private Point searchNearestIntersectionFrom( final Point referencePoint ) {

            // found the position of reference point
            int positionOfReferencePoint = -1;
            for( int i = 0; i < this.intersectionList.size(); i++ ) {

                Point point = this.intersectionList.get(i);
                Coordinate pointCoordinate = point.getCoordinate();
                Coordinate referenceCoordinate = referencePoint.getCoordinate();
                if (pointCoordinate.equals2D(referenceCoordinate)) {
                    positionOfReferencePoint = i;
                    break;
                }
            }
            if (positionOfReferencePoint == -1) {
                return null; // not found
            }
            // It was found then return the next position, if there are a next element in the
            // intersection list
            Point nexIntersection = null;
            if ((positionOfReferencePoint + 1) < this.intersectionList.size()) {

                nexIntersection = this.intersectionList.get(positionOfReferencePoint + 1);
            }
            return nexIntersection;
        }
    } // end class SegmentNode

    /** maintains the segment position of intersection */
    private IntersectCursor                         cursor = IntersectCursor.NULL;

    /** maintains the list of split line segments an its intersection points with the ring */
    private List<SplitLineSegmentIntersectionNode>  splitLineSegmentWithIntersectionList = new LinkedList<SplitLineSegmentIntersectionNode>();

    /**
     * maintains the links between the an intersection point and the segment line and segment ring
     * which have an intersection relation
     */
    private Map<Point, IntersectionLink>            intersectionLinks = new LinkedHashMap<Point, IntersectionLink>();

    private GeometryFactory                         geomFactory;

    private IntersectCursor                         backCursor;
    

    /**
     * A new instance of {@link LineBoundaryIntersectionAssociation}
     * 
     * @param lineCoords line's coordinates
     * @param ring exterior ring or an interior ring (hole) of polygon
     */
    public LineBoundaryIntersectionAssociation( final Coordinate[] lineCoords, final LinearRing ring ) {

        this.geomFactory = ring.getFactory();
        this.splitLineSegmentWithIntersectionList = makeIntersectionSegmentList(lineCoords, ring);
    }

    /**
     * Creates an array composed by the segments and its intersection points with the ring.
     * 
     * @param splitLine
     * @param ring
     * @return List of segment and intersection points
     */
    private List<SplitLineSegmentIntersectionNode> makeIntersectionSegmentList(
            final Coordinate[] splitLine, final LinearRing ring ) {

        this.splitLineSegmentWithIntersectionList = new ArrayList<SplitLineSegmentIntersectionNode>();
        for( int i = 0; i < (splitLine.length - 1); i++ ) {

            LineString lineSegment = this.geomFactory.createLineString(
                    new Coordinate[]{splitLine[i], splitLine[i + 1]});

            List<Point> segmentIntersectionList = new GeometryList<Point>();
            // make the intersection between the line segment and each boundary segment in order to
            // create the intersection link
            List<LineString> ringSegmentList = ringToSegmentList(ring);
            for( LineString ringSegment : ringSegmentList ) {

                Geometry intersectionGeom = lineSegment.intersection(ringSegment);

                if (intersectionGeom instanceof Point) {

                    IntersectionLink link = new IntersectionLink(lineSegment, ringSegment,
                            (Point) intersectionGeom);
                    this.intersectionLinks.put(link.getIntersection(), link);

                    if (!segmentIntersectionList.contains(intersectionGeom)) {
                        // many ring segment could intersect in the same point with the split line,
                        // so
                        segmentIntersectionList.add((Point) intersectionGeom);
                    }
                }
            }
            SplitLineSegmentIntersectionNode lineSegmentIntersection;
            lineSegmentIntersection = new SplitLineSegmentIntersectionNode(lineSegment,
                    segmentIntersectionList);
            this.splitLineSegmentWithIntersectionList.add(lineSegmentIntersection);
        }
        return this.splitLineSegmentWithIntersectionList;
    }

    private List<LineString> ringToSegmentList( LinearRing ring ) {

        List<LineString> segmentList = new GeometryList<LineString>();

        Coordinate[] ringCoords = ring.getCoordinates();
        for( int i = 0; i < ringCoords.length - 1; i++ ) {

            LineString segment = this.geomFactory.createLineString(new Coordinate[]{ringCoords[i],
                    ringCoords[i + 1]});
            segmentList.add(segment);
        }

        return segmentList;
    }

    /**
     * Searches the first intersection in the segment list
     */
    public void moveFirstIntersection() {

        this.cursor = IntersectCursor.NULL;
        this.backCursor = this.cursor;

        for( int segmentPosition = 0; segmentPosition < this.splitLineSegmentWithIntersectionList.size(); segmentPosition++ ) {

            SplitLineSegmentIntersectionNode segmentNode = this.splitLineSegmentWithIntersectionList
                    .get(segmentPosition);
            if (!segmentNode.getIntersectionList().isEmpty()) {

                Point point = segmentNode.searchNearestIntersection();
                IntersectionLink link = this.intersectionLinks.get(point);
                LineString ringSegment = link.getRingSegment();

                this.cursor = new IntersectCursor();
                this.cursor.setCurrentIntersection(segmentPosition, point, ringSegment);

                break;
            }
        }
    }

    /**
     * The cursor is moved before the first intersection
     */
    public void moveBeforeFirst() {

        this.cursor = IntersectCursor.NULL;
    }

    /**
     * Searches the next intersection in the segment list.
     */
    public void moveNextIntersection() {

        if (this.splitLineSegmentWithIntersectionList.isEmpty()) {
            this.cursor = IntersectCursor.NULL; // there is not next
            return;
        }
        if(this.cursor == IntersectCursor.NULL){
            moveFirstIntersection();
            return;
        }
        this.backCursor = (IntersectCursor) this.cursor.clone();
        
        // the next intersection could be in the same segment or in the next segments.
        // thus the search must begin in the last visited segment.
        final int lastVisitedSegment = this.cursor.getSegmentPosition();
        final Point lastVistedIntersection = this.cursor.getIntersectionPoint();

        // search the next intersection point in the latest segment
        SplitLineSegmentIntersectionNode node = this.splitLineSegmentWithIntersectionList
                .get(lastVisitedSegment);
        Point intersectionFound = node.searchNearestIntersectionFrom(lastVistedIntersection);
        if (intersectionFound != null) {
            // found a new intersection in the last visited segment

            IntersectionLink link = this.intersectionLinks.get(intersectionFound);
            LineString ringSegment = link.getRingSegment();

            this.cursor.setCurrentIntersection(lastVisitedSegment, intersectionFound, ringSegment);

            return;
        }

        // search an intersection in the rest of segments
        for( int i = lastVisitedSegment + 1; i < this.splitLineSegmentWithIntersectionList.size(); i++ ) {

            node = this.splitLineSegmentWithIntersectionList.get(i);

            for( Point currentIntersection : node.getIntersectionList() ) {

                if ((currentIntersection != null)
                        && (!lastVistedIntersection.equals(currentIntersection))) {
                    // found an intersection in the current segment
                    IntersectionLink link = this.intersectionLinks.get(currentIntersection);
                    LineString ringSegment = link.getRingSegment();
                    this.cursor.setCurrentIntersection(i, currentIntersection, ringSegment);

                    return;
                }
            }
        }
        // it did not find an intersection, so reset the cursor
        this.cursor = IntersectCursor.NULL;
    }
    
    /**
     * Move the cursor to the back position.
     * Only one step backward is possible.    
     * 
     */
    public void moveBackIntersection(){
       
        assert this.backCursor != IntersectCursor.NULL: "illegal state!. It could occurs if the client call this method without a previous calling to the moveNextIntersection() method ";  //$NON-NLS-1$
       
       this.cursor = this.backCursor;
       this.backCursor = IntersectCursor.NULL;
    }

    public LineString getRingSegment() {
        return this.cursor.getIntersectedRingSegment();
    }

    /**
     * @return the intersection coordinate found in the last find intersection operation
     */
    public Coordinate getIntersection() {
        return this.cursor.getIntersectionPoint().getCoordinate();
    }

    /**
     * @return the intersection coordinate found in the last find intersection operation
     */
    public int getIntersectionSegmentPosition() {
        return this.cursor.getSegmentPosition();
    }

    /**
     * @return the count of intersection between the line and the boundary
     */
    public int countIntersections() {

        List<Point> intersectionList = new GeometryList<Point>();
        for( SplitLineSegmentIntersectionNode lineIntersection : this.splitLineSegmentWithIntersectionList ) {

            for( Point point : lineIntersection.getIntersectionList() ) {

                if (!intersectionList.contains(point)) {
                    intersectionList.add(point);
                }
            }
        }
        return intersectionList.size();
    }
    
    public int getVisitedIntersection(){
        return this.cursor.getVisitedIntersection();
    }

    /**
     * Build a LineString between the the intersection of the first and the second segment.
     * 
     * 
     * @param firstSegmentPosition
     * @param firstIntersection
     * @param lastSegmentPosition
     * @param secondRingSegment
     * @return a LineString
     */
    public LineString buildLineBetweenIntersection( 
            final int firstSegmentPosition,
            final Coordinate firstIntersection, 
            final int lastSegmentPosition,
            final Coordinate secondIntersection ) {

        List<Coordinate> coordinates = new ArrayList<Coordinate>();

        // add the first intersection as first vertex
        coordinates.add(firstIntersection);

        // extract the interior vertex from the interior segments (it uses the first vertex)
        SplitLineSegmentIntersectionNode node = this.splitLineSegmentWithIntersectionList
                .get(firstSegmentPosition);
        assert lastSegmentPosition < this.splitLineSegmentWithIntersectionList.size();
        
        for( int i = firstSegmentPosition; i <= lastSegmentPosition; i++ ) {

            node = this.splitLineSegmentWithIntersectionList.get(i);
            
            Coordinate[] vertexList = node.getSegment().getCoordinates();
            for( int j = 0; j < vertexList.length; j++ ) {

                Coordinate curVertex = vertexList[j];
                if( coordinates.get(0).distance(curVertex)  < coordinates.get(0).distance(secondIntersection)) {  
                    // the distance of the current vertex is contained in the segment length
                    if (!coordinates.contains(curVertex)) {
                        coordinates.add(curVertex);
                    }
                }
            }
        }
        // add the intersection associated to the last segment has last vertex
        if (!coordinates.contains(secondIntersection)) {
            coordinates.add(secondIntersection );
        }

        // build the line string
        Coordinate[] coordsArray = coordinates.toArray(new Coordinate[coordinates.size()]);
        LineString newLine = this.geomFactory.createLineString(coordsArray);

        return newLine;
    }
    
    /**
     * Builds a split line fragment using the position of specified segments.
     * 
     * @param firstSegmentPosition
     * @param lastSegmentPosition
     * @return an split line fragment between the first and last segment (both included)
     */
    public LineString buildLineBetweenIntersectionSegments( 
            final int firstSegmentPosition,
            final int lastSegmentPosition ) {
        
        List<Coordinate> coordinatList = new LinkedList<Coordinate>();

        // extract the interior vertex from the interior segments (it uses the first vertex)
        assert (firstSegmentPosition <= lastSegmentPosition) && (lastSegmentPosition < this.splitLineSegmentWithIntersectionList.size());
        
        for( int i = firstSegmentPosition; i <= lastSegmentPosition; i++ ) {

            SplitLineSegmentIntersectionNode node = this.splitLineSegmentWithIntersectionList.get(i);
            
            Coordinate[] vertexList = node.getSegment().getCoordinates();
            for( int j = 0; j < vertexList.length; j++ ) {
                if (!coordinatList.contains(vertexList[j])) {
                    coordinatList.add(vertexList[j]);
                }
            }
        }
        // build the line string
        Coordinate[] coordinates = coordinatList.toArray(new Coordinate[coordinatList.size()]);
        LineString splitLineFragment = this.geomFactory.createLineString(coordinates);

        return splitLineFragment;
    }

    public LineString buildLineBetweenIntersectionPoints( 
            final int firstSegmentPosition, final Coordinate firstIntersection,
            final int lastSegmentPosition, final Coordinate lastIntersection ) {
        
        assert (firstSegmentPosition <= lastSegmentPosition) && (lastSegmentPosition < this.splitLineSegmentWithIntersectionList.size());
        
        List<Coordinate> coordinatList = new LinkedList<Coordinate>();

        if(firstSegmentPosition == lastSegmentPosition ){
            // both intersections belong to the same line segment, then truncate the segment between the intersections point
      
            List<Coordinate> lineFragment = truncateSegmentBetween(firstSegmentPosition, firstIntersection, lastIntersection);
            coordinatList.addAll(lineFragment);
            
        } else {
            // Truncates the header of first segment using the first intersection has first vertex,
            // Insert the coordinates of intermediate line fragment
            // The last segment is truncated from the last intersection vertex
            List<Coordinate> firstLineFragment = trunkateHeaderSegment(firstSegmentPosition, firstIntersection);
            coordinatList.addAll(firstLineFragment);
            
            for( int i = firstSegmentPosition + 1; i < lastSegmentPosition; i++ ) {
    
                SplitLineSegmentIntersectionNode node = this.splitLineSegmentWithIntersectionList.get(i);
                
                Coordinate[] vertexList = node.getSegment().getCoordinates();
                for( int j = 0; j < vertexList.length; j++ ) {
                    if (!coordinatList.contains(vertexList[j])) {
                        coordinatList.add(vertexList[j]);
                    }
                }
            }
            // adds the coordinates of last segment
            List<Coordinate> lastLineFragment = trunkateTailSegment(lastSegmentPosition, lastIntersection);
            for( Coordinate coordinate : lastLineFragment ) {
                if (!coordinatList.contains(coordinate)) {
                    coordinatList.add(coordinate);
                }
            }
        }
        
        // build the line string
        Coordinate[] coordinates = coordinatList.toArray(new Coordinate[coordinatList.size()]);
        LineString splitLineFragment = this.geomFactory.createLineString(coordinates);

        return splitLineFragment;
    }
    

    /**
     * Removes the coordinates before the first intersection and after the last intersection.
     * @param firstSegmentPosition Segment to truncate
     * @param firstIntersection
     * @param lastIntersection
     * @return a truncated line fragment based in the indeed segment
     */
    private List<Coordinate> truncateSegmentBetween( 
            final int           firstSegmentPosition,
            final Coordinate    firstIntersection, 
            final Coordinate    lastIntersection ) {

        List<Coordinate> newLine = new LinkedList<Coordinate>();
        
        SplitLineSegmentIntersectionNode node = this.splitLineSegmentWithIntersectionList.get(firstSegmentPosition);
        
        Coordinate[] lineFragment = node.getSegment().getCoordinates();

        final double firstIntersectDistance = lineFragment[0].distance(firstIntersection);
        final double lastIntersectDistance = lineFragment[0].distance(lastIntersection);
        
        newLine.add(firstIntersection);
        for( int i = 0; i < lineFragment.length; i++ ) {
            
            // searches the position of new coordinate
            double currentDistance = lineFragment[0].distance(lineFragment[i]);
            if((firstIntersectDistance < currentDistance) && (currentDistance < lastIntersectDistance) ){
                newLine.add(lineFragment[i]);
            }
        }
        newLine.add(lastIntersection);

        return newLine;
    }

    /**
     * Makes a line fragment using the first intersection has initial vertex of the new segment.
     * 
     * @param firstSegmentPosition
     * @param firstIntersection
     * @return a new line fragment that have the intersection coordinate has first vertex
     */
    private List<Coordinate> trunkateHeaderSegment( final int firstSegmentPosition, final Coordinate firstIntersection ) {

        List<Coordinate> newLine = new LinkedList<Coordinate>();
        
        SplitLineSegmentIntersectionNode node = this.splitLineSegmentWithIntersectionList.get(firstSegmentPosition);
        
        Coordinate[] lineFragment = node.getSegment().getCoordinates();
        final double vertexDistance = lineFragment[0].distance(firstIntersection);
        
        int found = -1;
        for( int i = 0; i < lineFragment.length; i++ ) {
            
            // searches the position of new coordinate
            double currentDistance = lineFragment[0].distance(lineFragment[i]);
            if(currentDistance > vertexDistance ){
                found = i;
                break;
            }
        }
        if(found != -1){
            // add the vertex and the rest of coordinates in the new segment
            newLine.add(firstIntersection);
            for(int i = found; i < lineFragment.length; i++ ){
                newLine.add(lineFragment[i]);
            }
            
        } else {
            // vertex is the last coordinate
            newLine.add(firstIntersection);
        }
        return newLine;
    }


    /**
     * Makes a new line that is cut from the last intersection 
     * 
     * @param lastSegmentPosition
     * @param lastIntersection
     * @return a new line as a list of coordinate
     */
    private List<Coordinate> trunkateTailSegment( 
            final int lastSegmentPosition,
            final Coordinate lastIntersection ) {
        
        List<Coordinate> newLine = new LinkedList<Coordinate>();
        
        SplitLineSegmentIntersectionNode node = this.splitLineSegmentWithIntersectionList.get(lastSegmentPosition);
        
        Coordinate[] lineFragment = node.getSegment().getCoordinates();
        final double vertexDistance = lineFragment[0].distance(lastIntersection);
        
        int found = -1;
        for( int i = 0; i < lineFragment.length; i++ ) {
            
            // searches the position of new coordinate
            double currentDistance = lineFragment[0].distance(lineFragment[i]);
            if(currentDistance < vertexDistance ){
                newLine.add(lineFragment[i]);
            } else {
                found = i;
                break;
            }
        }
        if(found != -1){
            // add the vertex and the rest of coordinates in the new segment
            newLine.add(lastIntersection);
            
        } else {
            // vertex is the last coordinate
            newLine.add(lastIntersection);
        }
        return newLine;
    }
    /**
     * Returns the segment in the specified position.
     * 
     * @param position
     * @return the segment of split line correspondent to the position
     */
    public LineString getSplitLineSegment( final int segmentPosition ) {

        SplitLineSegmentIntersectionNode segmentNode =this.splitLineSegmentWithIntersectionList.get(segmentPosition);
        return segmentNode.getSegment();
    }

    /**
     * 
     * @return the count of segments of split line
     */
    public int size() {
        
        return this.splitLineSegmentWithIntersectionList.size();
    }


}
