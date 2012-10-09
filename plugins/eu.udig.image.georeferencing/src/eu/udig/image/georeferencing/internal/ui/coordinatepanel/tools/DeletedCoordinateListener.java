/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios) 
 * Axios agrees to licence under Lesser General Public License (LGPL).
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
package eu.udig.image.georeferencing.internal.ui.coordinatepanel.tools;

import java.awt.Point;

import eu.udig.image.georeferencing.internal.ui.InputEvent;

/**
 * This listener will be used to broadcast on which point the user has clicked
 * with the purpose of deleting the presenter under its cursor.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public interface DeletedCoordinateListener extends CoordinateToolListener {

	/**
	 * The current event and the point where it was clicked on the map.
	 * 
	 * @param coor
	 *            Point where it was clicked.
	 * @param event
	 *            Input event.
	 */
	public void deletedCoordinate(Point coor, InputEvent event);
}
