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
package net.refractions.udig.project.internal.render.impl;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import net.refractions.udig.project.render.ILabelPainter;

import org.geotools.geometry.GeometryFactoryFinder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.renderer.label.LabelCacheImpl;
import org.geotools.renderer.lite.LabelCache;
import org.geotools.renderer.lite.SynchronizedLabelCache;

import com.vividsolutions.jts.geom.GeometryFactory;


/**
 * Extends the Geotools default labeller so that the geotools renderer doesn't clear the cache when it runs.  
 * The comments in the geotools default label cache states this is ok.
 * <p>
 * Also over-rides end so that geotools renderers don't cause the rendering and we can do it once at the end.
 * </p>
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class UDIGLabelCache extends SynchronizedLabelCache implements ILabelPainter {
    Set<String> activeLayers = new HashSet<String>();

    private LabelCache wrappedLabelCache;

    /**
     * 
     */
    public UDIGLabelCache() {
        this(new LabelCacheImpl()); // switch to new one here!
    }

    /**
     * @param defaultLabelCache
     */
    public UDIGLabelCache( LabelCache labelCache ) {
        super(labelCache);
        this.wrappedLabelCache = labelCache;
    }

    public Object getAdapter( Class adapter ) {
        return null;
    }

    @Override
    public synchronized void startLayer( String layerId ) {
        activeLayers.add(layerId);
        super.startLayer(layerId);
    }

    @Override
    public synchronized void stop() {
        activeLayers.clear();
        super.stop();
    }

    @Override
    public synchronized void end( Graphics2D graphics, Rectangle displayArea ) {
        if (activeLayers.isEmpty()) {
            super.end(graphics, displayArea);
            //System.out.println("Labels are rendered");
        } else {
            //System.out.println("Labels are NOT rendered");
        }
    }

    @Override
    public synchronized void clear() {
        if (activeLayers.isEmpty())
            super.clear();
    }

    @Override
    public synchronized void clear( String layerId ) {
        if (!activeLayers.contains(layerId))
            super.clear(layerId);
    }

    @Override
    public synchronized void endLayer( String layerId, Graphics2D graphics, Rectangle displayArea ) {
        activeLayers.remove(layerId);
        super.endLayer(layerId, graphics, displayArea);
    }

    /**
     * Returns wrapped label cache for giving rendering hints e.g.
     * 
     * @return
     */
    protected LabelCache getWrapperLabelCache() {
        return wrappedLabelCache;
    }

}
