/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.commands;

import java.util.List;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.filter.Filter;

/**
 * Clears the edit Blackboards and the current edit shape and state.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class DefaultCancelEditingCommand extends AbstractCommand implements UndoableMapCommand {

    private EditToolHandler handler;
    private PrimitiveShape currentShape;
    private EditState currentState;
    private List<EditGeom> geoms;
    private Filter oldFilter;

    public DefaultCancelEditingCommand( EditToolHandler handler ) {
        this.handler = handler;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        Layer editLayer = (Layer) handler.getEditLayer();
        if (currentShape != null) {
            this.currentShape = handler.getCurrentShape();
            this.currentState = handler.getCurrentState();
            this.geoms = handler.getEditBlackboard(editLayer).getGeoms();
        }
        handler.setCurrentShape(null);
        handler.setCurrentState(EditState.NONE);

        oldFilter = (Filter) editLayer.getFilter();
        editLayer.setFilter(Filter.EXCLUDE);

        EditUtils.instance.cancelHideSelection(editLayer);

        EditBlackboard editBlackboard = handler.getEditBlackboard(editLayer);
        editBlackboard.startBatchingEvents();
        editBlackboard.clear();
        editBlackboard.fireBatchedEvents();

        handler.repaint();
    }

    public String getName() {
        return Messages.DefaultCancelEditingCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        Layer editLayer = (Layer) handler.getEditLayer();
        editLayer.setFilter(oldFilter);
        
        EditBlackboard editBlackboard = handler.getEditBlackboard(editLayer);
        editBlackboard.startBatchingEvents();
        for( EditGeom geom : geoms ) {
            copyFeature(editBlackboard, geom);
        }
        handler.setCurrentState(this.currentState);
        editBlackboard.fireBatchedEvents();
        handler.repaint();
    }

    /**
     * Copies the geometry back onto the editblackboard.
     * 
     * @return
     */
    private void copyFeature( EditBlackboard editBlackboard, EditGeom geom ) {
        EditGeom newGeom = editBlackboard.newGeom(geom.getFeatureIDRef().get(), geom.getShapeType());
        for( PrimitiveShape shape : geom ) {
            PrimitiveShape newShape;
            if (shape == geom.getShell()) {
                newShape = newGeom.getShell();
            } else {
                newShape = newGeom.newHole();
            }
            if (shape == currentShape)
                handler.setCurrentShape(newShape);
            for( int i = 0; i < shape.getNumCoords(); i++ ) {
                editBlackboard.addCoordinate(shape.getCoord(i), newShape);
            }
            newGeom.setChanged(geom.isChanged());
        }
    }

}
