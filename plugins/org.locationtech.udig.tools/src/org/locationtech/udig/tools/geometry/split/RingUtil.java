/**
 * 
 */
package org.locationtech.udig.tools.geometry.split;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;

import org.locationtech.udig.tools.geometry.internal.util.GeometryList;

/**
 * Provides utility method to manipulate rings
 * <p>
 * </p>
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
final class RingUtil {
    
    private RingUtil(){
        // private for utility class.
    }
    

    public static List<LineString> ringToSegmentList( LinearRing ring ) {
        
        GeometryFactory gf = ring.getFactory();
        
        List<LineString> segmentList = new GeometryList<LineString>();
        
        Coordinate[] ringCoords = ring.getCoordinates();
        for( int i = 0; i < ringCoords.length - 1; i++ ) {
            
            LineString segment = gf.createLineString(new Coordinate[]{ringCoords[i], ringCoords[i+1]});
            segmentList.add(segment);
        }
        return segmentList;
    }


    /**
     * Transform the segment list to a ring.
     * 
     * @param adaptedRingSegmentList
     * 
     * @return a new ring
     */
    public static LinearRing segmentListToRing( List<LineString> adaptedRingSegmentList ) {
        
        List<Coordinate> coordList = new LinkedList<Coordinate>();
        for( LineString line: adaptedRingSegmentList ) {
            
            // insert the line coordinate in the final coordinate array
            Coordinate[] lineCoords = line.getCoordinates();
            
            for( int i = 0; i < lineCoords.length; i++ ) {
                
                if (!coordList.contains(lineCoords[i])) {
                    coordList.add(lineCoords[i]);
                }
            }
        }
        // add the first coordinate has last coordinate in order to close the ring
        coordList.add(coordList.get(0));
        Coordinate[] linearRingCoords =  coordList.toArray(new Coordinate[ coordList.size() ]);
        
        GeometryFactory factory = adaptedRingSegmentList.get(0).getFactory();
        LinearRing ring = factory.createLinearRing(linearRingCoords);
       
        return ring;
    }

    /**
     * Close the given line forming a ring and return its coordinates.
     * 
     * @param coordinates
     *            Line coordinates
     * @return An array coordinates forming a closed line, a ring.
     */
    public static Coordinate[] builRing(Coordinate[] coordinates) {

        final int length = coordinates.length;
        Coordinate[] closed  = Arrays.copyOf(coordinates, length +1);
        closed[length] = coordinates[0];
        
        return closed;
    }


    /**
     * Go through the rings, for each ring, calculate if there is an
     * intersection with the provided segment. If that's true, calculates the
     * distance of this intersection respect the beginning of the segment. Map
     * each ring which it's closest distance.
     * 
     * @param rings
     * @param segment
     * @param lineSegment
     * @return A map with each ring and it's closest distance.
     */
    public static Map<LinearRing, Double> setRingWithClosestIntersectionDistance( final List<LinearRing> rings,
                                                                            final Geometry segment,
                                                                            final Coordinate[] lineSegment) {

        Map<LinearRing, Double> ringsDistance = new HashMap<LinearRing, Double>();

        for (LinearRing eachRing : rings) {

            Geometry intersection = eachRing.intersection(segment);

            int numGeometries = intersection.getNumGeometries();
            double distance;
            // if the intersection is found, return;
            if (numGeometries == 1) {
                // calculate the distance and put this ring with its distance.

                Coordinate intersectionPoint = intersection.getCoordinate();
                distance = SplitUtil.calculateDistanceFromFirst(intersectionPoint, lineSegment[0], lineSegment[1]);

                ringsDistance.put(eachRing, Math.abs(distance));
            } else if (numGeometries > 1) {

                // calculate the closest distance, and put this ring with the
                // closest distance.
                double minDistance = Double.MAX_VALUE;

                // find the closest one to coordinate[i]
                for (int j = 0; j < intersection.getNumGeometries(); j++) {

                    Coordinate curPoint = intersection.getGeometryN(j).getCoordinate();

                    distance = SplitUtil.calculateDistanceFromFirst(curPoint, lineSegment[0], lineSegment[1]);

                    if (Math.abs(distance) < minDistance) {
                        minDistance = Math.abs(distance);
                    }
                }

                ringsDistance.put(eachRing, Math.abs(minDistance));
            }
        }
        return ringsDistance;
    }

    
}
