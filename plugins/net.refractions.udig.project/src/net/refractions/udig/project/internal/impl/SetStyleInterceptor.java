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


import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.SetDefaultStyleProcessor;

/**
 * Sets the default style of the layer.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SetStyleInterceptor implements LayerInterceptor {

    public void run( Layer layer ) {
        if( layer.getStyleBlackboard().getContent().isEmpty()){
            IGeoResource geoResource = layer.getGeoResource();
            if( geoResource == null ){
                throw new NullPointerException("Layer requires GeoResource to determine default style");
            }
            SetDefaultStyleProcessor defaultStyleProcessor = createDefaultStyles(geoResource, layer);
            defaultStyleProcessor.run();
        }
    }

    /**
     * Creates a default style from each styleContent extension and puts them on the style blackboard of the layer.
     * @param theResource
     * @param theLayer
     * @return
     */
    private SetDefaultStyleProcessor createDefaultStyles( final IGeoResource theResource, final Layer theLayer ) {
        SetDefaultStyleProcessor scp = new SetDefaultStyleProcessor(theResource, theLayer);
        return scp;
    }

}
