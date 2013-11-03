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
 * A listener interested in being notified of when the layers in a map are changed.
 * Events are fired when the layers of the Map change: added, removed or reordered.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IMapCompositionListener {

    /**
     * Called when an event occurs.
     * 
     * @param event The event.
     */
    public void changed( MapCompositionEvent event );
}
