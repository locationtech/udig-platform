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

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.Selection;
import org.locationtech.udig.tools.edit.support.SnapBehaviour;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.locationtech.jts.geom.Coordinate;

/**
 * This class manages the movement of a vertex. Once a vertex has been moved this command determines
 * how much the vertex has moved from its original position so the incremental move can be undone.
 * Additionally this command performs post-order snapping. It searches the layer below it for the
 * closest vertex and snaps to that vertex.
 * 
 * @author jones
 * @since 1.1.0
 */
public class MoveVertexCommand extends AbstractCommand implements UndoableMapCommand {

    private Point start;
    private EditToolHandler handler;
    private Selection toMove;
    private Point lastPoint;
    private MoveSelectionCommand command;
    private EditState stateAfterSnap;
    private boolean doSnap;
    private SnapBehaviour snapBehaviour;

    public MoveVertexCommand( Point lastPoint, Selection toMove,
            EditToolHandler handler, Point start, EditState stateAfterSnap, boolean doSnap ) {
        this.lastPoint = lastPoint;
        this.toMove = toMove;
        this.handler = handler;
        this.start = start;
        this.stateAfterSnap=stateAfterSnap;
        this.doSnap=doSnap;
        this.snapBehaviour=PreferenceUtil.instance().getSnapBehaviour();
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        if (command == null) {
            ILayer selectedLayer = handler.getEditLayer();
            EditBlackboard editBlackboard = handler.getEditBlackboard(selectedLayer);
            
            Coordinate destinationCoord=null;
            Point dest;
            destinationCoord = calculateDestinationPoint(editBlackboard, destinationCoord); 
    
            dest=editBlackboard.toPoint(destinationCoord);
            int deltaX = dest.getX() - lastPoint.getX();
            int deltaY = dest.getY()
                    - lastPoint.getY();
            if( deltaX!=0 || deltaY!=0 ){
                editBlackboard.moveSelection(deltaX, deltaY, toMove);
            }
            editBlackboard.setCoords(dest, destinationCoord);
    
            deltaX = dest.getX() - start.getX();
            deltaY = dest.getY() - start.getY();
    
            
            command=new MoveSelectionCommand(editBlackboard, deltaX, deltaY, toMove );
            command.setMap(getMap());

        }else{
            command.run(new SubProgressMonitor(monitor, 1));
        }
    }

    private Coordinate calculateDestinationPoint( EditBlackboard editBlackboard, Coordinate destinationCoord ) {
        if( doSnap  ){
            destinationCoord = EditUtils.instance.getClosestSnapPoint(handler, editBlackboard, lastPoint, false, 
                    snapBehaviour, stateAfterSnap);
        }
        Point dest;
        if (destinationCoord == null) {
            dest=editBlackboard.overVertex(lastPoint, PreferenceUtil.instance().getVertexRadius(), true);
            if( dest==null )
                dest=lastPoint;
            destinationCoord = editBlackboard.toCoord(dest);
        }
        return destinationCoord;
    }

    public String getName() {
        return Messages.SnapToVertexCommand_name; 
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        command.rollback(new SubProgressMonitor(monitor, 1));
    }

}
