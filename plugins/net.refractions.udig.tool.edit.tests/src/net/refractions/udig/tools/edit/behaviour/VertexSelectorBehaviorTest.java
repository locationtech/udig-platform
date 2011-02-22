package net.refractions.udig.tools.edit.behaviour;

import junit.framework.TestCase;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.TestHandler;

public class VertexSelectorBehaviorTest extends TestCase {

    /*
     * Test method for 'net.refractions.udig.tools.edit.mode.VertexSelectorMode.isValid(EditToolHandler, MapMouseEvent, EventType)'
     */
    public void testIsValid() throws Exception {
        SelectVertexBehaviour mode=new SelectVertexBehaviour();
        final int none=MapMouseEvent.NONE;
        final int ctrl = MapMouseEvent.CTRL_DOWN_MASK;
        final int shift = MapMouseEvent.SHIFT_DOWN_MASK;
        final int alt = MapMouseEvent.ALT_DOWN_MASK;
        final int button1 = MapMouseEvent.BUTTON1;
        final int button2 = MapMouseEvent.BUTTON2;

        TestHandler handler=new TestHandler();
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 0, 0, none, none, 0), EventType.DOUBLE_CLICK));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 0, 0, none, none, button1), EventType.PRESSED));

        handler.setCurrentShape(handler.getEditBlackboard().getGeoms().get(0).getShell());
        handler.getEditBlackboard().addPoint(10,0, handler.getCurrentGeom().getShell());


        assertFalse(mode.isValid(handler,  new MapMouseEvent(null, 10, 0, none, none, button1), EventType.PRESSED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, none, button1), EventType.DRAGGED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, none, button1), EventType.DRAGGED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, none, button1), EventType.EXITED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, none, button1), EventType.MOVED));
        assertTrue(mode.isValid(handler, new MapMouseEvent(null, 10, 0, none, none, button1), EventType.RELEASED));

        assertTrue(mode.isValid(handler, new MapMouseEvent(null, 10,0, ctrl,none, button1), EventType.RELEASED));
        assertTrue(mode.isValid(handler, new MapMouseEvent(null, 10,0, shift, none, button1), EventType.RELEASED));

        assertFalse(mode.isValid(handler, new MapMouseEvent(null, 10,0, alt, none, button1), EventType.RELEASED));
        assertFalse(mode.isValid(handler, new MapMouseEvent(null,
                0, 0, shift|ctrl, button1, button1), EventType.RELEASED));

        assertFalse(mode.isValid(handler,  new MapMouseEvent(null, 10, 0, none, none, button2), EventType.RELEASED));
    }

    /*
     * Test method for 'net.refractions.udig.tools.edit.mode.VertexSelectorMode.run(EditToolHandler, MapMouseEvent, EventType)'
     */
    public void testRun() throws Exception {
        SelectVertexBehaviour mode=new SelectVertexBehaviour();

        final int none=MapMouseEvent.NONE;
        final int ctrl = MapMouseEvent.CTRL_DOWN_MASK;
        final int shift = MapMouseEvent.SHIFT_DOWN_MASK;
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
        handler.handleEvent(new MapMouseEvent(null, 9, 10, none, none, button1), EventType.RELEASED);
        assertEquals(1, handler.getEditBlackboard().getSelection().size());
        assertEquals(Point.valueOf(10,10), handler.getEditBlackboard().getSelection().iterator().next());

        //test click on same point
        handler.handleEvent(new MapMouseEvent(null, 10, 10, none, none, button1), EventType.RELEASED);
        assertEquals(Point.valueOf(10,10), handler.getEditBlackboard().getSelection().iterator().next());
        assertEquals(1, handler.getEditBlackboard().getSelection().size());

        //test click on new point
        handler.handleEvent( new MapMouseEvent(null, 15, 15, none, none, button1), EventType.RELEASED);
        assertEquals(Point.valueOf(15,15), handler.getEditBlackboard().getSelection().iterator().next());
        assertEquals(1, handler.getEditBlackboard().getSelection().size());

        //test add to selection via SHIFT-click
        MapMouseEvent event = new MapMouseEvent(null, 10, 10, shift, none, button1);
        handler.handleEvent( event, EventType.RELEASED);
        assertEquals(2, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(15,15)));

        //test no change to selection if SHIFT-click on selected vertex
        event= new MapMouseEvent(null, 15, 15, shift, none, button1);
        handler.handleEvent( event, EventType.RELEASED);
        assertEquals(2, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(15,15)));

        //test add to selection via CTRL-click
        event= new MapMouseEvent(null, 20, 15, ctrl, none, button1);
        handler.handleEvent( event, EventType.RELEASED);
        assertEquals(3, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(15,15)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,15)));

        //test remove from selection via CTRL-click
        event= new MapMouseEvent(null, 15, 15, ctrl, none, button1);
        handler.handleEvent( event, EventType.RELEASED);
        assertEquals(2, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,15)));

        //test no change when SHIFT-click and CTRL-click on nothing
        event= new MapMouseEvent(null, 300, 300, shift, none, button1);
        handler.handleEvent( event, EventType.RELEASED);
        assertEquals(2, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,15)));

        event= new MapMouseEvent(null, 300, 300, ctrl, none, button1);
        handler.handleEvent( event, EventType.RELEASED);
        assertEquals(2, handler.getEditBlackboard().getSelection().size());
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(10,10)));
        assertTrue(handler.getEditBlackboard().getSelection().contains(Point.valueOf(20,15)));

        handler.getEditBlackboard().selectionClear();

        //test no add when click SHIFT-click and CTRL-click on nothing
        event= new MapMouseEvent(null, 300, 300, shift, none, button1);
        handler.handleEvent( event, EventType.RELEASED);
        assertEquals(0, handler.getEditBlackboard().getSelection().size());

        event= new MapMouseEvent(null, 300, 300, ctrl, none, button1);
        handler.handleEvent( event, EventType.RELEASED);
        assertEquals(0, handler.getEditBlackboard().getSelection().size());

    }

}
