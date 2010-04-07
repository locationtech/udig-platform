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

import java.util.Collections;
import java.util.Set;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.Selection;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Sets the selection on the editblackboard.  Depending on the Enum used
 * the new points may be added to the selection or may replace the selection.
 * 
 * @author jones
 * @since 1.1.0
 */
public class SelectVertexCommand extends AbstractCommand implements UndoableMapCommand {

    private Type type;
    private Set<Point> points;
    private EditBlackboard editBlackboard;
    private Selection oldPoints;

    /**
     * new instance 
     * @param editBlackboard blackboard to modify
     * @param points points to add or set
     * @param type indicates whether to add or set.
     */
    public SelectVertexCommand( EditBlackboard editBlackboard, Set<Point> points, Type type ) {
        this.editBlackboard=editBlackboard;
        this.points=points;
        this.type=type;
    }

    /**
     * new instance 
     * @param editBlackboard blackboard to modify
     * @param point point to add or set
     * @param type indicates whether to add or set.
     */
    public SelectVertexCommand( EditBlackboard editBlackboard2, Point point, Type type ) {
        this( editBlackboard2, Collections.singleton(point), type);
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        editBlackboard.startBatchingEvents();
        oldPoints= new Selection(editBlackboard.getSelection());
        oldPoints.disconnect();
        if( type==Type.ADD)
            editBlackboard.selectionAddAll(points);
        else if( type==Type.SET ){
                editBlackboard.selectionClear();
                if( !points.isEmpty() )
                    editBlackboard.selectionAddAll(points);
        }else{
            if( !points.isEmpty() )
                editBlackboard.selectionRemoveAll(points);
        }
         editBlackboard.fireBatchedEvents();   
    }

    public String getName() {
        return Messages.SelectPointCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        Selection selection = editBlackboard.getSelection();
        synchronized (selection) {
            editBlackboard.selectionClear();
            editBlackboard.selectionAddAll(oldPoints);
        }
    }
    
    public enum Type{ ADD, SET, REMOVE };

}
