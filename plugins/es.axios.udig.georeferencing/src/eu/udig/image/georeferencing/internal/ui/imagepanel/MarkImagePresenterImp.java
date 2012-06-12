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
package eu.udig.image.georeferencing.internal.ui.imagepanel;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Observable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import eu.udig.image.georeferencing.internal.process.MarkModel;
import eu.udig.image.georeferencing.internal.process.MarkModel.MarkModelChange;
import eu.udig.image.georeferencing.internal.ui.AbstractMarkPresenter;
import eu.udig.image.georeferencing.internal.ui.InputEvent;

/**
 * <p>
 * Mark Presenter.
 * 
 * It will be the graphical representation of the mark model in the canvas.
 * </p>
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.0.0
 */
class MarkImagePresenterImp extends AbstractMarkPresenter implements MarkImagePresenter{

	private MarkModel	mark;
	private Canvas		canvas;
	private GC			gc;

	private enum DrawAction {
		MOVE, DELETE, SCROLL, NEW, NONE, ZOOM
	};

	private DrawAction			action				= DrawAction.NONE;

	private Rectangle			extentPointLabel	= null;

	private int					xRelativeToCanvas;
	private int					yRelativeToCanvas;
	private float				scale				= 1;

	private static int			EXTEND_LABEL_WIDTH	= 10;
	private static int			EXTEND_LABEL_HEIGHT	= 25;

	private static final int	EXTENT_SIZE			= 10;
	

	/**
	 * Constructor of the mark presenter.
	 * 
	 * It'll create a presenter and set the extent used by it.
	 * 
	 * @param canvas
	 *            The canvas from the {@link ImageComposite}.
	 * @param mark
	 *            Mark model.
	 * @param xRelativeToCanvas
	 *            X position relative to the canvas.
	 * @param yRelativeToCanvas
	 *            Y position relative to the canvas.
	 * @param scale
	 *            Zoom scale factor.
	 */
	public MarkImagePresenterImp(	Canvas canvas,
									MarkModel mark,
									int xRelativeToCanvas,
									int yRelativeToCanvas,
									float scale) {

		assert mark != null;

		this.canvas = canvas;
		this.mark = mark;
		this.mark.addObserver(this);

		// make variable extend taking into account the ID number
		if (Integer.parseInt(mark.getID()) > 99) {
			EXTEND_LABEL_WIDTH = 25;
		} else if (Integer.parseInt(mark.getID()) > 9) {
			EXTEND_LABEL_WIDTH = 15;
		}

		this.extent = new Rectangle(this.mark.getXImage() - (EXTENT_SIZE / 2), this.mark.getYImage()
					- (EXTENT_SIZE / 2), EXTENT_SIZE, EXTENT_SIZE);

		this.extentPointLabel = new Rectangle(this.mark.getXImage() - 3, this.mark.getYImage() - 3, EXTEND_LABEL_WIDTH,
					EXTEND_LABEL_HEIGHT);
		this.xRelativeToCanvas = xRelativeToCanvas;
		this.yRelativeToCanvas = yRelativeToCanvas;
		this.scale = scale;
		createContent(this.canvas);
		relocateTheExtend();
	}

	/**
	 * Adds a listener to the canvas. It'll paint whenever an event is triggered
	 * and its output will vary on the current action.
	 * 
	 * @param parent
	 */
	private void createContent(Canvas parent) {

		parent.addListener(SWT.Paint, new Listener() {

			public void handleEvent(Event event) {
				gc = event.gc;

				relocateTheExtend();

				switch (action) {
				case DELETE:
					delete();
					break;
				case SCROLL:
				case NEW:
				case MOVE:
				case ZOOM:
				default:
					draw();
					break;
				}
				action = DrawAction.NONE;
			}
		});
	}

	/**
	 * Adjust the presenter extent. The extent must represent the current point
	 * relative location in the canvas. The mark has fixed values respect the
	 * image, but respect the canvas, this values will change based on the
	 * current scroll.
	 */
	private void relocateTheExtend() {

		this.extentPointLabel.x = Math.round(this.mark.getXImage() * this.scale) - 3 - this.xRelativeToCanvas;
		this.extentPointLabel.y = Math.round(this.mark.getYImage() * this.scale) - 3 - this.yRelativeToCanvas;

		this.extent.x = Math.round(this.mark.getXImage() * this.scale) - (EXTENT_SIZE / 2) - this.xRelativeToCanvas;
		this.extent.y = Math.round(this.mark.getYImage() * this.scale) - (EXTENT_SIZE / 2) - this.yRelativeToCanvas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.axios.udig.ui.georeferencing.internal.ui.MarkPresenters#draw(org.eclipse
	 * .swt.graphics.GC)
	 */
	public void draw() {

		assert this.gc != null;
		assert !this.gc.isDisposed();

		if (this.hide)
			return;

		String id = this.mark.getID();
		int x = getX();
		int y = getY();

		Color c = createColor(this.mark.hashCode());
		org.eclipse.swt.graphics.Color color = new org.eclipse.swt.graphics.Color(gc.getDevice(), c.getRed(),
					c.getGreen(), c.getBlue());
		gc.setForeground(color);
		gc.setBackground(color);

		Font swtFont = new Font(gc.getDevice(), "Arial", 10, SWT.NORMAL); //$NON-NLS-1$
		gc.setFont(swtFont);

		if (this.feedback) {

			Color yellow = Color.YELLOW;
			org.eclipse.swt.graphics.Color coloryellow = new org.eclipse.swt.graphics.Color(gc.getDevice(),
						yellow.getRed(), yellow.getGreen(), yellow.getBlue());
			gc.setForeground(coloryellow);
			gc.setBackground(coloryellow);
			gc.fillOval(x - 3, y - 3, 6, 6);
			Color orange = Color.ORANGE;
			org.eclipse.swt.graphics.Color colorOrange = new org.eclipse.swt.graphics.Color(gc.getDevice(),
						orange.getRed(), orange.getGreen(), orange.getBlue());
			gc.setForeground(colorOrange);
			gc.drawOval(x - 3, y - 3, 6, 6);
			gc.setForeground(color);
			gc.setBackground(color);
		} else {
			gc.fillOval(x - 3, y - 3, 6, 6);
		}

		gc.drawString(String.valueOf(id), x - 3, y + 6, true);
	}

	private int getX() {

		int x = Math.round(this.mark.getXImage() * this.scale) - this.xRelativeToCanvas;

		return x;
	}

	private int getY() {

		int y = Math.round(this.mark.getYImage() * this.scale) - this.yRelativeToCanvas;

		return y;
	}

	/**
	 * Update the mark presenter whenever the mark model suffers a change. This
	 * class is an observer of {@link MarkModel}
	 */
	public void update(Observable o, Object arg) {

		MarkModelChange change = (MarkModelChange) arg;
		switch (change) {
		case NEW:
			this.action = DrawAction.NEW;
			break;
		case MODIFY:
			this.action = DrawAction.MOVE;
			break;
		case DELETE:
			this.action = DrawAction.DELETE;
			break;
		default:
			return;
		}

		relocateTheExtend();
		this.canvas.redraw(this.extentPointLabel.x, this.extentPointLabel.y, EXTEND_LABEL_WIDTH, EXTEND_LABEL_HEIGHT,
					false);
	}

	public MarkModel getMarkModel() {

		return this.mark;
	}

	/**
	 * <p>
	 * For the following events will return true if the given X and Y are
	 * contained inside this presenter's extent:
	 * 
	 * Mouse_down, Delete, Mouse_drag.
	 * 
	 * When a Pan event occurs, it'll update the x and y position relative to
	 * the canvas.
	 * </p>
	 * 
	 * @param event
	 *            Input event.
	 * @param x
	 *            Position.
	 * @param y
	 *            Position.
	 * @return True if this presenter's mark is affected by the event.
	 */
	public boolean eventHandler(InputEvent event, int x, int y) {

		switch (event) {

		case MOUSE_DOWN:
		case DELETE:
		case MOUSE_DRAG:
		case MOUSE_OVER:
			// see if this points belong to the current presenter.
			return extentContains(x, y);

		case PAN:
			scroll(x, y);
			break;
		default:
			break;

		}

		return false;
	}

	/**
	 * Handles the Zoom event.
	 * 
	 * @param event
	 *            Input event.
	 * @param x
	 *            Position.
	 * @param y
	 *            Position.
	 * @return Nothing by default.
	 */
	@Override
	public boolean eventHandler(InputEvent event, float scale) {

		switch (event) {
		case ZOOM:

			zoom(scale);
			break;
		default:
			break;
		}
		return false;
	}

	/**
	 * Updates the values used by the extent. They are positions relative to the
	 * canvas, so the presenter knows depending on the current scroll where it
	 * has to be drawn.
	 * 
	 * @param x
	 *            X position relative to the canvas.
	 * @param y
	 *            Y position relative to the canvas.
	 */
	private void scroll(int x, int y) {

		// update the relative position with the image
		this.xRelativeToCanvas = x;
		this.yRelativeToCanvas = y;
		action = DrawAction.SCROLL;
	}

	/**
	 * Updates the zoom scale.
	 * 
	 * @param scale
	 *            Zoom scale.
	 */
	private void zoom(float scale) {
		this.scale = scale;
		action = DrawAction.ZOOM;
	}

	public Canvas getCanvas() {

		return this.canvas;
	}

	public int getXRelativeToCanvas() {

		return this.xRelativeToCanvas;
	}

	public int getYRelativeToCanvas() {

		return this.yRelativeToCanvas;
	}


}
