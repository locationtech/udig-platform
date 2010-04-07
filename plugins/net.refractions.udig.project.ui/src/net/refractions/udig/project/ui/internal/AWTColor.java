/*
 * Created on Mar 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.refractions.udig.project.ui.internal;

import java.awt.Color;

/**
 * @author ptozer TODO To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Style - Code Templates
 */
public class AWTColor {
    // List<Color> allColours = new ArrayList();
    private Color colour;
    /**
     * 
     */
    public AWTColor( Color colour ) {
        super();
        this.colour = colour;
    }

    /**
     * @return Returns the colour.
     */
    public Color getColour() {
        return colour;
    }
    /**
     * @param colour The colour to set.
     */
    public void setColour( Color colour ) {
        this.colour = colour;
    }
//
//    /**
//     * An AWTCOlor is equal to another when the red, green, blue and alpha for each colour are
//     * identical.
//     * 
//     * @param otherColour
//     * @return
//     */
//    public boolean equals( Object otherColour ) {
//        if (this.colour.getRed() == otherColour.getColour().getRed()
//                && this.colour.getGreen() == otherColour.getColour().getGreen()
//                && this.colour.getBlue() == otherColour.getColour().getBlue()) {
//            return true;
//        }
//        return false;
//    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((colour == null) ? 0 : colour.hashCode());
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AWTColor other = (AWTColor) obj;
        if (colour == null) {
            if (other.colour != null)
                return false;
        } else if (!colour.equals(other.colour))
            return false;
        return true;
    }
    
    
}
