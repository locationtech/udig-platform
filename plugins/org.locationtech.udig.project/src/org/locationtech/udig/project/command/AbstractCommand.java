/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command;

import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;

/**
 * TODO Purpose of org.locationtech.udig.project.command
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public abstract class AbstractCommand implements MapCommand {

    private Map map;

    /**
     * @see org.locationtech.udig.project.command.MapCommand#setMap(IMap)
     * @uml.property name="map"
     */
    public void setMap( IMap map2 ) {
        this.map = (Map) map2;
    }

    /**
     * @see org.locationtech.udig.project.command.MapCommand#getMap()
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
