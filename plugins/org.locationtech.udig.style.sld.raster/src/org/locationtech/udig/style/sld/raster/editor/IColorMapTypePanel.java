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

import org.eclipse.swt.widgets.Composite;
import org.geotools.brewer.color.BrewerPalette;
import org.geotools.styling.ColorMap;

/**
 * Interface for a single banded raster
 * color map panel. 
 * 
 * @author Emily
 *
 */
public interface IColorMapTypePanel {

	/**
	 * Standate NO DATA flag for color map
	 * entries
	 */
	public static final String NO_DATA_LABEL = "-no data-"; //$NON-NLS-1$
	
	/**
	 * Default NO DATA value
	 */
	public static final Double DEFAULT_NO_DATA = -9999d;
	/**
	 * 
	 * @return the name of the color map type
	 * associated with this panel
	 */
	public String getName();
	
	
	/**
	 * 
	 * @return the color map created by the panel
	 * 
	 * @throws Exception
	 */
	public ColorMap getColorMap() throws Exception;
	
	/**
	 * Sets the color palette to be used by the panel
	 * 
	 * @param palette color palette
	 * @param reverse if the color palette should be reversed
	 */
	public void setColorPalette(BrewerPalette palette, boolean reverse);
	
	/**
	 * Sets the default color palette to use the 
	 * first time the panel is displayed.
	 * 
	 * @param palette
	 */
	public void setInitialColorPalette(BrewerPalette palette);
	
	/**
	 * 
	 * @return the panel control
	 */
	public Composite createControl(Composite parent);
	
	
	/**
	 * Initializes the panel with the given color map.
	 * 
	 * @param cm
	 */
	public void init(ColorMap cm);
	
	/**
	 * Sets the value formatter, updating the 
	 * format of the current values
	 * @param format new value formatter
	 */
	public void setFormatter(ValueFormatter format);
	
	/**
	 * This function is called to compute
	 * the breaks for the given raster map.
	 */
	public void computeValues();
	
	/**
	 * 
	 * @return the gui label associated with the compute
	 * values function
	 */
	public String getComputeValuesLabel();
	
	/**
	 * Determines if this panel supports a
	 * given color map type.
	 * 
	 * @param colorMapType one of ColorMap.TYPE_RAMP, ColorMap.TYPE_INTERVALS or
	 * ColorMap.TYPE_VALUES
	 * @return <code>true</code> if panel supports color map type, <code>false</code> otherwise
	 */
	public boolean canSupport(int colorMapType);
	
	/**
	 * Refresh the panel
	 */
	public void refresh();
}
