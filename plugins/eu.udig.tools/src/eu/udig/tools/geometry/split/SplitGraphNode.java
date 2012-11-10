/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Diputación Foral de Gipuzkoa, Ordenación Territorial 
 *
 *      http://b5m.gipuzkoa.net
 *      http://www.axios.es 
 *
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial (DFG-OT). 
 * DFG-OT agrees to licence under Lesser General Public License (LGPL).
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
 */
package eu.udig.tools.geometry.split;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geomgraph.DirectedEdge;
import com.vividsolutions.jts.geomgraph.EdgeEnd;
import com.vividsolutions.jts.geomgraph.Node;

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