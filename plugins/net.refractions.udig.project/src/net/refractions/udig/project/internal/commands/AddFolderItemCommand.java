/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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

