/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Deletes specified layers from the map.
 * <p>
 * 
 * @author Vitalus
 * @since UDIG 1.1.0
 */
public class DeleteLayersCommand extends AbstractCommand implements UndoableMapCommand {

    private List<ILayer> removedLayers = null;

    private int index;

    private Map map = null;

    private ILayer selectedLayer;;

    /**
     * @param layers
     */
    public DeleteLayersCommand( ILayer[] layers ) {

        removedLayers = new ArrayList<ILayer>();
        for( int i = 0; i < layers.length; i++ ) {
            removedLayers.add(layers[i]);
            if (map == null)
                map = ((Layer) layers[i]).getMapInternal();
        }
    }

    /**
     * @see org.locationtech.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        map.getMapLayers().addAll(index, removedLayers);
        if( selectedLayer!=null )
            map.getEditManagerInternal().setSelectedLayer((Layer) selectedLayer);
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {

        selectedLayer=map.getEditManager().getSelectedLayer();
        if( !DeleteLayerCommand.selectNewLayer(map, removedLayers) )
            selectedLayer=null;
        
        List< ? extends ILayer> layers = map.getLayersInternal();
        layers.removeAll(removedLayers);
        for( ILayer layer : removedLayers ) {
            map.getColourScheme().removeItem(layer.getID().toString(),
                    ((Layer) layer).getDefaultColor()); // remove from scheme
        }

    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        // TODO Internationalization
        return Messages.DeleteLayersCommand_name;
    }
}
