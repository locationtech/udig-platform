/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.tools.edit.activator;

import java.util.Iterator;

import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ApplicationGISInternal;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.commands.RemoveAllVerticesCommand;
import net.refractions.udig.tools.edit.commands.RemoveSelectedVerticesCommand;
import net.refractions.udig.tools.edit.commands.SetCurrentGeomCommand;
import net.refractions.udig.tools.edit.commands.SetEditStateCommand;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionFactory;

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

    public void activate( EditToolHandler handler ) {
        IActionBars2 actionBars = handler.getContext().getActionBars();
        if( actionBars==null )
            return;
        IWorkbenchPart part=ApplicationGISInternal.getActiveEditor();
        
        if( part == null ) return;
        
        oldAction=ApplicationGIS.getToolManager().getDELETEAction();
        IKeyBindingService keyBindingService = part.getSite().getKeyBindingService();
        if( oldAction!=null )
            keyBindingService.unregisterAction(oldAction);

        deleteVertexHandler = new DeleteVertexHandler(handler);
        if( oldAction!=null ){
            deleteVertexHandler.setImageDescriptor(oldAction.getImageDescriptor());
            deleteVertexHandler.setDisabledImageDescriptor(oldAction.getDisabledImageDescriptor());
        }
        ApplicationGIS.getToolManager().setDELETEAction(deleteVertexHandler,part);
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteVertexHandler);
        actionBars.updateActionBars();
        keyBindingService.registerAction(deleteVertexHandler);
    }

    public void deactivate( EditToolHandler handler ) {
        IActionBars2 actionBars = handler.getContext().getActionBars();
        if( actionBars==null || oldAction==null ){
            return;
        }
        IWorkbenchPart part=ApplicationGISInternal.getActiveEditor();
        
        if( part == null ) return;
        
        IWorkbenchPartSite site = part.getSite();
        
        IKeyBindingService keyBindingService = site.getKeyBindingService();
        keyBindingService.unregisterAction(deleteVertexHandler);
        deleteVertexHandler=null;
        
        ApplicationGIS.getToolManager().setDELETEAction(oldAction,part);
        actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), oldAction);
        if( oldAction!=null ){
            keyBindingService.registerAction(oldAction);
        }
        oldAction=null;
        
        actionBars.updateActionBars();
    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    static class DeleteVertexHandler extends Action{
        private EditToolHandler handler;
        DeleteVertexHandler(EditToolHandler handler){
            this.handler=handler;
            setText(Messages.DeleteGlobalActionSetterActivator_title);
            setToolTipText(Messages.DeleteGlobalActionSetterActivator_tooltip);
        }
        @Override
        public void runWithEvent( Event event ) {
            EditGeom currentGeom = handler.getCurrentGeom();
            if( currentGeom==null )
                return;
            EditBlackboard editBlackboard = currentGeom.getEditBlackboard();
            if( editBlackboard.getSelection().isEmpty() || hasNoPoints() ){
                UndoableComposite composite=new UndoableComposite();
                composite.getCommands().add(new SetEditStateCommand(handler, EditState.BUSY));
                composite.getCommands().add(new RemoveAllVerticesCommand(handler));
                composite.getCommands().add(handler.getCommand(handler.getAcceptBehaviours()));
                composite.getCommands().add(new SetCurrentGeomCommand(handler, (PrimitiveShape)null));
                composite.getFinalizerCommands().add(new SetEditStateCommand(handler, EditState.MODIFYING));
                handler.getContext().sendASyncCommand(composite);
            }else{
                UndoableComposite composite=new UndoableComposite();
                composite.getFinalizerCommands().add(new SetEditStateCommand(handler, EditState.MODIFYING));
                composite.getCommands().add(new SetEditStateCommand(handler, EditState.BUSY));
                RemoveSelectedVerticesCommand removeCommand = new RemoveSelectedVerticesCommand(handler);
                removeCommand.setRunAnimation(editBlackboard.getSelection().size()<10);
                composite.getCommands().add(removeCommand);
                handler.getContext().sendASyncCommand(composite);
            }
        }
        private boolean hasNoPoints() {
            Iterator<Point> iter = handler.getCurrentGeom().getShell().iterator();
            EditBlackboard editBlackboard = handler.getCurrentGeom().getEditBlackboard();
            while( iter.hasNext()){
                if( !editBlackboard.getSelection().contains(iter.next()) )
                    return false;
            }
            return true;
        }
    }
    
}
