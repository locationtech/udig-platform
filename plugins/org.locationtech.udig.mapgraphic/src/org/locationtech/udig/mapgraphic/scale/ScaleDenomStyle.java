/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.scale;

import java.awt.Color;

/**
 * A style that represents the background color for the map scale.
 * 
 * @author Emily
 * @since 1.1.0
 */
public class ScaleDenomStyle {
    
    private Color backgroundColor;
    

    /**
     * Create new Style with the provided color
     * @param color
     */
    public ScaleDenomStyle( Color color ) {
        this.backgroundColor = color;
    }
    
    /**
     * Creates a new Style with no background color
     */
    public ScaleDenomStyle(){
    	backgroundColor = Color.WHITE;
    }
    
    /**
     * 
     * @return the background color; can be null if no background to be displayed
     */
    public Color getColor(){
    	return backgroundColor;
    }
    
    /**
     * Sets the background color; can be null if no background color 
     * to be displayed
     * @param color
     */
    public void setColor(Color color){
    	this.backgroundColor = color;
    }
}
