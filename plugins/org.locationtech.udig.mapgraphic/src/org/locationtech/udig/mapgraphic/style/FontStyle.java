/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.style;

import java.awt.Color;
import java.awt.Font;

import org.locationtech.udig.project.internal.StyleBlackboard;

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
    private Color color;
    /**
     * Create a new "empty"/"default" style
     */
    public FontStyle() {
        this.font = Font.decode(null);
        this.font = font.deriveFont(8.0f);
        this.color = Color.BLACK;
    }
    
    /**
     * Create new Style with the provided font
     * @param font2
     */
    public FontStyle( Font font2 ) {
        this.font = font2;
        this.color = Color.BLACK;
    }

    /**
     * Create new Style with the provided font
     * @param font2
     */
    public FontStyle( Font font2, Color color ) {
        this(font2);
        this.color = color;
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
    
    /**
     * 
     * @return the font color
     */
    public Color getColor(){
    	return color;
    }
    
    /**
     * Sets the font color
     * @param color
     */
    public void setColor(Color color){
    	this.color = color;
    }
}
