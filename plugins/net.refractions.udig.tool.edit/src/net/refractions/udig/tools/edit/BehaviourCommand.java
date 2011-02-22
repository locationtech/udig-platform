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

import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.PostDeterminedEffectCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
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
