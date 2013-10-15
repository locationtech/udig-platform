/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.support;

import java.util.Queue;

import org.junit.Ignore;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.MouseTracker;

@Ignore
public class TestMouseTracker extends MouseTracker{
    public TestMouseTracker( EditToolHandler handler2 ) {
        super(handler2);
    }

    @Override
    public void setDragStarted( Point dragStarted ) {
        super.setDragStarted(dragStarted);
    }
    
    @Override
    public Queue<MapMouseEvent> getModifiablePreviousEvents() {
        return super.getModifiablePreviousEvents();
    }
    
    @Override
    public void updateState( MapMouseEvent e, EventType type ) {
        super.updateState(e, type);
    }

}