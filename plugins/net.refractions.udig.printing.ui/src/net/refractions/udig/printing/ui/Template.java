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
package net.refractions.udig.printing.ui;

import java.awt.Dimension;
import java.util.Iterator;

import net.refractions.udig.printing.model.Box;
import net.refractions.udig.printing.model.Page;
import net.refractions.udig.project.internal.Map;

import org.eclipse.swt.graphics.Rectangle;

/**
 * A Template describes each element to be realized onto a Page. It is used to provide users with a
 * preformatted Page that can be used multiple times to easily print maps without having to worry
 * about repositioning and resizing each Box every time they create a new page.
 * <p>
 * <em>Note:</em> All Box Printers used to initialize the page must also have a
 * net.refractions.udig.printing.ui.boxprinter extension defined for it.
 * </p>
 * 
 * @author Richard Gould
 */

public interface Template extends Cloneable {

    /** 
     * indicates that the scale hint is not specified.
     */
    public static final int SCALE_UNSPECIFIED = 0;
    
    /**
     * unspecified page orientation
     */
    public static final int ORIENTATION_UNSPECIFIED = 0;
    
    /**
     * landscape page orientation
     */
    public static final int ORIENTATION_LANDSCAPE = 1;
    
    /**
     * portrait orientation
     */
    public static final int ORIENTATION_PORTRAIT = 2;
    
    
    /**
     * This method initializes the template to the page according to its own interests. Some
     * interests may include:
     * <ul>
     * <li>All the boxes that are part of the page. All created boxes most be returned by
     * {@link #iterator()}</li>
     * <li>Set the preferred size of the page. IE A0, US Legal, Landscape... (use
     * {@link Page#setSize(Dimension)})</li>
     * <li>The name of the page or map</li>
     * <li>The scale of the map</li>
     * </ul>
     * <p>
     * <em>Note:</em> All Box Printers used to in the Boxes in the page must also have a
     * net.refractions.udig.printing.ui.boxprinter extension defined for it. This is so that the
     * boxes can be restored after the application is shut down.
     * </p>
     * 
     * @param page the Page to initialize. It has a default name and size, both of which can be
     *        changed
     * @param map the Map that this page is centered around
     */
    public void init( Page page, Map map );

    /**
     * Returns an iterator that iterates over each Box contained in the template. This is used by
     * the framework to access a Template's Boxes.
     * 
     * @return an iterator where each element is of type Box
     * @see Box
     */
    public Iterator<Box> iterator();

    /**
     * @return A human-readable String that identifies this template
     */
    public String getName();

    /**
     * @return An abbbreviation that can be used to combine this name with map names in labels.
     */
    public String getAbbreviation();

    /**
     * Templates <b>must</b> clone themselves and their contents properly.
     * 
     * @return a copy of this template
     * @see Cloneable
     * @see Object#clone
     */
    public Template clone();
    
    /**
     * Returns the page orientation that's recommended by the developer
     * of the template.  One of ORIENTATION_UNSPECIFIED, 
     * ORIENTATION_PORTRAIT, or ORIENTATION_LANDSCAPE.  This should
     * be considered a hint to the caller of init() to pass an appropriately
     * sized Page.
     * 
     * @return one of ORIENTATION_UNSPECIFIED, ORIENTATION_PORTRAIT, or
     * ORIENTATION_LANDSCAPE
     */
    public int getPreferredOrientation();
    
    /**
     * A suggestion to the template to draw its map at the given scale. 
     * 
     * @param scaleDenom the scale denominator.
     */
    public void setMapScaleHint(double scaleDenom);

    /**
     * Get the scale hint
     *
     * @return
     */
    public double getMapScaleHint();

    /**
     * A suggestion to the template to zoom the map to the selected feature. 
     * 
     * @param hint true if the template should zoom to the selection, false otherwise
     */
    public void setZoomToSelectionHint(boolean hint);

    /**
     * Get the zoom hint
     *
     * @return
     */
    public boolean getZoomToSelectionHint();
    
    /**
     * Gets the bounds of the map in page-space coordinates.
     * 
     * @throws IllegalStateException if called before init(...)
     *
     * @return the bounds of the map area
     */
    public Rectangle getMapBounds() throws IllegalStateException;
    
    /**
     * Gets the number of pages in this template
     *
     * @return the number of pages
     */
    public abstract int getNumPages();
    
    /**
     * sets the active page, which is the page that will be prepared next time init() is called.
     */
    public abstract void setActivePage(int page);
    
    /**
     * gets the active page, which is the page that will be prepared next time init() is called.
     *
     * @return
     */
    public abstract int getActivePage();
    
}
