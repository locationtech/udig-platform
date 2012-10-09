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
package eu.udig.image.georeferencing.internal.ui.imagepanel.tools;

import org.eclipse.swt.graphics.Cursor;

import eu.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;

/**
 * Tool used to zoom out the image.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
public class ZoomOutImageTool extends ZoomImageTool {

	public ZoomOutImageTool(Cursor cursor, ImageComposite imgComposite) {
		super(cursor, imgComposite);

	}

	@Override
	protected void changeZoomScale() {

		this.imgComposite.decreaseScale(Float.parseFloat("0.1")); //$NON-NLS-1$		
	}

}
