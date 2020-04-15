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
package org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.image.georeferencing.internal.process.MarkModel;
import org.locationtech.udig.image.georeferencing.internal.process.MarkModel.MarkModelChange;
import org.locationtech.udig.image.georeferencing.internal.ui.AbstractMarkPresenter;
import org.locationtech.udig.image.georeferencing.internal.ui.InputEvent;

/**
 * {@link MarkModel} presenter on the map.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
final class MarkMapPresenterImp extends AbstractMarkPresenter implements MarkMapPresenter {

	private MarkModel			mark		= null;
	private MapGraphicContext	context		= null;

	private static final int	EXTENT_SIZE	= 10;

	/**
	 * Set the mark that will be representing this presenter.
	 * 
	 * @param markModel
	 *            The mark model.
	 */
	public MarkMapPresenterImp(MarkModel markModel) {

		this.mark = markModel;
		this.mark.addObserver(this);
	}

	/**
	 * Right now, it only makes an action when the {@link MarkModel} is deleted.
	 */
	public void update(Observable arg0, Object arg) {

		MarkModelChange change = (MarkModelChange) arg;
		switch (change) {
		case NEW:
		case MODIFY:
			// nothing
			break;
		case DELETE:
			delete();
			break;
		default:
			return;
		}
	}

	public void draw() {

		if (hide)
			return;

		ViewportGraphics graphics = context.getGraphics();

		graphics.setColor(createColor(mark.hashCode()));
		// get the map coordinate and
		// convert that to a screen point to draw (this way anytime
		// the map is updated with zooming or panning, etc, the point
		// will always be drawn relative to the map and not the screen.
		Point point = null;
		if (!(mark.getXCoord().equals(Double.NaN)) || !(mark.getYCoord().equals(Double.NaN))) {
			point = context.worldToPixel(new Coordinate(mark.getXCoord(), mark.getYCoord()));
		}
		if (point != null) {
			final int halfsize = EXTENT_SIZE / 2;

			this.extent = new Rectangle((int) (point.getX() - halfsize), (int) (point.getY() - halfsize), EXTENT_SIZE,
						EXTENT_SIZE);

			if (this.feedback) {
				Color yellow = Color.YELLOW;
				graphics.setColor(yellow);				
				graphics.fillOval(extent.x , extent.y, extent.width, extent.height);
				Color orange = Color.ORANGE;
				graphics.setColor(orange);
				graphics.drawOval(extent.x, extent.y, extent.width, extent.height);
				// set back to his default color
				graphics.setColor(createColor(mark.hashCode()));
			} else {
				graphics.fillOval(extent.x, extent.y, extent.width, extent.height);
			}

			Rectangle2D stringBounds = graphics.getStringBounds(String.valueOf(mark.getID()));
			graphics.drawString(String.valueOf(mark.getID()), (int) point.getX(),
						(int) (point.getY() + stringBounds.getHeight()), ViewportGraphics.ALIGN_MIDDLE,
						ViewportGraphics.ALIGN_BOTTOM);

		}

	}

	public void setContext(MapGraphicContext context) {

		this.context = context;

	}

	public MarkModel getMarkModel() {

		return this.mark;
	}

	/**
	 * Manages the event seeing if the given x,y is contained by the presenter
	 * extent.
	 * 
	 * @param event
	 *            Input event.
	 * @param x
	 *            Map position.
	 * @param y
	 *            Map position.
	 */
	public boolean eventHandler(InputEvent event, int x, int y) {

		switch (event) {
		// in both cases see if this point is contained in the extent
		case MOUSE_DOWN:
		case DELETE:
		case MOUSE_DRAG:

			return extentContains(x, y);
		default:
			break;
		}

		return false;
	}

	/**
	 * This presenter will show its feedback.
	 * 
	 * @param feedback
	 *            True if the presenter needs to show its feedback.
	 */
	public void showSelectedFeedback(boolean feedback) {

		this.feedback = feedback;
	}
}
