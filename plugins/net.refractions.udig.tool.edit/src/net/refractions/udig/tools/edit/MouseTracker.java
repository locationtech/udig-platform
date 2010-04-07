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
package net.refractions.udig.tools.edit;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.ViewportPane;
import net.refractions.udig.tools.edit.support.Point;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * Keeps track of the what mouse events have happened.  Must be in event thread to call this method.
 * 
 * @author jones
 * @since 1.1.0
 */
public class MouseTracker {

    private static final int MAX_QUEUE_SIZE = 10;
    private Point dragStarted;
    private Queue<MapMouseEvent> previousEvents=new ConcurrentLinkedQueue<MapMouseEvent>();
    private Point currentPoint;
    private volatile Set<EventType> updateTriggers=new HashSet<EventType>();
    private EditToolHandler handler;

    public MouseTracker( EditToolHandler handler2 ){
        this.handler=handler2;
    }
    
    /**
     * Called by EditToolhandler to updates the state.
     *
     * @param e The most recent event
     * @param type the type of event
     */
    protected void updateState(MapMouseEvent e, EventType type){
        checkAccess();
        previousEvents.add(e);
        
        if( previousEvents.size()>MAX_QUEUE_SIZE)
            previousEvents.remove();
        
        switch( type ) {
        case PRESSED:
            break;
        case DRAGGED:
            currentPoint=Point.valueOf(e.x, e.y);
            break;
        case DOUBLE_CLICK:
            
            break;
        case ENTERED:
            
            break;
        case EXITED:
            
            break;
        case MOVED:
            currentPoint=dragStarted=Point.valueOf(e.x, e.y);
            break;
        case RELEASED:
            break;

        default:
            break;
        }
        
        if( updateTriggers.contains(type) ){
            if( e.source instanceof ViewportPane ){
                handler.repaint();
            }
        }
    }
    
    private void checkAccess() {
        if( Display.getCurrent()==null )
            SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
    }

    /**
     * @return Returns the point where the drag started or null if not dragging.
     */
    public Point getDragStarted(){
        checkAccess();
        return dragStarted;
    }

    /**
     * This is a copy modifying queue will have no effect on original queue
     * 
     * @return Returns the queue of previous events.
     */
    public Queue<MapMouseEvent> getPreviousEvents() {
        checkAccess();
        return new LinkedList<MapMouseEvent>(previousEvents);
    }

    /**
     * @param dragStarted The dragStarted to set.
     */
    protected void setDragStarted( Point dragStarted ) {
        checkAccess();
        this.dragStarted = dragStarted;
    }

    /**
     * Returns the previous events queue.  This is a thread-safe queue.
     * 
     * @param previousEvents The previousEvents to set.
     */
    protected Queue<MapMouseEvent> getModifiablePreviousEvents( ) {
        checkAccess();
        return previousEvents;
    }

    /**
     * Returns the current location of the mouse.
     * @return
     */
    public Point getCurrentPoint() {
        checkAccess();
        return currentPoint;
    }

}
