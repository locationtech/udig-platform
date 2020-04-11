/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.render.internal.feature.basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.geotools.styling.Style;
import org.geotools.util.Range;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.core.MinMaxScaleCalculator;
import org.locationtech.udig.project.ProjectBlackboardConstants;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.style.sld.SLDContent;

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
        styleIds.add(ProjectBlackboardConstants.LAYER__STYLE_FILTER);
        styleIds.add("org.locationtech.udig.style.cache");
        
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
        Boolean memory = (Boolean) context.getLayer().getStyleBlackboard().get("org.locationtech.udig.style.cache");
        
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
     * @see org.locationtech.udig.project.render.IRenderMetrics#createRenderer()
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
