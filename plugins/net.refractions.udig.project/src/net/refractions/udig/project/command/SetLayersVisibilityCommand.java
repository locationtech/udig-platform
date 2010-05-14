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

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Set the visiblity of the layer
 * @author Jody
 * @since 1.2.0
 */
public class SetLayersVisibilityCommand extends AbstractCommand implements UndoableMapCommand {

    private final List<ILayer> layers;
    private final boolean newValue;
    private List<Boolean> oldvalues;

    public SetLayersVisibilityCommand( final List<ILayer> list, final boolean isVisible ) {
        this.layers = list;
        this.newValue = isVisible;
    }

    public String getName() {
        return "Set Layer Visibility";
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        if( layers.isEmpty() ){
            return;
        }
        if( oldvalues == null ){
            oldvalues = new ArrayList<Boolean>( layers.size() );
            for( ILayer layer : layers ){
                oldvalues.add( layer.isVisible() );
            }
        }
        for( ILayer layer : layers ){
            ((Layer)layer).setVisible(  newValue );
        }
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( layers.isEmpty() ){
            return;
        }
        for( int i=0; i<layers.size();i++){
            Layer layer = (Layer)layers.get(i);
            layer.setVisible( oldvalues.get(i));
        }
        oldvalues = null;
    }
}
