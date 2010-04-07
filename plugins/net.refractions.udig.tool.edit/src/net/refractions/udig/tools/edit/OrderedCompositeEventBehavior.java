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

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;

/**
 * A Composite Mode (See GOF Composite Pattern) where contained modes are ordered and are executed in order.
 * Each mode can see the state changes affected by the prior mode.
 * 
 * @author jones
 * @since 1.1.0
 */
public class OrderedCompositeEventBehavior implements EventBehaviour, LockingBehaviour {
private List<EventBehaviour> modes=new ArrayList<EventBehaviour>();
private boolean processAsCommand;
    
    public OrderedCompositeEventBehavior(List<EventBehaviour> modes, boolean processAsCommand) {
        this.modes=modes;
        this.processAsCommand=processAsCommand;
    }
    
    public boolean isValid( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        if( processAsCommand )
            return true;
        
        for( EventBehaviour mode : modes ) {
            if( mode.isValid(handler,e, eventType) ){
                return true;
            }
        }
        return false;
    }

    public UndoableMapCommand getCommand( EditToolHandler handler, MapMouseEvent e, EventType eventType ) {
        if( processAsCommand )
            return new EventBehaviourCommand(modes, handler, e, eventType);
        
        UndoableComposite command=new UndoableComposite();
        for( EventBehaviour mode : modes ) {
            if( mode.isValid(handler,e, eventType) ){
                UndoableMapCommand command2 = mode.getCommand(handler, e, eventType);
                if( command2!=null )
                    command.getCommands().add(command2);
            }
        }
        return command;
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("Very Strange I don't know how this happenned...", error); //$NON-NLS-1$
    }

    public Object getKey(EditToolHandler handler) {
        for( EventBehaviour behaviour : modes ) {
            if (behaviour instanceof LockingBehaviour) {
                LockingBehaviour locker = (LockingBehaviour) behaviour;
                if( handler.isLockOwner(locker) )
                    return locker.getKey(handler);
            }
        }
        return NULL_KEY;
    }
    private static final Object NULL_KEY=new Object();
    
    @Override
    public String toString() {
        return this.modes.toString();
    }
}
