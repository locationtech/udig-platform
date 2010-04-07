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
