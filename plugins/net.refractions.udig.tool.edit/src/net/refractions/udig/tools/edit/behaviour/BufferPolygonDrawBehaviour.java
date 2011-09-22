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
import net.refractions.udig.tools.edit.AbstractEditTool;
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
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.ShapeType;

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
public class BufferPolygonDrawBehaviour extends AbstractBufferPolygonBehaviour{
	
	private ArrayList<Coordinate> mouseCoordinate = new ArrayList<Coordinate>();
	private DrawShapeCommand drawCommand;
    private Creator creator;
	private BufferUpdateBehaviour bufferUpdateBehaviour;
	private IMapListener mapListener;
   
    /**
     * @param bufferUpdateBehaviour 
     * @param iToolContext 
     * @param drawType 
     * @param context
     */
    public BufferPolygonDrawBehaviour(BufferUpdateBehaviour bufferUpdateBehaviour) {
		super(bufferUpdateBehaviour);
    }
    
    @Override
    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
    	
    	checkPreference();
    	
    	if( creator == null ){
            creator = new Creator();
            handler.getBehaviours().add(creator);
        }
    	
    	if(mapListener == null){
            addMapListener(handler);
        }
        
        if((eventType==EventType.PRESSED && e.button == MapMouseEvent.BUTTON1) || (eventType==EventType.DOUBLE_CLICK && e.button == MapMouseEvent.BUTTON1)){
            mouseCoordinate.add(getMouseCoordinate(handler,e));
        }
        
        paint(handler);
        
        return null;
    }
    
    private void paint(EditToolHandler handler){
    	if(mouseCoordinate.size() > 0){
            
	        Geometry bufferGeometry = bufferCoordinates(mouseCoordinate);
	        path = geometryToPath(handler, bufferGeometry);
	        
	        //Convert Path to shape - this is a little bit of a cheat but it works  
	        AffineTransform transform=AffineTransform.getTranslateInstance(0, 0);
	        Shape transformedShape = path.createTransformedShape(transform);
	        
	        //draw handler shape
	        updateDrawHandler(handler, transformedShape);
        }
    }
    
    @Override
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        //As we want the background ScrollZoom tool to remain active use of the Command (on a mac) 
        //Alt (on windows) modifiers is required before the scroll buffer can be adjusted. 
        if(eventType==EventType.WHEEL && e.modifiersDown() )
            return true;
        
        return (eventType==EventType.PRESSED && e.button == MapMouseEvent.BUTTON1) || (eventType==EventType.DOUBLE_CLICK && e.button == MapMouseEvent.BUTTON1);
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
            drawCommand.setPaint(PreferenceUtil.instance().getDrawGeomsLine());
            handler.getContext().sendSyncCommand(drawCommand);
        }
        
        drawCommand.setShape(transformedShape);
        handler.repaint();
    }
    
    private void addMapListener(final EditToolHandler handler){
        mapListener = new IMapListener(){
            public void changed( MapEvent event ) {
                switch( event.getType() ) {    
                case NAV_COMMAND:
                	paint(handler);
                    break;
                default:
                    break;
                }
                
            }
            
        };
        handler.getContext().getMap().addMapListener(mapListener);
    }

    /*
     * Generate poligon command
     */
    private class Creator implements LockingBehaviour{

        public Object getKey( EditToolHandler handler ) {
            return BufferPolygonDrawBehaviour.this;
        }

        public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        	return handler.isLockOwner(this) && (eventType==EventType.PRESSED && e.button == MapMouseEvent.BUTTON2) || (eventType==EventType.DOUBLE_CLICK && e.button == MapMouseEvent.BUTTON1); 
        }

        public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            try{
            	handler.lock(this);
                PathIterator iter = drawCommand.getShape().getPathIterator(AffineTransform.getTranslateInstance(0,0), 1.0);
                UndoableComposite commands=new UndoableComposite();
                commands.getCommands().add(handler.getContext().getEditFactory().createNullEditFeatureCommand());
                EditBlackboard bb = handler.getEditBlackboard(handler.getEditLayer());
                commands.getCommands().add(new DeselectEditGeomCommand(handler, bb.getGeoms())); 
                ShapeType shapeType = determineLayerType(handler);
                
                
                CreateEditGeomCommand createEditGeomCommand = new CreateEditGeomCommand(bb, "newShape", shapeType); //$NON-NLS-1$
                commands.getCommands().add(createEditGeomCommand);
                commands.getCommands().add(EditUtils.instance.appendPathToShape(iter, shapeType, handler, bb, createEditGeomCommand.getShapeProvider()));
                commands.getCommands().add( new SetCurrentGeomCommand(handler, createEditGeomCommand.getShapeProvider()));
                commands.getCommands().add(handler.getCommand(handler.getAcceptBehaviours()));
                
                commands.getFinalizerCommands().add( new SetEditStateCommand(handler, EditState.NONE));

                return commands;
            }finally{
                creator = null;
                handler.getBehaviours().remove(this);
                drawCommand.setValid(false);
                drawCommand.dispose();
                drawCommand=null;
                path=null;
                resetBuffer(handler);
                mouseCoordinate.clear();
                handler.unlock(this);
            }
        }

        public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
            EditPlugin.log("", error); //$NON-NLS-1$
        }

        private ShapeType determineLayerType( EditToolHandler handler ) {
            Class<?> type = handler.getEditLayer().getSchema().getGeometryDescriptor().getType().getBinding();
            
            if( LineString.class.isAssignableFrom(type) || LinearRing.class.isAssignableFrom(type) 
                    || MultiLineString.class.isAssignableFrom(type))
                return ShapeType.LINE;
            
            return ShapeType.POLYGON;
        }
        
    }

}