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

import net.refractions.udig.render.internal.feature.basic.BasicFeatureRenderer;

import org.geotools.map.MapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.shape.ShapefileRenderer;

/**
 * The default victim renderer. Based on the Lite-Renderer from Geotools.
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ShapefileFeatureRenderer extends BasicFeatureRenderer {
    ShapefileRenderer renderer;

    public ShapefileFeatureRenderer() {
    }

    @Override
    protected GTRenderer getRenderer() {
        if (renderer == null) {
            renderer = new ShapefileRenderer();
            HashMap<String, Object> rendererHints = new HashMap<String, Object>();
            rendererHints.put("optimizedDataLoadingEnabled", true); //$NON-NLS-1$
            renderer.setRendererHints(rendererHints);
            // renderer.removeRenderListener(StreamingRenderer.DEFAULT_LISTENER);
            renderer.addRenderListener(listener);

            // JG - these may be overriden by the preferences before use?
            RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            renderer.setJava2DHints(hints);
        }
        renderer.setMapContent(map);
        return renderer;
    }

}