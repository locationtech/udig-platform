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

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.AbstractContext;
import net.refractions.udig.project.render.ILabelPainter;
import net.refractions.udig.ui.graphics.ViewportGraphics;

/**
 * 
 * Context used by map graphics. Contains a graphics handle and the layer which
 * represents the graphic. This interface is an extension of AbstractContext
 * but is read-only, all write methods of AbstractContext are unsuportted.
 * 
 * @author Justin Deoliveira, Refractions Research Inc.
 * @since 0.6.0
 */
public interface MapGraphicContext extends AbstractContext {
    
    /**
     * 
     * @return The layer on the map that represents the map graphic.
     */
    ILayer getLayer();
    
    /**
     * 
     * @return The graphics handle.
     */
    ViewportGraphics getGraphics();
    
    /**
     * Returns the labeller for the next rendering.  
     *
     * @return the labeller that draws the labels on the top of the map.
     */
    ILabelPainter getLabelPainter();    
}