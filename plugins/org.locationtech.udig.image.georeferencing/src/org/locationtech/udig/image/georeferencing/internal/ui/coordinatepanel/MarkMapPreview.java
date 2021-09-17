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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Observable;

import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.locationtech.udig.image.georeferencing.internal.process.MarkModel;
import org.locationtech.udig.image.georeferencing.internal.ui.AbstractMarkPresenter;
import org.locationtech.udig.image.georeferencing.internal.ui.InputEvent;

/**
 * Preview created to show the {@link MarkMapPresenterImp} moving or D&D through
 * the map in real time.
 *
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 *
 */
class MarkMapPreview extends AbstractMarkPresenter  implements MarkMapPresenter  {

	private MarkMapPresenter parent = null;

	private MapGraphicContext context = null;

	private Point point = null;

	private static final int	EXTENT_SIZE	= 10;

	/**
	 * Constructor. Like all the previews, they have a parent presenter. With
	 * this constructor, it knows which one is his father.
	 *
	 * @param parent
	 *            The parent presenter.
	 */
	public MarkMapPreview(MarkMapPresenter parent) {
		this.parent = parent;
	}

	public void update(Observable o, Object arg) {

	}

	/**
	 * Mouse drag handler
	 */
	public boolean eventHandler(InputEvent event, int x, int y) {

		switch (event) {
		case MOUSE_DRAG:

			this.point = new Point(x, y);
			break;

		default:
			break;
		}
		return false;
	}

	public void draw() {

		assert this.context != null;

		if (hide)
			return;

		ViewportGraphics graphics = context.getGraphics();

		graphics.setColor(createColor(getMarkModel().hashCode()));

		if (point != null) {
			final int halfsize = EXTENT_SIZE / 2;

			this.extent = new Rectangle((int) (point.getX() - halfsize), (int) (point.getY() - halfsize), EXTENT_SIZE,
						EXTENT_SIZE);

			graphics.fillOval(extent.x, extent.y, extent.width, extent.height);
			Rectangle2D stringBounds = graphics.getStringBounds(String.valueOf(getMarkModel().getID()));
			graphics.drawString(String.valueOf(getMarkModel().getID()), (int) point.getX(),
						(int) (point.getY() + stringBounds.getHeight()), ViewportGraphics.ALIGN_MIDDLE,
						ViewportGraphics.ALIGN_BOTTOM);
		}
	}

	public MarkModel getMarkModel() {
		return parent.getMarkModel();
	}

	public void setContext(MapGraphicContext context) {
		this.context = context;
	}
}
