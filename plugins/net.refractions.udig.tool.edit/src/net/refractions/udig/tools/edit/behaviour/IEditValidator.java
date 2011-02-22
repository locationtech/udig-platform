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
package net.refractions.udig.tools.edit.behaviour;

import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.EventType;

/**
 * A very general validator strategy that essentially returns true if the current state is
 * considered to be legal by the instance of the validator.
 *
 * @author Jesse
 * @since 1.1.0
 */
public interface IEditValidator {
    IEditValidator TRUE = new IEditValidator(){

        public String isValid( EditToolHandler handler, MapMouseEvent event, EventType type ) {
            return null;
        }

    };

    /**
     * Returns null if the validator considers the state to be "legal" for the new event or
     * a string which is the human readable message describing the problem.
     *
     * @param handler the handler to use for obtaining the state
     * @param event the event that just occurred.
     * @param type they type of event
     * @return null if the validator considers the state to be "legal" for the new event or
     * a string which is the human readable message describing the problem.

     */
    String isValid( EditToolHandler handler, MapMouseEvent event, EventType type);
}
