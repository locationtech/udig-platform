/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Iterator;

import net.refractions.udig.catalog.ITransientResolve;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMapListener;
import net.refractions.udig.project.MapEvent;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.LockingBehaviour;
import net.refractions.udig.tools.edit.MouseTracker;
import net.refractions.udig.tools.edit.commands.CreateEditGeomCommand;
import net.refractions.udig.tools.edit.commands.DeselectEditGeomCommand;
import net.refractions.udig.tools.edit.commands.SetCurrentGeomCommand;
import net.refractions.udig.tools.edit.commands.SetEditStateCommand;
import net.refractions.udig.tools.edit.impl.SmartBufferTool;
import net.refractions.udig.tools.edit.preferences.PreferenceConstants;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.ShapeType;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars2;
import org.eclipse.ui.PlatformUI;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.operation.buffer.BufferOp;

/**
 * Create and draw a Polygon from a Point or Line. This behaviour provides input 
 * feedback as well as creating the final geomirty.
 * 
 * Requirements:
 * <ul>
 * <li>EventType==WHEEL</li>
 * <li>button1 is down</li>
 * <li>button2 is down</li>
 * </ul>
 * 
 * <ul>
 * <li>if only one mouse coordinate is added then a buffer point will be created</li>
 * <li>if more than one mouse coordinate is added then a buffer line will be created</li>
 * <li>handler is locked until middle mouse is clicked</li>
 * @author leviputna
 * @since 1.2.0
 */
public class BufferPolygonCursorControlBehaviour extends AbstractBufferPolygonBehaviour{
	
	
    private DrawShapeCommand drawCommand;

	public BufferPolygonCursorControlBehaviour(BufferUpdateBehaviour bufferUpdateBehaviour) {
		super(bufferUpdateBehaviour);
	}

    @Override
    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
    	
    	Shape transformedShape;
    	
		if(eventType != EventType.EXITED){
    	
	    	//1) get current mouse location
	    	Coordinate mouseLocation = getMouseCoordinate(handler,e);
	    	
	    	//2) transform current shape is buffer hasn't changed or buffer current mouse location
	    	ArrayList<Coordinate> mouseCoordinate = new ArrayList<Coordinate>();
	    	mouseCoordinate.add(mouseLocation);
	    	Geometry bufferGeometry = bufferCoordinates(mouseCoordinate);
	
	        path = geometryToPath(handler, bufferGeometry);
	        
	        //Convert Path to shape - this is a little bit of a cheat but it works  
	        AffineTransform transform=AffineTransform.getTranslateInstance(0, 0);
	        transformedShape = path.createTransformedShape(transform);
	        
	        
	        
	        
    	}else{
    		transformedShape = null;
    	}
    	
    	//draw handler shape
        updateDrawHandler(handler, transformedShape);
      
        return null;
    }
    
    /**
     * Draw the input handler on the map to provide visual feedback about the shape 
     * that will be created.
     * @param handler handler that calls this Behaviour
     * @param transformedShape the shape to draw
     */
    private void updateDrawHandler( EditToolHandler handler, Shape transformedShape ) {
        if( drawCommand==null ){
            drawCommand=new DrawShapeCommand();
            drawCommand.setPaint(PreferenceUtil.instance().getFeedbackColor());
            drawCommand.setStroke(ViewportGraphics.LINE_DASH, 1);
            handler.getContext().sendSyncCommand(drawCommand);
        }
        
        drawCommand.setShape(transformedShape);
        handler.repaint();
    }
        
    @Override
    public Object getKey( EditToolHandler handler ) {
        return this;
    }

    @Override
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        //As we want the background ScrollZoom tool to remain active use of the Command (on a mac) 
        //Alt (on windows) modifiers is required before the scroll buffer can be adjusted. 
        
    	return(eventType==EventType.WHEEL && e.modifiersDown()) || eventType==EventType.MOVED || eventType==EventType.EXITED;
    }

    @Override
    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}