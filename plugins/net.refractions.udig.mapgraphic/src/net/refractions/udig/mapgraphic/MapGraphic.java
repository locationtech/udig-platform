/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.mapgraphic;

import net.refractions.udig.ui.graphics.ViewportGraphics;


/**
 * An object which draws a graphic on a map.
 * 
 * @author Justin Deoliveira, Refractions Research Inc.
 * @since 0.6.0
 */
public interface MapGraphic {
 
    /** extension point id **/
    public static final String XPID = "net.refractions.udig.mapgraphic.mapgraphic"; //$NON-NLS-1$
    
    /**
     * Draws the graphic.  Check the clip area of the {@link ViewportGraphics} object to determine what
     * area needs to be refreshed.
     * 
     * @param context The drawing context.
     */
    void draw(MapGraphicContext context);
}