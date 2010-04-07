/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command;

import org.eclipse.core.runtime.IProgressMonitor;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;

/**
 * A command specific to modifying the state of a map.
 * <p>
 * This is the only way to get write access to a Map. You need to submit your command
 * to the Map (and the framework will carefully schedule the update in between screen
 * redraws etc...).
 * </p>
 * Example:<pre></code>
 * map.sendCommandSync( new AbstractCommand(){
 *  public void run( IProgressMonitor monitor ) throws Exception {
 *      // us getMap()
 *      getMap().getContextModel().lowerLayer(layer);
 *  }
 * });
 * </code></pre>
 * @author  Jesse
 * @since   1.0.0
 */
public interface MapCommand extends Command {

	/**
     * Called when before the command is executed. API mutable?throw unsupportedexception?
     * @param map   The map executing the command.
     * @uml.property   name="map"
     */
	public void setMap(IMap map);

	/**
	 * Returns the map if called during execute (or undo command is an undoable command)
	 * 
	 * API mutable?
	 * 
	 * @return the map if called during execute (or undo command is an undoable command)
	 */
	public Map getMap();
}
