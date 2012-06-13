/**
 * 
 */
package eu.udig.image.georeferencing.internal.ui.coordinatepanel.tools;

import net.refractions.udig.ui.operations.AbstractPropertyValue;

/**
 * This property is checked by the coordinate tools in order to become visible/invisible
 * 
 * @author Mauricio Pazos
 *
 */
public class CoordToolPropertyValue extends AbstractPropertyValue<Object> {
	
	public static final String ID ="eu.udig.image.georeferencing.internal.ui.coordinatepanel.tools.CoordToolPropertyValue"; //$NON-NLS-1$
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
		return isVisible();
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
