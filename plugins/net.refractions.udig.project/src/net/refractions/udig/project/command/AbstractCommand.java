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
 * TODO Purpose of net.refractions.udig.project.command
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public abstract class AbstractCommand implements MapCommand {

    private Map map;

    /**
     * @see net.refractions.udig.project.command.MapCommand#setMap(IMap)
     * @uml.property name="map"
     */
    public void setMap( IMap map2 ) {
        this.map = (Map) map2;
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getMap()
     * @uml.property name="map"
     */
    public Map getMap() {
        return map;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    public Command copy() {
        return null;
    }

}
