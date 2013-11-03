/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.Dimension;

import org.locationtech.udig.TestViewportPane;
import org.locationtech.udig.project.command.PostDeterminedEffectCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.render.RenderManager;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.ShapeType;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

public class StartHoleCuttingBehaviourTest {

    /*
     * Test method for 'org.locationtech.udig.tools.edit.behaviour.SelectHoleBehaviour.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Test
    public void testIsValid() throws Exception {
        TestHandler handler=new TestHandler();
        handler.getTestEditBlackboard().util.setVertexRadius(4);
        
        StartHoleCuttingBehaviour behavior=new StartHoleCuttingBehaviour();

        handler.setCurrentState(EditState.MODIFYING);

        //Current Shape must be set
        MapMouseEvent event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));
        
        EditGeom editGeom = handler.getEditBlackboard().getGeoms().get(0);
        
        handler.setCurrentShape(editGeom.getShell());
        handler.getEditBlackboard().addPoint(0,0, handler.getCurrentShape());
        
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        // mouse must be within a shell
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        handler.getEditBlackboard().addPoint(100,0, handler.getCurrentShape());
        handler.getEditBlackboard().addPoint(100,100, handler.getCurrentShape());
        handler.getEditBlackboard().addPoint(0,100, handler.getCurrentShape());
        handler.getEditBlackboard().addPoint(0,0, handler.getCurrentShape());
        
        // now we have something
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));
        
        PrimitiveShape hole = handler.getCurrentGeom().newHole();
        handler.getEditBlackboard().addPoint(30,30, hole);
        handler.getEditBlackboard().addPoint(60,30, hole);
        handler.getEditBlackboard().addPoint(60,60, hole);
        handler.getEditBlackboard().addPoint(30,60, hole);
        handler.getEditBlackboard().addPoint(30,30, hole);
        
        //mouse is in another hole
        event = new MapMouseEvent(null, 40,40, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));
        
        // no buttons should be down
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.BUTTON2, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));
        
        // button2 isn't legal
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON2);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // no modifiers only
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.ALT_DOWN_MASK, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // should work, just checking state is still good;
        handler.setCurrentState(EditState.MODIFYING);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));
        
        // doesn't work with event pressed
        assertFalse(behavior.isValid(handler, event, EventType.PRESSED));

        // doesn't work when shapetype is POINT
        handler.getCurrentGeom().setShapeType(ShapeType.POINT);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // doesn't work when shapetype is LINE
        handler.getCurrentGeom().setShapeType(ShapeType.LINE);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

    }

    @Test
    public void testRunCommands() throws Exception {
        TestHandler handler=new TestHandler();
        ((RenderManager)handler.getContext().getRenderManager()).setMapDisplay(new TestViewportPane(new Dimension(500,500)));
        handler.getTestEditBlackboard().util.setVertexRadius(4);
        
        StartHoleCuttingBehaviour behavior=new StartHoleCuttingBehaviour();
        MapMouseEvent event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);

        try {
            behavior.getCommand(handler,event, EventType.RELEASED );
            fail();
        } catch (Exception e) {
            // good
        }
        
        handler.setCurrentState(EditState.MODIFYING);

        EditGeom editGeom = handler.getEditBlackboard().getGeoms().get(0);
        handler.setCurrentShape(editGeom.getShell());

        handler.getEditBlackboard().addPoint(0,0, handler.getCurrentShape());
        handler.getEditBlackboard().addPoint(100,0, handler.getCurrentShape());
        handler.getEditBlackboard().addPoint(100,100, handler.getCurrentShape());
        handler.getEditBlackboard().addPoint(0,100, handler.getCurrentShape());
        handler.getEditBlackboard().addPoint(0,0, handler.getCurrentShape());
        
        UndoableMapCommand command = behavior.getCommand(handler, event, EventType.RELEASED);
        
        command.setMap((Map) handler.getContext().getMap());
        if (command instanceof PostDeterminedEffectCommand) {
            PostDeterminedEffectCommand c = (PostDeterminedEffectCommand) command;
            c.execute(new NullProgressMonitor());
        }else{
            command.run(new NullProgressMonitor());
        }
        
        assertEquals("Current shape should equal new hole", editGeom.getHoles().get(0), handler.getCurrentShape()); //$NON-NLS-1$
        assertEquals("A point should have been added to hole", Point.valueOf(10,10), handler.getCurrentShape().getPoint(0)); //$NON-NLS-1$
        assertEquals( EditState.CREATING, handler.getCurrentState());
        
        command.rollback(new NullProgressMonitor());

        assertEquals( editGeom.getShell(), handler.getCurrentShape());
        assertEquals(0, editGeom.getHoles().size());
        assertEquals( EditState.MODIFYING, handler.getCurrentState());
        
    }
    
    
}
