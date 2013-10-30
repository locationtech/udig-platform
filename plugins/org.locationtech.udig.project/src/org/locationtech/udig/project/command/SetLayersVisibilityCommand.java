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
