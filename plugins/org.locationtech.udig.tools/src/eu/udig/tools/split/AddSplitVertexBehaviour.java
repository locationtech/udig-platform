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
package org.locationtech.udig.tools.split;

import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.behaviour.AddVertexWhileCreatingBehaviour;
import org.locationtech.udig.tools.edit.support.Point;

/**
 * Inherit in order to redefines the {{@link #isNotDuplicated(EditToolHandler, Point)} 
 * 
 * <p>
 * Requirements:
 * <ul>
 * <li>event type == RELEASE</li>
 * <li>edit state == CREATING</li>
 * <li>no modifiers down</li>
 * <li>button 1 released</li>
 * <li>no buttons down</li>
 * <li>current shape and geometry are set</li>
 * <li>mouse is not over a vertex of the current shape</li>
 * </ul>
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
class AddSplitVertexBehaviour extends AddVertexWhileCreatingBehaviour {

	/**
	 * This split tool allows duplicate vertex. Thus this method is override to return true.
	 * @param handler
	 * @param e
	 * @return true If this point is allowed
	 */
	@Override
	protected boolean isNotDuplicated(EditToolHandler handler, Point point) {
	    return true; 
	}
}
