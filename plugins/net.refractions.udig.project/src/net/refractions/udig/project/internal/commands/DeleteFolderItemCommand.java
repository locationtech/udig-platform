package net.refractions.udig.project.internal.commands;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Folder;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Deletes a Folder item from the map. More specifically, deletes a Folder item from the LegendItem
 * list of the map.
 * 
 * @author nchan
 * @since 1.2.0
 */
public class DeleteFolderItemCommand extends AbstractCommand implements UndoableMapCommand {

    /**
     * Folder to be deleted
     */
    private Folder folder;

    /**
     * Index of the folder to be deleted in the LegendItem list
     */
    private int index;

    /**
     * Flags if folder is existing in the map. Assigned on run. 
     * true - folder is existing in the map, otherwise, false
     */
    private boolean isInMap;

    public DeleteFolderItemCommand( Folder folder ) {
        this.folder = folder;
    }

    @Override
    public String getName() {
        return Messages.DeleteFolderItemCommand_Name;
    }

    @Override
    public void run( IProgressMonitor monitor ) throws Exception {
        initRunConditions();
        if (isInMap) {
            getMap().getLegend().remove(folder);
            // TODO - Save currently selected item
            // TODO - If currently selected item is folder, select next item
        }
    }

    @Override
    public void rollback( IProgressMonitor monitor ) throws Exception {
        if (isInMap) {
            getMap().getLegend().add(index, folder);
            // TODO - Restore currently selected item
        }
    }

    /**
     * Checks if folder is existing in the map and initialises isInMap and index variables.
     */
    private void initRunConditions() {
        if (folder != null && getMap().getLegend().contains(folder)) {
            isInMap = true;
            index = getMap().getLegend().indexOf(folder);
        } else {
            isInMap = false;
            index = -1;
        }
    }

}
