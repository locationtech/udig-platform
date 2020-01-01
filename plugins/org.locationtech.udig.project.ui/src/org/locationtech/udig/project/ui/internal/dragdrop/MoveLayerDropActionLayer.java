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

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.commands.AddLayersCommand;
import org.locationtech.udig.project.internal.commands.DeleteLayersCommand;
import org.locationtech.udig.ui.IDropAction;

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
