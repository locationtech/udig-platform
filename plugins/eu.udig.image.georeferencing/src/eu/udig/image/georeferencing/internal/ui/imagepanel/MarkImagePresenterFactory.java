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
import org.eclipse.swt.widgets.Canvas;

import eu.udig.image.georeferencing.internal.process.MarkModel;

/**
 * Factory to make {@link MarkImagePresenter} products.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.0.0
 * 
 */
public final class MarkImagePresenterFactory {

	private MarkImagePresenterFactory() {
		// singleton
	}

	/**
	 * Creates the {@link MarkImagePresenterImp} using the given parameters and
	 * add this presenter to the list of presenters contained by the image
	 * composite.
	 * 
	 * @param markModel a mark model.
	 * @param position 	position on image.
	 * @param hScroll 	Horizontal scroll position.
	 * @param vScroll 	Vertical scroll position.
	 * @param canvas	The canvas for the new mark presenter.
	 * @param scale		The current scale in the canvas
	 */
	public static MarkImagePresenter createMarkPresenter(	
													final MarkModel markModel,
													final Point 	position,
													final int 		hScroll,
													final int 		vScroll,
													final Canvas 	canvas,
													final float 	scale) {

		MarkImagePresenterImp newMarkPresenter = new MarkImagePresenterImp(
															canvas, markModel, 
															hScroll, vScroll, 
															scale);
		markModel.initializeImagePosition(position);
		
		return newMarkPresenter;
	}

}
