/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.legend.ui;

import java.awt.Color;

public class LegendStyle {
    public  int verticalMargin; //distance between border and icons/text
    public  int horizontalMargin; //distance between border and icons/text
    public  int verticalSpacing; //distance between layers
    public  int horizontalSpacing; //space between image and text
    public  Color foregroundColour;
    public  Color backgroundColour;
    public  int indentSize;
    public int imageWidth;
    public  int imageHeight; //size of glyph image
    
    /**
     * Creates a new empty legend style
     */
    public LegendStyle() {
    	
    }
    
    
	/**
	 * Creates a new legend style copying the values
	 * from the old legend style
	 * @param oldStyle
	 */
	public LegendStyle(LegendStyle oldStyle) {
		super();
		this.verticalMargin = oldStyle.verticalMargin;
		this.horizontalMargin = oldStyle.horizontalMargin;
		this.verticalSpacing = oldStyle.verticalSpacing;
		this.horizontalSpacing = oldStyle.horizontalSpacing;
		this.foregroundColour = oldStyle.foregroundColour;
		this.backgroundColour = oldStyle.backgroundColour;
		this.indentSize = oldStyle.indentSize;
		this.imageWidth = oldStyle.imageWidth;
		this.imageHeight = oldStyle.imageHeight;
	}
    
}
