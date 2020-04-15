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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geomgraph.DirectedEdge;
import org.locationtech.jts.geomgraph.DirectedEdgeStar;
import org.locationtech.jts.geomgraph.EdgeEnd;

/**
 * A {@link DirectedEdgeStar} for the {@link SplitGraphNode nodes} in a
 * {@link SplitGraphBuilder}
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 */
class SplitEdgeStar extends DirectedEdgeStar {

	/**
	 * Adds a DirectedEdge to the list of incident edges on this star
	 * 
	 * @param de
	 *            non null directed edge to insert on this node star
	 */
	public void insert(DirectedEdge de) {

		assert de != null : "edge can not be null"; //$NON-NLS-1$
		insertEdgeEnd(de, de);
	}

	/**
	 * Overrides {@link DirectedEdgeStar#insert(EdgeEnd)} just to delegate to
	 * {@link #insert(DirectedEdge)} forcing the argument type
	 */
	@Override
	public void insert(EdgeEnd ee) {

		insert((DirectedEdge) ee);
	}

	/**
	 * Removes the given edge from this edge star
	 * 
	 * @param edge
	 * @throws IllegalArgumentException
	 *             if <code>edge</code> is not one of this star's edges
	 */
	public void remove(DirectedEdge edge) {

		assert edge != null : "edge can not be null"; //$NON-NLS-1$

		int degree = getDegree();
		Object removed = edgeMap.remove(edge);
		int afterDegree = getDegree();
		assert afterDegree == degree - 1;

		if (edge != removed) {
			throw new IllegalArgumentException("Tried to remove an edge not registered in this edge star: " + edge); //$NON-NLS-1$
		}
		edgeList = null; // edge list has changed - clear the cache
	}

	/**
	 * Returns the list of Directed edges whose direction is outgoing from this
	 * star's node. That is, for all the DirectedEdges in the star, if the
	 * edge's start point is coincident whith the edge star node, returns the
	 * same DirectedNode, otherwise returns the edge's
	 * {@link DirectedEdge#getSym() symmetric edge}.
	 * 
	 * @return The list of Directed edges whose direction is outgoing from this
	 *         star's node.
	 */

	@SuppressWarnings("unchecked")
	private List<DirectedEdge> getOutgoingEdges() {

		final Coordinate nodeCoord = getCoordinate();
		final List<DirectedEdge> edges = getEdges();
		final List<DirectedEdge> outgoingEdges = new ArrayList<DirectedEdge>(edges.size());

		for (Iterator<DirectedEdge> it = edges.iterator(); it.hasNext();) {
			DirectedEdge edge = (DirectedEdge) it.next();
			if (!nodeCoord.equals2D(edge.getCoordinate())) {
				edge = edge.getSym();
			}
			assert nodeCoord.equals2D(edge.getCoordinate());
			outgoingEdges.add(edge);
		}
		return outgoingEdges;
	}

	/**
	 * Finds the edge with less angle, seeking in the given direction.
	 * 
	 * @param searchDirection
	 *            one of {@link CGAlgorithms#CLOCKWISE},
	 *            {@link CGAlgorithms#COUNTERCLOCKWISE}
	 * @return the edge forming the acutest angle with <code>edge</code> in the
	 *         <code>prefferredDirection</code> or <code>null</code> if there
	 *         are no edges in the preferred direction.
	 */
	public DirectedEdge findClosestEdgeInDirection(final DirectedEdge edge, final int searchDirection) {

		assert edge != null : "edge can not be null"; //$NON-NLS-1$
		assert getDegree() >= 2 : "there must be at least two edges in the edge star" + " :" + ((SplitEdge) edge.getEdge()).toString(); //$NON-NLS-1$ //$NON-NLS-2$

		final Coordinate nodeCoord = getCoordinate();

		assert nodeCoord.equals2D(edge.getCoordinate());

		double acutestAngle = Double.MAX_VALUE;
		DirectedEdge acutest = null;
		DirectedEdge adjacentEdge = null;

		final Coordinate tip1 = edge.getDirectedCoordinate();
		final Coordinate tail = nodeCoord;

		// ensure we're using outgoing edges
		final List<DirectedEdge> outgoingEdges = getOutgoingEdges();
		Iterator<DirectedEdge> it = outgoingEdges.iterator();
		while (it.hasNext()) {
			adjacentEdge = (DirectedEdge) it.next();

			if (adjacentEdge == edge) {
				continue;
			}

			Coordinate tip2 = adjacentEdge.getDirectedCoordinate();

			double angle = computeAngleInDirection(tip1, tail, tip2, searchDirection);

			if (angle < acutestAngle) {
				acutestAngle = angle;
				acutest = adjacentEdge;
			}
		}

		return acutest;
	}

	/**
	 * Computes the angle comprised between the vector <code>tail:tip1</code>
	 * looking in the specified <code>direction</code> to the vector
	 * <code>tail:tip2</code>
	 * 
	 * @param tip1
	 * @param tail
	 * @param tip2
	 * @param direction
	 *            one of {@link CGAlgorithms#CLOCKWISE},
	 *            {@link CGAlgorithms#COUNTERCLOCKWISE}
	 * @return the angle in radians defined by the vectors tail-tip1:tail-tip2
	 *         calculated in the specified <code>direction</code> from tail-tip1
	 */
	public double computeAngleInDirection(Coordinate tip1, Coordinate tail, Coordinate tip2, int direction) {

		final int orientation = CGAlgorithms.computeOrientation(tail, tip1, tip2);

		// minimal angle (non oriented)
		double angle = Angle.angleBetween(tip1, tail, tip2);
		if (orientation != direction) {
			angle = Angle.PI_TIMES_2 - angle;
		}
		return angle;
	}

	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer("SplitEdgeStar[degree: "); //$NON-NLS-1$
		sb.append(getDegree()).append(", edges: "); //$NON-NLS-1$
		for (Iterator<?> it = getEdges().iterator(); it.hasNext();) {
			DirectedEdge de = (DirectedEdge) it.next();
			sb.append("DirectedEdge["); //$NON-NLS-1$
			sb.append(de.getEdge()).append(" "); //$NON-NLS-1$
			sb.append("]"); //$NON-NLS-1$
		}
		sb.append("]"); //$NON-NLS-1$
		return sb.toString();
	}

}
