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

/**
 * A Template describes each element to be realized onto a Page. It is used to provide users with a
 * performatted Page that can be used multiple times to easily print maps without having to worry
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
     * Templates <b>must</b> clone themselves and their contents properly.
     *
     * @return a copy of this template
     * @see Cloneable
     * @see Object#clone
     */
    public Template clone();

}
