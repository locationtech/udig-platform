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
