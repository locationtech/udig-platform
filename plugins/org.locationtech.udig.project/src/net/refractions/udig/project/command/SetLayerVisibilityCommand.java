/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.command;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Set the visiblity of the layer
 * @author Jesse
 * @since 1.1.0
 */
public class SetLayerVisibilityCommand extends AbstractCommand implements UndoableMapCommand {

    private final ILayer layer;
    private final boolean newValue;
    private boolean oldvalue;

    public SetLayerVisibilityCommand( final ILayer layer, final boolean newValue ) {
        this.layer = layer;
        this.newValue = newValue;
    }

    public String getName() {
        return "Set Layer Visibility";
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        this.oldvalue=layer.isVisible();
        ((Layer)layer).setVisible(newValue);
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        ((Layer)layer).setVisible(oldvalue);
    }


}
