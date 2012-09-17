package net.refractions.udig.tools.edit.behaviour;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.TestHandler;

import org.junit.Test;

public class DoubleClickRunAcceptBehaviourTest {

    /*
     * Test method for 'net.refractions.udig.tools.edit.behaviour.StartEditingBehaviour.isValid(EditToolHandler, MapMouseEvent, EventType)'
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
