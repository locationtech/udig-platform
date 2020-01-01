/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.render.glass;

import org.eclipse.swt.graphics.GC;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.ui.render.displayAdapter.ViewportPane;

/**
 * A glass pane that draws onto the image drawn on the screen.
 *  
 * <p>The draw command is drawn after the background image has been
 * drawn and before the draw commands are executed:
 * <ul>
 * <li>Draw Commands
 * <li>Glass Pane
 * <li>Background Image (map layers; this image comes from the render manager)
 * </ul>
 * </p>
 * 
 * 
 * @author Emily Gouge (Refractions Research, Inc)
 * @since 1.1.0
 */
public abstract class GlassPane {

    /**
     * The viewport pane that does the drawing on the screen. 
     */
    protected ViewportPane parent;
    /**
     * The site associated with the GlassPane.  This
     * site contains information about the viewport and 
     * the map.
     */
    private GlassPaneSite site;
    
    /**
     * Creates a new glass pane with a given parent viewport.
     * 
     * <p>A new glasspane site is created using the map
     * and render manager associated with the viewportpane</p>
     * 
     * @param parent
     */
    public GlassPane(ViewportPane parent){
        this.parent = parent;

        // create a new site
        Map map = parent.getMapEditor().getMap();
        RenderManager manager = map.getRenderManagerInternal();
        site = new GlassPaneSite(manager, map);
    }

    /**
     * Gets the site associated with the glasspane.
     *
     * @return
     */
    public GlassPaneSite getSite(){
        return this.site;
    }
    

    /**
     * This function does the drawing.  This is called whenever
     * the viewport is redrawn.
     * 
     * <p>This should contain the draw commands of the items
     * you want drawn on the glass pane.  All draw commands
     * are pixel draw commands therefore any location information
     * needs to be converted to screen coordinates.  This can be done
     * using the GlassPaneSite information</p>
     *
     * @param graphics
     */
    public abstract void draw(GC graphics);

}
