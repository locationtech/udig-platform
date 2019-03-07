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
package org.locationtech.udig.project.tests.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;

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
