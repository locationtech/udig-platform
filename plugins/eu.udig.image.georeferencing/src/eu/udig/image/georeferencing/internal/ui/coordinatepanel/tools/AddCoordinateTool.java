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
package eu.udig.image.georeferencing.internal.ui.coordinatepanel.tools;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Tool responsible of adding a coordinate to the map.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.0.0
 * 
 */
public class AddCoordinateTool extends AbstractModalTool {

	public static final String				ID			= "eu.udig.image.georeferencing.tools.addcoordinate";	//$NON-NLS-1$
	public static final String				CATEGORY_ID	= "eu.udig.image.georeferencing.categorytools";			//$NON-NLS-1$

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
