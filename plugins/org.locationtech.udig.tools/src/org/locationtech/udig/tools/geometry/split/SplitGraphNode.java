/*******************************************************************************
 * Copyright (c) 2006,2012,2013 County Council of Gipuzkoa, Department of Environment
 *                              and Planning and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Aritz Davila (Axios) - initial API, implementation, and documentation
 *    Mauricio Pazos (Axios) - initial API, implementation, and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.geometry.split;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geomgraph.DirectedEdge;
import com.vividsolutions.jts.geomgraph.EdgeEnd;
import com.vividsolutions.jts.geomgraph.Node;

/**
 * Custom node type. Does nothing special by now but to force the use of {@link SplitEdgeStar}
 * instances as the node's list of incident edges and allow to remove edges from its list of
 * incident edges.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
class SplitGraphNode extends Node {

    /**
     * Constructor for split graph node.
     * 
     * @param coord Provided coordinate
     * @param incidentEdges
     */
    public SplitGraphNode(Coordinate coord, SplitEdgeStar incidentEdges) {

        super(coord, incidentEdges);
    }

    /**
     * Add a directed edge.
     * 
     * @param edge
     */
    public void add(DirectedEdge edge) {

        super.add(edge);
    }

    @Override
    public void add(EdgeEnd edge) {

        add((DirectedEdge) edge);
    }

    /**
     * Removes the given <code>edge</code> from this node's {@link #getEdges() edge list}.
     * 
     * @param edge
     */
    public void remove(DirectedEdge edge) {

        SplitEdgeStar edges = (SplitEdgeStar) getEdges();
        edges.remove(edge);
    }

    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer("Node["); //$NON-NLS-1$
        sb.append(coord.x).append(":").append(coord.y); //$NON-NLS-1$
        sb.append(", ").append(label); //$NON-NLS-1$
        sb.append(", ").append(getEdges()); //$NON-NLS-1$
        sb.append("]"); //$NON-NLS-1$
        return sb.toString();
    }
}
