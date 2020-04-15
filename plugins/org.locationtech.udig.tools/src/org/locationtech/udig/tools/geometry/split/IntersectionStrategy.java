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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;

/**
 * This strategy implement the method required to intersect the split line and the polygon to split
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.2
 */
final class IntersectionStrategy {

    /**
     * 
     * Maintains the intersection. It assure that the intersection state is consistent.
     * 
     * @author Mauricio Pazos (www.axios.es)
     * @author Aritz Davila (www.axios.es)
     * @since 1.3.2
     */
    private static class IntersectMemento{
        
        private Coordinate intersectionPoint = null;
        private int intersectionPosition = -1; 

        public IntersectMemento() {
            
        }
        public void setIntersection(final int position, final Geometry point){
            
            assert position >= 0 && point != null : "illegal intersection state"; //$NON-NLS-1$
            
            this.intersectionPoint = point.getCoordinate();
            this.intersectionPosition = position;
        }

        public Coordinate getIntersection() {
            
            return this.intersectionPoint;
        }
        
        public int getIntersectionPosition() {
            return this.intersectionPosition;
        }
        
        public boolean intersects(){
            return this.intersectionPosition != -1;
        }
    }
    
    private IntersectMemento intersectState = null; 

    /**
     * @return the intersection coordinate found in the last find intersection operation
     */
    public Coordinate getIntersection() {
        return this.intersectState.getIntersection();
    }
    /**
     * @return the intersection position found in the last find intersection operation
     */
    public int getIntersectionPosition() {
        return this.intersectState.getIntersectionPosition();
    }
    /**
     * @return true if the last operation has found an intersection
     */
    public boolean foundIntersection(){
        return this.intersectState.intersects();
    }

    /**
     * <pre>
     * 
     * Will find the first intersection between the split line and the boundary.
     * -Gets the intersection.
     * -if the result are more than 1 point, analyze it and return the closest one.
     * </pre>
     * 
     * @param lineCoords Coordinates of the lineString.
     * @param geomFactory Geometry factory.
     * @param boundary Boundary to intersect with.
     * @return The coordinate where the split line intersects with the given boundary.
     */
    public void findFirstIntersection( Coordinate[] lineCoords, Geometry boundary ) {

        assert boundary instanceof LinearRing || boundary instanceof LineString
                || boundary instanceof MultiLineString : "Boundary must be the linear ring."; //$NON-NLS-1$
        
        this.intersectState = new IntersectMemento();
    
        int numGeometries = 0;
        Geometry intersection = null;
        int i; 
        
        for( i = 0; i < lineCoords.length - 1; i++ ) {

            intersection = SplitUtil.intersection(lineCoords, i, boundary, false);

            numGeometries = intersection.getNumGeometries();
            if (numGeometries >= 1) {

                break;// found intersection
            }
        } 

        // if the intersection is found, return;
        if (numGeometries == 1) {

            this.intersectState.setIntersection(i, intersection);

        } else if (numGeometries > 1) {

            Coordinate firstLineCoord = lineCoords[i];
            Coordinate secondLineCoord = lineCoords[i + 1];

            double minDistance = Double.MAX_VALUE;
            double distance;
            // find the closest one to coordinate[i]
            for( int j = 0; j < intersection.getNumGeometries(); j++ ) {

                Geometry pointToTest = intersection.getGeometryN(j);

                distance = SplitUtil.calculateDistanceFromFirst(pointToTest.getCoordinate(), firstLineCoord,
                        secondLineCoord);

                if (Math.abs(distance) < minDistance) {
                    this.intersectState.setIntersection(i, pointToTest);
                    minDistance = Math.abs(distance);
                }
            }
        }
    }
    /**
     * <pre>
     * 
     * Will find the last intersection between the split line and the boundary.
     * -Gets the intersection.
     * -if the result are more than 1 point, analyze it and return the closest one.
     * </pre>
     * 
     * @param coordinates Line coordinates.
     * @param gf Geometry factory.
     * @param boundary A linear ring which will be the boundary of the polygon.
     * @return The coordinate where the split line intersects with the given boundary.
     */
    public void findLastIntersection( final Coordinate[] coordinates, final Geometry boundary ) {

        assert boundary instanceof LinearRing || boundary instanceof LineString || boundary instanceof MultiLineString : "Boundary must be the linear ring."; //$NON-NLS-1$

        this.intersectState = new IntersectMemento();
        for( int i = coordinates.length - 1; 0 <= i; i-- ) {

            Geometry intersection = SplitUtil.intersection(coordinates, i, boundary, true);

            int numGeometries = intersection.getNumGeometries();

            // if the intersection is found, return;
            if (numGeometries == 1) {

                this.intersectState.setIntersection(i, intersection);

                return;

            } else if (numGeometries > 1) {
                // find the closest one to coordinate[i]
                Coordinate firstLineCoord = coordinates[i];
                Coordinate secondLineCoord = coordinates[i - 1];

                double minDistance = Double.MAX_VALUE;
                double distance;

                for( int j = 0; j < intersection.getNumGeometries(); j++ ) {

                    Geometry pointToTest = intersection.getGeometryN(j);

                    distance = SplitUtil.calculateDistanceFromFirst(pointToTest.getCoordinate(),
                            firstLineCoord, secondLineCoord);

                    if (Math.abs(distance) < minDistance) {
                        this.intersectState.setIntersection(i, pointToTest);

                        minDistance = Math.abs(distance);
                    }
                }

                return;
            }
        }
    }

    /**
     * Find last coordinate position that intersects with the boundary
     * 
     * @param coordinates
     * @param boundary
     * @return -1 if not found or the position of last coordinate that intersects with the boundary
     */
    public void findLastIntersectionPosition( final Coordinate[] coordinates, final Geometry boundary ) {

        assert boundary instanceof LinearRing || boundary instanceof LineString
                || boundary instanceof MultiLineString : "Boundary must be the linear ring."; //$NON-NLS-1$

        this.intersectState = new IntersectMemento();
        
        for( int i = coordinates.length - 1; 0 <= i; i-- ) {

            Geometry intersection = SplitUtil.intersection(coordinates, i, boundary, true);

            int numGeometries = intersection.getNumGeometries();

            // if the intersection is found, return;
            if (numGeometries >= 1) {

                this.intersectState.setIntersection(i, intersection);
                return;

            }
        }

    }
    
   

    
    
}
