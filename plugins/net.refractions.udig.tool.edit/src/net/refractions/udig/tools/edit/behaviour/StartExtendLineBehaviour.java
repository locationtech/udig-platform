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
package net.refractions.udig.tools.edit.behaviour;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.commands.SetEditStateCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Edit Mode is set to CREATING or CREATING_BACKWARD
 * <p>Requirements:
 * <ul>
 * <li>EventType==Released</li>
 * <li>CurrentShape != null</li>
 * <li>mouse is over end vertex</li>
 * <li>button1 was released</li>
 * <li>no buttons or modifiers down</li>
 * <li>mode == MODIFYING or NONE</li>
 * </ul>
 * </p>
 * </p>
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
