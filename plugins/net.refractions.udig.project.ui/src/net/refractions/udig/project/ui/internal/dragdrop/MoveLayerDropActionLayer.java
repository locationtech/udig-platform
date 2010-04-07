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

import java.util.Collection;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.commands.AddLayersCommand;
import net.refractions.udig.project.internal.commands.DeleteLayersCommand;
import net.refractions.udig.ui.IDropAction;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Moves layers from one map to another when destination is a layer.
 * @author Jesse
 * @since 1.1.0
 */
public class MoveLayerDropActionLayer extends IDropAction {


    public MoveLayerDropActionLayer() {
    }

    @Override
    public boolean accept() {
        return MoveLayerDropAction.toLayer(this)!=null &&
            MoveLayerDropAction.toLayer(this).getMap()!=((Layer) getDestination()).getMap();
    }

    @Override
    public void perform( IProgressMonitor monitor ) {
        Collection<Layer> layers = MoveLayerDropAction.toCollection(getData());
        
        Layer layer=(Layer) getDestination();
        layer.getMap().sendCommandASync(new AddLayersCommand(layers, layer.getZorder()));
        
        layers.iterator().next().getMap().sendCommandASync(new DeleteLayersCommand(layers.toArray(new Layer[0])));
    }

}
