package net.refractions.udig.tools.edit;

import java.awt.Dimension;

import junit.framework.TestCase;
import net.refractions.udig.project.tests.support.TestMapDisplay;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.TestHandler;
import net.refractions.udig.tools.edit.support.TestMouseTracker;

public class MouseTrackerTest extends TestCase {

    final static int BUTTON1=MapMouseEvent.BUTTON1;
    final static int BUTTON2=MapMouseEvent.BUTTON2;
    final static int NONE=MapMouseEvent.NONE;
    final static int SHIFT=MapMouseEvent.SHIFT_DOWN_MASK;
    final static int CTRL=MapMouseEvent.CTRL_DOWN_MASK;
    final static TestMapDisplay DISPLAY = new TestMapDisplay(new Dimension(500,500));
    /*
     * Test method for 'net.refractions.udig.tools.edit.latest.MouseTracker.updateState(MapMouseEvent, EventType)'
     */
    public void testUpdateState() throws Exception {
        TestMouseTracker tracker=new TestMouseTracker(new TestHandler() );

        MapMouseEvent event=new MapMouseEvent( DISPLAY, 10,15,NONE,BUTTON1, BUTTON1 );
        tracker.updateState(event, EventType.MOVED);

        assertEquals(Point.valueOf(10,15), tracker.getDragStarted());

        event=new MapMouseEvent( DISPLAY, 10,10,NONE,BUTTON1, BUTTON1 );
        tracker.updateState(event, EventType.DRAGGED);
        assertEquals(Point.valueOf(10,15), tracker.getDragStarted());

    }

}
