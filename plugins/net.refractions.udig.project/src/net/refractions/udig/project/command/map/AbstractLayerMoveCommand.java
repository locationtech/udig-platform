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
package net.refractions.udig.project.command.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableCommand;
import net.refractions.udig.project.internal.Map;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Abstract parent class for layer move commands.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
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
