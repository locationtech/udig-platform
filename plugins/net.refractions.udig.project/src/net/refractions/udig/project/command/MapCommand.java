/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;

/**
 * A command specific to modifying the state of a map.
 *
 * @author  Jesse
 * @since   1.0.0
 */
public interface MapCommand extends Command{

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
