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
package org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools;

import org.eclipse.swt.events.MouseEvent;

import org.locationtech.udig.image.georeferencing.internal.ui.InputEvent;

/**
 * Associated an {@link InputEvent} to a point X and Y.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
public class ImageInputEvent {

	public InputEvent	event;
	public int			x, y;
	public MouseEvent	mouseEvent;

	public ImageInputEvent(MouseEvent mouseEvent, InputEvent event, int x, int y) {

		this.mouseEvent = mouseEvent;
		this.event = event;
		this.x = x;
		this.y = y;
	}
}
