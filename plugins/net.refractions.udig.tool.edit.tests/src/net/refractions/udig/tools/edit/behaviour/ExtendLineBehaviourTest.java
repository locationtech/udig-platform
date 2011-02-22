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

import junit.framework.TestCase;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.TestHandler;

/**
 * Extend a line test
 * @author jones
 * @since 1.1.0
 */
public class ExtendLineBehaviourTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.behaviour.ExtendLineBehaviour.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    public void testIsValid() throws Exception {
        TestHandler handler=new TestHandler();
        handler.getTestEditBlackboard().util.setVertexRadius(4);

        StartExtendLineBehaviour behavior=new StartExtendLineBehaviour();

        handler.setCurrentState(EditState.MODIFYING);

        //Current Shape must be set
        MapMouseEvent event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        EditGeom editGeom = handler.getEditBlackboard().getGeoms().get(0);
        handler.setCurrentShape(editGeom.getShell());
        handler.getEditBlackboard().addPoint(0,0, handler.getCurrentShape());
        handler.getEditBlackboard().addPoint(11,11, handler.getCurrentShape());

        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));

        // no buttons should be down
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.BUTTON2, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));
        // button2 isn't legal
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON2);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // no modifiers only
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.ALT_DOWN_MASK, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // doesn't work with BUSY
        handler.setCurrentState(EditState.BUSY);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // doesn't work with CREATING
        handler.setCurrentState(EditState.CREATING);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // should work, just checking state is still good;
        handler.setCurrentState(EditState.NONE);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));

        // doesn't work with event pressed
        assertFalse(behavior.isValid(handler, event, EventType.PRESSED));

        // not valid if not over end point
        event = new MapMouseEvent(null, 20,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // valid over first point
        event = new MapMouseEvent(null, 0,0, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));

    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.behaviour.ExtendLineBehaviour.getCommand(EditToolHandler, MapMouseEvent, EventType)'
     */
    public void testGetCommand() throws Exception {
        TestHandler handler=new TestHandler();
        handler.getTestEditBlackboard().util.setVertexRadius(4);

        StartExtendLineBehaviour behavior=new StartExtendLineBehaviour();
        handler.getBehaviours().add(behavior);
        handler.setCurrentState(EditState.MODIFYING);

        EditGeom editGeom = handler.getEditBlackboard().getGeoms().get(0);
        handler.setCurrentShape(editGeom.getShell());
        handler.getEditBlackboard().addPoint(0,0, handler.getCurrentShape());
        handler.getEditBlackboard().addPoint(11,11, handler.getCurrentShape());


        MapMouseEvent event = new MapMouseEvent(null, 0,0, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));

        handler.handleEvent(event, EventType.RELEASED);

        assertEquals(Point.valueOf(11,11), handler.getCurrentShape().getPoint(0));
        assertEquals(EditState.CREATING, handler.getCurrentState());

        handler.setCurrentState(EditState.NONE);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));

        handler.handleEvent(event, EventType.RELEASED);
        assertEquals(Point.valueOf(0,0), handler.getCurrentShape().getPoint(0));
        assertEquals(EditState.CREATING, handler.getCurrentState());

        handler.setCurrentState(EditState.NONE);

        handler.handleEvent(event, EventType.RELEASED);
        assertEquals(Point.valueOf(0,0), handler.getCurrentShape().getPoint(0));
        assertEquals(EditState.CREATING, handler.getCurrentState());
    }

}
