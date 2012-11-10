/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal.commands;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Adds a Folder item to the map. More specifically, adds a Folder item into the LegendItem list of
 * the map.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class AddFolderItemCommand extends AbstractCommand implements UndoableMapCommand {

    /**
     * Default folder insert position.
     */
    private static final int POSITION = 0;
    
    /**
     * Folder to be added
     */
    private Folder folder;
    
    /**
     * Creates an add folder command.
     * @param folder
     */
    public AddFolderItemCommand (Folder folder) {
        this.folder = folder;
    }
    
    @Override
    public String getName() {
        return Messages.AddFolderItemCommand_Name;
    }

    @Override
    public void run( IProgressMonitor monitor ) throws Exception {
        if (folder != null) {
            getMap().getLegend().add(POSITION, folder);
            //TODO - Save currently selected item
            //TODO - Select folder as currently selected item
        }
    }

    @Override
    public void rollback( IProgressMonitor monitor ) throws Exception {
        if (folder != null) {
            getMap().getLegend().remove(folder);
            //TODO - Restore currently selected item
        }
    }

}

