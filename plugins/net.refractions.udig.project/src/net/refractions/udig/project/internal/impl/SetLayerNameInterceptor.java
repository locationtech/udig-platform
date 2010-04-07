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
package net.refractions.udig.project.internal.impl;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.project.interceptor.LayerInterceptor;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.ui.ProgressManager;

/**
 * Sets the name of a newly created layer.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SetLayerNameInterceptor implements LayerInterceptor {

    public void run( Layer layer ) {
        try {
            IGeoResourceInfo info = layer.getGeoResource().getInfo(ProgressManager.instance().get());
            nameLayer(info, layer);
        } catch (IOException e) {
            //shouldn't happen
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

    /**
     * Goes through some options for create a layer's default name.
     * <ol>
     * <li>info.getTitle() - long title; not the best but is at least human readable
     * <li>info.getName() - internal name - not really considered human readable
     *
     * @param info
     * @param layer
     */
    private void nameLayer( IGeoResourceInfo info, Layer layer ) {
        String label = info.getTitle(); // may be empty?
        if( label == null || label.trim().length() == 0){
            label = info.getName(); // really should not be empty?
        }
        if( label == null || label.trim().length() == 0){
            label = "newLayer";
        }
        layer.setName(label);
    }

}
