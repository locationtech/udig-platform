/**
 *
 */
package org.locationtech.udig.project.ui.wizard.export.image;

/**
 * The types of paper to export, width and height have unit Millimeter
 *
 * @author Jesse
 * @author Frank Gasdorf
 */
public enum Paper {

    LETTER(216, 356), // in millimeters
    LEGAL(216, 279), 
    A0(841, 1189), 
    A1(594, 841), 
    A2(420, 594), 
    A3(297, 420), 
    A4(210, 297);

    static final double MILLIMETERS_PER_INCH = 25.4;

    private final int width;

    private final int height;

    /**
     *
     * @param width width in millimeters
     * @param height height in millimeters
     */
    Paper(int width, int height) {
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

    /**
     * Calculates the pixels for the given width in Millimeter and Resolution in dots per inch (dpi)
     * 
     * @param widthInMillimeter width in Millimeter (mm)
     * @param dpi resolution in dots per inch (dpi)
     * @return width in pixel
     */
    public static int toPixels(int widthInMillimeter, double dpi) {
        double size = (widthInMillimeter / MILLIMETERS_PER_INCH) * dpi;
        return (int) (Math.floor(Math.round(size)));
    }

    /**
     * get paper width in pixels
     *
     * @param landscape is landscape?
     * @return paper width in pixels
     */
    public int getPixelWidth(boolean landscape, int dpi) {
        if (landscape) {
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
        if (landscape) {
            return toPixels(width, dpi);
        }
        return toPixels(height, dpi);
    }

}
