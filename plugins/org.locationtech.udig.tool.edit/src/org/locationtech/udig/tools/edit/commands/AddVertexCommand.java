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

import org.eclipse.core.runtime.IProgressMonitor;

import org.locationtech.jts.geom.Coordinate;

/**
 * Command for adding a vertext to a {@link org.locationtech.udig.tools.edit.support.EditGeom}
 * <p>
 * This command will take the current snapping  policy into account when the point is added.
 * <p>
 * @author jones
 * @since 1.1.0
 */
public class AddVertexCommand extends AbstractCommand implements UndoableMapCommand {

	/** Coordinate as generated with toCoordinate( point, useSnapping) */
    private Coordinate toAdd = null;
    private boolean useSnapping;
    private Point point;
    private Coordinate addedCoord;
    private final EditBlackboard board;
    private IBlockingProvider<PrimitiveShape> shapeProvider;
    private EditToolHandler handler;
    private int index;
    private boolean showAnimation=true;
    /**
     * Create an AddVertext command; coordinate will be added at the indicated point.
     * 
     * @param handler2
     * @param editBlackboard
     * @param point
     */
    public AddVertexCommand( EditToolHandler handler2, EditBlackboard editBlackboard,
            Point point ) {
        this(handler2, editBlackboard, new EditUtils.EditToolHandlerShapeProvider(handler2), point, true);
    }

    /**
     * Create an AddVertext command; coordinate will be created at the indicated Point when
     * executed.
     * 
     * @param handler EditToolHandler responsible for adding vertex
     * @param bb edit blackboard
     * @param provider Shape being updated
     * @param point Point clicked on by user
     * @param useSnapping true if we would like to use the current snapping policy
     */
    public AddVertexCommand( EditToolHandler handler2, EditBlackboard bb,
            IBlockingProvider<PrimitiveShape> provider, Point point, boolean useSnapping ) {
        this.handler = handler2;
        board = bb;
        shapeProvider = provider;
        this.point = point;
        this.useSnapping = useSnapping;
    }
    /**
     * Will use EditUtils.instance.getClosestSnapPoint if useSnapping is true.
     * 
     * @param point Point as provided by the user
     * @param useSnapping true if we want the current snapping policy applied
     * @return Coordinate created from the provided point
     */    
    private Coordinate toCoordinate( Point point, boolean useSnapping ) {
        Coordinate toCoord = board.toCoord(point);
        if (useSnapping) {
            Coordinate newCoord = EditUtils.instance.getClosestSnapPoint(handler, board, point,
                    false, PreferenceUtil.instance().getSnapBehaviour(), handler.getCurrentState());
            if (newCoord != null) {
                this.point = board.toPoint(newCoord);
                return newCoord;
            }
            return toCoord;
        }

        return toCoord;
    }


    public void rollback( IProgressMonitor monitor ) throws Exception {
        if (addedCoord == null){
            return;
        }
        if (handler.getContext().getMapDisplay() != null && showAnimation) {
            IAnimation animation = new DeleteVertexAnimation(point);
            AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), animation);
        }
        board.removeCoordinate(index, addedCoord, shapeProvider.get(monitor));
        addedCoord = null;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
    	if( toAdd == null ){
            toAdd = toCoordinate(point, useSnapping);
    	}
        PrimitiveShape shape = shapeProvider.get(monitor);
        boolean collapseVertices = board.isCollapseVertices();
        try {
            board.setCollapseVertices(false);
            board.addCoordinate(toAdd, shape);
            addedCoord = toAdd;
            index=shape.getNumPoints()-1;
        } finally {
            board.setCollapseVertices(collapseVertices);
        }
        if (handler.getContext().getMapDisplay() != null && showAnimation) {
            IAnimation animation = new AddVertexAnimation(point.getX(), point.getY());
            AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), animation);
        }
    }

    public String getName() {
        return Messages.AddVertexCommand_name + toAdd;
    }

    /**
     * @return Returns the toAdd.
     */
    public Point getPointToAdd() {
        return this.point;
    }

    public boolean isShowAnimation() {
        return showAnimation;
    }

    public void setShowAnimation( boolean showAnimation ) {
        this.showAnimation = showAnimation;
    }

}
