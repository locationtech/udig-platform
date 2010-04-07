/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * A collection of Commands that are done as one command.
 * See Gang of Four composite pattern.  
 * @author   Jesse
 * @since   1.0.0
 */
public class CompositeCommand implements MapCommand {

    /** Treated as a List<Command> by subclasses */
    protected List<MapCommand> commands = new CopyOnWriteArrayList<MapCommand>();
    
    /** Treated as a List<Command> by subclasses */
    protected List<MapCommand> finalizerCommands = new CopyOnWriteArrayList<MapCommand>();

	private Map map;

    private String name;

	/**
	 * Creates a new instance of CompositeCommand
	 * 
	 * @param commands A list of commands to execute.
	 */
	@SuppressWarnings("unchecked")
    public CompositeCommand(List commands) {
		this.commands.addAll( commands );
	}

    public CompositeCommand(){  super();  }
	/**
	 * @see net.refractions.udig.project.internal.command.MapCommand#run()
	 */
	public void run(IProgressMonitor monitor) throws Exception {
        monitor.beginTask(getName(), 2+10*commands.size()+10*finalizerCommands.size());
        monitor.worked(2);
        try{
		for (MapCommand command : commands) {
            
			SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 10);
            command.setMap(getMap());
            command.run(subProgressMonitor);
            subProgressMonitor.done();
		}
        }finally{
            for( MapCommand c : finalizerCommands ) {
                try{
                    SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 10);
                    c.setMap(getMap());
                    c.run(subProgressMonitor);
                    subProgressMonitor.done();
                }catch( Exception e){
                    ProjectPlugin.log("", e); //$NON-NLS-1$
                }
            }
        }
        monitor.done();

	}

    
	/**
	 * @see net.refractions.udig.project.internal.command.MapCommand#copy()
	 */
	public MapCommand copy() {
		return new CompositeCommand(new LinkedList<MapCommand>(commands));
	}

	/**
	 * @see net.refractions.udig.project.command.MapCommand#getName()
	 */
	public String getName() {
        if( name==null )
            return toString();
		return name;
	}

    public void setName( String newName) {
        name=newName;
    }

    
	/**
     * @see net.refractions.udig.project.command.MapCommand#setMap(IMap)
     * @uml.property   name="map"
     */
	public void setMap(IMap map) {
		this.map = (Map) map;
        for( MapCommand command : commands ) {
            command.setMap(map);
        }
	}

	/**
     * @see net.refractions.udig.project.command.MapCommand#getMap()
     * @uml.property   name="map"
     */
	public Map getMap() {
		return map;
	}
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("["); //$NON-NLS-1$

        for (MapCommand command : commands)
            buffer.append(command.getName() + ","); //$NON-NLS-1$
        buffer.replace(buffer.length() - 1, buffer.length(), "]"); //$NON-NLS-1$
        return buffer.toString();
    }

    /**
     * @return Returns the commands.
     */
    public List<MapCommand> getCommands() {
        return this.commands;
    }
    
    /**
     * @return Returns the commands that will always be run at the end of the command.
     */
    public List<MapCommand> getFinalizerCommands() {
        return this.finalizerCommands;
    }

    /**
     * Adds a command to the list of commands that will be run when this command is executed
     *
     * @param command
     */
    public void addCommand( MapCommand command ){
        commands.add(command);
    }

    /**
     * Adds a command to the list of finalizer commands that will be run when this command is executed
     *
     * @param command
     */
    public void addFinalizerCommand( MapCommand command ){
        finalizerCommands.add(command);
    }
    
}
