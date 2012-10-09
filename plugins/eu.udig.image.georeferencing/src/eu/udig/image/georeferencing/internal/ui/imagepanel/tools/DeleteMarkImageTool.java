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
import eu.udig.image.georeferencing.internal.ui.imagepanel.MarkImagePresenter;

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
