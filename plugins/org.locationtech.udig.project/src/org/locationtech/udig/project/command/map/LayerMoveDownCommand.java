/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerLegendItem;

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
        // need to reverse otherwise nothing happens on multiselect
        Collections.reverse(selection);
        int lastAllowedIndex = 0; 
        for( ILayer layer : selection ) {
            int layerIndex = layer.getZorder();
            if (layerIndex == lastAllowedIndex) {
                lastAllowedIndex++;
                continue;
            }
            getMap().lowerLayer((Layer) layer);
        }
    }
    
    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( ILayer layer : selection ) {
            getMap().raiseLayer((Layer) layer);
        }
    }

}
