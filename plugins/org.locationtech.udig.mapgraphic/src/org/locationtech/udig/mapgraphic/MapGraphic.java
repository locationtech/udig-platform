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

import org.locationtech.udig.ui.graphics.ViewportGraphics;


/**
 * An object which draws a graphic on a map.
 * 
 * @author Justin Deoliveira, Refractions Research Inc.
 * @since 0.6.0
 */
public interface MapGraphic {
 
    /** extension point id **/
    public static final String XPID = "org.locationtech.udig.mapgraphic.mapgraphic"; //$NON-NLS-1$
    
    /**
     * Draws the graphic.  Check the clip area of the {@link ViewportGraphics} object to determine what
     * area needs to be refreshed.
     * 
     * @param context The drawing context.
     */
    void draw(MapGraphicContext context);
}
