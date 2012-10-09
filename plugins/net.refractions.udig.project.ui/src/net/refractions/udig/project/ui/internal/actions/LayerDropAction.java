/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.actions;

import static net.refractions.udig.project.ui.internal.dragdrop.MoveLayerDropAction.toCollection;

import java.util.Collection;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.ui.IDropAction;
import net.refractions.udig.ui.ViewerDropLocation;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Action that moves layers when a layer within a map is dropped on a layer within the same map.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class LayerDropAction extends IDropAction {

    @Override
    public boolean accept( ) {
        Collection<Layer> layers = toCollection(getData());
        if( !(getDestination() instanceof Layer) ){
            if( !(getData() instanceof Layer) && layers.isEmpty()){
                return false;
            }
        }
        Layer destination2 = (Layer)getDestination();

        for( Layer layer : layers ) {
            if( layer==destination2 && getViewerLocation()!=ViewerDropLocation.NONE )
                return false;
            if( destination2.getMap()!=layer.getMap())
                return false;
        }
            
        return true;

    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        Layer target = (Layer) getDestination();
        Collection<Layer> layers = toCollection(getData());

        for( Layer layer : layers ) {

            ViewerDropLocation location = getViewerLocation();

            if (location == ViewerDropLocation.NONE) {
                layer.setZorder(0);
                continue;
            }
            if (Math.abs(layer.getZorder() - target.getZorder()) == 1) {
                int tmp = layer.getZorder();
                layer.setZorder(target.getZorder());
                target.setZorder(tmp);
                continue;
            }

            // Moving something AFTER a layer is the same as moving something BEFORE a layer.
            // So we will use BEFORE as much as possible to prevent duplication here.
            // This code will retrieve the layer before. Or the first one, if we are at the
            // beginning of the list.
            if (location == ViewerDropLocation.BEFORE) {
                int i = target.getZorder();

                if (layer.getZorder() > target.getZorder()) {
                    i++;
                }

                if (i > layer.getMap().getMapLayers().size()) {
                    layer.setZorder(layer.getMap().getMapLayers().size());
                    continue;
                }
                if (layer.equals(target)) {
                    continue;
                }
                layer.setZorder(i);

            } else if (location == ViewerDropLocation.ON) {
                int i = target.getZorder();

                // if( layer.getZorder()>target.getZorder()){
                // i--;
                // }

                if (layer.equals(target)) {
                    continue;
                }
                layer.setZorder(i);

            } else {
                int i = target.getZorder();

                if (layer.getZorder() < target.getZorder()) {
                    i--;
                }
                if (i < 0)
                    i = 0;
                layer.setZorder(i);
            }
        }
    }
}
