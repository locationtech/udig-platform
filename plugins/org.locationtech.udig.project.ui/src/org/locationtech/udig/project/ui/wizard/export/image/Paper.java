/**
 *
 */
package org.locationtech.udig.project.ui.wizard.export.image;


/**
 * The types of paper to export
 *
 * @author Jesse
 */
public enum Paper {

	LETTER(216,356), //in millimeters
	LEGAL(216,279),
	A0(841,1189),
	A1(594,841),
	A2(420,594),
	A3(297, 420),
	A4(210,297);

	final double MILLIMETERS_PER_INCH = 25.4;

	private final int width;
	private final int height;

	/**
	 *
	 * @param width width in millimeters
	 * @param height height in millimeters
	 */
	Paper(int width, int height){
		this.width = width;
		this.height = height;
	}


	/**
	 * gets the page width in millimeters
	 *
	 * @return width
	 */
	public int getWidth() {
	    return width;
	}

	/**
	 * gets the page height in millimeters
	 *
	 * @return height
	 */
	public int getHeight() {
	    return height;
	}

	private int toPixels(int width2, double dpi) {
		return (int) ((width2/MILLIMETERS_PER_INCH)*dpi);
	}

	/**
	 * get paper width in pixels
	 *
	 * @param landscape is landscape?
	 * @return paper width in pixels
	 */
	public int getPixelWidth(boolean landscape, int dpi) {
	    if( landscape ){
	        return toPixels(height, dpi);
	    }

	 	return toPixels(width, dpi);
	}

	/**
	 * get paper height in pixels
	 *
	 * @param landscape is landscape?
	 * @return paper height in pixels
	 */
	public int getPixelHeight(boolean landscape, int dpi) {
        if( landscape ){
            return toPixels(width, dpi);
        }
		return toPixels(height, dpi);
	}


}
