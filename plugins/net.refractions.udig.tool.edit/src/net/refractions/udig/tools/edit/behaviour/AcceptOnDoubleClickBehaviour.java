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

import net.refractions.udig.project.command.UndoRedoCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.commands.AddVertexCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * <p>Requirements:
 * <ul>
 * <li>EventType==DOUBLE_CLICKED</li>
 * <li>EditState==MODIFIED or CREATING</li>
 * <li>no modifiers</li>
 * <li>button1 clicked</li>
 * <li>no buttons down</li> * </ul>
 * </p>
 * <p>Action:
 * <ul>
 * <li>Adds the point where double click occurs <b>if</b> addPoint is true. (Default behaviour)</li
 * <li>Runs Accept Behaviours</li>
 * <li>If current state is CREATING the changes state to MODIFYING</li>
 * </ul>
 * </p>
 * @author jones
 * @since 1.1.0
 */
public class AcceptOnDoubleClickBehaviour implements EventBehaviour {

    boolean addPoint=true;

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean goodState = handler.getCurrentState()!=EditState.NONE;
        boolean releasedEvent = eventType==EventType.DOUBLE_CLICK;
        boolean noModifiers =  !(e.modifiersDown());
        boolean button1 = e.button==MapMouseEvent.BUTTON1;
        boolean onlyButton1Down = e.buttons-(e.buttons&MapMouseEvent.BUTTON1)==0;
        boolean shapeIsSet=handler.getCurrentShape()!=null;
        if( !(shapeIsSet && goodState && releasedEvent && noModifiers && button1 && onlyButton1Down) )
            return false;

        boolean changedGeom=false;
        for( EditGeom geom : handler.getEditBlackboard(handler.getEditLayer()).getGeoms() ) {
            if( geom.isChanged() ){
                changedGeom=true;
                break;
            }
        }
        return changedGeom ;
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        List<UndoableMapCommand> commands=new ArrayList<UndoableMapCommand>();

        if( handler.getCurrentState()==EditState.CREATING && addPoint){
            Point clickPoint = Point.valueOf(e.x, e.y);
            EditBlackboard editBlackboard = handler.getEditBlackboard(handler.getEditLayer());

            Point destination = editBlackboard.overVertex(clickPoint, PreferenceUtil.instance().getVertexRadius());
            if( destination==null ){

                AddVertexCommand addVertexCommand = new AddVertexCommand(handler, editBlackboard, clickPoint);

                try {
                    addVertexCommand.setMap(handler.getContext().getMap());
                    addVertexCommand.run(new NullProgressMonitor());
                } catch (Exception e1) {
                    throw (RuntimeException) new RuntimeException( ).initCause( e1 );
                }
                commands.add(new UndoRedoCommand(addVertexCommand));
            }
        }

        commands.add(handler.getCommand(handler.getAcceptBehaviours()));
        UndoableComposite undoableComposite = new UndoableComposite(commands);
        return undoableComposite;
    }

    /**
     * Returns true if a point will be added at the location of the double click.
     * @return Returns true if a point will be added at the location of the double click.
     */
    public boolean isAddPoint() {
        return this.addPoint;
    }

    /**
     * Sets the parameter of whether a point should be added at the location of the double click.
     * @param addPoint true if a point should be added at the location of the double click.
     */
    public void setAddPoint( boolean addPoint ) {
        this.addPoint = addPoint;
    }

}
