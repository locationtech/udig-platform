/* Image Georeferencing
 * 
 * Axios Engineering 
 *      http://www.axios.es 
 *
 * (C) 2011, Axios Engineering S.L. (Axios)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Axios BSD
 * License v1.0 (http://udig.refractions.net/files/asd3-v10.html).
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
