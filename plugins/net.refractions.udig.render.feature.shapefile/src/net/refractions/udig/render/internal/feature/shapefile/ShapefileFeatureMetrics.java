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
package net.refractions.udig.render.internal.feature.shapefile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.refractions.udig.core.MinMaxScaleCalculator;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.preferences.PreferenceConstants;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;

import org.eclipse.jface.preference.IPreferenceStore;
import org.geotools.styling.Style;
import org.geotools.util.Range;

/**
 * The metrics object for the BasicFeatureRenderer
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ShapefileFeatureMetrics extends AbstractRenderMetrics {

    protected static List<String> EXPECTED = Collections.unmodifiableList(Arrays
            .asList(new String[]{"net.refractions.udig.project.view",
                    "net.refractions.udig.style.sld",}));

    public ShapefileFeatureMetrics( IRenderContext context2, ShapefileFeatureMetricsFactory factory ) {
        super(context2, factory, EXPECTED);
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
        this.latencyMetric = LATENCY_LOCAL;
        this.timeToDrawMetric = DRAW_DATA_INDEX;
    }

    /**
     * @see net.refractions.udig.project.render.IRenderMetrics#createRenderer()
     */
    public Renderer createRenderer() {
        Renderer renderer = new ShapefileFeatureRenderer();
        renderer.setContext(context);
        renderer.setName(context.getLayer().getName());
        return renderer;
    }

    @Override
    public boolean canStyle( String styleID, Object value ) {
        return value != null && value instanceof Style;
    }

    public Set<Range<Double>> getValidScaleRanges() {
        Style style = (Style) context.getLayer().getStyleBlackboard().get(
                "net.refractions.udig.style.sld");
        return MinMaxScaleCalculator.getValidScaleRanges(style);
    }
}
