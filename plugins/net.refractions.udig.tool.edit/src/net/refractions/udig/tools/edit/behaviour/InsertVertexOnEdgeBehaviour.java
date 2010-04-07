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

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.commands.InsertOnNearestEdgeCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.ClosestEdge;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.Point;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

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
