package net.refractions.udig.mapgraphic.internal;

import java.awt.Graphics2D;

import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.RenderManager;
import net.refractions.udig.project.internal.render.impl.RenderContextImpl;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.ui.graphics.AWTGraphics;
import net.refractions.udig.ui.graphics.ViewportGraphics;

public class MapGraphicContextImpl extends RenderContextImpl
    implements MapGraphicContext  {

    private ViewportGraphics vpg;

    public MapGraphicContextImpl(IRenderContext context, Graphics2D destination) {
        super();
        setGeoResourceInternal(context.getGeoResource());
        setLayerInternal((Layer) context.getLayer());
        setRenderManagerInternal((RenderManager) context.getRenderManager());
        setMapInternal((Map) context.getMap());
        vpg = new AWTGraphics(destination);
    }

    public ViewportGraphics getGraphics() {
        return vpg;
    }


}
