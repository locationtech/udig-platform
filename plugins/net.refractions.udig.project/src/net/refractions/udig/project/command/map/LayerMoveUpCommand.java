package net.refractions.udig.project.command.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.ContextModel;
import net.refractions.udig.project.internal.Layer;

/**
 * Move the selected layers up.
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.2.0
 */
public class LayerMoveUpCommand extends AbstractCommand implements UndoableMapCommand {
    private List<ILayer> selection;
    public LayerMoveUpCommand( IStructuredSelection structuredSelection ) {
        selection = new ArrayList<ILayer>();
        if (structuredSelection.isEmpty()) {
            return;
        }
        for( Iterator< ? > iter = structuredSelection.iterator(); iter.hasNext(); ) {
            Object item = iter.next();
            if (item instanceof ILayer) {
                ILayer layer = (ILayer) item;
                selection.add(layer);
            }
        }
    }
    public LayerMoveUpCommand( List<ILayer> selection ) {
        this.selection = selection;
    }
    public String getName() {
        return "Move Up";
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        for( ILayer layer : selection ) {
            getMap().raiseLayer((Layer) layer);
        }
    }
    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( ILayer layer : selection ) {
            getMap().lowerLayer((Layer) layer);
        }
    }

}