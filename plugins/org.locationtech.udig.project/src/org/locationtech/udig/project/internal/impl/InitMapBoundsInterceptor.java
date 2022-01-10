/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.udig.project.interceptor.LayerInterceptor;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.internal.render.impl.ViewportModelImpl;
import org.locationtech.udig.ui.ProgressManager;

/**
 * If first layer it sets the viewport bounds to be the bounds of the layer.
 *
 * @author jesse
 * @since 1.1.0
 */
public class InitMapBoundsInterceptor implements LayerInterceptor {

    @Override
    public void run(final Layer layer) {
        if (layer.getMap() == null) {
            // this check is here because we could be doing a copy
            return;
        }
        if (layer.getMap().getProject() == null) {
            // this check is here because we are probably loading
            return;
        }
        final Map map = layer.getMapInternal();
        final ViewportModel viewportModel = map.getViewportModelInternal();

        ReferencedEnvelope bounds = viewportModel.getBounds();
        // If first layer or if the CRS has been unchanged from the original BBox
        if (map.getMapLayers().size() == 1
                || bounds == ViewportModelImpl.getDefaultReferencedEnvelope()) {
            bounds = map.getBounds(ProgressManager.instance().get());
            viewportModel.setBounds(bounds);
        }
    }

}
