/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.support;

import java.util.Queue;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.MouseTracker;

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
