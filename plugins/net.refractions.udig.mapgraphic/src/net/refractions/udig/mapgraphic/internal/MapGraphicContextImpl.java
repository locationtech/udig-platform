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
                    if (!context.getMap().getViewportModel().isBoundsChanging()){
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