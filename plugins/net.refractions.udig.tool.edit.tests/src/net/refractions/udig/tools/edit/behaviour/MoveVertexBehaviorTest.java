/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.behaviour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Dimension;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.CommandManager;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.tests.support.TestMapDisplay;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.SnapBehaviour;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.geometry.jts.JTS;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

public class MoveVertexBehaviorTest {
    final static int BUTTON1=MapMouseEvent.BUTTON1;
    final static int BUTTON2=MapMouseEvent.BUTTON2;
    final static int NONE=MapMouseEvent.NONE;
    final static int SHIFT=MapMouseEvent.SHIFT_DOWN_MASK;
    final static int CTRL=MapMouseEvent.CTRL_DOWN_MASK;
    final static TestMapDisplay DISPLAY = new TestMapDisplay(new Dimension(500,500));
    private MoveVertexBehaviour mode;
    private TestHandler handler;
    private EditBlackboard editBlackboard;
    
    @Before
    public void setUp() throws Exception {
        mode=new MoveVertexBehaviour();
        handler=new TestHandler();

        handler.getTestEditBlackboard().util.setVertexRadius(4);
        handler.getTestEditBlackboard().util.setSnappingRadius(0);

        MapMouseEvent event=new MapMouseEvent( DISPLAY, 10,10,NONE,BUTTON1, BUTTON1 );
        assertFalse(mode.isValid(handler, event, EventType.DRAGGED));
        
        editBlackboard = handler.getEditBlackboard();
        EditGeom currentGeom = editBlackboard.getGeoms().get(0);
        editBlackboard.addPoint(10,10, currentGeom.getShell());
        editBlackboard.addPoint(20,10, currentGeom.getShell());
        editBlackboard.addPoint(30,10, currentGeom.getShell());
        handler.setCurrentShape(currentGeom.getShell());
    }
    
    /*
     * Test method for 'net.refractions.udig.tools.edit.mode.MoveVertexMode.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Test
    public void testIsValid() {
        handler.getEditBlackboard().selectionAdd( Point.valueOf(10,10) );
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,10));

        MapMouseEvent event = new MapMouseEvent( DISPLAY, 10,10,NONE,BUTTON1, BUTTON1 );
        assertTrue(mode.isValid(handler, event, EventType.DRAGGED));      
        
        handler.setCurrentState(EditState.MODIFYING);
        event=new MapMouseEvent( DISPLAY, 10,10,NONE,BUTTON1, BUTTON1 );
        assertTrue(mode.isValid(handler, event, EventType.DRAGGED));      
        
        // button isn't button1
        event=new MapMouseEvent( DISPLAY, 10,10,NONE, BUTTON2, BUTTON1 );
        assertFalse(mode.isValid(handler, event, EventType.DRAGGED));        
        
        // not dragged event type
        event=new MapMouseEvent( DISPLAY, 10,10,NONE,BUTTON1, BUTTON1 );
        assertFalse(mode.isValid(handler, event, EventType.RELEASED));        

        // a modifier is down
        event=new MapMouseEvent( DISPLAY, 10,10, CTRL, BUTTON1, BUTTON1 );
        assertFalse(mode.isValid(handler, event, EventType.DRAGGED));
        
        // drag did not start over a selected vertex
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,10));
        event=new MapMouseEvent( DISPLAY, 10,10, NONE, BUTTON1, BUTTON1 );
        assertFalse(mode.isValid(handler, event, EventType.DRAGGED));

        handler.getMouseTracker().setDragStarted(Point.valueOf(10,10));
        
        // state is not MODIFIED or NONE
        handler.setCurrentState(EditState.CREATING);
        event=new MapMouseEvent( DISPLAY, 10,10, NONE, BUTTON1, BUTTON1 );
        assertFalse(mode.isValid(handler, event, EventType.DRAGGED));
        
        
        handler.setCurrentState(EditState.MODIFYING);
        assertTrue(mode.isValid(handler, event, EventType.DRAGGED));
           
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.mode.MoveVertexMode.run(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Test
    public void testRun() throws Exception {

        handler.getEditBlackboard().selectionAdd(Point.valueOf(10,10));

        MapMouseEvent event=new MapMouseEvent( DISPLAY, 10,15,NONE,BUTTON1, BUTTON1 );
        try{
            mode.getCommand(handler, event, EventType.RELEASED);
            fail();
        }catch (Exception e) {
            // good behaviour because mode only works on EventType.DRAGGED
        }
        
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,10));
        mode.getCommand(handler, event, EventType.DRAGGED);
        assertEquals(1, editBlackboard.getCoords(10,15).size());
        assertEquals(0, editBlackboard.getCoords(10,10).size());
        assertEquals(1, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,15)));

        //return to original position.
        event=new MapMouseEvent( DISPLAY, 10,10,NONE,BUTTON1, BUTTON1 );
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,15));
        mode.getCommand(handler, event, EventType.DRAGGED);
        assertEquals(1, editBlackboard.getCoords(10,10).size());
        assertEquals(0, editBlackboard.getCoords(10,15).size());
        
        //move 2 points
        handler.getEditBlackboard().selectionAdd(Point.valueOf(20,10));
        assertEquals(2, handler.getEditBlackboard().getSelection().size());
        
        handler.unlock(mode);
        mode=new MoveVertexBehaviour();
        
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,10));
        event=new MapMouseEvent( DISPLAY, 10,15,NONE,BUTTON1, BUTTON1 );
        
        mode.getCommand(handler, event, EventType.DRAGGED);
        
        assertEquals(1, editBlackboard.getCoords(10,15).size());
        assertEquals(0, editBlackboard.getCoords(10,10).size());
        assertEquals(1, editBlackboard.getCoords(20,15).size());
        assertEquals(0, editBlackboard.getCoords(20,10).size());
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,15));
        event=new MapMouseEvent( DISPLAY, 10,10,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        assertEquals(1, editBlackboard.getCoords(10,10).size());
        assertEquals(0, editBlackboard.getCoords(10,15).size());
        
        
    }

    @Test
    public void testUndo() throws Exception {

        editBlackboard.selectionAdd(Point.valueOf(10,10));
        editBlackboard.selectionAdd(Point.valueOf(20,10));
        EditBlackboard bb = handler.getEditBlackboard();
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,10));
        MapMouseEvent event = new MapMouseEvent( DISPLAY, 10,11,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        assertEquals(1, bb.getCoords(10,11).size());
        assertTrue(handler.getCurrentShape().hasVertex(Point.valueOf(10,11)));
        
        event = new MapMouseEvent( DISPLAY, 10,12,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        assertEquals(1, bb.getCoords(10,12).size());
        assertTrue(handler.getCurrentShape().hasVertex(Point.valueOf(10,12)));
        
        event = new MapMouseEvent( DISPLAY, 10,13,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        assertEquals(1, bb.getCoords(10,13).size());
        assertTrue(handler.getCurrentShape().hasVertex(Point.valueOf(10,13)));
        
        event = new MapMouseEvent( DISPLAY, 10,14,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        assertEquals(1, bb.getCoords(10,14).size());
        assertTrue(handler.getCurrentShape().hasVertex(Point.valueOf(10,14)));
        
        event = new MapMouseEvent( DISPLAY, 10,15,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        assertEquals(1, bb.getCoords(10,15).size());
        assertTrue(handler.getCurrentShape().hasVertex(Point.valueOf(10,15)));

        assertEquals(1, editBlackboard.getCoords(10,15).size());
        assertEquals(1, editBlackboard.getCoords(20,15).size());
        assertEquals(0, editBlackboard.getCoords(10,10).size());
        assertEquals(0, editBlackboard.getCoords(20,10).size());
        
        //test undo first button must release so the Position tracker will execute:
        event=new MapMouseEvent( DISPLAY, 10,10,NONE,BUTTON1, BUTTON1 );
        EventBehaviour tracker = findPositionTracker();
        handler.getContext().getMap().sendCommandSync(tracker.getCommand(handler, event, EventType.RELEASED));

        assertEquals(1, editBlackboard.getCoords(10,15).size());
        assertEquals(1, editBlackboard.getCoords(20,15).size());
        assertEquals(0, editBlackboard.getCoords(10,10).size());
        assertEquals(0, editBlackboard.getCoords(20,10).size());
        
        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).undo(false);
        
        assertEquals(0, editBlackboard.getCoords(10,15).size());
        assertEquals(0, editBlackboard.getCoords(20,15).size());
        assertEquals(1, editBlackboard.getCoords(10,10).size());
        assertEquals(1, editBlackboard.getCoords(20,10).size());
        
        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).redo(false);
        
        assertEquals(1, editBlackboard.getCoords(10,15).size());
        assertEquals(1, editBlackboard.getCoords(20,15).size());
        assertEquals(0, editBlackboard.getCoords(10,10).size());
        assertEquals(0, editBlackboard.getCoords(20,10).size());
    }
    
    @Ignore
    @Test
    public void testPostSnapping() throws Exception {
        EditGeom newGeom = editBlackboard.newGeom("id", null); //$NON-NLS-1$
        editBlackboard.addPoint(30,20, newGeom.getShell());
        editBlackboard.addPoint(30,40, newGeom.getShell());
        
        PreferenceUtil.instance().setSnapBehaviour(SnapBehaviour.ALL_LAYERS);
        handler.getTestEditBlackboard().util.setSnappingRadius(12);
        
        editBlackboard.selectionAdd(Point.valueOf(10,10));
        
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,12));
        MapMouseEvent event = new MapMouseEvent( DISPLAY, 10,13,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);

        assertEquals(1, editBlackboard.getCoords(10,13).size());
        
        event = new MapMouseEvent( DISPLAY, 40,10,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        handler.handleEvent(event, EventType.RELEASED);

        assertEquals(2, editBlackboard.getCoords(30,20).size());
        assertEquals(0, editBlackboard.getCoords(10,10).size());

        handler.getEditBlackboard().removeCoordsAtPoint(20,10);

        //Testing searching through the layer for the closest point.
        assertEquals(2, editBlackboard.getCoords(30,20).size());

        ILayer editLayer = handler.getEditLayer();
        FeatureSource<SimpleFeatureType, SimpleFeature> source = editLayer.getResource(FeatureSource.class, new NullProgressMonitor());
        SimpleFeature feature = source.getFeatures().features().next();
        Coordinate coord = ((Geometry) feature.getDefaultGeometry()).getCoordinates()[1];
        Coordinate t = JTS.transform(coord, new Coordinate(), editLayer.layerToMapTransform());
        java.awt.Point pointOnScreen = handler.getContext().worldToPixel(t);
        handler.getMouseTracker().setDragStarted(Point.valueOf(30,20));
        event = new MapMouseEvent( DISPLAY, pointOnScreen.x+5,pointOnScreen.y,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertEquals(1, editBlackboard.getCoords(pointOnScreen.x,pointOnScreen.y).size());
        assertEquals(1, editBlackboard.getCoords(30,20).size());
        assertEquals(t, editBlackboard.getCoords(pointOnScreen.x, pointOnScreen.y).get(0));
        
    }
    
    @Test
    public void testSnappingDuringDragging() throws Exception {
        editBlackboard.selectionAdd(Point.valueOf(10,10));
        editBlackboard.selectionAdd(Point.valueOf(20,10));
        
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,12));
        MapMouseEvent event = new MapMouseEvent( DISPLAY, 10,13,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);

        assertEquals(1, editBlackboard.getCoords(10,13).size());
        assertEquals(1, editBlackboard.getCoords(20,13).size());
        assertEquals(0, editBlackboard.getCoords(10,10).size());
        assertEquals(0, editBlackboard.getCoords(20,10).size());
    }
    
    private EventBehaviour findPositionTracker( ){
        List<EventBehaviour> behaviours = handler.getBehaviours();
        for( EventBehaviour behaviour : behaviours ) {
            if( behaviour instanceof MoveVertexBehaviour.PositionTracker ){
                return behaviour;
            }
        }
        return null;
    }

    @Test
    public void testDragAcrossAnotherVertex() throws Exception {
        editBlackboard.selectionAdd(Point.valueOf(10,10));

        
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,12));
        MapMouseEvent event = new MapMouseEvent( DISPLAY, 12,10,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        
        assertEquals(1, editBlackboard.getCoords(12,10).size());
        assertEquals(0, editBlackboard.getCoords(10,10).size());

        event = new MapMouseEvent( DISPLAY, 20,10,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        
        assertEquals(2, editBlackboard.getCoords(20,10).size());
        assertEquals(0, editBlackboard.getCoords(12,10).size());
        
        event = new MapMouseEvent( DISPLAY, 20,15,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        assertEquals(1, editBlackboard.getCoords(20,10).size());
        assertEquals(1, editBlackboard.getCoords(20,15).size());
        assertTrue( handler.getCurrentShape().hasVertex(Point.valueOf(20,10)));
        assertTrue( handler.getCurrentShape().hasVertex(Point.valueOf(20,15)));
        assertTrue( handler.getCurrentShape().hasVertex(Point.valueOf(20,10)));
//        assertTrue( handler.getCurrentShape().hasVertex(Point.valueOf(20,10), handler.getEditBlackboard().getCoords(20,10).get(0)));
//        assertTrue( handler.getCurrentShape().hasVertex(Point.valueOf(20,15), handler.getEditBlackboard().getCoords(20,15).get(0)));
        
    }
    
    @Test
    public void testSnapToVertex() throws Exception{

        MapMouseEvent event;
        //drag close to point and it should snap to a coord in point.
        
        assertEquals(1, editBlackboard.getCoords(20,10).size());
        handler.getEditBlackboard().selectionClear();
        handler.getEditBlackboard().selectionAdd(Point.valueOf(10,10));
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,10));
        event=new MapMouseEvent( DISPLAY, 13,10,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        
        event=new MapMouseEvent( DISPLAY, 18,10,NONE,BUTTON1, BUTTON1 );
        mode.getCommand(handler, event, EventType.DRAGGED);
        
        handler.handleEvent(event, EventType.RELEASED);
        
        assertEquals(2, editBlackboard.getCoords(20,10).size());
        assertEquals(0, editBlackboard.getCoords(10,10).size());
    }

}
