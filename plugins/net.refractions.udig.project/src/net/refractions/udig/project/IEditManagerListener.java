/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project;

/**
 * A listener that will be notified when changes to the IEditManager occur.
 * 
 * @author jeichar
 * @since 0.9.0
 */
public interface IEditManagerListener {

    /**
     * Called when an event occurs.
     * 
     * @param event The event.
     */
    public void changed( EditManagerEvent event );
}
