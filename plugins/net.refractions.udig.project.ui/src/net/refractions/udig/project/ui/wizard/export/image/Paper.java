/**
 *
 */
package net.refractions.udig.project.ui.wizard.export.image;


/**
 * The types of paper to export
 *
 * @author Jesse
 */
public enum Paper {

	LETTER(216,356),
	LEGAL(216,279),
	A0(841,1189),
	A1(594,841),
	A2(420,594),
	A3(594,420),
	A4(210,297);

	private final int width;
	private final int height;

	Paper(int width, int height){
		this.width = toPixels(width);
		this.height = toPixels(height);
	}

	private int toPixels(int width2) {
		return (int) ((width2/25.4)*72);
	}

	public int getWidth(boolean landscape) {
	    if( landscape ){
	        return height;
	    }

	 	return width;
	}

	public int getHeight(boolean landscape) {
        if( landscape ){
            return width;
        }
		return height;
	}


}
