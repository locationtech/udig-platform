/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.behaviour;

import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import net.refractions.udig.core.IProvider;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseWheelEvent;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventBehaviour;
import net.refractions.udig.tools.edit.EventType;
import net.refractions.udig.tools.edit.commands.DrawSnapAreaCommand;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.Point;

/**
 * <p>
 * Requirements:
 * <ul>
 * <li>EventType==WHEEL</li>
 * <li>Modifier is ALT</li>
 * </ul>
 * </p>
 * <p>
 * Action:
 * <ul>
 * <li>Increases or decreases size of the snap shape depending on number of clicks.</li>
 * </ul>
 * </p>
 * Note: This is not undoable.
 * 
 * @author jones
 * @since 1.1.0
 */
public class SetSnapSizeBehaviour implements EventBehaviour {

    private DrawSnapAreaCommand command;

    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {

        boolean onlyAltDown = e.isAltDown()
                && (e.modifiers & MapMouseEvent.ALT_DOWN_MASK) == MapMouseEvent.ALT_DOWN_MASK;
        return onlyAltDown && eventType == EventType.WHEEL;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, final MapMouseEvent e,
            EventType eventType ) {
        Rectangle oldBounds=null;
        synchronized (this) {
            if( command!=null ){
                oldBounds=command.getValidArea();
            }
            if (command == null) {
                class PointProvider implements IProvider<Point>{

                    public Point get(Object... params) {
                        return Point.valueOf(e.x, e.y);
                    }
                    
                }
                command = new DrawSnapAreaCommand(new PointProvider());
                handler.getContext().sendSyncCommand(command);
            }
            if (task != null)
                task.cancel();

            task = new Deactivator(handler);
            timer.schedule(task, 1000);
        }

        int oldRadius = PreferenceUtil.instance().getSnappingRadius();
        if( !(e instanceof MapMouseWheelEvent) )
                throw new RuntimeException("Expected a MapMouseWheelEvent but got a: "+e.getClass().getName()); //$NON-NLS-1$
        MapMouseWheelEvent event = (MapMouseWheelEvent) e;
        int i = oldRadius + event.clickCount;
        if( i<0 )
            i=0;
        PreferenceUtil.instance().setSnappingRadius(i);
        Rectangle bounds;
        synchronized (this) {
            bounds = command.getValidArea();
        }
        if( oldBounds!=null ) 
            bounds.createUnion(oldBounds);
        
        handler.getContext().getViewportPane().repaint(bounds.x, bounds.y, bounds.width, bounds.height);
        return null;
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    Timer timer = new Timer();
    Deactivator task;
    class Deactivator extends TimerTask {

        private EditToolHandler handler;

        public Deactivator( EditToolHandler handler ) {
            this.handler=handler;
        }

        @Override
        public void run() {
            synchronized (SetSnapSizeBehaviour.this) {
                task = null;
                command.setValid(false);
                command = null;
                handler.repaint();
                handler.getContext().getViewportPane().repaint();
            }
        }

    }

}
