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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.locationtech.udig.core.IProvider;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.AnimationUpdater;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.LockingBehaviour;
import org.locationtech.udig.tools.edit.animation.MessageBubble;
import org.locationtech.udig.tools.edit.commands.DrawSnapAreaCommand;
import org.locationtech.udig.tools.edit.commands.MoveVertexCommand;
import org.locationtech.udig.tools.edit.commands.SetEditStateCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.GeometryCreationUtil;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.Selection;
import org.locationtech.udig.tools.edit.support.SnapBehaviour;
import org.locationtech.udig.tools.edit.validator.LegalShapeValidator;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

/**
 * Mode that moves Vertices
 * <p>
 * Requirements:
 * <ul>
 * <li>currentGeom!=null;</li>
 * <li>currentState is MODIFIED or NONE </li>
 * <li>eventType is DRAGGED</li>
 * <li>no modifiers</li>
 * <li>{@link org.locationtech.udig.tools.edit.MouseTracker#getDragStarted()} is over selected
 * vertex (if not all vertices are selected)</li>
 * <li>If all vertices are selected then
 * {@link org.locationtech.udig.tools.edit.MouseTracker#getDragStarted()} must be within the
 * geometry.
 * </ul>
 * </p>
 * <p>
 * Action:
 * <ul>
 * <li>move the vertices selected by {@link SelectVertexBehaviour} to location in
 * {@link org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent}</li>
 * <li>Locks the EditTool handler until mouse is released so other behaviours won't interfere</li>
 * </ul>
 * </p>
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class MoveVertexBehaviour implements EventBehaviour, LockingBehaviour {

    PositionTracker tracker;

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean isLegalState = handler.getCurrentState() == EditState.MODIFYING
                || handler.getCurrentState() == EditState.NONE
                || handler.getCurrentState() == EditState.MOVING;
        boolean isEventDragged = eventType == EventType.DRAGGED;
        boolean noModifiersDown = !e.modifiersDown();
        boolean button1IsDown = (e.buttons ^ MapMouseEvent.BUTTON1) == 0;
        boolean currentGeomNotNull = handler.getCurrentGeom() != null;

        return isLegalState && isEventDragged && noModifiersDown && button1IsDown
                && currentGeomNotNull
                && (handler.isLockOwner(this) || startedOverSelectedVertex(handler));
    }

    private boolean startedOverSelectedVertex( EditToolHandler handler ) {
        // if tracker!=null then the move has started so the selection is no longer
        // at the same spot as the selected point so return true
        if (tracker != null)
            return true;
        Point started = handler.getMouseTracker().getDragStarted();
        EditBlackboard editBlackboard = handler.getEditBlackboard(handler.getEditLayer());
        Point point = handler.getEditBlackboard(handler.getEditLayer())
                .overVertex(Point.valueOf(started.getX(), started.getY()),
                        PreferenceUtil.instance().getVertexRadius());
        if (point == null) {
            point = Point.valueOf(started.getX(), started.getY());
        }
        return getPointsToMove(handler, editBlackboard).contains(point);
    }

    DrawSnapAreaCommand drawSnapArea;
	private IProvider<IEditValidator> validatorFactory=new IProvider<IEditValidator>(){

		public IEditValidator get(Object... params) {
			return new LegalShapeValidator();
		}
		
	};

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        if (handler.getCurrentState() != EditState.MOVING)
            handler.setCurrentState(EditState.MOVING);

        if (!isValid(handler, e, eventType))
            throw new IllegalArgumentException("Not valid state", new Exception()); //$NON-NLS-1$

        EditBlackboard editBlackboard2 = handler.getEditBlackboard(handler.getEditLayer());
        editBlackboard2.startBatchingEvents();
        try {
            if (tracker == null) {
                handler.lock(this);
                Point closestPoint = editBlackboard2.overVertex(Point.valueOf(e.x, e.y),
                        PreferenceUtil.instance().getVertexRadius(), false);
                Map<EditGeom, Boolean> changedStatus=new HashMap<EditGeom, Boolean>();
                for( EditGeom geom : editBlackboard2.getGeoms() ) {
                    changedStatus.put(geom, geom.isChanged());
                }
                
                IEditValidator validator = validatorFactory.get(handler, e, eventType);

                // If at the start the geometry isn't valid then who are we to complain?
                if( validator.isValid(handler, e, eventType)!=null ){
                	validator=null;
                }
                
                tracker = new PositionTracker(closestPoint, handler.getMouseTracker()
                        .getDragStarted(), getPointsToMove(handler, editBlackboard2), 
                        changedStatus, validator);
                handler.getBehaviours().add(tracker);
                if (isSnappingValid() && PreferenceUtil.instance().getSnapBehaviour()!=SnapBehaviour.GRID ) {
                        drawSnapArea = new DrawSnapAreaCommand(tracker);
                    handler.getContext().getViewportPane().addDrawCommand(drawSnapArea);

                }
            }

            if (tracker.lastPoint == null) {
                tracker.lastPoint = handler.getMouseTracker().getDragStarted();
            }

            Point point = Point.valueOf(e.x, e.y);

            int deltaX = point.getX() - tracker.lastPoint.getX(), deltaY = point.getY()
                    - tracker.lastPoint.getY();

            doMove(deltaX, deltaY, handler, editBlackboard2, tracker.selection);

            tracker.lastPoint = point;
            return null;
        } finally {
            editBlackboard2.fireBatchedEvents();
            handler.repaint();
        }
    }

    protected void doMove( int deltaX, int deltaY, EditToolHandler handler,
            EditBlackboard editBlackboard2, Selection selectionToMove ) {
        editBlackboard2.moveSelection(deltaX, deltaY, selectionToMove);
    }

    /**
     * Returns true if snapping should be used.
     * 
     * @return
     */
    protected boolean isSnappingValid() {
        return PreferenceUtil.instance().getSnapBehaviour()!=SnapBehaviour.OFF;
    }

    /**
     * Returns the points that will be moved.
     * 
     * @param handler
     * @return the points that will be moved.
     */
    protected Selection getPointsToMove( EditToolHandler handler, EditBlackboard blackboard ) {
        return blackboard.getSelection();
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public class PositionTracker implements LockingBehaviour, IProvider<Point> {
        Point lastPoint;
        private Point start;
        private Selection selection;
        private Map<EditGeom, Boolean> dirtyStatesBeforeMove;
		private IEditValidator validator;

        public PositionTracker( Point closestPoint, Point dragStarted, Selection selection,
                Map<EditGeom, Boolean> dirtyStatesBeforeMove, IEditValidator validator ) {
            
            this.selection = selection;
            lastPoint = closestPoint != null ? closestPoint : dragStarted;
            this.start = lastPoint;
            this.dirtyStatesBeforeMove=dirtyStatesBeforeMove;
            this.validator = validator;
        }

        public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            return eventType == EventType.RELEASED;
        }

        public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
                EventType eventType ) {

            if (drawSnapArea != null) {
                drawSnapArea.setValid(false);
                drawSnapArea = null;
            }
            handler.getBehaviours().remove(this);
            tracker = null;
            handler.unlock(this);
            
            if( validator!=null ){
                return doValidation(handler, e, eventType);
            }

            // TODO/ this is a workaround to test some bugs we are aware of and can't quickly fix so...
            String errorMessage = testToGeometry(handler.getCurrentGeom());
            if( errorMessage!=null ){
               openErrorBubble(handler, e, errorMessage);
               return new SetEditStateCommand(handler, EditState.MODIFYING);
            }
            return createMoveCommand(handler);
        }

        private UndoableMapCommand doValidation( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            String errorMessage = validator.isValid(handler, e, eventType);
            
            if( errorMessage==null ){
                return createMoveCommand(handler);
            }else{
                openErrorBubble(handler, e, errorMessage);
                return new SetEditStateCommand(handler, EditState.MODIFYING);
            }
        }

        private String testToGeometry(EditGeom geom) {
        	Class<? extends Geometry> type = determinGeometryType(geom);
    		if (type != null) {
    			try {
    				GeometryCreationUtil.createGeom(type, geom.getShell(), false);
    			} catch (IllegalStateException e) {
    				return "Change resulted in an invalid Geometry.\n\nPlease try again";
    			}
    		}
    		return null;
		}

    	private Class<? extends Geometry> determinGeometryType(EditGeom geom) {
    		switch( geom.getShapeType() ) {
            case LINE:
                return LineString.class;
            case POINT:
                return org.locationtech.jts.geom.Point.class;            
            case POLYGON:
                return Polygon.class;
            default:
            	return null;
            }
    	}

		private void openErrorBubble( EditToolHandler handler, MapMouseEvent e, String errorMessage ) {
            MessageBubble bubble=new MessageBubble(e.getPoint().x, e.getPoint().y, errorMessage, //$NON-NLS-1$ 
                    PreferenceUtil.instance().getMessageDisplayDelay());
            AnimationUpdater.runTimer(handler.getContext().getMapDisplay(), bubble);
            EditBlackboard editBlackboard = handler.getEditBlackboard(handler.getEditLayer());
            doMove(start.getX()-lastPoint.getX(), start.getY()-lastPoint.getY(), 
                    handler, editBlackboard, selection);
            Set<Entry<EditGeom, Boolean>> entries = dirtyStatesBeforeMove.entrySet();
            for( Entry<EditGeom, Boolean> entry : entries ) {
                entry.getKey().setChanged(entry.getValue());
            }
        }

        /**
         *
         * @param handler
         * @return
         */
        private UndoableMapCommand createMoveCommand( EditToolHandler handler ) {
            UndoableComposite command = new UndoableComposite();
            command.getCommands().add(
                    new MoveVertexCommand(lastPoint, this.selection, handler, start,
                            EditState.MODIFYING, isSnappingValid()));
            command.getFinalizerCommands().add(
                    new SetEditStateCommand(handler, EditState.MODIFYING));
            return command;
        }

        public void handleError( EditToolHandler handler, Throwable error,
                UndoableMapCommand command ) {
            EditPlugin.log("", error); //$NON-NLS-1$
        }

        public Point get(Object... params) {
            return lastPoint;
        }

        public Object getKey( EditToolHandler handler ) {
            return MoveVertexBehaviour.this;
        }

    }

    public Object getKey( EditToolHandler handler ) {
        return this;
    }

    /**
     * Sets the factory to use for validating the geometries that are being edited.  The default returns the {@link LegalShapeValidator}
     * class.
     * 
     * <p>
     * The parameters passed in to the provider are:  {@link EditToolHandler}, {@link MapMouseEvent}, {@link EventType}
     * </p>
     * 
     * @param validatorFactory the factory for creating the validator
     */
	public void setValidatorFactory(IProvider<IEditValidator> validatorFactory) {
		this.validatorFactory = validatorFactory;
	}

}
