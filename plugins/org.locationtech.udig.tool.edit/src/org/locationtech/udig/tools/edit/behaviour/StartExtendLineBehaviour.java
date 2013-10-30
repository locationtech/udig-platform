/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.commands.SetEditStateCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Edit Mode is set to CREATING or CREATING_BACKWARD
 * <p>Requirements: * <ul> * <li>EventType==Released</li>
 * <li>CurrentShape != null</li>
 * <li>mouse is over end vertex</li>
 * <li>button1 was released</li>
 * <li>no buttons or modifiers down</li>
 * <li>mode == MODIFYING or NONE</li> * </ul> * </p> * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public class StartExtendLineBehaviour implements EventBehaviour {

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalEventType=eventType==EventType.RELEASED;
        boolean legalState=handler.getCurrentState()==EditState.MODIFYING ||handler.getCurrentState()==EditState.NONE; 
        boolean shapeAndGeomNotNull=handler.getCurrentShape()!=null;
        boolean button1Released=e.button==MapMouseEvent.BUTTON1;
        
        if( !legalState || !legalEventType || !shapeAndGeomNotNull || !button1Released 
        || e.buttonsDown() || e.modifiersDown() )
            return false;
        
        PrimitiveShape currentShape = handler.getCurrentShape();
        Point point=currentShape.getEditBlackboard().overVertex(Point.valueOf(e.x, e.y),
                PreferenceUtil.instance().getVertexRadius());
        if( currentShape.getPoint(0).equals(point) || currentShape.getPoint(currentShape.getNumPoints()-1).equals(point))
            return true;
        
        return false;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        EditBlackboard editBlackboard = handler.getCurrentShape().getEditBlackboard();
        Point point=editBlackboard.overVertex(Point.valueOf(e.x, e.y),
                PreferenceUtil.instance().getVertexRadius());
        if( point.equals(handler.getCurrentShape().getPoint(0))){
            List<UndoableMapCommand> commands=new ArrayList<UndoableMapCommand>();
            commands.add(new ReversePointsInShapeCommand(handler, handler.getCurrentShape()));
            commands.add(new SetEditStateCommand(handler, EditState.CREATING));
            return new UndoableComposite(commands);
        }else
            return new SetEditStateCommand(handler, EditState.CREATING);
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    private static class ReversePointsInShapeCommand extends AbstractCommand implements UndoableMapCommand{
        PrimitiveShape shape;
        ReversePointsInShapeCommand(EditToolHandler handler, PrimitiveShape shape){
            this.shape=shape;
        }
        
        public void run( IProgressMonitor monitor ) throws Exception {
                EditUtils.instance.reverseOrder(shape);
        }

        public String getName() {
            return ""; //$NON-NLS-1$
        }

        public void rollback( IProgressMonitor monitor ) throws Exception {
            run(monitor);
        }
        
    }
}
