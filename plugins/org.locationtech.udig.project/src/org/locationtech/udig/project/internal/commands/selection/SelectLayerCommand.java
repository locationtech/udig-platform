/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.selection;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

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
