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
package net.refractions.udig.tools.edit.commands;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * <ul>
 * <li>Sets the currentGeom to be the first geom on the black board</li>
 * <li>Sets the state to CREATING</li>
 * <li>Adds a point to the geom</li>
 * </ul>
 * 
 * @author jones
 * @since 1.1.0
 * @see net.refractions.udig.tools.edit.behaviour.StartEditingBehaviour
 */
public class StartEditingCommand extends AbstractCommand implements UndoableMapCommand{

    private final EditToolHandler handler;
    private EditState currentState;
    private PrimitiveShape currentShape;
    private ILayer layer;
    private ShapeType type;
    private EditState endState;
    private AddVertexCommand addVertexCommand;

    public StartEditingCommand(EditToolHandler handler, MapMouseEvent event, ShapeType type){
        this(handler, event, type, EditState.CREATING);
    }
    public StartEditingCommand(EditToolHandler handler, MapMouseEvent event, ShapeType type, EditState endState){
        this.handler=handler;
        this.layer=handler.getEditLayer();
        this.type=type;
        this.endState=endState;
        addVertexCommand=new AddVertexCommand(handler, handler.getEditBlackboard(layer), Point.valueOf(event.x, event.y));
    }
    
    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.StartEditingCommand_name, 32);
        monitor.worked(2);
        
        EditBlackboard editBlackboard = handler.getEditBlackboard(layer);
        
        EditGeom editGeom = editBlackboard.getGeoms().get(0);
        if( editGeom.getShell().getNumPoints()>0 )
            editGeom=editBlackboard.newGeom(null, type);
        else{
            editGeom.setShapeType(type);
        }
        
        this.currentState=handler.getCurrentState();
        this.currentShape=handler.getCurrentShape();
        handler.setCurrentShape(editGeom.getShell());
        handler.setCurrentState(endState);

        addVertexCommand.setMap(handler.getContext().getMap());
        addVertexCommand.run((monitor));
        monitor.done();
    }

    public String getName() {
        return Messages.StartEditingCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.StartEditingCommand_undo,32);
        monitor.worked(2);
        handler.setCurrentState(currentState);
        handler.setCurrentShape(currentShape);
        monitor.done();
    }

}
