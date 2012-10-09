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
package eu.udig.image.georeferencing.internal.ui.imagepanel.tools;

import org.eclipse.swt.graphics.Cursor;

import eu.udig.image.georeferencing.internal.ui.InputEvent;
import eu.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;

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
