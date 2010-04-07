/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.render.internal.wmsc.basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.catalog.wmsc.server.TileSet;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.render.Renderer;
import net.refractions.udig.project.render.AbstractRenderMetrics;
import net.refractions.udig.project.render.IRenderContext;
import net.refractions.udig.project.render.IRenderMetricsFactory;

import org.geotools.util.Range;

/**
 * Used to describe the rendering abilities of
 * a BasicWMSCRenderer.
 * 
 * 
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.1.0
 */
public class BasicWMSCRenderMetrics extends AbstractRenderMetrics {
    HashSet<Range<Double>> ranges = new HashSet<Range<Double>>();
    
    public BasicWMSCRenderMetrics( IRenderContext context, IRenderMetricsFactory factory, TileSet tiles ) {
        super(context, factory, new ArrayList<String>());   //currently this renderer does not make use of styles
        
//        if (tiles != null){
//            double r[] = tiles.getResolutions();
//            for( int i = 0; i < r.length; i++ ) {
//                Double v = ScaleUtils.unitsPerPixelToScaleDenominator(r[i], tiles.getCoordinateReferenceSystem());
//                ranges.add(new Range<Double>(Double.class, new Double(v), new Double(v)));
//            }
//        }
    }

    public boolean canAddLayer( ILayer layer ) {
        return false;
    }

    /** Style not used by renderer */
    public boolean canStyle( String styleID, Object value ) {
        return false;
    }

    public Renderer createRenderer() {
        return new BasicWMSCRenderer();
    }
    
    public Set<Range<Double>> getValidScaleRanges(){
        return ranges;
    }
}
