/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.render.internal.feature.shapefile;

import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.refractions.udig.render.internal.feature.basic.BasicFeatureRenderer;
import net.refractions.udig.render.internal.feature.basic.RendererPlugin;
import net.refractions.udig.render.internal.feature.basic.Trace;

import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.renderer.shape.ShapefileRenderer;

/**
 * The default victim renderer. Based on the Lite-Renderer from Geotools.
 *
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ShapefileFeatureRenderer extends BasicFeatureRenderer{
    ShapefileRenderer renderer;
    
    public ShapefileFeatureRenderer() {
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(ShapefileRenderer.class.getClassLoader());
            Logger logger = Logger.getLogger("org.geotools.renderer.shape");//$NON-NLS-1$
            if (RendererPlugin.isDebugging(Trace.FINEST)) {
                logger.setLevel(Level.FINE);
                logger.addHandler(new Handler(){

                    @Override
                    public void publish( LogRecord record ) {
                        System.err.println(record.getMessage());
                    }

                    @Override
                    public void flush() {
                        System.err.flush();
                    }

                    @Override
                    public void close() throws SecurityException {

                    }

                });
            } else {
                logger.setLevel(Level.SEVERE);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }
    }
    
    @Override
    protected GTRenderer getRenderer() {
        if (renderer == null) {
            renderer = new ShapefileRenderer(map);
            HashMap<String, Object> rendererHints = new HashMap<String, Object>();
            rendererHints.put("optimizedDataLoadingEnabled", true); //$NON-NLS-1$
            renderer.setRendererHints(rendererHints);
            renderer.removeRenderListener(StreamingRenderer.DEFAULT_LISTENER);
            renderer.addRenderListener(listener);

            RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            renderer.setJava2DHints(hints);
        }
        renderer.setContext(map);
        return renderer;
    }

}