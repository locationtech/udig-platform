/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.activator;

import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditState;
import net.refractions.udig.tools.edit.EditToolHandler;

/**
 * Resets the state that has to do with Editing.  IE.  Current edit state and shape 
 * 
 * @author jones
 * @since 1.1.0
 */
public class ResetAllStateActivator implements Activator {

    public void activate( EditToolHandler handler ) {
        handler.setCurrentShape(null);
        handler.setCurrentState(EditState.NONE);
        handler.getEditBlackboard(handler.getEditLayer()).clear();
    }

    public void deactivate( EditToolHandler handler ) {
    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
    }

}
