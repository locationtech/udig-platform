/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit;

import java.util.LinkedList;
import java.util.List;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.PostDeterminedEffectCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.tool.edit.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Command used by (@link org.locationtech.udig.tools.edit.EditToolHandler} and 
 * {@link org.locationtech.udig.tools.edit.OrderedCompositeEventBehavior} for executing the 
 * behaviours in a tool.
 * 
 * @author jones
 * @since 1.1.0
 */
public class BehaviourCommand extends AbstractCommand implements PostDeterminedEffectCommand {

    private List<Behaviour> behaviours;
    private List<UndoableMapCommand> commandsRan=new LinkedList<UndoableMapCommand>();
    private EditToolHandler handler;

    public BehaviourCommand(List<Behaviour> behaviours, EditToolHandler handler) {
        this.behaviours=behaviours;
        this.handler=handler;
    }
    
    
    public boolean execute( IProgressMonitor monitor ) throws Exception {
        if( commandsRan.isEmpty() ){
            monitor.beginTask(getName(), commandsRan.size()*12);
            monitor.worked(2);
            for( Behaviour behaviour : behaviours ) {
                monitor.beginTask(getName(), behaviours.size()*12);
                monitor.worked(2);
                if( behaviour.isValid(handler)){
                    UndoableMapCommand c=null;
                    try{
                        c=behaviour.getCommand(handler);
                        if( c==null )
                            continue;
                        c.setMap(getMap());
                        IProgressMonitor submonitor = new SubProgressMonitor(monitor, 10);
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
                    	EditPlugin.trace( e.getClass().getName()+" executing "+c+":"+e.getMessage(), e);
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
        return !commandsRan.isEmpty();
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
        for(int i = commandsRan.size()-1; i>=0; i--)
        {
            UndoableMapCommand command = commandsRan.get(i);
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
