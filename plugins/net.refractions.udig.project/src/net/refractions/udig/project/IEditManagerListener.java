/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
