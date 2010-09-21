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
package net.refractions.udig.tools.edit.activator;

import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;

/**
 * Resets the state of the EditToolHandler.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ResetHandlerActivator implements Activator {

    public void activate( EditToolHandler handler ) {
        handler.setCurrentShape(null);
        handler.setCurrentState(EditState.NONE);
    }

    public void deactivate( EditToolHandler handler ) {
    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
    }

}
