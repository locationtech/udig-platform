/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.raster.editor;

import java.awt.Color;

import org.locationtech.udig.ui.graphics.Glyph;

import org.eclipse.swt.graphics.Image;

/**
 * Class to track a color
 * map entry.
 * 
 * @author Emily
 *
 */
public class ColorEntry {

	private Color color = Color.BLACK;
	private double opacity = 1;
	private double value = 0;
	private String label = ""; //$NON-NLS-1$
	
	/**
	 * Creates a new color entry with the default values
	 */
	public ColorEntry(){
		
	}
	/**
	 * Creates a new color entry with the specified value
	 * @param color
	 * @param opacity
	 * @param value
	 * @param label
	 */
	public ColorEntry(Color color, double opacity, double value, String label){
		this.color = color;
		this.opacity = opacity;
		this.value = value;
		this.label = label;
		
	}
	/**
	 * 
	 * @return the color associated with the entry
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * Sets the color associated with the entry
	 * @param color 
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * 
	 * @return the opacity associated with the entry
	 */
	public double getOpacity() {
		return opacity;
	}
	
	/**
	 * Sets the entry opacity
	 * @param opacity
	 */
	public void setOpacity(double opacity) {
		this.opacity = opacity;
	}
	
	/**
	 * 
	 * @return the entry value
	 */
	public double getValue() {
		return value;
	}
	/**
	 * 
	 * @param value the entry value
	 */
	public void setValue(double value) {
		this.value = value;
	}
	/**
	 * 
	 * @return the entry label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * the entry label
	 * @param label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * 
	 * @return a glyph made from the color associated
	 * with the entry
	 */
	public Image getImage(){
		return Glyph.swatch(color).createImage();
	}
	
	
}
