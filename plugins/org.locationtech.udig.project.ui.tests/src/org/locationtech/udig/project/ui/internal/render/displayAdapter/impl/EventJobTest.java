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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener;

import org.junit.Before;
import org.junit.Test;

public class EventJobTest {

    private EventJob job;
    private TestListener l;

    @Before
    public void setUp() {
        job=new EventJob();
        l = new TestListener();
        job.addMouseListener(l);
        job.addMouseMotionListener(l);
        job.addMouseWheelListener(l);
    }
    
    /*
     * Test method for 'org.locationtech.udig.project.ui.internal.render.displayAdapter.impl.EventJob.fire(int, Object)'
     */
    @Test
    public void testFire() throws Exception {
        
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.RELEASED, null);
        job.fire(EventJob.ENTERED, null);
        job.fire(EventJob.EXITED, null);
        job.fire(EventJob.DOUBLE_CLICK, null);
        job.fire(EventJob.MOVED, null);
        job.fire(EventJob.DRAGGED, null);
        job.fire(EventJob.WHEEL, null);
        
        assertEquals(EventType.PRESSED, l.type.get(0));
        assertEquals(EventType.RELEASED, l.type.get(1));
        assertEquals(EventType.ENTERED, l.type.get(2));
        assertEquals(EventType.EXITED, l.type.get(3));
        assertEquals(EventType.DOUBLE_CLICK, l.type.get(4));
        assertEquals(EventType.MOVED, l.type.get(5));
        assertEquals(EventType.DRAGGED, l.type.get(6));
        assertEquals(EventType.WHEEL, l.type.get(7));
        
        l.events.clear();
        l.type.clear();
    }
    
    @Test
    public void testClickTest1() throws Exception {
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.MOVED, null);
        
        assertEquals(2, l.type.size());
        assertEquals(EventType.PRESSED, l.type.get(0));
        assertEquals(EventType.MOVED, l.type.get(1));
    }
    
    @Test
    public void testDoubleClick() throws Exception {
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.RELEASED, null);
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.RELEASED, null);

        assertEquals(1, l.type.size());
        assertEquals(EventType.DOUBLE_CLICK, l.type.get(0));
        
        l.events.clear();
        l.type.clear();
        
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.RELEASED, null);
        job.fire(EventJob.RELEASED, null);

        assertEquals(4, l.type.size());
        assertEquals(EventType.PRESSED, l.type.get(0));
        assertEquals(EventType.PRESSED, l.type.get(1));
        assertEquals(EventType.RELEASED, l.type.get(2));
        assertEquals(EventType.RELEASED, l.type.get(3));
        
        l.events.clear();
        l.type.clear();
        
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.RELEASED, null);
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.RELEASED, null);

        assertEquals(1, l.type.size());
        assertEquals(EventType.DOUBLE_CLICK, l.type.get(0));
        
        l.events.clear();
        l.type.clear();
        
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.RELEASED, null);
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.RELEASED, null);
        job.fire(EventJob.ENTERED, null);

        assertEquals(2, l.type.size());
        assertEquals(EventType.DOUBLE_CLICK, l.type.get(0));
        
        l.events.clear();
        l.type.clear();

        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.MOVED, null);
        job.fire(EventJob.RELEASED, null);
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.DOUBLE_CLICK, null);
        job.fire(EventJob.ENTERED, null);

        assertEquals(6, l.type.size());
        
        l.events.clear();
        l.type.clear();
                
        job.fire(EventJob.PRESSED, null);
        Thread.sleep(ProjectUIPlugin.getDefault().getDoubleClickSpeed()+100);
        job.fire(EventJob.RELEASED, null);
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.DOUBLE_CLICK, null);

        assertEquals(4, l.type.size());
        
    }
    
    @Test
    public void testConcurrency() throws Exception {
        EventJob job=new EventJob();
        TestListener l=new TestListener();
        job.addMouseListener(l);
        job.addMouseMotionListener(l);
        job.addMouseWheelListener(l);
        
        job.fire(EventJob.PRESSED, null);
        job.fire(EventJob.RELEASED, null);
        job.fire(EventJob.ENTERED, null);
        job.fire(EventJob.EXITED, null);
        job.fire(EventJob.DOUBLE_CLICK, null);
        job.fire(EventJob.MOVED, null);
        job.fire(EventJob.DRAGGED, null);
        job.fire(EventJob.WHEEL, null);
        
        assertEquals(EventType.PRESSED, l.type.get(0));
        assertEquals(EventType.RELEASED, l.type.get(1));
        assertEquals(EventType.ENTERED, l.type.get(2));
        assertEquals(EventType.EXITED, l.type.get(3));
        assertEquals(EventType.DOUBLE_CLICK, l.type.get(4));
        assertEquals(EventType.MOVED, l.type.get(5));
        assertEquals(EventType.DRAGGED, l.type.get(6));
        assertEquals(EventType.WHEEL, l.type.get(7));
        
        l.type.clear();
        l.events.clear();
        
        for( int i=0; i<700; i++){
            MapMouseEvent event=new MapMouseEvent(null, i,0,MapMouseEvent.NONE, MapMouseEvent.NONE, MapMouseEvent.BUTTON1);
            job.fire(EventJob.PRESSED, event);
        }

        Thread.sleep(200);

        assertTrue("number of events="+l.type.size(), 200<l.type.size());    //$NON-NLS-1$
    }
    
    enum EventType{
        PRESSED, RELEASED, ENTERED, EXITED, DOUBLE_CLICK, MOVED, DRAGGED, WHEEL, HOVERED
    }
    
    class TestListener implements MapMouseListener, MapMouseMotionListener, MapMouseWheelListener{
        List<EventType> type=new ArrayList<EventType>();
        List<MapMouseEvent> events=new ArrayList<MapMouseEvent>();
        public void mousePressed( MapMouseEvent event ) {
            type.add(EventType.PRESSED);
            events.add(event);
        }

        public void mouseReleased( MapMouseEvent event ) {
            type.add(EventType.RELEASED);
            events.add(event);
        }

        public void mouseEntered( MapMouseEvent event ) {
            type.add(EventType.ENTERED);
            events.add(event);
        }

        public void mouseExited( MapMouseEvent event ) {
            type.add(EventType.EXITED);
            events.add(event);
        }

        public void mouseDoubleClicked( MapMouseEvent event ) {
            type.add(EventType.DOUBLE_CLICK);
            events.add(event);
        }

        public void mouseMoved( MapMouseEvent event ) {
            type.add(EventType.MOVED);        
            events.add(event);
        }

        public void mouseDragged( MapMouseEvent event ) {
            type.add(EventType.DRAGGED);        
            events.add(event);
        }

        public void mouseWheelMoved( MapMouseWheelEvent e ) {
            type.add(EventType.WHEEL);
        }

        public void mouseHovered( MapMouseEvent event ) {
            type.add(EventType.HOVERED);
        }
        
    }
    
    
}
