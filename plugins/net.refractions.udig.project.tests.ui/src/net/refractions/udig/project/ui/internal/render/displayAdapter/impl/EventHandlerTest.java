package net.refractions.udig.project.ui.internal.render.displayAdapter.impl;

import junit.framework.TestCase;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;

public class EventHandlerTest extends TestCase {

    public class TestEventJob extends EventJob {
        int type;
        Object event;
       
        
        @Override
        public void fire( int type, Object event ) {
            this.type=type;
            this.event=event;
        }

    }

    private EventHandler handler;
    private TestEventJob eventJob;

    @Override
    protected void setUp() throws Exception {
        eventJob=new TestEventJob();
        handler=new EventHandler(null, eventJob);
    }
    
    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.controlResized(Event)'
     */
    public void testControlResized() {
        
        
        
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseDoubleClick(Event)'
     */
    public void testMouseDoubleClick() {
        Event e=new Event();
        e.button=1;
        e.stateMask=SWT.ALT;
        handler.mouseDoubleClick(e);
        MapMouseEvent event=(MapMouseEvent) eventJob.event;
        assertEquals(MapMouseEvent.ALT_DOWN_MASK, event.modifiers);
        assertEquals(MapMouseEvent.BUTTON1, event.button);
        assertEquals(MapMouseEvent.BUTTON1, event.buttons);
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseDown(Event)'
     */
    public void testMouseDown() {
        Event e=new Event();
        e.button=1;
        e.stateMask=SWT.ALT|SWT.BUTTON1;

        handler.mouseDown(e);
        MapMouseEvent event=(MapMouseEvent) eventJob.event;
        assertEquals(MapMouseEvent.ALT_DOWN_MASK, event.modifiers);
        assertEquals(MapMouseEvent.BUTTON1, event.button);
        assertEquals(MapMouseEvent.BUTTON1, event.buttons);
        
        e.button=2;
        e.stateMask=SWT.ALT|SWT.BUTTON1|SWT.BUTTON2;
        
        handler.mouseDown(e);
        event=(MapMouseEvent) eventJob.event;
        
        assertEquals(MapMouseEvent.ALT_DOWN_MASK, event.modifiers);
        assertEquals(MapMouseEvent.BUTTON2, event.button);
        assertEquals(MapMouseEvent.BUTTON1|MapMouseEvent.BUTTON2, event.buttons);
        
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseUp(Event)'
     */
    public void testMouseUp() {
        Event e=new Event();
        e.button=1;
        e.stateMask=SWT.ALT|SWT.BUTTON1;

        handler.mouseDown(e);
        e.button=2;
        e.stateMask=SWT.ALT|SWT.BUTTON2|SWT.BUTTON1;
        handler.mouseDown(e);
        
        e.button=2;
        e.stateMask=SWT.ALT;
        
        handler.mouseUp( e );
        
        MapMouseEvent event = (MapMouseEvent) eventJob.event;
        
        assertEquals(MapMouseEvent.BUTTON2, event.button);
        assertEquals(MapMouseEvent.NONE, event.buttons);
        

        e.button=1;
        e.stateMask=SWT.ALT;
        handler.mouseUp( e );
        event = (MapMouseEvent) eventJob.event;
        assertEquals(MapMouseEvent.BUTTON1, event.button);
        assertEquals(MapMouseEvent.NONE, event.buttons);
        
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseMove(Event)'
     */
    public void testMouseMove() {

    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseEnter(Event)'
     */
    public void testMouseEnter() {

    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseExit(Event)'
     */
    public void testMouseExit() {

    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseHover(Event)'
     */
    public void testMouseHover() {

    }

}
