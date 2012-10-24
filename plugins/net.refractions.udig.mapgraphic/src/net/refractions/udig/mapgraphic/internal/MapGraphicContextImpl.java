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
package net.refractions.udig.mapgraphic.internal;

import java.awt.Graphics2D;

import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.impl.RenderContextImpl;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IViewportModelListener;
import net.refractions.udig.project.render.ViewportModelEvent;
import net.refractions.udig.ui.graphics.AWTGraphics;
import net.refractions.udig.ui.graphics.ViewportGraphics;

public class MapGraphicContextImpl extends RenderContextImpl 
    implements MapGraphicContext  {
    
    private static final String BLACKBOARD_LAYER_KEY = "HAS_LISTENER"; //$NON-NLS-1$
    
    private ViewportGraphics vpg;
    
    public MapGraphicContextImpl(final IRenderContext context, Graphics2D destination) {
        super();
        setGeoResourceInternal(context.getGeoResource());
        setLayerInternal((Layer) context.getLayer());
        setRenderManagerInternal((RenderManager) context.getRenderManager());
        setMapInternal((Map) context.getMap());
        vpg = new AWTGraphics(destination, context.getMapDisplay().getDPI());
        
        //add listener if doesn't already exist for layer
        IViewportModelListener listener = (IViewportModelListener)context.getLayer().getBlackboard().get(BLACKBOARD_LAYER_KEY);
        if (listener == null){
            listener = new IViewportModelListener(){
                public void changed( ViewportModelEvent event ) {
                    // need to invalidate image & refresh the layer
                    if (!context.getViewportModel().isBoundsChanging()){
                        context.getLayer().getBlackboard().put(MapGraphicRenderer.BLACKBOARD_IMAGE_KEY, null);
                        context.getLayer().refresh(null);
                    }
                }
            };
            context.getViewportModel().addViewportModelListener(listener);
            context.getLayer().getBlackboard().put(BLACKBOARD_LAYER_KEY, listener);
        }
    }

    public ViewportGraphics getGraphics() {
        return vpg;
    }
    
    public void dispose() {
        IViewportModelListener listener = (IViewportModelListener)getLayer().getBlackboard().get(BLACKBOARD_LAYER_KEY);
        if (listener != null){
            this.getViewportModel().removeViewportModelListener(listener);
            getLayer().getBlackboard().put(BLACKBOARD_LAYER_KEY, null);
        }
        super.dispose();
    }

}