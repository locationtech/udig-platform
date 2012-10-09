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
package net.refractions.udig.project.tests.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

import org.geotools.util.Range;

public class DummyRenderMetrics extends AbstractRenderMetrics {

    private IRenderContext context;

    public DummyRenderMetrics( IRenderContext context ) {
        super(context, null, new ArrayList<String>());
        this.context=context;
    }

    public Renderer createRenderer() {
        return new DummyRenderer();
    }

    public IRenderContext getRenderContext() {
        return context;
    }

    public IRenderMetricsFactory getRenderMetricsFactory() {
        return null;
    }

    public boolean canStyle( String styleID, Object value ) {
        return true;
    }

    public boolean isOptimized() {
        return false;
    }

    public boolean canAddLayer( ILayer layer ) {
        return false;
    }

    @SuppressWarnings("unchecked")
    public Set<Range<Double>> getValidScaleRanges() {
        return new HashSet<Range<Double>>();
    }

}
