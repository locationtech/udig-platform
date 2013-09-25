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
package org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.tools;

import org.eclipse.swt.graphics.Cursor;
import org.locationtech.udig.image.georeferencing.internal.ui.imagepanel.ImageComposite;


/**
 * 
 * Tool used to zoom in the image.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
public class ZoomInImageTool extends ZoomImageTool {

	public ZoomInImageTool(Cursor cursor, ImageComposite imgComposite) {
		super(cursor, imgComposite);
	}

	@Override
	protected void changeZoomScale() {

		this.imgComposite.increaseScale(Float.parseFloat("0.1")); //$NON-NLS-1$
	}
}
