/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.render.internal.feature.basic;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.locationtech.udig.project.render.ILabelPainter;

import org.geotools.geometry.jts.LiteShape2;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.styling.TextSymbolizer;
import org.geotools.util.NumberRange;
import org.opengis.feature.Feature;

/**
 * Wraps the context's {@link ILabelPainter} to allow labeling produced by the Streaming/Shapefile
 * renderers to be combined with the labels from other labels.
 * <p>
 * Streaming and Shapefile renderer both assume they are the start and end points of rendering so
 * they start and finish the label cache, as well as assign their own layerIds and clear the cache.
 * <p>
 * None of these are acceptable for uDig so this class intercepts the calls and handles these cases
 * as well as translating the geometries so they are relative to the full display area.
 * 
 * @author Jesse
 * @since 1.1.0
 * @version 1.2.1
 */
public class LabelCacheDecorator implements LabelCache{
    private final LabelCache wrapped;
    //private final Point origin;
    private final String layerId;
    
    /**
     * Create a new LabelCacheDecorator to protect a wrapped label cache
     * from being cleared.
     * 
     * @param wrapped LabelCache being protected from being cleared
     * @param origin The origin of the paint area
     * @param layerId Target layer id
     */
    public LabelCacheDecorator( final LabelCache wrapped, Point origin, String layerId ) {
        super();
        this.wrapped = wrapped;
        //this.origin = origin;
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
    
    public void put( String layerId, TextSymbolizer symbolizer, Feature feature, LiteShape2 shape,
            NumberRange<Double> scaleRange ) {
        // this may change to Feature in the future
        wrapped.put(this.layerId, symbolizer, feature, shape, scaleRange);
    }

    public void start() {
        wrapped.start();
    }

    public void startLayer( String layerId ) {
        // this is called by the frame work earlier.  Geotools doesn't need to call this
    }

    /**
     * We should not stop label cache from one renderer while it is still shared
     * between many renderers.
     * <p>
     * This prevents labels being rendered when a layer is removed.
     */
    public void stop() {
    }

}
