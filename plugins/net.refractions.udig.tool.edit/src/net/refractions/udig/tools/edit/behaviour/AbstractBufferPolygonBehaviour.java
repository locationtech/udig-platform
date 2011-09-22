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
public abstract class AbstractBufferPolygonBehaviour implements EventBehaviour, LockingBehaviour{

	protected int bufferSegments = EditPlugin.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_BUFFER_SEGMENTS);
	protected int capType = Integer.parseInt(EditPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUFFER_CAP_TYPE));
	protected GeneralPath path;
	private BufferUpdateBehaviour bufferUpdateBehaviour;
    
    /**
     * @param smartBufferTool 
     * @param iToolContext 
     * @param drawType 
     * @param context
     */
    public AbstractBufferPolygonBehaviour(
			BufferUpdateBehaviour bufferUpdateBehaviour) {
    	this.bufferUpdateBehaviour = bufferUpdateBehaviour;
	}

	@Override
    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e,
            EventType eventType ) {
        return null;
    }
        
    @Override
    public Object getKey( EditToolHandler handler ) {
        return this;
    }

    @Override
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        return false;
    }

    @Override
    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }
    
    protected Coordinate getMouseCoordinate(EditToolHandler handler, MapMouseEvent e){
    	MouseTracker tracker = handler.getMouseTracker();
        
        int translationX = tracker.getDragStarted().getX();
        int translationY = tracker.getDragStarted().getY();
            
        //convert to world Coordinate so that when the map pans/zooms 
        //our smart buffer moves with it
        return(handler.getContext().pixelToWorld(translationX, translationY));
    }
    
    protected Geometry bufferCoordinates(ArrayList<Coordinate> mouseCoordinate){
        GeometryFactory fac = new GeometryFactory(new PrecisionModel());
        Geometry bufferGeometry = null;
        
        //Buffer Geometry
        if(mouseCoordinate.size() == 0){
            Coordinate coordinate = mouseCoordinate.get(0);
            Point point = fac.createPoint(new Coordinate(coordinate.x,coordinate.y));
            
            bufferGeometry = BufferOp.bufferOp(point,getBufferAmount(),bufferSegments,capType);
            return bufferGeometry;
        }else if(mouseCoordinate.size() == 1){
            Coordinate coordinate = mouseCoordinate.get(0);
            Point point = fac.createPoint(new Coordinate(coordinate.x,coordinate.y));
            
            bufferGeometry = BufferOp.bufferOp(point,getBufferAmount(),bufferSegments,capType);
            return bufferGeometry;
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
            bufferGeometry = BufferOp.bufferOp(line,getBufferAmount(),bufferSegments,capType);
        }
        
        return bufferGeometry;
    }
    
    protected GeneralPath geometryToPath(EditToolHandler handler, Geometry geometry){
        
        Coordinate[] bufferGeometryCoordinates = geometry.getCoordinates();
        GeneralPath path = new GeneralPath();
        
        if(bufferGeometryCoordinates.length > 0){
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
          return path;
    }
    
    protected void checkPreference(){
        this.bufferSegments = EditPlugin.getDefault().getPreferenceStore().getInt(PreferenceConstants.P_BUFFER_SEGMENTS);
        this.capType =  Integer.parseInt(EditPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.P_BUFFER_CAP_TYPE));
    }
    
    protected void resetBuffer(EditToolHandler handler){
    	bufferUpdateBehaviour.resetBuffer(handler);
    }
    
    protected double getBufferAmount(){
    	return (double) bufferUpdateBehaviour.getBufferAmount();
    }
    

}