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

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.AbstractModalTool;

import org.locationtech.jts.geom.Coordinate;

/**
 * Tool responsible of adding a coordinate to the map.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.3
 * 
 */
public class AddCoordinateTool extends AbstractModalTool {

	public static final String				ID			= "org.locationtech.udig.image.georeferencing.tools.addcoordinate";	//$NON-NLS-1$
	public static final String				CATEGORY_ID	= "org.locationtech.udig.image.georeferencing.categorytools";			//$NON-NLS-1$

	private Set<CapturedCoordinateListener>	listeners	= new HashSet<CapturedCoordinateListener>();

	public AddCoordinateTool() {

	}

	@Override
	public void setActive(boolean active) {

		super.setActive(active);

		for (CapturedCoordinateListener listener : listeners) {

			listener.activated(active);
		}
	}

	@Override
	public void mousePressed(MapMouseEvent e) {

		// valid button = left click.
		if (!(e.button == MapMouseEvent.BUTTON1)) {
			return;
		}

		IMap map = ApplicationGIS.getActiveMap();
		Point point = e.getPoint();
		Coordinate newCoord = map.getViewportModel().pixelToWorld(point.x, point.y);
		broadcastCoordinate(newCoord);
	}

	/**
	 * Add the listener.
	 * 
	 * @param listener
	 *            {@link CapturedCoordinateListener} listener.
	 */
	public void addCapturedCoordinateListener(CapturedCoordinateListener listener) {

		assert listener != null;
		listeners.add(listener);
	}

	/**
	 * Delete the listener.
	 * 
	 * @param listener
	 *            {@link CapturedCoordinateListener} listener.
	 */
	public void removeCapturedCoordinateListener(CapturedCoordinateListener listener) {

		assert listener != null;
		listeners.remove(listener);
	}

	/**
	 * Broadcast to all the listeners the current coordinate.
	 * 
	 * @param newCoord
	 *            Captured coordinate.
	 */
	private void broadcastCoordinate(Coordinate newCoord) {

		if (newCoord == null) {
			return;
		}

		for (CapturedCoordinateListener listener : listeners) {

			listener.capturedCoordinate(newCoord);
		}
	}
}
