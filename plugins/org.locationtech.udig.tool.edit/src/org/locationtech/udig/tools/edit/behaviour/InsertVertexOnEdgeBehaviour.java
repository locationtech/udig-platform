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

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventBehaviour;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.commands.InsertOnNearestEdgeCommand;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.ClosestEdge;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.Point;

import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Inserts a vertex at the location where the mouse event occurred
 * 
 * <p>Requirements: * <ul> * <li>event type == RELEASE</li>
 * <li>edit state == MODIFYING </li>
 * <li>no modifiers down</li>
 * <li>button 1 released</li>
 * <li>no buttons down</li>
 * <li>current shape and geom are set</li>
 * <li>mouse is not over a vertex of the current shape</li>
 * <li>mouse is over an edge </li> * </ul> * </p> * @author jones
 * @since 1.1.0
 */
public class InsertVertexOnEdgeBehaviour implements EventBehaviour {

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        boolean legalEventType=eventType==EventType.RELEASED;
        boolean shapeAndGeomNotNull=handler.getCurrentShape()!=null;
        boolean button1Released=e.button==MapMouseEvent.BUTTON1;        
        boolean legalState= handler.getCurrentState()==EditState.NONE || handler.getCurrentState()==EditState.MODIFYING;
        return legalState && legalEventType && shapeAndGeomNotNull && button1Released 
        && !e.buttonsDown() && !e.modifiersDown() && !overShapeVertex(handler, e)
        && isOverEdge(handler, e);
    }

    private boolean isOverEdge(EditToolHandler handler, MapMouseEvent e) {
        ILayer selectedLayer = handler.getEditLayer();
        Class<?> type = selectedLayer.getSchema().getGeometryDescriptor().getType().getBinding();
        boolean polygonLayer=Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type);

        ClosestEdge edge=handler.getCurrentGeom().getClosestEdge(Point.valueOf(e.x,e.y), polygonLayer);
        if( edge==null )
            return false;
        return edge.getDistanceToEdge()<=PreferenceUtil.instance().getVertexRadius();
    }

    private boolean overShapeVertex(EditToolHandler handler, MapMouseEvent e) {
        
        Point vertexOver=handler.getEditBlackboard(handler.getEditLayer()).overVertex(Point.valueOf(e.x, e.y), 
                PreferenceUtil.instance().getVertexRadius());
        
        return handler.getCurrentShape().hasVertex( vertexOver );
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        if( !isValid(handler, e, eventType) ){
            throw new IllegalStateException("Cannot insert a vertext here"); //$NON-NLS-1$
        }
        ILayer editLayer = handler.getEditLayer();

        EditBlackboard editBlackboard = handler.getEditBlackboard( editLayer );        
        Point toInsert = Point.valueOf(e.x,e.y);
        
        return new InsertOnNearestEdgeCommand( handler, editBlackboard, toInsert );

    }
    

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}
