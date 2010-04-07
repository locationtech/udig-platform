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
package net.refractions.udig.project.ui.internal.dragdrop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.commands.AddLayersCommand;
import net.refractions.udig.project.internal.commands.DeleteLayersCommand;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.LayersView;
import net.refractions.udig.project.ui.internal.MapEditor;
import net.refractions.udig.ui.IDropAction;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 *  Moves layers from one map to another when destination is a map or LayersView or MapEditor.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MoveLayerDropAction extends IDropAction {

    @Override
    public boolean accept() {
        Layer layer = toLayer(this);
        if (layer==null ) return false;
        Object destination2 = getDestination();
        if (destination2 instanceof LayersView || destination2 instanceof MapEditor) {
            destination2=ApplicationGIS.getActiveMap();
        }
        return layer.getMap()!=destination2;
    }

     static Layer toLayer(IDropAction action) {
        Layer layer;
        if ( action.getData() instanceof Layer ){
            layer=(Layer) action.getData();
        }else {
           Object[] layers= (Object[])action.getData();
           layer=(Layer) layers[0];
           // check that all layers are from same map
           for( Object layer2 : layers ) {
               if (((ILayer) layer2).getMap()!=layer.getMap() )
                   return null;
           }
        }
        return layer;
    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        Object data2 = getData();
        IMap map;
        if (getDestination() instanceof LayersView || getDestination() instanceof MapEditor) {
            map=ApplicationGIS.getActiveMap();
        }else{
            map=(Map) getDestination();
        }

        Collection<Layer> layers = toCollection(data2);
        map.sendCommandASync( new AddLayersCommand(layers) );
        layers.iterator().next().getMap().sendCommandASync(new DeleteLayersCommand(layers.toArray(new Layer[0])));
    }

    @SuppressWarnings("unchecked")
    public
     static Collection<Layer> toCollection( Object data2 ) {
        Collection<Layer> layers;
        if ( data2 instanceof Layer ){
            layers=Collections.singleton((Layer) data2);
        }else {
            layers = new ArrayList<Layer>();
           Object[] array = (Object[]) data2;
           for( Object object : array ) {
            if( object instanceof Layer){
                layers.add((Layer) object);
            }
        }
        }
        return layers;
    }

}
