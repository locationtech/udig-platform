/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.render.internal.feature.shapefile;

import java.awt.RenderingHints;
import java.util.HashMap;

import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.locationtech.udig.render.internal.feature.basic.BasicFeatureRenderer;

/**
 * The default victim renderer. Based on the Lite-Renderer from Geotools.
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class ShapefileFeatureRenderer extends BasicFeatureRenderer {
    
    //ShapefileRenderer renderer;
    StreamingRenderer renderer;
    
    public ShapefileFeatureRenderer() {
    }

    @Override
    protected GTRenderer getRenderer() {
        if (renderer == null) {
            //renderer = new ShapefileRenderer();
            renderer = new StreamingRenderer();
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
