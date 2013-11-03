/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.legend.ui;

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
