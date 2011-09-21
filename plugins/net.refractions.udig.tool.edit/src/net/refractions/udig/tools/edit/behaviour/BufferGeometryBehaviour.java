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
import net.refractions.udig.tools.edit.preferences.PreferenceConstants;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditUtils;
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
public class BufferGeometryBehaviour implements EventBehaviour, LockingBehaviour{
    
    private GeneralPath path;
    private IToolContext context;
    private ArrayList<Coordinate> mouseCoordinate = new ArrayList<Coordinate>();
    private DrawShapeCommand drawCommand;
    private Double buffer;
    private Geometry bufferGeometry;
    private Creator creator;
    private int bufferSegments = EditPlugin.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_BUFFER_SEGMENTS);
    private int capType = Integer.parseInt(EditPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUFFER_CAP_TYPE));
    private int maxBufferSize;
    private IMapListener mapListener;
    
    /**
     * @param iToolContext 
     * @param drawType 
     * @param context
     */
    public BufferGeometryBehaviour(IToolContext iToolContext) {
       this.context = iToolContext;
    }
    
    private void addMapListener(final EditToolHandler handler){
        mapListener = new IMapListener(){
            public void changed( MapEvent event ) {
                switch( event.getType() ) {    
                case NAV_COMMAND:
                    createPath(handler);
                    AffineTransform transform=AffineTransform.getTranslateInstance(0, 0);
                    Shape transformedShape = path.createTransformedShape(transform);
                    drawHandler(handler,transformedShape);
                    break;
                default:
                    break;
                }
                
            }
            
        };
        handler.getContext().getMap().addMapListener(mapListener);
    }
    
    
    
    @Override
    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {

        if(mapListener == null){
            addMapListener(handler);
        }
        
        if(buffer == null){
            resetBufferSize(handler);
        }
        
        //add command to draw polygon
        if( creator == null ){
            creator = new Creator();
            handler.getBehaviours().add(creator);
            handler.lock(this);
        }
        
        checkPreference();
        bufferPath(handler, e, eventType);

        //Convert Path to shape - this is a little bit of a cheat but it works  
        AffineTransform transform=AffineTransform.getTranslateInstance(0, 0);
        Shape transformedShape = path.createTransformedShape(transform);
        
        //draw handler shape
        drawHandler(handler, transformedShape);
        setMessage(buffer, handler);
        return null;
    }
        
    @Override
    public Object getKey( EditToolHandler handler ) {
        return this;
    }

    @Override
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        //As we want the background ScrollZoom tool to remain active use of the Command (on a mac) 
        //Alt (on windows) modifiers is required before the scroll buffer can be adjusted. 
        
        if( handler.isLockOwner(this) && eventType==EventType.WHEEL && e.modifiersDown() )
            return true;
        
        return (eventType==EventType.PRESSED && e.button == MapMouseEvent.BUTTON1) || (eventType==EventType.DOUBLE_CLICK && e.button == MapMouseEvent.BUTTON1);
    }

    @Override
    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    private void bufferPath(EditToolHandler handler, MapMouseEvent e, EventType eventType  ) {
        mouseTracking(handler,eventType,e);
        bufferGeometry();
        createPath(handler);
    }
    
    private void mouseTracking(EditToolHandler handler, EventType eventType, MapMouseEvent e){
        MouseTracker tracker = handler.getMouseTracker();
        
        if(eventType == EventType.WHEEL && !mouseCoordinate.isEmpty()){
            
            MapMouseWheelEvent event = (MapMouseWheelEvent) e;

            double pxSize = handler.getContext().getPixelSize().x;
            
            //we want to increase our buffer by one px each mouse scroll so we 
            //get a smooth buffer at all zooms levels. 
            double dif = event.clickCount * pxSize;
            if((buffer + dif > 0) && (buffer + dif < pxSize * maxBufferSize)){
                buffer += dif;
            }
            
        }else if((eventType==EventType.PRESSED && e.button == MapMouseEvent.BUTTON1) || (eventType==EventType.DOUBLE_CLICK && e.button == MapMouseEvent.BUTTON1)){
            int translationX = tracker.getDragStarted().getX();
            int translationY = tracker.getDragStarted().getY();
            
            //convert to world Coordinate so that when the map pans/zooms 
            //our smart buffer moves with it
            Coordinate world = handler.getContext().pixelToWorld(translationX, translationY);
            mouseCoordinate.add(world);
        }
    }
    
    private void bufferGeometry(){
        GeometryFactory fac = new GeometryFactory(new PrecisionModel());
        
        //Buffer Geometry
        if(mouseCoordinate.size() == 1){
            Coordinate coordinate = mouseCoordinate.get(0);
            Point point = fac.createPoint(new Coordinate(coordinate.x,coordinate.y));
            
            bufferGeometry = BufferOp.bufferOp(point,buffer,bufferSegments,capType);
        }else if (mouseCoordinate.size() > 1){

            Coordinate[] coords = new Coordinate[mouseCoordinate.size()];
            Iterator<Coordinate> itr = mouseCoordinate.iterator();
            
            int i =0;
            while(itr.hasNext()) {
                Coordinate element = itr.next(); 
                coords[i] = new Coordinate(element.x, element.y);
                i++;
            } 
            
            LineString line = fac.createLineString(coords);
            bufferGeometry = BufferOp.bufferOp(line,buffer,bufferSegments,capType);
            
        }else{
            System.out.println("type error...");
        }
    }
    
    private void createPath(EditToolHandler handler){
        
        Coordinate[] bufferGeometryCoordinates = bufferGeometry.getCoordinates();
        path = new GeneralPath();

        //Convert Geometry to Path
          for (int i = 0; i < bufferGeometryCoordinates.length; i++) { 
              
              Coordinate coordinate = bufferGeometryCoordinates[i];
              
              //convert back to screen coordinates so we can give some visual feedback
              double[] points = new double[] { coordinate.x,coordinate.y };
              handler.getEditLayer().getMap().getViewportModel().worldToScreenTransform().transform(points, 0, points, 0, 1);
              
              if( i == 0 ){
                  path.moveTo(points[0], points[1]);
              }else{
                  path.lineTo(points[0], points[1]);
              }

          }
          path.closePath();
    }
    
    /**
     * Draw the input handler on the map to provide visual feedback about the shape 
     * that will be created.
     * @param handler handler that calls this Behaviour
     * @param transformedShape the shape to draw
     */
    private void drawHandler( EditToolHandler handler, Shape transformedShape ) {
        if( drawCommand==null ){
            drawCommand=new DrawShapeCommand();
            drawCommand.setPaint(PreferenceUtil.instance().getDrawGeomsLine());
            handler.getContext().sendSyncCommand(drawCommand);
        }
        
        drawCommand.setShape(transformedShape);
        handler.repaint();
    }

    private void checkPreference(){
        this.maxBufferSize = EditPlugin.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_BUFFER_MAX_SIZE);
        this.bufferSegments = EditPlugin.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_BUFFER_SEGMENTS);
        this.capType =  Integer.parseInt(EditPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUFFER_CAP_TYPE));
    }

    private void setMessage(final double distance, final EditToolHandler handler) {
        Runnable runnable = new Runnable(){
            public void run() {
                if( PlatformUI.getWorkbench().isClosing() )
                    return;
                IActionBars2 bars = handler.getContext().getActionBars();
                if (bars!=null){
                    bars.getStatusLineManager().setErrorMessage(null);
                    bars.getStatusLineManager().setMessage("Buffer: " + formatDistance(distance));
                }
                
            }
        };
        if( Display.getCurrent()!=null )
            runnable.run();
        else
            Display.getDefault().asyncExec(runnable);
    }

    /**
     * Truncates a double to the given number of decimal places. Note: truncation at zero decimal
     * places will still show up as x.0, since we're using the double type.
     * 
     * @param value number to round-off
     * @param decimalPlaces number of decimal places to leave
     * @return the rounded value
     */
    private double round( double value, int decimalPlaces ) {
        double divisor = Math.pow(10, decimalPlaces);
        double newVal = value * divisor;
        newVal = (Long.valueOf(Math.round(newVal)).intValue()) / divisor;
        return newVal;
    }

    /**
         * @param distance
         * @return
         */
        private String formatDistance( double distance ) {
            String message = "";
           // bla = Unit.unitToMeter();
    System.out.println();
            if (distance > 0.01) { // km
                message = message.concat(round((distance * 100),2) + " km"); //$NON-NLS-1$
            } else { // mm
                message = message.concat(round(distance * 100000, 1) + " m"); //$NON-NLS-1$
            }
    
            return message;
        }

    private void resetBufferSize(EditToolHandler handler){
        double pxSize = handler.getContext().getPixelSize().x;
        buffer = pxSize * (double) EditPlugin.getDefault().getPreferenceStore().getFloat(PreferenceConstants.P_BUFFER_DEFULT_SIZE);
        setMessage(buffer, handler);
    }

    /*
     * Generate poligon command
     */
    private class Creator implements LockingBehaviour{

        public Object getKey( EditToolHandler handler ) {
            return BufferGeometryBehaviour.this;
        }

        public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            return handler.isLockOwner(this) && (eventType==EventType.PRESSED && e.button == MapMouseEvent.BUTTON2) || (eventType==EventType.DOUBLE_CLICK && e.button == MapMouseEvent.BUTTON1);
            
        }

        public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
            try{
                
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
                resetBufferSize(handler);
                creator = null;
                handler.getBehaviours().remove(this);
                drawCommand.setValid(false);
                drawCommand.dispose();
                drawCommand=null;
                path=null;
                buffer=null;
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