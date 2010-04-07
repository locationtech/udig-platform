/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.mapgraphic.style;

import java.awt.Font;

import net.refractions.udig.project.internal.StyleBlackboard;

/**
 * A style that essentially is a font.  This is primarily intended for use with MapGraphics so that the font
 * of the mapgraphics text can be specified.
 * 
 * @see StyleBlackboard
 * @see FontStyleContent
 * @author jesse
 * @since 1.1.0
 */
public class FontStyle {
    private Font font;

    /**
     * Create a new "empty"/"default" style
     */
    public FontStyle() {
        this.font = Font.decode(null);
        this.font = font.deriveFont(8.0f);
    }
    
    /**
     * Create new Style with the provided font
     * @param font2
     */
    public FontStyle( Font font2 ) {
        this.font = font2;
    }

    /**
     * Returns the font.  Maybe null if the default is intended to be used.
     *
     * @return the font.  Maybe null if the default is intended to be used.
     */
    public Font getFont() {
        return font;
    }

    /**
     * Set the font to use for the layer's mapgraphic
     *
     * @param font the new font
     */
    public void setFont( Font font ) {
        this.font = font;
    }
}
