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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.CommandManager;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.behaviour.CreateShapeBehaviour.ShapeFactory;
import org.locationtech.udig.tools.edit.behaviour.accept.AcceptChangesBehaviour;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.ShapeType;
import org.locationtech.udig.tools.edit.support.TestHandler;
import org.locationtech.udig.ui.WaitCondition;
import org.locationtech.udig.ui.tests.support.UDIGTestUtil;

import org.geotools.data.FeatureSource;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import org.locationtech.jts.geom.Polygon;

/**
 * Tests the ShapeCreationBehaviour.
 * @author jones
 * @since 1.1.0
 */
public class ShapeCreationBehaviourTest {

    @Test
    public void testIsValid() throws Exception {
        TestHandler handler=new TestHandler();
        
        CreateShapeBehaviour behav=new CreateShapeBehaviour(new ShapeFactory(){
            @Override
            public GeneralPath create( int width, int height ) {
                GeneralPath path=new GeneralPath();
                path.append(new Rectangle(width, height), false);
                return path;
            }
        });
        
        MapMouseEvent event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(behav.isValid(handler, event, EventType.DRAGGED));
        
        //released no legal
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.RELEASED));

        // must be mouse button 1
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON2, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.DRAGGED));

        // cannot be Modify state
        handler.setCurrentState(EditState.MODIFYING);
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.DRAGGED));

        // cannot be creating state
        handler.setCurrentState(EditState.CREATING);
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.DRAGGED));
        
        // make sure state is good
        handler.setCurrentState(EditState.NONE);
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(behav.isValid(handler, event, EventType.DRAGGED));

        // Alt down is not legal
        handler.setCurrentState(EditState.NONE);
        event=new MapMouseEvent( null, 0,0, MapMouseEvent.ALT_DOWN_MASK, 
                MapMouseEvent.BUTTON2, MapMouseEvent.BUTTON1);
        assertFalse(behav.isValid(handler, event, EventType.DRAGGED));

    
    }
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.behaviour.ShapeCreationBehaviour.getCommand(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Test
    public void testRectangleShape() throws Exception {
        final TestHandler handler=new TestHandler();
        ILayer layer = handler.getContext().getMapLayers().get(0);
        FeatureSource<SimpleFeatureType, SimpleFeature> resource = layer.getResource(FeatureSource.class, null);
        SimpleFeature feature = resource.getFeatures().features().next();
        ((EditManager)handler.getContext().getEditManager()).setEditFeature(feature, (Layer) layer);
        
        final EditBlackboard editBlackboard = handler.getEditBlackboard();
        PrimitiveShape shell = editBlackboard.getGeoms().get(0).getShell();
        editBlackboard.addPoint(100,100,shell);
        shell.getEditGeom().setShapeType(ShapeType.POINT);
        editBlackboard.newGeom("newone", null); //$NON-NLS-1$
        
        CreateShapeBehaviour behav=new CreateShapeBehaviour(new ShapeFactory(){
            @Override
            public GeneralPath create( int width, int height ) {
                GeneralPath path=new GeneralPath();
                path.append(new Rectangle(width, height), false);
                return path;
            }
        });
        
        handler.getBehaviours().add(behav);
        
        assertNotNull( handler.getContext().getEditManager().getEditFeature());

        handler.getAcceptBehaviours().add(new AcceptChangesBehaviour(Polygon.class, false));
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,0));
        MapMouseEvent event=new MapMouseEvent( null, 20, 20, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        
        assertTrue( handler.isLocked() );
        assertTrue( handler.isLockOwner(behav) );

        event=new MapMouseEvent( null, 20, 20, MapMouseEvent.NONE, 
                MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        handler.setTesting(false);
        handler.handleEvent(event, EventType.RELEASED);

        UDIGTestUtil.inDisplayThreadWait(1000, new WaitCondition(){

            public boolean isTrue() {
                return handler.getCurrentState()==EditState.NONE && editBlackboard.getGeoms().size()==1
                && 2==editBlackboard.getCoords(10,0).size();
            }
        }, false);
        
        assertNotSame( feature, handler.getContext().getEditManager().getEditFeature());
        assertFalse( handler.isLocked() );
        assertEquals( 1, editBlackboard.getGeoms().size());
        assertEquals( 2, editBlackboard.getCoords(10,0).size() );
        assertEquals( 1, editBlackboard.getCoords(20,0).size() );
        assertEquals( 1, editBlackboard.getCoords(20,20).size() );
        assertEquals( 1, editBlackboard.getCoords(10,20).size() );
        assertEquals(ShapeType.POLYGON, editBlackboard.getGeoms().get(0).getShapeType());
        
        ((CommandManager)((Map)handler.getContext().getMap()).getCommandStack()).undo(false);
        
        assertNotSame(feature, handler.getContext().getEditManager().getEditFeature());
        assertFalse( handler.isLocked() );
        assertEquals(2, editBlackboard.getGeoms().size());
        assertEquals(ShapeType.POINT, editBlackboard.getGeoms().get(0).getShapeType());
        assertEquals(Point.valueOf(100,100), editBlackboard.getGeoms().get(0).getShell().getPoint(0));
        
    }

    @Test
    public void testDonut() throws Exception {
        TestHandler handler=new TestHandler();
        
        CreateShapeBehaviour behav=new CreateShapeBehaviour(new ShapeFactory(){
            @Override
            public GeneralPath create( int width, int height ) {
                
                Rectangle2D shell =new Rectangle2D.Float(0,0,width,height);
                float i = ((float)width)/4;
                float j = ((float)height)/4;
                Rectangle2D hole =new Rectangle2D.Float(i,j,((float)width)/2,((float)height)/2);
                GeneralPath path=new GeneralPath();
                path.append(shell, false);
                path.append(hole, false);
                
                return path;
            }
        });
        
        handler.getBehaviours().add(behav);
        
        handler.getAcceptBehaviours().add(new AcceptChangesBehaviour(Polygon.class, false));
        handler.getMouseTracker().setDragStarted(Point.valueOf(10,0));
        MapMouseEvent event=new MapMouseEvent( null, 26, 16, MapMouseEvent.NONE, 
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        
        assertTrue( handler.isLocked() );
        assertTrue( handler.isLockOwner(behav) );

        event=new MapMouseEvent( null, 26, 16, MapMouseEvent.NONE, 
                MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertFalse( handler.isLocked() );
        assertEquals( 2, handler.getEditBlackboard().getCoords(10,0).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(26,0).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(26,16).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(10,16).size() );
        
        assertEquals( 2, handler.getEditBlackboard().getCoords(14,4).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(22,4).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(22,12).size() );
        assertEquals( 1, handler.getEditBlackboard().getCoords(14,12).size() );
    }
    
}
