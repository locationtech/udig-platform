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
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geomgraph.Edge;
import org.locationtech.jts.geomgraph.NodeFactory;
import org.locationtech.jts.geomgraph.PlanarGraph;

/**
 * This graph is built using the polygon to split and ths split line.
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
final class Graph extends PlanarGraph{
    
    /* Factory used for nodding edges. Required by the superclass. */
    private static final NodeFactory    NODE_FACTORY        = new SplitGraphNodeFactory();

    
    /**
     * a new instance of Graph
     */
    public Graph(){
        
        super(NODE_FACTORY);
        
    }
    

    /**
     * Adds the edges for the given hole list.
     * 
     * @param nodedHolesList
     *            List of hole rings.
     * @param boundary
     *            position for ON.
     * @param interior
     *            position for LEFT.
     * @param exterior
     *            position for RIGHT.
     */
    public void addEdges(final List<Geometry> nodedHolesList,final  int boundary, final int interior, final int exterior) {

        for (Geometry geom : nodedHolesList) {
            if (!geom.isEmpty()){
                addEdges(geom, boundary, interior, exterior);
            }
        }
    }
    


    /**
     * Add edges for the provided geometry.
     * 
     * @param linearGeom
     *            The geometry which edges will be based on.
     * @param onLoc
     *            position for ON.
     * @param leftLoc
     *            position for LEFT.
     * @param rightLoc
     *            position for RIGHT.
     */
    private void addEdges(final Geometry linearGeom, final int onLoc,final  int leftLoc,final  int rightLoc) {

        final int nParts = linearGeom.getNumGeometries();
        List<SplitEdge> edges = new ArrayList<SplitEdge>();

        for (int i = 0; i < nParts; i++) {

            SplitEdge edge = createEdge(linearGeom, i, onLoc, leftLoc, rightLoc);
            edges.add(edge);
        }
        // for each edge in the list, adds two DirectedEdge, one reflecting
        // the given edge and other the opposite
        super.addEdges(edges);
    }
    
    /**
     * Create and edge with the bases geometry and the the given location.
     * 
     * @param linearGeom
     * @param i
     * @param onLoc
     * @param leftLoc
     * @param rightLoc
     * @return An split edge.
     */
    private SplitEdge createEdge(final Geometry linearGeom,final  int i,final  int onLoc,final  int leftLoc, final int rightLoc) {

        Geometry currGeom = linearGeom.getGeometryN(i);
        Coordinate[] coords = currGeom.getCoordinates();
        
        final SplitEdge edge = SplitEdge.newInstance(coords, onLoc, leftLoc, rightLoc);
        
        return edge;
    }


    public void addEdge( final SplitEdge edge ) {
        
        List<Edge> edges = new ArrayList<Edge>();
        edges.add(edge);
        
        super.addEdges(edges);        
    }


    

    

}
