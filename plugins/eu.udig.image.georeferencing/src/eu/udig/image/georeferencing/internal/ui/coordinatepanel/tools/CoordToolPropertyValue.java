/**
 * 
 */
package eu.udig.image.georeferencing.internal.ui.coordinatepanel.tools;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.operations.AbstractPropertyValue;

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

	/** 
	 * 
	 */
	public CoordToolPropertyValue() {
		geoRefViewOpen = false;
	}

	private static synchronized boolean isVisible() {
		return geoRefViewOpen;
	}

	public static synchronized void setVisible(boolean visible) {
		geoRefViewOpen = visible;
	}

	/* (non-Javadoc)
	 * @see net.refractions.udig.ui.operations.PropertyValue#isTrue(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isTrue(Object object, String value) {
		
		if(!isVisible()) return false;
		
		if(ILayer.class.isAssignableFrom(object.getClass())){
			
			ILayer layer = (ILayer)object;
			if(layer.getCRS() == null) return false;
		}
		
		return true;
	}



	/* (non-Javadoc)
	 * @see net.refractions.udig.ui.operations.PropertyValue#canCacheResult()
	 */
	@Override
	public boolean canCacheResult() {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.refractions.udig.ui.operations.PropertyValue#isBlocking()
	 */
	@Override
	public boolean isBlocking() {
		return false;
	}

}
