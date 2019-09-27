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
package org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools;

import org.locationtech.jts.geom.Coordinate;

/**
 * This listener will be used to broadcast which coordinate has been captured or
 * added.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public interface CapturedCoordinateListener extends CoordinateToolListener {

	/**
	 * Uses the captured/added coordinate.
	 * 
	 * @param newCoord
	 *            Added coordinate.
	 */
	public void capturedCoordinate(Coordinate newCoord);

}
