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
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.junit.Test;

public class MouseDownVertexSelectTest {

    /*
     * Test method for 'org.locationtech.udig.tools.edit.mode.VertexSelectorMode.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Test
    public void testIsValid() throws Exception {
        SelectVertexOnMouseDownBehaviour mode=new SelectVertexOnMouseDownBehaviour();
        final int none=MapMouseEvent.NONE;
        final int ctrl = MapMouseEvent.CTRL_DOWN_MASK;
        final int shift = MapMouseEvent.SHIFT_DOWN_MASK;
        final int alt = MapMouseEvent.ALT_DOWN_MASK;
        final int button1 = MapMouseEvent.BUTTON1;
        final int button2 = MapMouseEvent.BUTTON2;
        
        TestHandler handler=new TestHandler();
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 0, 0, none, none, 0), EventType.DOUBLE_CLICK));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 0, 0, none, button1, button1), EventType.PRESSED));

        handler.setCurrentShape(handler.getEditBlackboard().getGeoms().get(0).getShell());
        handler.getEditBlackboard().addPoint(10,0, handler.getCurrentGeom().getShell());

        
        assertTrue(mode.isValid(handler,  new MapMouseEvent(null, 10, 0, none, button1, button1), EventType.PRESSED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, button1, button1), EventType.DRAGGED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, button1, button1), EventType.DRAGGED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, button1, button1), EventType.EXITED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, button1, button1), EventType.MOVED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, button1, button1), EventType.RELEASED));

        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10,0, ctrl,button1, button1), EventType.PRESSED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10,0, shift, button1, button1), EventType.PRESSED));

        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10,0, alt, button1, button1), EventType.PRESSED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 
                0, 0, shift|ctrl, button1, button1), EventType.PRESSED));
        
        assertFalse(mode.isValid(handler,  new MapMouseEvent(null, 10, 0, none, button2, button2), EventType.PRESSED));
    }

    /*
     * Test method for 'org.locationtech.udig.tools.edit.mode.VertexSelectorMode.run(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Test
    public void testRun() throws Exception {
        SelectVertexOnMouseDownBehaviour mode=new SelectVertexOnMouseDownBehaviour();

        final int none=MapMouseEvent.NONE;
        final int button1 = MapMouseEvent.BUTTON1;
        
        TestHandler handler=new TestHandler();
        handler.getBehaviours().add(mode);
        handler.getTestEditBlackboard().util.setVertexRadius(4);
        handler.setCurrentShape(handler.getEditBlackboard().getGeoms().get(0).getShell());
        handler.getEditBlackboard().addPoint(10,10, handler.getCurrentGeom().getShell());
        handler.getEditBlackboard().addPoint(15,15, handler.getCurrentGeom().getShell());
        handler.getEditBlackboard().addPoint(20,15, handler.getCurrentGeom().getShell());
        handler.getEditBlackboard().addPoint(30,25, handler.getCurrentGeom().getShell());
        
        //test click on point
        handler.handleEvent( new MapMouseEvent(null, 9, 10, none, button1, button1), EventType.PRESSED);
        assertEquals(1, handler.getEditBlackboard().getSelection().size());
        assertEquals(Point.valueOf(10,10), handler.getEditBlackboard().getSelection().iterator().next());

        //test click on same point
        handler.handleEvent( new MapMouseEvent(null, 10, 10, none, button1, button1), EventType.PRESSED);
        assertEquals(Point.valueOf(10,10), handler.getEditBlackboard().getSelection().iterator().next());
        assertEquals(1, handler.getEditBlackboard().getSelection().size());
        
        //test click on new point
        handler.handleEvent( new MapMouseEvent(null, 15, 15, none, button1, button1), EventType.PRESSED);
        assertEquals(Point.valueOf(15,15), handler.getEditBlackboard().getSelection().iterator().next());
        assertEquals(1, handler.getEditBlackboard().getSelection().size());
        
        //do nothing on click on nothing
        MapMouseEvent event = new MapMouseEvent(null, 300, 300, none, button1, button1);
        handler.handleEvent( event, EventType.PRESSED);
        assertEquals(1, handler.getEditBlackboard().getSelection().size());
        
    }

}
