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
package org.locationtech.udig.project.ui.internal.render.displayAdapter.impl;

import static org.junit.Assert.assertEquals;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;

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
     * Test method for 'org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.controlResized(Event)'
     */
    @Test
    public void testControlResized() {
        
        
        
    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseDoubleClick(Event)'
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
     * Test method for 'org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseDown(Event)'
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
     * Test method for 'org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseUp(Event)'
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
     * Test method for 'org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseMove(Event)'
     */
    @Test
    public void testMouseMove() {

    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseEnter(Event)'
     */
    @Test
    public void testMouseEnter() {

    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseExit(Event)'
     */
    @Test
    public void testMouseExit() {

    }

    /*
     * Test method for 'org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.EventHandler.mouseHover(Event)'
     */
    @Test
    public void testMouseHover() {

    }

}
