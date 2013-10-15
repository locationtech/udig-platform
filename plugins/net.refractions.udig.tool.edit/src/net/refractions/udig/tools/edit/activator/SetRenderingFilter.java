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

import net.refractions.udig.project.ILayer;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditUtils;

/**
 * 
 * This hides the selected features on the current layer.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class SetRenderingFilter implements Activator {

    public void activate( EditToolHandler handler ) {
        ILayer selectedLayer = handler.getEditLayer();
        EditUtils.instance.hideSelectedFeatures(handler, selectedLayer);

    }

    public void deactivate( EditToolHandler handler ) {
        EditUtils.instance.cancelHideSelection(handler.getEditLayer());
    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

}
