/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.project.ui.render.glass;

import net.refractions.udig.project.IAbstractContext;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.impl.AbstractContextImpl;
import net.refractions.udig.project.internal.render.RenderManager;

/**
 * This class tracks the context of the glass pane.  This include
 * the map associated with the pane, the render manager, and 
 * includes some functions for converting to world coordinates
 * to screen coordinates.
 *
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.2.0
 */
public class GlassPaneSite extends AbstractContextImpl {

    /**
     * Creates a new GlassPaneSite from the given render manager
     * and map.
     * 
     * @param manger  RenderManager responsible for rendering the map
     * @param map     the map the glass pane is drawn on 
     */
    public GlassPaneSite(RenderManager manger, Map map){
        super();
        setMapInternal(map);
        setRenderManagerInternal(manger);
    }
    
    /**
     * Creates a copy of the current context using the
     * map and render manager of the current context.
     */
    public IAbstractContext copy() {
        GlassPaneSite copy = new GlassPaneSite(this.getRenderManagerInternal(), this.getMapInternal());
        return copy;
    }

}
