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
import com.vividsolutions.jts.geomgraph.Node;
import com.vividsolutions.jts.geomgraph.NodeFactory;

/**
 * Custom node factory to create {@link SplitGraphNode}s initialized with an
 * empty {@link SplitEdgeStar}
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.1.0
 * @see SplitGraphNode
 */
class SplitGraphNodeFactory extends NodeFactory {

	/**
	 * Create a node with the provided coordinate.
	 * 
	 * @param coord
	 *            The coordinate for creating an splitGraphNode.
	 */
	@Override
	public Node createNode(Coordinate coord) {

		return new SplitGraphNode(coord, new SplitEdgeStar());
	}
}