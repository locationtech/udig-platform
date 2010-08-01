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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.core.MinMaxScaleCalculator;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ProjectBlackboardConstants;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.preferences.PreferenceConstants;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;
import net.refractions.udig.style.sld.SLDContent;

import org.eclipse.jface.preference.IPreferenceStore;
import org.geotools.styling.Style;
import org.geotools.util.Range;

/**
 * The metrics object for the BasicFeatureRenderer
 * 
 * @author Jesse Eichar
 */
public class BasicFeatureMetrics extends AbstractRenderMetrics {

    /*
     * list of styles the basic wms renderer is expecting to find and use
     */
    protected static List<String> listExpectedStyleIds() {
        ArrayList<String> styleIds = new ArrayList<String>();
        styleIds.add(SLDContent.ID);
        styleIds.add(ProjectBlackboardConstants.LAYER__DATA_QUERY);
        styleIds.add("net.refractions.udig.style.cache");
        
        return styleIds;
    }

    public BasicFeatureMetrics( IRenderContext context, BasicFeatureMetricsFactory factory ) {
        super(context, factory, listExpectedStyleIds());
        
        // RESOLUTION QUALITY VS SPEED TRADEOFFS
        IPreferenceStore store = ProjectPlugin.getPlugin().getPreferenceStore();
        boolean antiAliasing = store.getBoolean(PreferenceConstants.P_ANTI_ALIASING);
        if( antiAliasing ){
            this.resolutionMetric = RES_DENSE;
        }
        else {
            this.resolutionMetric = RES_PIXEL;
        }
        // DATA SOURCE PERFORMANCE INDICATORS
        ID id = context.getGeoResource().getID();
        Boolean memory = (Boolean) context.getLayer().getStyleBlackboard().get("net.refractions.udig.style.cache");
        
        if( id.isMemory() ){
            this.latencyMetric = LATENCY_MEMORY;
            this.timeToDrawMetric = DRAW_DATA_MEMORY;
        }
        else if( Boolean.TRUE.equals( memory ) ){
            this.latencyMetric = LATENCY_MEMORY_CACHE;
            this.timeToDrawMetric = DRAW_DATA_MEMORY;
        }
        else if( id.isFile() ){
            this.latencyMetric = LATENCY_LOCAL;
            this.timeToDrawMetric = DRAW_DATA_RAW;
        }
        else if( id.isJDBC() || id.isWFS() ){
            this.latencyMetric = LATENCY_NETWORK;
            this.timeToDrawMetric = DRAW_DATA_RAW;
        }
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetrics#createRenderer()
     */
    public Renderer createRenderer() {
        Renderer renderer = new BasicFeatureRenderer();
        renderer.setContext(context);
        renderer.setName(context.getLayer().getName());
        return renderer;
    }

    public boolean canStyle( String SyleID, Object value ) {
        return value != null && value instanceof Style;
    }

    public Set<Range<Double>> getValidScaleRanges() {
        Object value = context.getLayer().getStyleBlackboard().get(SLDContent.ID);
        if( value == null ) {
            return new HashSet<Range<Double>>();
        }
        if( value instanceof Style ){
            Style style = (Style) value;
            return MinMaxScaleCalculator.getValidScaleRanges(style);
        }
        else {
            System.out.println("Unexpected "+value.getClass()+" for "+SLDContent.ID+":"+value);            
            return new HashSet<Range<Double>>();
        }
    }
}
