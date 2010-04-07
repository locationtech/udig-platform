/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;

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
