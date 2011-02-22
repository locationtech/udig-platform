package net.refractions.udig.tools.edit.behaviour;

import junit.framework.TestCase;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.TestHandler;

public class AddVertexWhileCreatingBehaviourTest extends TestCase {

    /*
     * Test method for 'net.refractions.udig.tools.edit.behaviour.StartEditingBehaviour.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    public void testIsValid() throws Exception {
        TestHandler handler=new TestHandler();
        handler.getTestEditBlackboard().util.setVertexRadius(4);

        AddVertexWhileCreatingBehaviour behavior=new AddVertexWhileCreatingBehaviour();

        handler.setCurrentState(EditState.CREATING);

        //Current Shape must be set
        MapMouseEvent event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        EditGeom editGeom = handler.getEditBlackboard().getGeoms().get(0);
        handler.setCurrentShape(editGeom.getShell());
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

        // works only with CREATING
        handler.setCurrentState(EditState.MODIFYING);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // works only with CREATING
        handler.setCurrentState(EditState.NONE);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));

        // should work, just checking state is still good;
        handler.setCurrentState(EditState.CREATING);
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertTrue(behavior.isValid(handler, event, EventType.RELEASED));

        // doesn't work with event pressed
        assertFalse(behavior.isValid(handler, event, EventType.PRESSED));

        //doesn't work if the mouse is over an existing vertex in current shape
        handler.getEditBlackboard().addPoint(11,11, handler.getCurrentShape());
        event = new MapMouseEvent(null, 10,10, MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
        assertFalse(behavior.isValid(handler, event, EventType.RELEASED));
    }

}
