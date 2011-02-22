/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.project.internal.commands;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Add a layer to a map.
 * @author Jesse
 * @since 1.0.0
 */
public class AddLayerCommand extends AbstractCommand implements UndoableMapCommand {

    private Layer layer;
    private int index = -1;
    private ILayer selectedLayer;
    /**
     * Construct <code>AddLayerCommand</code>.
     *
     * @param layer the layer that will be added.
     */
    public AddLayerCommand( Layer layer ) {
        this.layer = layer;
    }

    /**
     * Construct <code>AddLayerCommand</code>.
     *
     * @param layer the layer that will be added.
     * @param index the zorder that the layer will be added.
     */
    public AddLayerCommand( Layer layer, int index ) {
        this.layer = layer;
        this.index = index;
    }

    /**
     * Remove the layer that was added during execution.
     *
     * @see net.refractions.udig.project.command.UndoableCommand#rollback()
     */
    public void rollback( IProgressMonitor monitor ) throws Exception {
        getMap().getLayersInternal().remove(layer);
        getMap().getEditManagerInternal().setSelectedLayer((Layer) selectedLayer);
    }

    /**
     * Adds a layer to the map. Defensive programming is recommended but command framework protects
     * against exceptions raised in commands.
     *
     * @see net.refractions.udig.project.command.MapCommand#run()
     */
    public void run( IProgressMonitor monitor ) throws Exception {
        selectedLayer=getMap().getEditManager().getSelectedLayer();
        if (index < 0 || index > getMap().getLayersInternal().size())
            getMap().getLayersInternal().add(layer);
        else
            getMap().getLayersInternal().add(index, layer);
    }

    /**
     * Each command has a name that is displayed with the undo/redo buttons.
     *
     * @see net.refractions.udig.project.command.MapCommand#getName()
     */
    public String getName() {
        return Messages.AddLayerCommand_Name + layer.getName();
    }

}
