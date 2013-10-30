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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EventType;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.TestHandler;

import org.junit.Test;

public class DoubleClickRunAcceptBehaviourTest {

    /*
     * Test method for 'org.locationtech.udig.tools.edit.behaviour.StartEditingBehaviour.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    @Test
    public void testIsValid() throws Exception {
        TestHandler handler=new TestHandler();
        
        AcceptOnDoubleClickBehaviour behavior=new AcceptOnDoubleClickBehaviour();

        handler.setCurrentState(EditState.CREATING);

        //Current Shape must be set
        MapMouseEvent event = new MapMouseEvent(null, 10,10, MapMouseEvent.BUTTON1, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.DOUBLE_CLICK));
        
        EditGeom editGeom = handler.getEditBlackboard().getGeoms().get(0);
        handler.setCurrentShape(editGeom.getShell());
        editGeom.setChanged(true);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.DOUBLE_CLICK));
        // only button1 should be down
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.BUTTON2, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.DOUBLE_CLICK));
        // button2 isn't legal
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON2);
        assertFalse(behavior.isValid(handler, event, EventType.DOUBLE_CLICK));

        // no modifiers only
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.ALT_DOWN_MASK, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.DOUBLE_CLICK));

        // MODIFYING is legal
        handler.setCurrentState(EditState.MODIFYING);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.DOUBLE_CLICK));
        
        // not a legal state
        handler.setCurrentState(EditState.NONE);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.DOUBLE_CLICK));
        
        // should work, just checking state is still good;
        handler.setCurrentState(EditState.CREATING);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.BUTTON1, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.DOUBLE_CLICK));
        
        // doesn't work with event pressed
        assertFalse(behavior.isValid(handler, event, EventType.PRESSED));        
    }


}
