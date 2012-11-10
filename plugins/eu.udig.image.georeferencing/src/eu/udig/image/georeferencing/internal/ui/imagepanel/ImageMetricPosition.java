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

import org.eclipse.swt.graphics.ImageData;

/**
 * Stores and updates some variables used by the imageComposite while zooming
 * and panning.
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 * 
 */
final class ImageMetricPosition {

	private int			maxX;
	private int			maxY;
	private int			hScrollValue		= 0;
	private int			vScrollValue		= 0;
	private int			hScrollBeforeZoom	= 0;
	private int			vScrollBeforeZoom	= 0;

	private float		scale				= 1;

	private float		previousScale;
	private float		scaleBeforeZoom;
	private ImageData	imageData;

	public ImageMetricPosition() {

	}

	public void setImageData(ImageData data) {
		this.imageData = data;
	}

	public void updateMaxXY(int x, int y) {
		this.maxX = x;
		this.maxY = y;
	}

	public void updateMaxXY() {

		this.maxX = Math.round(imageData.width * scale);
		this.maxY = Math.round(imageData.height * scale);
	}

	public void updateScrollValues(int hScroll, int vScroll) {
		this.hScrollValue = hScroll;
		this.vScrollValue = vScroll;
	}

	public void updateScale(float scale) {
		this.scale = scale;

		this.scaleBeforeZoom = previousScale;
		this.previousScale = scale;
	}

	/**
	 * Sets the default values.
	 */
	public void setDefaultValues() {

		assert this.imageData != null : "can't be null imageData"; //$NON-NLS-1$

		this.scale = 1;
		this.hScrollValue = 0;
		this.vScrollValue = 0;
		this.hScrollBeforeZoom = 0;
		this.vScrollBeforeZoom = 0;
		this.previousScale = 1;
		this.maxX = imageData.width;
		this.maxY = imageData.height;
	}

	/**
	 * @return the maxX
	 */
	public int getMaxX() {
		return maxX;
	}

	/**
	 * @return the maxY
	 */
	public int getMaxY() {
		return maxY;
	}

	/**
	 * @return the hScrollValue
	 */
	public int getHScrollValue() {
		return hScrollValue;
	}

	/**
	 * @return the vScrollValue
	 */
	public int getVScrollValue() {
		return vScrollValue;
	}

	/**
	 * @return the xscale
	 */
	public float getScale() {
		return scale;
	}

	/**
	 * @return the previousScale
	 */
	public float getPreviousScale() {
		return previousScale;
	}

	/**
	 * @return the imageData
	 */
	public ImageData getImageData() {
		return imageData;
	}

	public void increaseScale(float add) {

		this.scale = scale + add;
	}

	/**
	 * Decreased the scale unless the resultant scale will be less or equal to
	 * 0.
	 * 
	 * @param add
	 */
	public void decreaseScale(float subtractValue) {

		if (scale - subtractValue > 0) {
			this.scale = scale - subtractValue;
		}
	}

	/**
	 * Stores the value of the previous scale.
	 */
	public void updatePreviousScale() {

		this.scaleBeforeZoom = previousScale;
		this.previousScale = this.scale;
	}

	/**
	 * Stores the current value of the scroll to be used later.
	 */
	public void updatePreviousScroll() {

		this.hScrollBeforeZoom = this.hScrollValue;
		this.vScrollBeforeZoom = this.vScrollValue;
	}

	public float getScaleBeforeZoom() {

		return this.scaleBeforeZoom;
	}

	public int getHScrollBeforeZoom() {

		return this.hScrollBeforeZoom;
	}

	public int getVScrollBeforeZoom() {

		return this.vScrollBeforeZoom;
	}
}
