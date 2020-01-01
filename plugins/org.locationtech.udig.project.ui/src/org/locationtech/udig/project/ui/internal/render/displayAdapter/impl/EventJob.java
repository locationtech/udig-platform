/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.render.displayAdapter.impl;

import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplayListener;
import org.locationtech.udig.project.render.displayAdapter.MapDisplayEvent;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseMotionListener;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseWheelListener;

public class EventJob {

    private CopyOnWriteArraySet<IMapDisplayListener> paneListener = new CopyOnWriteArraySet<IMapDisplayListener>();
    private CopyOnWriteArraySet<MapMouseWheelListener> wheel = new CopyOnWriteArraySet<MapMouseWheelListener>();
    private CopyOnWriteArraySet<MapMouseMotionListener> motion = new CopyOnWriteArraySet<MapMouseMotionListener>();
    private CopyOnWriteArraySet<MapMouseListener> mouse = new CopyOnWriteArraySet<MapMouseListener>();

    public static final int DOUBLE_CLICK = 1;
    public static final int PRESSED = 2;
    public static final int RELEASED = 3;
    public static final int MOVED = 4;
    public static final int DRAGGED = 5;
    public static final int ENTERED = 6;
    public static final int EXITED = 7;
    public static final int WHEEL = 8;
    public static final int RESIZED = 9;
    public static final int HOVERED = 10;
    
    /**
     * fires an event to all listeners. If an event is being processed then the new one is ignored
     * because some event take a while to process.
     * 
     * @param type the type of the event
     * @param event the event data object
     */
    public void fire( int type, Object event ) {
        Event event1 = new Event(type, event);
        if (event1.type == PRESSED || next!=null) {
            tryForDoubleClick(event1);
        } else {
            runEvent(event1);
        }
    }

    private static class Event {
        final int type;
        final Object data;
        Event( int type, Object event ) {
            this.type = type;
            this.data = event;
        }
    }
    
    /**
     * Dispatch events.  Usually 1 but sometimes (in the case of an aborted double click) it can be more.
     *
     * @param event2 events to send
     */
    private void runEvent( Event... event2 ) {
        for( Event event : event2 ) {
            if( event==null )
                continue;
            try{
                switch( event.type ) {
                case DOUBLE_CLICK: {
                    sendDoubleClickEvent(event);
                    break;
                }
                case PRESSED: {
                    sendMousePressedEvent(event);
                    break;
                }
                case RELEASED: {
                    sendMouseReleased(event);
                    break;
                }
                case MOVED: {
                    sendMouseMoved(event);
                    break;
                }
                case DRAGGED: {
                    sendMouseDragged(event);
                    break;
                }
                case ENTERED: {
                    sendMouseEntered(event);
                    break;
                }
                case EXITED: {
                    sendMouseExited(event);
                    break;
                }
                case WHEEL: {
                    sendMouseWheel(event);
                    break;
                }
                case RESIZED: {
                    sendResized(event);
                    break;
                }       
                case HOVERED: {
                    sendHovered(event);
                    break;
                }
                default:
                    System.err.println("Event requested that does not exist " + event.type); //$NON-NLS-1$
                }
            }catch(Throwable t){
                ProjectUIPlugin.log("", t); //$NON-NLS-1$
            }
        }
    }
    private DoubleClickAttempt next = null;

    private final Runnable checkDoubleClick=new Runnable(){

        public void run() {
            if( next==null )
                return;
            DoubleClickAttempt e=next;
            next=null;
            runEvent(e.first, e.release, e.third);
        }
        
    };
    /**
     * Waits to see if the next series of events is a double-click event.
     * 
     * @param event
     * @return
     * @return
     * @throws InterruptedException
     */
    private void tryForDoubleClick( Event event ) {
        if (next == null) {
            next=new DoubleClickAttempt();
            next.first=event;
            Display.getCurrent().timerExec(ProjectUIPlugin.getDefault().getDoubleClickSpeed(), checkDoubleClick);
        }else{
            if( next.release==null ){
                if( event.type==RELEASED ){

                    next.release=event;
                    return;
                }else{
                    Event first=next.first;
                    next=null;
                    runEvent(first, event);
                }
            }else{
                switch( event.type ) {
                case RELEASED:
                    if( next.third!=null ){
                        next=null;
                        sendDoubleClickEvent(new Event(DOUBLE_CLICK, event.data));
                        return;
                    }
                    cancelDoubleClickWait(event);
                    break;
                case PRESSED:
                    if ( next.release!=null && next.third==null ){
                        next.third=event;
                    }else{
                        cancelDoubleClickWait(event);
                    }
                    break;
                default:
                    cancelDoubleClickWait(event);
                    break;
                }
            }
        }
    }

    /**
     *
     * @param event
     */
    private void cancelDoubleClickWait( Event event ) {
        Event first = next.first;
        Event release = next.release;
        Event third = next.third;
        next=null;
        runEvent(first, release, third, event);
    }

    /**
     * @param event
     */
    private void sendResized( Event event ) {
        for( IMapDisplayListener l : this.paneListener ){
            try {
                l.sizeChanged((MapDisplayEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a resized event", t);//$NON-NLS-1$
            }
        }
    }

    /**
     * @param event
     */
    private void sendMouseWheel( Event event ) {
        for(MapMouseWheelListener l:this.wheel){
            try {
                l.mouseWheelMoved((MapMouseWheelEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a mouse wheel event", t);//$NON-NLS-1$
            }
        }
    }

    /**
     * @param event
     */
    private void sendMouseExited( Event event ) {
        for( MapMouseListener l : this.mouse ){
            try {
                l.mouseExited((MapMouseEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a moouse exited event", t);//$NON-NLS-1$
            }
        }
    }
    /**
     * @param event
     */
    private void sendHovered( Event event ) {
        for( MapMouseMotionListener l : this.motion ) {
            try {
                l.mouseHovered((MapMouseEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a mouse dragged event", t);//$NON-NLS-1$
            }            
        }
    }

    /**
     * @param event
     */
    private void sendMouseEntered( Event event ) {
        for( MapMouseListener l : this.mouse ){
            try {
                l.mouseEntered((MapMouseEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a mouse entered event", t);//$NON-NLS-1$
            }
        }
    }

    /**
     * @param event
     */
    private void sendMouseDragged( Event event ) {
        for( MapMouseMotionListener l : this.motion ) {
            try {
                l.mouseDragged((MapMouseEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a mouse dragged event", t);//$NON-NLS-1$
            }            
        }
    }

    /**
     * @param event
     */
    private void sendMouseMoved( Event event ) {
        for( MapMouseMotionListener l : this.motion ) {
            try {
                l.mouseMoved((MapMouseEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a mouse dragged event", t);//$NON-NLS-1$
            }            
        }
    }

    /**
     * @param event
     */
    private void sendMouseReleased( Event event ) {
        for( MapMouseListener l : this.mouse ){
            try {
                l.mouseReleased((MapMouseEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a mouse released event", t);//$NON-NLS-1$
            }
        }
    }

    /**
     * @param event
     */
    private void sendMousePressedEvent( Event event ) {
        for( MapMouseListener l : this.mouse ){
            try {
                l.mousePressed((MapMouseEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a mouse pressed event", t);//$NON-NLS-1$
            }
        }
    }

    /**
     * @param event
     */
    private void sendDoubleClickEvent( Event event ) {
        for( MapMouseListener l : this.mouse ){
            try {
                l.mouseDoubleClicked((MapMouseEvent) event.data);
            } catch (Throwable t) {
                ProjectUIPlugin.log("Error processing a double clicked event", t);//$NON-NLS-1$
            }
        }
    }

    /**
     * Adds a MapEditorListener
     * 
     * @param l the listener
     */
    public void addMapEditorListener( IMapDisplayListener l ) {
        paneListener.add(l);
    }
    /**
     * Removes a MapEditorListener Listener
     * 
     * @param l the listener
     */
    public void removeMapEditorListener( IMapDisplayListener l ) {
        paneListener.remove(l);
    }
    /**
     * Adds a MapMouseListener Listener
     * 
     * @param l the listener to add.
     */
    public void addMouseListener( MapMouseListener l ) {
        mouse.add(l);
    }
    /**
     * Adds a MapMouseMotionListener Listener
     * 
     * @param l the listener to add.
     */
    public void addMouseMotionListener( MapMouseMotionListener l ) {
        motion.add(l);

    }
    /**
     * Adds a MapMouseWheelListener Listener
     * 
     * @param l the listener to add.
     */
    public void addMouseWheelListener( MapMouseWheelListener l ) {
        wheel.add(l);
    }
    /**
     * Removes a MapMouseListener Listener
     * 
     * @param l the listener to remove.
     */
    public void removeMouseListener( MapMouseListener l ) {
        mouse.remove(l);
    }
    /**
     * Removes a MapMouseMotionListener Listener
     * 
     * @param l the listener to remove.
     */
    public void removeMouseMotionListener( MapMouseMotionListener l ) {
        motion.remove(l);
    }
    /**
     * Removes a MapMouseWheelListener Listener
     * 
     * @param l the listener to remove.
     */
    public void removeMouseWheelListener( MapMouseWheelListener l ) {
        wheel.remove(l);
    }
    
    static class DoubleClickAttempt{
        Event first;
        Event release;
        Event third;
    }
}
