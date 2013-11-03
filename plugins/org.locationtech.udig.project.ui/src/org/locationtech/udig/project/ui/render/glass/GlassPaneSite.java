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

import org.locationtech.udig.project.IAbstractContext;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.impl.AbstractContextImpl;
import org.locationtech.udig.project.internal.render.RenderManager;

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
