package org.locationtech.udig.image.georeferencing.internal.ui.coordinatepanel.tools;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.operations.AbstractPropertyValue;

/**
 * This property is checked by the coordinate tools {@link AddCoordinateTool}, {@link DeleteCoordinateTool} and {@link MoveCoordinateTool}
 * in order to become visible/invisible
 *
 * @author Mauricio Pazos
 * @since 1.3.3
 *
 */
public class CoordToolPropertyValue extends AbstractPropertyValue<Object> {

	private static boolean geoRefViewOpen;

	private static synchronized boolean isVisible() {
		return geoRefViewOpen;
	}

	public static synchronized void setVisible(boolean visible) {
		geoRefViewOpen = visible;
	}

	@Override
	public boolean isTrue(Object object, String value) {

		if(!isVisible()) return false;

		if(ILayer.class.isAssignableFrom(object.getClass())){

			ILayer layer = (ILayer)object;
			if(layer.getCRS() == null) return false;
		}

		return true;
	}

	@Override
	public boolean canCacheResult() {
		return false;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

}
