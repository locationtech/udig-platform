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
import org.eclipse.swt.graphics.Rectangle;

import eu.udig.image.georeferencing.internal.ui.InputEvent;
import eu.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;

/**
 * Tool used to pan the image.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
public class PanImageTool extends AbstractImageTool {

	/* Used by the image pan method */
	private int	xScrollStart	= -1;
	private int	yScrollStart	= -1;

	public PanImageTool(Cursor cursor, ImageComposite imgComposite) {
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

		imagePan(ev.x, ev.y, ev.event);

		return false;
	}

	/**
	 * <p>
	 * Performs a drag&drop operation.
	 * 
	 * On mouse down, it captures the start position, on mouse drag, it performs
	 * the pan operation and on mouse up, it reset the start position variables
	 * ending the cycle.
	 * </p>
	 * 
	 * @param x
	 *            Position in the canvas.
	 * @param y
	 *            Position in the canvas.
	 * @param eventType
	 *            Input event.
	 */
	private void imagePan(int x, int y, InputEvent eventType) {

		switch (eventType) {
		case MOUSE_DOWN:
			// start
			xScrollStart = x + Math.abs(this.imgComposite.getHScrollValue());
			yScrollStart = y + Math.abs(this.imgComposite.getVScrollValue());
			break;
		case MOUSE_DRAG:
			// continue
			if (xScrollStart != -1 && yScrollStart != -1) {
				doPan(x, y);
			}
			break;
		case MOUSE_UP:
			// end
			xScrollStart = -1;
			yScrollStart = -1;
			break;
		default:
			break;
		}
	}

	/**
	 * <p>
	 * Performs the pan operation.
	 * 
	 * Given the X and Y positions, calculates if it can continue scrolling and
	 * update the scroll variables inside the {@link ImageMetricPosition} class.
	 * After doing it, broadcast this event to the presenters so they update and
	 * redraw the image.
	 * </p>
	 * 
	 * @param x
	 *            Position in the canvas.
	 * @param y
	 *            Position in the canvas.
	 */
	private void doPan(int x, int y) {

		Rectangle bounds = this.imgComposite.getCanvasClientArea();

		int xScrollSelection = x - xScrollStart;
		if ((xScrollSelection != 0) && (xScrollSelection + this.imgComposite.getMaxX() < bounds.width)) {

			xScrollSelection = bounds.width - this.imgComposite.getMaxX();
		}
		if ((xScrollSelection > 0)) {
			xScrollSelection = 0;
		}

		int yScrollSelection = y - yScrollStart;
		if ((yScrollSelection != 0) && (yScrollSelection + this.imgComposite.getMaxY() < bounds.height)) {

			yScrollSelection = bounds.height - this.imgComposite.getMaxY();
		}
		if ((yScrollSelection > 0)) {
			yScrollSelection = 0;
		}

		this.imgComposite.updateScrollValues(xScrollSelection, yScrollSelection);
		this.imgComposite.broadcastPanEvent();
		this.imgComposite.canvasRedraw();
	}

}
