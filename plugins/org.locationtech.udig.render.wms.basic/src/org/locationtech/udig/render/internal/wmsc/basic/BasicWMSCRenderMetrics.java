/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.render.internal.wmsc.basic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.geotools.util.Range;
import org.locationtech.udig.catalog.wmsc.server.TileSet;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.render.Renderer;
import org.locationtech.udig.project.render.AbstractRenderMetrics;
import org.locationtech.udig.project.render.IRenderContext;
import org.locationtech.udig.project.render.IRenderMetricsFactory;

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
