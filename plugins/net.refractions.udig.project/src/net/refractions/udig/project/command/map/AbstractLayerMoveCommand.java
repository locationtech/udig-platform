package net.refractions.udig.project.command.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.project.internal.Map;

import org.eclipse.jface.viewers.IStructuredSelection;

public abstract class AbstractLayerMoveCommand extends AbstractCommand implements UndoableCommand {

    private List<Integer> index;
    private List<ILayer> selection;
    
    public AbstractLayerMoveCommand( Map map, ILayer layer ) {
        selection = new ArrayList<ILayer>();
        selection.add(layer);
        index = new ArrayList<Integer>();
        index.add(getIndex(map, layer));
    }
    
    public AbstractLayerMoveCommand( Map map, IStructuredSelection structuredSelection ) {
        
        selection = new ArrayList<ILayer>();
        index = new ArrayList<Integer>();
        
        if (structuredSelection.isEmpty()) {
            return;
        }
        
        for( Iterator< ? > iter = structuredSelection.iterator(); iter.hasNext(); ) {
            Object item = iter.next();
            if (item instanceof ILayer) {
                final ILayer layer = (ILayer) item;
                selection.add(layer);
                index.add(getIndex(map, layer));
            }
        }
        
    }
    
    public AbstractLayerMoveCommand( Map map, List<ILayer> selection ) {
        this.selection = selection;
        index = new ArrayList<Integer>();
        for( ILayer iLayer : selection ) {
            final ILayer layer = (ILayer) iLayer;
            index.add(getIndex(map, layer));
        }
    }

    public List<ILayer> getSelection() {
        return selection;
    }
    
    public List<Integer> getIndex() {
        return index;
    }

    private Integer getIndex(Map map, ILayer layer) {
        return Integer.valueOf(map.getLayersInternal().indexOf(layer));
    }
    
}
