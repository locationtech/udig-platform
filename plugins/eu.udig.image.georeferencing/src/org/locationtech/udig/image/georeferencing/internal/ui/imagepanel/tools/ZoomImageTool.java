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

import org.eclipse.swt.graphics.Cursor;
import org.locationtech.udig.image.georeferencing.internal.ui.InputEvent;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;


/**
 * Common behaviour for the zoom in and zoom out tools.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
abstract class ZoomImageTool extends AbstractImageTool {

	public ZoomImageTool(Cursor cursor, ImageComposite imgComposite) {
		super(cursor, imgComposite);
	}

	@Override
	protected boolean canHandle(ImageInputEvent ev) {

		if (InputEvent.MOUSE_UP.equals(ev.event)) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.axios.udig.ui.georeferencing.internal.ui.imagepanel.tools.
	 * AbstractImageTool
	 * #executeBehaviour(es.axios.udig.ui.georeferencing.internal
	 * .ui.imagepanel.tools.ImageInputEvent,
	 * es.axios.udig.ui.georeferencing.internal.ui.MarkPresenter)
	 */
	@Override
	protected boolean executeBehaviour(ImageInputEvent ev) {

		changeZoomScale();

		this.imgComposite.focusPosition(ev.x, ev.y);

		return false;
	}

	protected abstract void changeZoomScale();

}
