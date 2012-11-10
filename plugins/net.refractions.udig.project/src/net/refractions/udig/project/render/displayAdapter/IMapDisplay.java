/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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