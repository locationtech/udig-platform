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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geomgraph.DirectedEdge;
import org.locationtech.jts.geomgraph.EdgeEnd;
import org.locationtech.jts.geomgraph.Node;

/**
 * Custom node type. Does nothing special by now but to force the use of
 * {@link SplitEdgeStar} instances as the node's list of incident edges and
 * allow to remove edges from its list of incident edges.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
class SplitGraphNode extends Node {

	/**
	 * Constructor for split graph node.
	 * 
	 * @param coord
	 *            Provided coordinate
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
	 * Removes the given <code>edge</code> from this node's {@link #getEdges()
	 * edge list}.
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
