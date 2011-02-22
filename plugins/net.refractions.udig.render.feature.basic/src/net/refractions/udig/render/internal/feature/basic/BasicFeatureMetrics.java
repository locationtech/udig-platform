/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.render.internal.feature.basic;

import java.util.HashSet;
import java.util.Set;

import javax.media.jai.util.Range;

import net.refractions.udig.core.MinMaxScaleCalculator;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.style.sld.SLDContent;

import org.geotools.styling.Style;





/**
 * The metrics object for the BasicFeatureRenderer
 *
 * @author Jesse Eichar
 */
public class BasicFeatureMetrics extends AbstractRenderMetrics {


    public BasicFeatureMetrics( IRenderContext context2, BasicFeatureMetricsFactory factory ) {
        super(context2, factory);
    }


    /**
     * @see net.refractions.udig.project.render.IRenderMetrics#createRenderer()
     */
    public Renderer createRenderer() {
        Renderer renderer=new BasicFeatureRenderer();
        renderer.setContext(context);
        renderer.setName(context.getLayer().getName());
        return renderer;
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetrics#getRenderContext()
     */
    public IRenderContext getRenderContext() {
        return context;
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetrics#getRenderMetricsFactory()
     */
    public IRenderMetricsFactory getRenderMetricsFactory() {
        return factory;
    }

    public boolean canAddLayer( ILayer layer ) {
        return true;
    }

    public boolean canStyle( String SyleID, Object value ) {
        return value != null && value instanceof Style;
    }


    public boolean isOptimized() {
        return false;
    }


    public Set<Range> getValidScaleRanges() {
        Style style = (Style) context.getLayer().getStyleBlackboard().get(SLDContent.ID);
        if( style == null ) {
            return new HashSet<Range>();
        }
        MinMaxScaleCalculator minMaxScaleCalculator = new MinMaxScaleCalculator();
        style.accept(minMaxScaleCalculator);
        return minMaxScaleCalculator.getRanges();
    }
}
