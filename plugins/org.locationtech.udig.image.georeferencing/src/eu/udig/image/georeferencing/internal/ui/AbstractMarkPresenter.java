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
package eu.udig.image.georeferencing.internal.ui;

import java.awt.Color;
import java.awt.Rectangle;


/**
 * Abstract class for the presenters.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
public abstract class AbstractMarkPresenter implements MarkPresenter {

	protected boolean	hide		= false;
	protected Rectangle	extent		= null;
	protected boolean	feedback	= false;

	public boolean eventHandler(InputEvent event, float scale) {
		// null implementation
		return false;
	}
	/**
	 * Tells this presenter to show the feedback.
	 * 
	 * @param feedback
	 */
	public void showSelectedFeedback(boolean feedback) {

		this.feedback = feedback;
	}	

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.axios.udig.ui.georeferencing.internal.ui.MarkPresenter#delete()
	 */
	public void delete() {

		hide = true;

	}

	protected Color createColor(int number) {
		if (number >= COLORS.length) {
			number = number % (COLORS.length);
		}
		return COLORS[number];
	}

	/**
	 * Check if the given X and Y are inside the presenter extent.
	 * 
	 * @param x
	 *            X position.
	 * @param y
	 *            Y position.
	 * @return True if the x and y are inside the extent.
	 */
	protected boolean extentContains(int x, int y) {

		if (extent == null) {
			return false;
		}
		// see if the the point is inside the extend.
		if (extent.contains(x, y)) {

			return true;
		}
		return false;
	}
}
