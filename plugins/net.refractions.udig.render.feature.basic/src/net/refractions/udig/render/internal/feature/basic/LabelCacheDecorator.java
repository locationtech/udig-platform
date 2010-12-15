/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.render.internal.feature.basic;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import net.refractions.udig.project.render.ILabelPainter;

import org.geotools.geometry.jts.LiteShape2;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.styling.TextSymbolizer;
import org.geotools.util.NumberRange;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Wraps the context's {@link ILabelPainter} to allow labelling produced by the Streaming/Shapefile renderers to be combined 
 * with the labels from other labels.  Streaming and Shapefile renderer both assume they are the start and end points of rendering
 * so they start and finish the label cache, as well as assign their own layerIds and clear the cache.  None of these are acceptable
 * for uDig so this class intercepts the calls and handles these cases as well as translating the geometries so they are relative to 
 * the full display area. 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class LabelCacheDecorator implements LabelCache{
    private final LabelCache wrapped;
    private final Point origin;
    private final String layerId;
    
    /**
     * 
     * @param wrapped 
     * @param origin huh
     * @param layerId Target layer id
     */
    public LabelCacheDecorator( final LabelCache wrapped, Point origin, String layerId ) {
        super();
        this.wrapped = wrapped;
        this.origin = origin;
        this.layerId=layerId;
    }

    public void clear() {
        // do nothing see doClear()
    }

    public void end( Graphics2D graphics, Rectangle displayArea ) {
        // do nothing so that Geotools renderers don't trigger the rendering of the labels
    }

    public void clear( String layerId ) {
        wrapped.clear(this.layerId);
    }

    public void disableLayer( String layerId ) {
        wrapped.disableLayer(this.layerId);
    }

    public void enableLayer( String layerId ) {
        wrapped.enableLayer(this.layerId);
    }

    public void endLayer( String layerId, Graphics2D graphics, Rectangle displayArea ) {
        // this is called by the frame work later.  Geotools doesn't need to call this
    }

    public List orderedLabels() {
        return wrapped.orderedLabels();
    }

    public void put( Rectangle2D area) {
        wrapped.put(area);
    }
    
    public void put( String layerId, TextSymbolizer symbolizer, SimpleFeature feature, LiteShape2 shape,
            NumberRange<Double> scaleRange ) {

        wrapped.put(this.layerId, symbolizer, feature, shape, scaleRange);
    }

    public void start() {
        wrapped.start();
    }

    public void startLayer( String layerId ) {
        // this is called by the frame work earlier.  Geotools doesn't need to call this
    }

    public void stop() {
        /*
         * Vitalus:
         * We should not stop label cache from one renderer while it is shared between many renderers.
         * Fix for labels rendering when the layer is removed.
         */ 
//        wrapped.stop();
    }

   public void put(String arg0, TextSymbolizer arg1, Feature arg2,
			LiteShape2 arg3, NumberRange<Double> arg4) {
		wrapped.put(arg0, arg1, arg2, arg3, arg4);
	}


}
