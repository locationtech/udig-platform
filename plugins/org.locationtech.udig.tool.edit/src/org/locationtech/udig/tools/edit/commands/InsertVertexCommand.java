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

import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.animation.AddVertexAnimation;
import org.locationtech.udig.tools.edit.animation.DeleteVertexAnimation;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.Selection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import org.locationtech.jts.geom.Coordinate;

/**
 * Command for inserting a Vertex to a {@link org.locationtech.udig.tools.edit.support.EditGeom}
 * 
 * @author jones
 * @since 1.1.0
 */
public class InsertVertexCommand extends AbstractCommand implements UndoableMapCommand {

    private final Coordinate toAdd;
    private final IBlockingProvider<PrimitiveShape> shape;
    private final EditBlackboard board;
    private int index;
    private final IMapDisplay mapDisplay;
    private EditToolHandler handler;
    private Selection oldSelection;
    private Point point;

    public InsertVertexCommand(EditToolHandler handler, EditBlackboard board, IMapDisplay display, IBlockingProvider<PrimitiveShape> shape, Point toAdd, int index, boolean useSnapping ) {
        this.board=board;
        this.shape=shape;
        this.index=index;
        this.mapDisplay=display;
        this.handler=handler;
        this.point = toAdd;
        this.toAdd=performSnapCalculation(toAdd, useSnapping);
    }
    

    private Coordinate performSnapCalculation(Point point, boolean useSnapping) {
        Coordinate toCoord = board.toCoord(point);
        if( useSnapping ){
        Coordinate newCoord = EditUtils.instance.getClosestSnapPoint(handler, board,point,false,
                PreferenceUtil.instance().getSnapBehaviour(), handler.getCurrentState());
        if( newCoord!=null ){
            this.point = board.toPoint(newCoord);
            return newCoord;
        }
        return toCoord;
        }
        
        return toCoord;
    }
    
    public void rollback( IProgressMonitor monitor ) throws Exception {
        board.startBatchingEvents();

        if (handler.getContext().getMapDisplay() != null) {
            IAnimation animation = new DeleteVertexAnimation(point);
            AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), animation);
        }
        
        board.removeCoordinate(index, toAdd, shape.get(new SubProgressMonitor(monitor, 1)));
        board.selectionClear();
        board.selectionAddAll(oldSelection);
        board.fireBatchedEvents();
        
        if ( getMap()!=null )
            handler.repaint();

    }

    public void run( IProgressMonitor monitor ) throws Exception {
        board.startBatchingEvents();
        PrimitiveShape primitiveShape = shape.get(new SubProgressMonitor(monitor, 1));
        board.insertCoordinate( toAdd, index, primitiveShape);
        oldSelection=new Selection(board.getSelection());
        oldSelection.disconnect();
        board.selectionClear();
        board.selectionAdd(point);
        board.fireBatchedEvents();
        if ( getMap()!=null )
            handler.repaint();
        
        if( mapDisplay!=null ){
            IAnimation animation=new AddVertexAnimation(point.getX(), point.getY());
            AnimationUpdater.runTimer(mapDisplay, animation);
            handler.repaint();                
        }
    }

    public String getName() {
        return Messages.InsertVertexCommand_name1+toAdd+Messages.InsertVertexCommand_name2+index;
    }

}
