/* uDig-Spatial Operations plugins
 * http://b5m.gipuzkoa.net
 * (C) 2006, Diputación Foral de Gipuzkoa, Ordenación Territorial.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.tools.split;

import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.behaviour.AddVertexWhileCreatingBehaviour;
import net.refractions.udig.tools.edit.support.Point;

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
