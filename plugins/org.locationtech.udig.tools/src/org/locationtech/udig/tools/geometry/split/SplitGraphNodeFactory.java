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
import org.locationtech.jts.geomgraph.Node;
import org.locationtech.jts.geomgraph.NodeFactory;

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
