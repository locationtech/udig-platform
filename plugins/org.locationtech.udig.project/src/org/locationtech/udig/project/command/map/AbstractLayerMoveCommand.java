/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableCommand;
import org.locationtech.udig.project.internal.LayerLegendItem;
import org.locationtech.udig.project.internal.Map;

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

    public AbstractLayerMoveCommand(Map map, ILayer layer) {
        selection = new ArrayList<ILayer>();
        selection.add(layer);
        index = new ArrayList<Integer>();
        index.add(getIndex(map, layer));
    }

    public AbstractLayerMoveCommand(Map map, IStructuredSelection structuredSelection) {

        selection = new ArrayList<ILayer>();
        index = new ArrayList<Integer>();

        if (structuredSelection.isEmpty()) {
            return;
        }

        for (Iterator<?> iter = structuredSelection.iterator(); iter.hasNext();) {
            final Object item = iter.next();
            ILayer layer = null;
            if (item instanceof ILayer) {
                layer = (ILayer) item;
            } else if (item instanceof LayerLegendItem) {
                final LayerLegendItem layerItem = (LayerLegendItem) item;
                layer = layerItem.getLayer();
            }
            if (layer != null) {
                selection.add(layer);
                index.add(getIndex(map, layer));
            }
        }

    }

    public AbstractLayerMoveCommand(Map map, List<ILayer> selection) {
        this.selection = selection;
        index = new ArrayList<Integer>();
        for (ILayer iLayer : selection) {
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
