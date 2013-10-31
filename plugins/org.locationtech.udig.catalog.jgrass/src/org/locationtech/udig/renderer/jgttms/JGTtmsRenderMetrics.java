/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.renderer.jgttms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;

import org.geotools.util.Range;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGTtmsRenderMetrics extends AbstractRenderMetrics {

    public JGTtmsRenderMetrics( IRenderContext context, JGTtmsRenderMetricsFactory factory,
            List<String> styleIds ) {
        super(context, factory, styleIds);
    }

    public Renderer createRenderer() {
        JGTtmsRenderer rasterRenderer = new JGTtmsRenderer();
        rasterRenderer.setContext(context);
        return rasterRenderer;
    }

    public boolean canAddLayer( ILayer layer ) {
        return false;
    }

    public boolean canStyle( String styleID, Object value ) {
        return false;
    }

    public Set<Range<Double>> getValidScaleRanges() {
        return new HashSet<Range<Double>>();
    }

}
