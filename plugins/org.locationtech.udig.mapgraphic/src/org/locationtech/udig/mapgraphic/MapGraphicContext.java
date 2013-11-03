/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.AbstractContext;
import org.locationtech.udig.project.render.ILabelPainter;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

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
