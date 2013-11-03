/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour;

import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.EventType;

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
