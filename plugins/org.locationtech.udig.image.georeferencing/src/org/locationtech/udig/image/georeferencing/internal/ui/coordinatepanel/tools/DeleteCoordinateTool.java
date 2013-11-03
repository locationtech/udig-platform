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
package org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;
import org.locationtech.udig.image.georeferencing.internal.ui.InputEvent;

/**
 * Map tool used to delete a MarkMapPresenter.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public class DeleteCoordinateTool extends AbstractModalTool{

	public static final String				ID			= "org.locationtech.udig.image.georeferencing.tools.deletecoordinate"; //$NON-NLS-1$
	public static final String				CATEGORY_ID	= "org.locationtech.udig.image.georeferencing.categorytools";			//$NON-NLS-1$

	private Set<DeletedCoordinateListener>	listeners	= new HashSet<DeletedCoordinateListener>();
	private static int						targets		= MOUSE | WHEEL | MOTION;

	public DeleteCoordinateTool() {

		super(targets);
	}

	@Override
	public void setActive(boolean active) {

		super.setActive(active);

		for (DeletedCoordinateListener listener : listeners) {
			listener.activated(active);
		}
	}

	@Override
	public void mousePressed(MapMouseEvent e) {

		// valid button = left click.
		if (!(e.button == MapMouseEvent.BUTTON1)) {
			return;
		}

		Point point = e.getPoint();
		broadcastCoordinate(point, InputEvent.MOUSE_DOWN);
	}

	@Override
	public void mouseMoved(MapMouseEvent e) {

		Point point = e.getPoint();
		broadcastCoordinate(point, InputEvent.MOUSE_DRAG);
	}

	/**
	 * Add the {@link DeletedCoordinateListener}.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void addDeletedCoordinateListener(DeletedCoordinateListener listener) {

		assert listener != null;
		listeners.add(listener);
	}

	/**
	 * Deletes the {@link DeletedCoordinateListener}.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void removeDeletedCoordinateListener(DeletedCoordinateListener listener) {

		assert listener != null;
		listeners.remove(listener);
	}

	/**
	 * Broadcast to all the listeners the current event and the point where it
	 * was clicked on the map.
	 * 
	 * @param point
	 *            Click point in the map.
	 * @param event
	 *            Input event.
	 */
	private void broadcastCoordinate(Point point, InputEvent event) {

		if (point == null) {
			return;
		}

		for (DeletedCoordinateListener listener : listeners) {

			listener.deletedCoordinate(point, event);
		}
	}
}
