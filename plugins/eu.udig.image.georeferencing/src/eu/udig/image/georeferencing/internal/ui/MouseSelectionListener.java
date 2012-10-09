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
package eu.udig.image.georeferencing.internal.ui;

import eu.udig.image.georeferencing.internal.process.MarkModel;

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
