/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project;

/**
 * A listener that is notified when events occur to the map.
 * @author Jesse
 * @since 1.1.0
 */
public interface IMapListener {

    /**
     * Called when a event occurs.
     *
     * @param event
     */
    public void changed( MapEvent event );
}
