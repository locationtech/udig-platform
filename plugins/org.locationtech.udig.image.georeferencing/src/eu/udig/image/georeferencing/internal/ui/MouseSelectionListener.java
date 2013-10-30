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
package org.locationtech.udig.image.georeferencing.internal.ui;

import org.locationtech.udig.image.georeferencing.internal.process.MarkModel;

/**
 * Listener used know when the mouse is going to select a presenter and its
 * associated mark or not.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
public interface MouseSelectionListener {

	/**
	 * Mouse is inside the extent of the mark presenter.
	 * 
	 * @param mark
	 *            The mark model contained its associated presenter.
	 */
	public void inEvent(MarkModel mark);

	/**
	 * Mouse is outside the extent of the mark presenter.
	 * 
	 * @param mark
	 *            The mark model contained its associated presenter.
	 */
	public void outEvent(MarkModel mark);

}
