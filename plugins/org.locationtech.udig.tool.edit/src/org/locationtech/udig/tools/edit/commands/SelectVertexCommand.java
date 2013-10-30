/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.util.Collections;
import java.util.Set;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.Selection;

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
