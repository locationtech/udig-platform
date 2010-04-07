/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.render.displayAdapter;

import java.awt.Dimension;

/**
 * The display area of a map.
 * 
 * @author jeichar
 * @since 0.3
 */
public interface IMapDisplay {
    /**
     * The size of the display area.
     * 
     * @return the size of the display area.
     * @see Dimension
     */
    public Dimension getDisplaySize();

    /**
     * Returns the width of the display area.
     * 
     * @return the width of the display area.
     */
    public int getWidth();

    /**
     * Returns the height of the display area.
     * 
     * @return the height of the display area.
     */
    public int getHeight();

	/**
	 * Returns the dots per inch of the display.
	 * 
	 * DPI is defined as the number of points per per linear inch. The DPI may be different in
	 * horizonal and vertical direction, in this case the average DPI will be returned  
	 * 
	 * @return the dots per inch of the display.
	 */
	public int getDPI();
}