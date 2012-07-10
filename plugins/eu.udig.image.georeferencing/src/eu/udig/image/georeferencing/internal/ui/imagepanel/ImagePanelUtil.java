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

import org.eclipse.swt.graphics.Point;

/**
 * This utility class maintains the set of function used in the image panel package and its subsystems.
 * 
 * @author Mauricio Pazos
 * @author Aritz Davila
 * @since 1.0.0
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
