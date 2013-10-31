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
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.MarkImagePresenter;

/**
 * Tool used to delete marks from the image.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
public class DeleteMarkImageTool extends AbstractImageTool {

	public DeleteMarkImageTool(Cursor cursor, ImageComposite imgComposite) {
		super(cursor, imgComposite);
	}

	@Override
	protected boolean canHandle(ImageInputEvent ev) {

		if (InputEvent.MOUSE_DRAG.equals(ev.event) || InputEvent.MOUSE_UP.equals(ev.event)) {
			return true;
		}
		return false;
	}

	/**
	 * Shows the feedback when the tool is over a mark.
	 * 
	 * If a click is done over a mark, it'll be deleted.
	 * 
	 * @param ev
	 *            Image input event.
	 */
	@Override
	protected boolean executeBehaviour(ImageInputEvent ev) {

		if (InputEvent.MOUSE_DRAG.equals(ev.event)) {
			showFeedback(ev.x, ev.y, ev.event);
		}

		if (InputEvent.MOUSE_UP.equals(ev.event)) {

			MarkImagePresenter presenter = this.imgComposite.getMarkUnderCursor(ev.x, ev.y);
			if (presenter == null) {
				return false;
			}
			this.imgComposite.getCmd().deleteMark(presenter.getMarkModel());
			this.imgComposite.getCmd().evalPrecondition();
		}

		return false;
	}
}
