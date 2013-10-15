/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.internal.commands.edit;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.EditManager;
import net.refractions.udig.project.internal.Map;

/**
 * TODO Purpose of net.refractions.udig.project.internal.commands.edit
 * <p>
 * </p>
 * 
 * @author Jesse
 * @since 1.0.0
 */
public abstract class AbstractLayerManagerControlCommand  {
    protected EditManager editManager;

    private Map map;


    /**
     * @see net.refractions.udig.project.command.MapCommand#setMap(IMap)
     * @uml.property name="map"
     */
    public void setMap( IMap map ) {
        this.map = (Map) map;
        editManager=this.map.getEditManagerInternal();
    }

    /**
     * @see net.refractions.udig.project.command.MapCommand#getMap()
     * @uml.property name="map"
     */
    public Map getMap() {
        return map;
    }

}
