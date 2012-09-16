package net.refractions.udig.project.ui.internal.render.displayAdapter.impl;

import static org.junit.Assert.assertEquals;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.junit.Before;
import org.junit.Test;

public class EventHandlerTest {

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

    @Before
    public void setUp() throws Exception {
        eventJob=new TestEventJob();
        handler=new EventHandler(null, eventJob);
    }
    
    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.controlResized(Event)'
     */
    @Test
    public void testControlResized() {
        
        
        
    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseDoubleClick(Event)'
     */
    @Test
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
    @Test
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
    @Test
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
    @Test
    public void testMouseMove() {

    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseEnter(Event)'
     */
    @Test
    public void testMouseEnter() {

    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseExit(Event)'
     */
    @Test
    public void testMouseExit() {

    }

    /*
     * Test method for 'net.refractions.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseHover(Event)'
     */
    @Test
    public void testMouseHover() {

    }

}
