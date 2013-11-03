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
package org.locationtech.udig.mapgraphic.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;

import org.geotools.util.Range;

/**
 * Used to describe the rendering abilities of MapGraphicRenderer
 */
public class MapGraphicRenderMetrics extends AbstractRenderMetrics{

    /**
     * Construct <code>MapGraphicRenderMetrics</code>.
     * 
     * @param context
     * @param factory
     */
    public MapGraphicRenderMetrics( IRenderContext context, IRenderMetricsFactory factory) {
        super(context, factory, new ArrayList<String>());
    }
    
    public Renderer createRenderer() {
        MapGraphicRenderer renderer = new MapGraphicRenderer();
        renderer.setContext(context);
        return renderer;
    }

    public boolean canAddLayer( ILayer layer ) {
        if ( layer.hasResource(MapGraphic.class) )
        	return true;
        return false;
    }
    public boolean canStyle( String SyleID, Object value ) {
        return true;
    }
    
    public Set<Range<Double>> getValidScaleRanges() {
        return new HashSet<Range<Double>>();
    }
}
