/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.project.command.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerLegendItem;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Move the selected layers down.
 * <p>
 * The list of layers provided (usually from a structured selection )
 * </p>
 * 
 * @author Jody Garnett (LISAsoft)
 * @since 1.2.0
 */
public class LayerMoveDownCommand extends AbstractCommand implements UndoableMapCommand {
    private List<ILayer> selection;

    public LayerMoveDownCommand( ILayer layer ) {
        selection = new ArrayList<ILayer>();
        selection.add( layer );
    }
    
    public LayerMoveDownCommand( IStructuredSelection structuredSelection ) {
        selection = new ArrayList<ILayer>();
        if (structuredSelection.isEmpty()) {
            return;
        }
        for( Iterator< ? > iter = structuredSelection.iterator(); iter.hasNext(); ) {
            Object item = iter.next();
            if (item instanceof ILayer) {
                ILayer layer = (ILayer) item;
                selection.add(layer);
            } else if (item instanceof LayerLegendItem) {
                final LayerLegendItem layerItem = (LayerLegendItem) item;
                selection.add(layerItem.getLayer());
            }
        }
    }
    public LayerMoveDownCommand( List<ILayer> selection ) {
        this.selection = selection;
    }
    public String getName() {
        return "Move Up"; //$NON-NLS-1$
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        for( ILayer layer : selection ) {
            getMap().lowerLayer((Layer) layer);
        }
    }
    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( ILayer layer : selection ) {
            getMap().raiseLayer((Layer) layer);
        }
    }

}