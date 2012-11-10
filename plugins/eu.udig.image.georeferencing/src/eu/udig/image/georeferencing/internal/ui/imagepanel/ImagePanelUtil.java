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
package eu.udig.image.georeferencing.internal.ui.imagepanel;

import org.eclipse.swt.graphics.Point;

/**
 * This utility class maintains the set of function used in the image panel package and its subsystems.
 * 
 * @author Mauricio Pazos
 * @author Aritz Davila
 * @since 1.3.3
 *
 */
public final class ImagePanelUtil {

	private ImagePanelUtil(){
		//utility class
	}
	

	/**
	 * Creates a point, used when the point is created within the canvas.
	 * 
	 * @param hScroll
	 *            h horizontal scroll
	 * @param vScroll
	 *            v vertical scroll
	 * @param xPoint
	 *            x canvas relative
	 * @param yPoint
	 *            y canvas relative
	 * 
	 * @return A point that represent the mark position.
	 */
	public static Point createMarkPosition(	final int hScroll,
											final int vScroll,
											final int xPoint,
											final int yPoint,
											final float scale) {

		int xImage = Math.round((xPoint + hScroll) / scale);
		int yImage = Math.round((yPoint + vScroll) / scale);

		return new Point(xImage, yImage);
	}
	
}
