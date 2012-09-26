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
import org.eclipse.swt.graphics.Point;

import eu.udig.image.georeferencing.internal.ui.InputEvent;
import eu.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;
import eu.udig.image.georeferencing.internal.ui.imagepanel.ImagePanelUtil;
import eu.udig.image.georeferencing.internal.ui.imagepanel.MarkImagePresenter;

/**
 * Tool used to move marks within the image.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.0.0
 * 
 */
public class MoveMarkImageTool extends AbstractImageTool {

	private MarkImagePreview	markPreview	= null;

	public MoveMarkImageTool(Cursor cursor, ImageComposite imgComposite) {
		super(cursor, imgComposite);
	}

	@Override
	protected boolean canHandle(ImageInputEvent ev) {

		if (InputEvent.MOUSE_DRAG.equals(ev.event) || InputEvent.MOUSE_UP.equals(ev.event)
					|| InputEvent.MOUSE_DOWN.equals(ev.event)) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean executeBehaviour(ImageInputEvent ev) {

		MarkImagePresenter presenter = this.imgComposite.getMarkUnderCursor(ev.x, ev.y);
		dragAndDrop(ev.x, ev.y, ev.event, presenter);
		return false;
	}

	/**
	 * Handles the event Drag&Drop, which is triggered by the Move tool. It also
	 * manages the show feedback
	 * 
	 * <p>
	 * D&D:
	 * 
	 * It'll do 3 different actions:
	 * </p>
	 * <p>
	 * 1- When a mouse down happens, find the mark presenter that is under the
	 * current mouse position and create a {@link MarkImagePreview} if there
	 * isn't anyone.
	 * </p>
	 * <p>
	 * 2-When mouse is being dragged, updates the preview with the current
	 * position.
	 * </p>
	 * <p>
	 * 3-When a mouse up happens, if the current mouse position is a valid
	 * position to drop a mark, it will update the current mark position within
	 * the image.
	 * </p>
	 * 
	 * <p>
	 * Show feedback:
	 * 
	 * It'll show a feedback when there isn't any markPreview, the mouse is
	 * moving around the canvas and there is a mark under the cursor position.
	 * </p>
	 * 
	 * @param x
	 *            Position in the canvas.
	 * @param y
	 *            Position in the canvas.
	 * @param inputEvent
	 *            Input event.
	 */
	private void dragAndDrop(int x, int y, InputEvent inputEvent, MarkImagePresenter presenter) {

		switch (inputEvent) {

		case MOUSE_DOWN:// D&D has started.

			if (presenter != null && this.markPreview == null) {

				this.markPreview = new MarkImagePreview(presenter, this.imgComposite.getCanvas());
			}
			break;
		case MOUSE_DRAG:// continue D&D

			if (this.markPreview != null) {
				this.markPreview.eventHandler(inputEvent, x, y);
			} else {
				showFeedback(x, y, inputEvent);
			}
			break;
		case MOUSE_UP: // D&D has finished
			if (this.markPreview != null) {

				int hScroll = Math.abs(this.imgComposite.getHScrollValue());
				int vScroll = Math.abs(this.imgComposite.getVScrollValue());
				Point point = ImagePanelUtil.createMarkPosition(hScroll, vScroll, x, y, this.imgComposite.getScale());

				// validate if the X and Y are inside the image.
				if (validateInside(point)) {

					// update the mark model.
					this.markPreview.getMarkModel().updateImagePosition(point);
				}
				// delete this preview.
				this.markPreview.delete();
				this.markPreview = null;
				this.imgComposite.canvasRedraw();
			}
			break;
		default:
			break;
		}
	}

}
