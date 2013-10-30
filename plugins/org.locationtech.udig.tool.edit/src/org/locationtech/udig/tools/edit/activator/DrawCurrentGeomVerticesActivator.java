/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.activator;

import net.refractions.udig.project.BlackboardEvent;
import net.refractions.udig.project.EditManagerEvent;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IBlackboardListener;
import net.refractions.udig.project.IEditManagerListener;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.commands.DrawPointCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditBlackboardAdapter;
import net.refractions.udig.tools.edit.support.EditBlackboardEvent;
import net.refractions.udig.tools.edit.support.EditBlackboardListener;
import net.refractions.udig.tools.edit.support.EditBlackboardEvent.EventType;

/**
 * 
 * Activates and deactivates the draw command that draws the vertices for the EditGeom.
 * 
 * @author jones
 * @since 1.1.0
 */
public class DrawCurrentGeomVerticesActivator implements Activator {

    protected DrawPointCommand command;
    protected IBlackboardListener mapBBlistener = new IBlackboardListener(){

        public void blackBoardChanged( BlackboardEvent event ) {
            if (event.getSource() != handler.getContext().getMap().getBlackboard()) {
                event.getSource().removeListener(this);
                return;
            }
            if (event.getKey() == EditToolHandler.CURRENT_SHAPE
                    || event.getKey().equals(EditToolHandler.CURRENT_SHAPE)
                    || event.getKey() == EditToolHandler.EDITSTATE) {
                handler.repaint();
            }
        }

        public void blackBoardCleared( IBlackboard source ) {
            handler.repaint();
        }

    };
    protected EditToolHandler handler;
    protected EditBlackboardListener editBBListener = new EditBlackboardAdapter(){
        @Override
        public void changed( EditBlackboardEvent e ) {
            if (e.getEditBlackboard() != handler.getEditBlackboard(handler.getEditLayer())) {
                if (e.getSource() instanceof EditBlackboard) {
                    EditBlackboard bb = (EditBlackboard) e.getSource();
                    bb.getListeners().remove(this);
                    return;
                }
            }

            if (e.getType() == EventType.SELECTION) {
                handler.repaint();
            }
        }

        @Override
        public void batchChange( java.util.List<EditBlackboardEvent> e ) {

            for( EditBlackboardEvent event : e ) {
                if (event.getEditBlackboard() != handler.getEditBlackboard(handler.getEditLayer())) {
                    EditBlackboard bb = (EditBlackboard) event.getSource();
                    bb.getListeners().remove(this);
                    return;
                }

                if (event.getType() == EventType.SELECTION && handler.getCurrentShape() != null) {
                    handler.repaint();
                    break;
                }
            }
        };

    };
    protected IEditManagerListener editManagerListener = new IEditManagerListener(){

        public void changed( EditManagerEvent event ) {
            if (event.getSource() != handler.getContext().getEditManager()) {
                event.getSource().removeListener(this);
                return;
            }

            if (event.getType() == EditManagerEvent.SELECTED_LAYER
                    && event.getOldValue() != event.getNewValue()) {
                if (event.getOldValue() != null) {
                    ILayer layer = (ILayer) event.getOldValue();
                    handler.getEditBlackboard(layer).getListeners().remove(editBBListener);
                }
                if (event.getNewValue() != null) {
                    ILayer layer = (ILayer) event.getNewValue();
                    handler.getEditBlackboard(layer).getListeners().add(editBBListener);
                    handler.repaint();
                }
            }
        }

    };

    public void activate( final EditToolHandler handler ) {
        this.handler = handler;
        command = new DrawPointCommand(handler, handler.getCurrentShape(), handler.getContext()
                .getViewportPane());
        command.setRadius(PreferenceUtil.instance().getVertexRadius());
        command.setDrawCurrentShape(true);
        handler.getContext().getViewportPane().addDrawCommand(command);
        handler.getContext().getMap().getEditManager().addListener(editManagerListener);
        handler.getContext().getMap().getBlackboard().addListener(mapBBlistener);
        handler.getEditBlackboard(handler.getEditLayer()).getListeners().add(editBBListener);
    }

    public void deactivate( EditToolHandler handler ) {
        command.setValid(false);
        IMap map = handler.getContext().getMap();
        map.getBlackboard().removeListener(mapBBlistener);
        map.getEditManager().removeListener(editManagerListener);
        handler.getEditBlackboard(handler.getEditLayer()).getListeners().remove(editBBListener);
    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("Unexpected Error while activating", error); //$NON-NLS-1$
        if (command != null)
            command.setValid(false);
        handler.getContext().getMap().getBlackboard().removeListener(mapBBlistener);
    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("Unexpected Error while deactivating", error); //$NON-NLS-1$
        if (command != null)
            command.setValid(false);
        handler.getContext().getMap().getBlackboard().removeListener(mapBBlistener);
    }

}
