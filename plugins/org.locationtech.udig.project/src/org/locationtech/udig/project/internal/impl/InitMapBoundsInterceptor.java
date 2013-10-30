/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal.impl;

import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.ui.ProgressManager;

import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * If first layer it sets the viewport bounds to be the bounds of the layer.
 * 
 * @author jesse
 * @since 1.1.0
 */
public class InitMapBoundsInterceptor implements LayerInterceptor {

    public void run( Layer layer ) {
        if(layer.getMap()==null ){
            // this check is here because we could be doing a copy
            return;
        }
        if(layer.getMap().getProject() == null) {
        	// this check is here because we are probably loading
        	return;
        }
        Map map = layer.getMapInternal();

        ReferencedEnvelope bounds = map.getViewportModelInternal().getBounds();
        //  If first layer or if the crs has been unchanged from the original BBox
		if( map.getMapLayers().size()==1 || bounds==ViewportModel.NIL_BBOX){
            bounds = map.getBounds(ProgressManager.instance().get());
            map.getViewportModelInternal().setBounds(bounds);
        }
    }

}
