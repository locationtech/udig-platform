/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Wien Government 
 *
 *      http://wien.gov.at
 *      http://www.axios.es 
 *
 * (C) 2010, Vienna City - Municipal Department of Automated Data Processing, 
 * Information and Communications Technologies.
 * Vienna City agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.internal.geometry.split;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geomgraph.Edge;
import com.vividsolutions.jts.geomgraph.NodeFactory;
import com.vividsolutions.jts.geomgraph.PlanarGraph;

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

            addEdges(geom, boundary, interior, exterior);
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
