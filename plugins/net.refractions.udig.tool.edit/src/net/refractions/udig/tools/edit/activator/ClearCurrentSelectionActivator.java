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
import net.refractions.udig.tools.edit.ClearSelection;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditUtils;

/**
 * Activator that clear the current selection on the editblackboard and cancel its hide status.
 * 
 * @author Aritz Dávila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 */
public class ClearCurrentSelectionActivator implements Activator {

    public void activate( EditToolHandler handler ) {

        ClearSelection clear = new ClearSelection(handler);
        clear.run();

        handler.getContext().sendASyncCommand(
                handler.getContext().getEditFactory().createSetEditFeatureCommand(null,
                        handler.getEditLayer()));
        EditUtils.instance.cancelHideSelection(handler.getEditLayer());
    }

    public void deactivate( EditToolHandler handler ) {
        // do nothing.

    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("Error creating and sending command", error); //$NON-NLS-1$

    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("Error invalidating command", error); //$NON-NLS-1$

    }

}
