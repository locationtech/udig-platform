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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;

import eu.udig.image.georeferencing.internal.process.MarkModel;
import eu.udig.image.georeferencing.internal.ui.InputEvent;
import eu.udig.image.georeferencing.internal.ui.MouseSelectionListener;
import eu.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;
import eu.udig.image.georeferencing.internal.ui.imagepanel.MarkImagePresenter;

/**
 * Abstract class for the tools used by the {@link ImageComposite}.
 * 
 * It'll handle the event and decide if it runs the behaviour associated to the
 * subclass tool.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.0.0
 * 
 */
abstract class AbstractImageTool implements ImageTool {

	protected Cursor						cursor			= null;
	protected ImageComposite				imgComposite	= null;
	protected boolean						active			= false;

	protected List<MouseSelectionListener>	listeners		= new LinkedList<MouseSelectionListener>();

	/**
	 * Constructor of the tools. It has an specific cursor and knows the parent
	 * composite, which will be the {@link ImageComposite}.
	 * 
	 * @param cursor
	 *            Tool cursor.
	 * @param imgComposite
	 *            Parent composite.
	 */
	protected AbstractImageTool(Cursor cursor, ImageComposite imgComposite) {

		this.cursor = cursor;
		this.imgComposite = imgComposite;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return True if this is the active tool.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Handle the event. Check if it's the active tool and if any of the mark
	 * presenters is affected by the current point.
	 * 
	 * @param ev
	 *            Event.
	 */
	public boolean eventHandle(ImageInputEvent ev) {

		if (!isActive()) {
			return false;
		}

		if (!isScrolling(ev) && !canHandle(ev)) {
			return false;
		}

		if (InputEvent.MOUSE_SCROLL.equals(ev.event)) {
			return executeMouseScrollBehaviour(ev);
		} else {
			return executeBehaviour(ev);
		}
	}

	/**
	 * This is a common behaviour for all the tools.
	 * 
	 * @param ev
	 *            Image input event.
	 * @return
	 */
	private boolean executeMouseScrollBehaviour(ImageInputEvent ev) {

		if (ev.mouseEvent.count == 3) {
			scrollZoomIn(ev.x, ev.y);
		} else {
			scrollZoomOut(ev.x, ev.y);
		}

		return true;
	}

	/**
	 * Handles the scroll zoom in.
	 * 
	 * @param x
	 *            Position.
	 * @param y
	 *            Position.
	 */
	private void scrollZoomIn(int x, int y) {

		this.imgComposite.setZoomInFeedback(x, y);
		this.imgComposite.increaseScale(0.1f);
		this.imgComposite.focusPosition(x, y);

	}

	/**
	 * Handles the scroll zoom out.
	 * 
	 * @param x
	 *            Position.
	 * @param y
	 *            Position.
	 */
	private void scrollZoomOut(int x, int y) {

		this.imgComposite.setZoomOutFeedback(x, y);
		this.imgComposite.decreaseScale(0.1f);
		this.imgComposite.focusPosition(x, y);
	}

	/**
	 * Checks if the current event is the mouse scroll event.
	 * 
	 * @param ev
	 *            Image input event.
	 * @return True if the event equals to the scrolling event.
	 */
	private boolean isScrolling(ImageInputEvent ev) {

		if (InputEvent.MOUSE_SCROLL.equals(ev.event)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Behaviour of each tool. It'll perform different actions depending on the
	 * events and the actual subclass tool.
	 * 
	 * @param ev
	 *            Event.
	 * 
	 * @return True if the tool has a behaviour associate to this event, false
	 *         otherwise.
	 */
	abstract protected boolean executeBehaviour(ImageInputEvent ev);

	/**
	 * <pre>
	 * 
	 * Check if the tool can handle the incoming events.
	 * The tool must be active and the events must fulfill its requirements.
	 * 
	 * </pre>
	 * 
	 * @param ev
	 * @return
	 */
	abstract protected boolean canHandle(ImageInputEvent ev);

	/**
	 * If there is any mark presenter under this (x,y) position, it'll tell that
	 * presenter to show his feedback.
	 * 
	 * @param x		Position in the canvas.
	 * @param y		Position in the canvas.
	 * @param 		inputEvent
	 *            
	 */
	protected void showFeedback(int x, int y, InputEvent inputEvent) {

		for (MarkImagePresenter presenter : this.imgComposite.getMarkPresenterList()) {

			if (presenter.eventHandler(inputEvent, x, y)) {
				presenter.showSelectedFeedback(true);
				broadcastMouseInFeedback(presenter.getMarkModel());
			} else {
				presenter.showSelectedFeedback(false);
				broadcastMouseOutFeedback(presenter.getMarkModel());
			}
		}
		this.imgComposite.canvasRedraw();
		this.imgComposite.getMainComposite().refreshMapGraphicLayer();
	}

	/**
	 * Check if the given point is inside the image area.
	 * 
	 * @param point
	 * @return
	 */
	protected boolean validateInside(Point point) {

		return this.imgComposite.validateInside(point);
	}

	public Cursor getCursor() {

		return cursor;
	}

	public void addMouseSelectionListener(MouseSelectionListener listener) {

		this.listeners.add(listener);
	}

	public void deleteMouseSelectionListener(MouseSelectionListener listener) {

		this.listeners.remove(listener);
	}

	/**
	 * Broadcast to all the listeners that the mouse is inside the extent.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void broadcastMouseInFeedback(MarkModel mark) {

		for (MouseSelectionListener listener : listeners) {

			listener.inEvent(mark);
		}
	}

	/**
	 * Broadcast to all the listeners that the mouse is not inside the extent.
	 * 
	 * @param mark
	 *            The mark model.
	 */
	private void broadcastMouseOutFeedback(MarkModel mark) {

		for (MouseSelectionListener listener : listeners) {

			listener.outEvent(mark);
		}
	}
}
