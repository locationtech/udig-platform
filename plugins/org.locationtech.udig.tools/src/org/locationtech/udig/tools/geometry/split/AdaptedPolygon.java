/**
 * 
 */
package org.locationtech.udig.tools.geometry.split;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import org.locationtech.udig.tools.geometry.internal.util.GeometryList;

/**
 * Adapted Polygon maintains the polygon modified during the split process.
 * <p>
 * In the split process, the original polygon is modified adding it the intersections
 * with the split line. This strategy assures that the intersection will be consistent in
 * all the split process. (We found that a an intersection in a step is not necessarily 
 * the same in the next step)
 * </p>
 * <p>
 * The original ring and its modifications (intersection point with the split line) is supported
 * by the following association:
 * </p>
 * <pre>     
 * original ring ---(n) original segment (1)---------(1) adapted segment
 * </pre>
 * <p>
 * Thus when an intersection point is found this intersection is added in the correspondent adapted segment.
 * The adapted segment is build when the client module calls {@link #asPolygon()} method.
 * </p> 
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
final class AdaptedPolygon implements Cloneable{

    private Polygon             originalPolygon;

    /**  
     * A ring has many original segment and each original segment has one adapted 
     * ring ---(n) original segment (1)---------(1) adapted segment
     * The order is very important: exterior ring follows by interior ring (or holes).
     */
    private Map<LinearRing, Map<String, LineString>>   ringSegmentAdaptedAssociation  = new LinkedHashMap<LinearRing, Map<String,LineString>>();

    private GeometryFactory geomFactory;
    
    
    /**
     * a new instance of {@link AdaptedPolygon}
     * @param originalSplitLine 
     */
    public AdaptedPolygon(final Polygon original, final LineString splitLine) {
        
        this.geomFactory = original.getFactory();
        this.originalPolygon = original;
        initSegmentAssociation(original, splitLine);
    }
    
    
    public AdaptedPolygon( Polygon original ) {
        this.geomFactory = original.getFactory();
        this.originalPolygon = original;
        initSegmentAssociation(original, null);
    }


    public final Object clone() {

        AdaptedPolygon duplicated = null;
        try {
            duplicated = (AdaptedPolygon) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            assert false;
        }
        
        duplicated.geomFactory = this.geomFactory;
        duplicated.originalPolygon = (Polygon) this.originalPolygon.clone();
        
        duplicated.ringSegmentAdaptedAssociation  = new LinkedHashMap<LinearRing, Map<String,LineString>>();
        
         for( Entry<LinearRing, Map<String,LineString>> ringSegmentEntry : this.ringSegmentAdaptedAssociation.entrySet() ) {
             
             LinearRing ring = ringSegmentEntry.getKey();
             
             Map<String, LineString> newSegmentMap = new LinkedHashMap<String, LineString>();
             Map<String, LineString> sourceSegmentMap = ringSegmentEntry.getValue();
             
             for( Entry<String, LineString> segmentEntry: sourceSegmentMap.entrySet() ) {

                 newSegmentMap.put(segmentEntry.getKey(), (LineString) segmentEntry.getValue().clone());
            }
            duplicated.ringSegmentAdaptedAssociation.put( ring, newSegmentMap);
         }
        return duplicated;
    }

    


    /**
     * Initialize the ring, segment and adapted segment association
     * 
     * @param originalPolygon
     * @param splitLine 
     */
    private void initSegmentAssociation(final Polygon originalPolygon, final LineString splitLine){

        // Extract exterior and interior rings. The ring is exploded in its segments and added in the association
        LinearRing exteriorRing = (LinearRing)this.originalPolygon.getExteriorRing();
        Map<String, LineString> exteriorSegments = createSegmentAdaptedAssociation(exteriorRing, splitLine);
        this.ringSegmentAdaptedAssociation.put(exteriorRing, exteriorSegments);
        
        for(int i = 0; i < this.originalPolygon.getNumInteriorRing(); i++){

            LinearRing interiorRing = (LinearRing)this.originalPolygon.getInteriorRingN(i);
            
            Map<String, LineString> interiorSegments = createSegmentAdaptedAssociation(interiorRing, splitLine);
            this.ringSegmentAdaptedAssociation.put( interiorRing, interiorSegments);
        }
    }
    

    /**
     * 
     * @param ring
     * @param splitLine
     * @return
     */
    private Map<String, LineString> createSegmentAdaptedAssociation(final LinearRing ring, final LineString splitLine ) {
        
        Map<String, LineString> segmentMap;
        List<LineString> segmentList = RingUtil.ringToSegmentList(ring);
        segmentMap = new LinkedHashMap<String, LineString>(segmentList.size());

        for( LineString segment : segmentList ) {
            
            // adapt the segment ring with the intersection points
            if(splitLine != null){
                Geometry intersection = splitLine.intersection(segment);
                LineString adaptedSegment = segment;
                if(intersection instanceof Point || intersection instanceof MultiPoint){
                    for( int i = 0; i < intersection.getNumGeometries(); i++ ) {
                        Point vertex = (Point) intersection.getGeometryN(i);
                        adaptedSegment = SplitUtil.insertVertexInLine(adaptedSegment, vertex.getCoordinate());
                    }
                } 
                segmentMap.put(adaptedSegment.toText(), adaptedSegment );
            } else { // add the original segment
                segmentMap.put(segment.toText(), segment );
            }
        }
        return segmentMap;
    }


    @Override
    public String toString() {

        StringBuilder str = new StringBuilder(100);
        for( Map<String, LineString> ringAdaptedSegmentLink : this.ringSegmentAdaptedAssociation.values() ) {
            
            str.append("{"); //$NON-NLS-1$
            for( Map.Entry<String, LineString> segmentAndAdapted : ringAdaptedSegmentLink.entrySet() ) {
                str.append("[Original ["); //$NON-NLS-1$
                str.append(segmentAndAdapted.getKey());
                str.append("] "); //$NON-NLS-1$
                str.append("Adapted ["); //$NON-NLS-1$
                str.append(segmentAndAdapted.getValue());
                str.append("]] "); //$NON-NLS-1$
            }
            str.append("}"); //$NON-NLS-1$
        }
        return str.toString();
    }

    /**
     * @return the original polygon
     */
    public Polygon getOriginalPolygon() {
        return originalPolygon;
    }

    /**
     * @return new instance of polygon adapted polygon
     */
    public Polygon asPolygon() {

        // makes the exterior ring using the modified segments
        LinearRing originalExteriorRing = (LinearRing) this.originalPolygon.getExteriorRing();
        
        LinearRing exteriorRing = buildAdaptedRing(originalExteriorRing);
        
        // makes the interior rings using the modified hole segments
        final Set<Entry<LinearRing, Map<String,LineString>>> entrySet = this.ringSegmentAdaptedAssociation.entrySet();
        
        LinearRing[] holes = new LinearRing[entrySet.size()-1];
        int i = 0;
        for( Entry<LinearRing, Map<String, LineString>> entry : entrySet  ) {
            
            LinearRing currentRing = entry.getKey();
            if( ! currentRing.equals(originalExteriorRing) ){
                // it is an interior ring (hole)
                LinearRing holeRing = buildAdaptedRing(currentRing);

                holes[i++] = holeRing;
            }
        }
        Polygon polygon = this.geomFactory.createPolygon(exteriorRing, holes);

        return polygon;
    }

    private LinearRing buildAdaptedRing( LinearRing ring ) {
        
        // retrieves the adapted segments which are associated to the ring
        final Map<String, LineString> exteriorAdaptedSegment = lookupRing(this.ringSegmentAdaptedAssociation, ring);
        assert exteriorAdaptedSegment != null;

        // Extracts the coordinates of the adapted segments
        List<LineString> values = new GeometryList<LineString>( exteriorAdaptedSegment.values() );
        LinearRing newRing = RingUtil.segmentListToRing( values );

        return newRing;
    }

    /**
     * This is a workaround because the Map.get() method does not work with geometry. That occurs
     * because Geometry does not implement hashCode method.
     * 
     * @param ringSegmentAdaptedAssociation2
     * @param requestRing
     * @return the segments rings
     */
    private Map<String, LineString> lookupRing(
            final Map<LinearRing, Map<String, LineString>> ringSegmentAdaptedAssociation, 
            final LinearRing requestRing ) {
        
        Map<String, LineString> segmentsOfRing = null;
        
        for( Entry<LinearRing, Map<String, LineString>> ringSegmentList : ringSegmentAdaptedAssociation.entrySet() ) {
            
            if(ringSegmentList.getKey().equals(requestRing)){
                segmentsOfRing = ringSegmentList.getValue();
                break;
            }
        }
        return segmentsOfRing;
    }


    /**
     * Inserts the vertex in the indeed ring segment in order to adapt the polygon.
     * 
     * @param orignalRing          ring that contain the segment
     * @param originalRingSegment   the segment to replace
     * @param coordinates
     */
    public void insertVertex( final LinearRing orignalRing, final LineString originalRingSegment, final Coordinate intersection ) {
       
        // search the ring, if it exist then adds the new vertex
        Map<String,LineString> segmentMap =  lookupRing(this.ringSegmentAdaptedAssociation, orignalRing);
        assert segmentMap != null: "inexistent ring"; //$NON-NLS-1$
        
        // search the segment to modify
        String wktSegment = originalRingSegment.toText();
        LineString adaptedSegment = segmentMap.get(wktSegment);
        assert adaptedSegment != null: "inexistent segment"; //$NON-NLS-1$

        LineString newAdaptedSegment = SplitUtil.insertVertexInLine(adaptedSegment, intersection);
        
        segmentMap.put(wktSegment, newAdaptedSegment);
    }

    /**
     * Returns the adapted ring associated to the original ring
     * @param originalRing
     * @return the adapted ring
     */
    public LinearRing getAdaptedRingOf( LinearRing originalRing ) {
        
        LinearRing adaptedRing = buildAdaptedRing(originalRing);
        
        return adaptedRing;
    }
}
