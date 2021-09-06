/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.activator;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionFactory;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ApplicationGISInternal;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.commands.RemoveAllVerticesCommand;
import org.locationtech.udig.tools.edit.commands.RemoveSelectedVerticesCommand;
import org.locationtech.udig.tools.edit.commands.SetCurrentGeomCommand;
import org.locationtech.udig.tools.edit.commands.SetEditStateCommand;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;

/**
 * Sets the Delete Global handler so that the selected vertices are deleted when the delete action
 * is pressed.
 *
 * @author jones
 * @since 1.1.0
 */
public class DeleteGlobalActionSetterActivator implements Activator {

    private IAction oldAction;

    private DeleteVertexHandler deleteVertexHandler;

    @Override
    public void activate(EditToolHandler handler) {
        IActionBars actionBars = handler.getContext().getActionBars();
        if (actionBars == null)
            return;
        MapPart part = ApplicationGISInternal.getActiveEditor();

        if (part == null)
            return;

        oldAction = ApplicationGIS.getToolManager().getDELETEAction();
        IKeyBindingService keyBindingService = part.getMapSite().getKeyBindingService();
        if (oldAction != null)
            keyBindingService.unregisterAction(oldAction);

        deleteVertexHandler = new DeleteVertexHandler(handler);
        if (oldAction != null) {
            deleteVertexHandler.setImageDescriptor(oldAction.getImageDescriptor());
            deleteVertexHandler.setDisabledImageDescriptor(oldAction.getDisabledImageDescriptor());
        }

        if (part instanceof IWorkbenchPart) {
            ApplicationGIS.getToolManager().setDELETEAction(deleteVertexHandler,
                    (IWorkbenchPart) part);
        }

        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteVertexHandler);
        actionBars.updateActionBars();
        keyBindingService.registerAction(deleteVertexHandler);
    }

    @Override
    public void deactivate(EditToolHandler handler) {
        IActionBars actionBars = handler.getContext().getActionBars();
        if (actionBars == null || oldAction == null) {
            return;
        }
        MapPart part = ApplicationGISInternal.getActiveEditor();

        if (part == null)
            return;

        IWorkbenchPartSite site = part.getMapSite();

        IKeyBindingService keyBindingService = site.getKeyBindingService();
        keyBindingService.unregisterAction(deleteVertexHandler);
        deleteVertexHandler = null;

        if (part instanceof IWorkbenchPart) {
            ApplicationGIS.getToolManager().setDELETEAction(oldAction, (IWorkbenchPart) part);
        }
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), oldAction);
        if (oldAction != null) {
            keyBindingService.registerAction(oldAction);
        }
        oldAction = null;

        actionBars.updateActionBars();
    }

    @Override
    public void handleActivateError(EditToolHandler handler, Throwable error) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    @Override
    public void handleDeactivateError(EditToolHandler handler, Throwable error) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    static class DeleteVertexHandler extends Action {
        private EditToolHandler handler;

        DeleteVertexHandler(EditToolHandler handler) {
            this.handler = handler;
            setText(Messages.DeleteGlobalActionSetterActivator_title);
            setToolTipText(Messages.DeleteGlobalActionSetterActivator_tooltip);
        }

        @Override
        public void runWithEvent(Event event) {
            EditGeom currentGeom = handler.getCurrentGeom();
            if (currentGeom == null)
                return;
            EditBlackboard editBlackboard = currentGeom.getEditBlackboard();
            if (editBlackboard.getSelection().isEmpty() || hasNoPoints()) {
                UndoableComposite composite = new UndoableComposite();
                composite.getCommands().add(new SetEditStateCommand(handler, EditState.BUSY));
                composite.getCommands().add(new RemoveAllVerticesCommand(handler));
                composite.getCommands().add(handler.getCommand(handler.getAcceptBehaviours()));
                composite.getCommands()
                        .add(new SetCurrentGeomCommand(handler, (PrimitiveShape) null));
                composite.getFinalizerCommands()
                        .add(new SetEditStateCommand(handler, EditState.MODIFYING));
                handler.getContext().sendASyncCommand(composite);
            } else {
                UndoableComposite composite = new UndoableComposite();
                composite.getFinalizerCommands()
                        .add(new SetEditStateCommand(handler, EditState.MODIFYING));
                composite.getCommands().add(new SetEditStateCommand(handler, EditState.BUSY));
                RemoveSelectedVerticesCommand removeCommand = new RemoveSelectedVerticesCommand(
                        handler);
                removeCommand.setRunAnimation(editBlackboard.getSelection().size() < 10);
                composite.getCommands().add(removeCommand);
                handler.getContext().sendASyncCommand(composite);
            }
        }

        private boolean hasNoPoints() {
            Iterator<Point> iter = handler.getCurrentGeom().getShell().iterator();
            EditBlackboard editBlackboard = handler.getCurrentGeom().getEditBlackboard();
            while (iter.hasNext()) {
                if (!editBlackboard.getSelection().contains(iter.next()))
                    return false;
            }
            return true;
        }
    }

}
