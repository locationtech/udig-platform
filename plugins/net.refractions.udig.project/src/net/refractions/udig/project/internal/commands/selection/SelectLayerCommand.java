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
package net.refractions.udig.project.internal.commands.selection;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

/**
 * Command that selects the currently selected layer of a map.
 * @author Jesse
 * @since 1.1.0
 */
public class SelectLayerCommand extends AbstractCommand implements MapCommand, UndoableMapCommand {

    private Layer old;
    private ILayer layer;

    /**
     * new instance
     * @param layer layer to select
     */
    public SelectLayerCommand( ILayer layer ) {
        super();
        this.layer = layer;
    }

    public String getName() {
        return Messages.SelectLayerCommand_name;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.SelectLayerCommand_selecting, 2);
        monitor.worked(1);

        EditManager editManagerInternal = getMap().getEditManagerInternal();
        old=editManagerInternal.getSelectedLayer();
        editManagerInternal.setSelectedLayer((Layer) layer);
        monitor.done();
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.SelectLayerCommand_undoing, 2);
        monitor.worked(1);

        EditManager editManagerInternal = getMap().getEditManagerInternal();
        editManagerInternal.setSelectedLayer(old);
        monitor.done();
    }

}
