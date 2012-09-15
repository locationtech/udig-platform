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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the SelectioBoxBehaviour
 * @author jones
 * @since 1.1.0
 */
public class SelectionBoxBehaviourTest {
    private TestHandler handler;
    private PrimitiveShape shell;
    
    @Before
    public void setUp() throws Exception {
        handler = new TestHandler();
        shell = handler.getEditBlackboard().getGeoms().get(0).getShell();
        handler.getEditBlackboard().addPoint(0, 10, shell);
        handler.getEditBlackboard().addPoint(20, 10, shell);
        handler.getEditBlackboard().addPoint(20, 20, shell);
        handler.getEditBlackboard().addPoint(10, 20, shell);
        handler.getEditBlackboard().addPoint(0, 10, shell);

        handler.getTestEditBlackboard().util.setVertexRadius(4);
    }

    /*
     * Test method for
     * 'net.refractions.udig.tools.edit.behaviour.AddVertexOnEdgeBehaviour.isValid(EditToolHandler,
     * MapMouseEvent, EventType)'
     */
    @Test
    public void testIsValid() {
        SelectVerticesWithBoxBehaviour behaviour = new SelectVerticesWithBoxBehaviour();

        handler.setCurrentState(EditState.MODIFYING);

        // current shape is not set
        MapMouseEvent event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.DRAGGED));

        handler.setCurrentShape(shell);

        // should work
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertTrue(behaviour.isValid(handler, event, EventType.DRAGGED));

        // creating not valid state
        handler.setCurrentState(EditState.CREATING);
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.DRAGGED));

        // make sure state is good
        handler.setCurrentState(EditState.MODIFYING);
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertTrue(behaviour.isValid(handler, event, EventType.DRAGGED));

        // alt not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.ALT_DOWN_MASK, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.DRAGGED));

        // ctrl not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.CTRL_DOWN_MASK, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.DRAGGED));

        // shift is acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.SHIFT_DOWN_MASK, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertTrue(behaviour.isValid(handler, event, EventType.DRAGGED));


        // DRAGGED not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.RELEASED));

        // DOUBLE_CLICK not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.DOUBLE_CLICK));

        // ENTERED not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.ENTERED));

        // EXITED not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.EXITED));

        // MOVED not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.MOVED));

        // WHEEL not acceptable
        event = new MapMouseEvent(null, 10, 10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        assertFalse(behaviour.isValid(handler, event, EventType.WHEEL));


    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.behaviour.SelectionBoxBehaviour.getCommand(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Test
    public void testGetCommand() {
        handler.getBehaviours().add( new SelectVerticesWithBoxBehaviour() );
        
        handler.setCurrentShape(shell);

        // should selct 1 point
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,0));
        MapMouseEvent event = new MapMouseEvent(null, 11, 11, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        event = new MapMouseEvent(null, 11, 11, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertEquals(1, handler.getEditBlackboard().getSelection().size());
        assertEquals(Point.valueOf(0,10), handler.getEditBlackboard().getSelection().iterator().next());

        // should replace old selection of (0,10) with 20,20 
        handler.getMouseTracker().setDragStarted(Point.valueOf(30,30));
        event = new MapMouseEvent(null, 19, 19, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        event = new MapMouseEvent(null, 19, 19, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertEquals(1, handler.getEditBlackboard().getSelection().size());
        assertEquals(Point.valueOf(20,20), handler.getEditBlackboard().getSelection().iterator().next());

        // should add (0,10) to selection 
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,0));
        event = new MapMouseEvent(null, 11, 11, MapMouseEvent.SHIFT_DOWN_MASK, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        event = new MapMouseEvent(null, 11, 11, MapMouseEvent.SHIFT_DOWN_MASK, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertEquals(2, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,20)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(0,10)));
        
        // should add all points to selection
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,0));
        event = new MapMouseEvent(null, 30, 30, MapMouseEvent.SHIFT_DOWN_MASK, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        event = new MapMouseEvent(null, 30,30, MapMouseEvent.SHIFT_DOWN_MASK, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertEquals(4, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(0,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,20)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,20)));
     
        // should make no change
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,0));
        event = new MapMouseEvent(null, 1, 1, MapMouseEvent.SHIFT_DOWN_MASK, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        event = new MapMouseEvent(null, 1,1, MapMouseEvent.SHIFT_DOWN_MASK, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertEquals(4, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(0,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,20)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,20)));
     
        // should clear selection
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,0));
        event = new MapMouseEvent(null, 1, 1, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        event = new MapMouseEvent(null, 1,1, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertEquals(0, handler.getEditBlackboard().getSelection().size());
    }

    @Test
    public void testSelectingWhen2ShapesAreOnBB() throws Exception {
        EditBlackboard bb = handler.getEditBlackboard();
        EditGeom geom2 = bb.newGeom("new", null); //$NON-NLS-1$
        bb.addPoint(100,0,geom2.getShell());
        bb.addPoint(100,10,geom2.getShell());

        handler.getBehaviours().add( new SelectVerticesWithBoxBehaviour() );
        
        handler.setCurrentShape(shell);

        // should selct 1 point
        handler.getMouseTracker().setDragStarted(Point.valueOf(0,0));
        MapMouseEvent event = new MapMouseEvent(null, 110, 40, MapMouseEvent.NONE, MapMouseEvent.BUTTON1,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.DRAGGED);
        event = new MapMouseEvent(null, 110, 40, MapMouseEvent.NONE, MapMouseEvent.NONE,
                MapMouseEvent.BUTTON1);
        handler.handleEvent(event, EventType.RELEASED);
        
        assertEquals(4, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(0,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,20)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,20)));
        
    }
}
