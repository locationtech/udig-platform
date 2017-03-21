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
package org.locationtech.udig.tools.edit;

import static org.junit.Assert.assertEquals;

import java.awt.Dimension;

import org.locationtech.udig.project.testsupport.TestMapDisplay;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.support.Point;
import org.locationtech.udig.tools.edit.support.TestHandler;
import org.locationtech.udig.tools.edit.support.TestMouseTracker;
import org.junit.Test;

public class MouseTrackerTest {

    final static int BUTTON1=MapMouseEvent.BUTTON1;
    final static int BUTTON2=MapMouseEvent.BUTTON2;
    final static int NONE=MapMouseEvent.NONE;
    final static int SHIFT=MapMouseEvent.SHIFT_DOWN_MASK;
    final static int CTRL=MapMouseEvent.CTRL_DOWN_MASK;
    final static TestMapDisplay DISPLAY = new TestMapDisplay(new Dimension(500,500));
    
    /*
     * Test method for 'org.locationtech.udig.tools.edit.latest.MouseTracker.updateState(MapMouseEvent, EventType)'
     */
    @Test
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
