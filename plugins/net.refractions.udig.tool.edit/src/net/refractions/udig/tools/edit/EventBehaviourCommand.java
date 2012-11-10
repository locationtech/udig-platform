/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit;

import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.PostDeterminedEffectCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tool.edit.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Command used by (@link net.refractions.udig.tools.edit.EditToolHandler} and 
 * {@link net.refractions.udig.tools.edit.OrderedCompositeEventBehavior} for executing the 
 * behaviours in a tool.
 * 
 * @author jones
 * @since 1.1.0
 */
public class EventBehaviourCommand extends AbstractCommand implements PostDeterminedEffectCommand {

    private List<EventBehaviour> behaviours;
    private List<UndoableMapCommand> commandsRan=new LinkedList<UndoableMapCommand>();
    private EditToolHandler handler;
    private MapMouseEvent event;
    private EventType eventType;

    public EventBehaviourCommand(List<EventBehaviour> behaviours, EditToolHandler handler,
            MapMouseEvent event, EventType eventType) {
        this.behaviours=behaviours;
        this.handler=handler;
        this.event=event;
        this.eventType=eventType;
    }
    
    public boolean execute( IProgressMonitor monitor ) throws Exception {
        if( commandsRan.isEmpty() ){
            monitor.beginTask(getName(), commandsRan.size()*12);
            monitor.worked(2);
            for( EventBehaviour behaviour : behaviours ) {

                if( canUnlock(behaviour) && behaviour.isValid(handler, event, eventType)){
                    UndoableMapCommand c=null;
                    try{
                        c=behaviour.getCommand(handler, event, eventType);
                        if( c==null )
                            continue;
                        IProgressMonitor submonitor = new SubProgressMonitor(monitor, 10);
                        c.setMap(getMap());
                        if (c instanceof PostDeterminedEffectCommand) {
                            PostDeterminedEffectCommand command = (PostDeterminedEffectCommand) c;
                            if( command.execute(submonitor) )
                                commandsRan.add(command);
                        }else{
                        c.run(submonitor);
                        commandsRan.add(c);
                        }
                        submonitor.done();
                    }catch(Exception e){
                        behaviour.handleError(handler, e, c);
                    }
                }
            }
        }else{
            monitor.beginTask(getName(), commandsRan.size()*12);
            monitor.worked(2);
            for( UndoableMapCommand command : commandsRan ) {
                command.setMap(getMap());
                IProgressMonitor submonitor = new SubProgressMonitor(monitor, 10);
                if (command instanceof PostDeterminedEffectCommand) {
                    ((PostDeterminedEffectCommand)command).execute(submonitor);
                }else{
                    command.run(submonitor);
                }
                submonitor.done();
            }
        }
        monitor.done();
        return !commandsRan.isEmpty();
    }

    /**
     * Returns true if the handler is unlocked or the behaviour has the correct key.
     * 
     * @param behaviour trying to run
     * @return Returns true if the handler is unlocked or the behaviour has the correct key.
     */
    private boolean canUnlock( EventBehaviour behaviour ) {
        if( !handler.isLocked() )
            return true;
        if (behaviour instanceof LockingBehaviour) {
            LockingBehaviour locker = (LockingBehaviour) behaviour;

            EditPlugin.trace(EditPlugin.HANDLER_LOCK, "Can unlock: "+(handler.behaviourLock==locker.getKey(handler)), null); //$NON-NLS-1$
            return handler.behaviourLock==locker.getKey(handler);
        }
        return false;
    }

    String name=Messages.EventBehaviourCommand_name;

    public String getName() {
        return name;
    }
    
    public void setName( String name ) {
        this.name = name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(getName(), commandsRan.size()*12);
        monitor.worked(2);
        for( UndoableMapCommand command : commandsRan ) {
            command.setMap(getMap());
            IProgressMonitor submonitor = new SubProgressMonitor(monitor, 10);
            command.rollback(submonitor);
            submonitor.done();
        }
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        throw new UnsupportedOperationException("PostDeterminedEffectCommands do not use the run method"); //$NON-NLS-1$
    }
}
