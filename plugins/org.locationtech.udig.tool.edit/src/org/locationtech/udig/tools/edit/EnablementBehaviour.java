/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit;

import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;

/**
 * A behaviour that is responsible for determining whether a tool is valid for a given event
 *  
 * @author Jesse
 * @since 1.1.0
 */
public interface EnablementBehaviour {
    /**
     * If method returns a String then no other behaviours will be executed and the string will be used as the status bar's Error message.  
     * If null is returned then the te rest of the behaviours can be ran and the error tate will be cleared.
     *
     * @param handler {@link EditToolHandler} for the current tool
     * @param e the event that just occurred
     * @param eventType the type of event
     * @return If String then no other behaviours will be executed and the string will be used as the status bar's Error message.  
     * If null is returned then the the rest of the behaviours can be ran and the error tate will be cleared.
     */
    String isEnabled(EditToolHandler handler, MapMouseEvent e, EventType eventType );
}
