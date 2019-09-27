/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.dragdrop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.commands.AddLayersCommand;
import org.locationtech.udig.project.internal.commands.DeleteLayersCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.LayersView;
import org.locationtech.udig.project.ui.internal.MapEditorPart;
import org.locationtech.udig.ui.IDropAction;

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
        if (destination2 instanceof LayersView || destination2 instanceof MapEditorPart) {
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
        if (getDestination() instanceof LayersView || getDestination() instanceof MapEditorPart) {
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
