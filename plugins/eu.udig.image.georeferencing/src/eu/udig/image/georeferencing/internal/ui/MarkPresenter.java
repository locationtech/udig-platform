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

import java.awt.Color;
import java.util.Observer;

import eu.udig.image.georeferencing.internal.process.MarkModel;

/**
 * Interface implemented by all the presenters that show marks.
 * 
 * It extends {@link Observer} because all the presenter will be observers of
 * the {@link MarkModel}.
 * 
 * @author Aritz Davila
 * @author Mauricio Pazos
 * @since 1.0.0
 * 
 */
public interface MarkPresenter extends Observer {

	public static final Color[]	COLORS	= {
			Color.GREEN,
			Color.BLUE,
			Color.CYAN,
			Color.RED,
			Color.MAGENTA,
			new Color(128, 0, 128),
			new Color(204, 204, 204),
			new Color(255, 97, 1),
			new Color(195, 227, 45)	};

	void showSelectedFeedback(boolean b);
	
	/**
	 * Method created to handle any input event by the presenters.
	 * 
	 * @param event
	 *            Input Event.
	 * @param x
	 *            Position.
	 * @param y
	 *            Position.
	 * @return True if the presenter is affected by this event.
	 */
	boolean eventHandler(InputEvent event, int x, int y);

	/**
	 * Method created to handle any input event by the presenters.
	 * 
	 * @param event
	 *            Input event.
	 * @param scale
	 *            Zoom scale.
	 * @return True if the presenter is affected by this event.
	 */
	boolean eventHandler(InputEvent event, float scale);

	/**
	 * Draw the presenter.
	 */
	void draw();

	/**
	 * Delete the presenter, won't draw it again.
	 */
	void delete();

	/**
	 * 
	 * @return The mark model represented by this presenter.
	 */
	MarkModel getMarkModel();
}