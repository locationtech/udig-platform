/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.project.command.map;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Command moves the layer to the last of the rendering order. In result the layer will be at the
 * front of all other layers.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LayerMoveFrontCommand extends AbstractLayerMoveCommand {
    
    public LayerMoveFrontCommand( Map map, ILayer layer ) {
        super(map, layer);
    }

    public LayerMoveFrontCommand( Map map, IStructuredSelection structuredSelection ) {
        super(map, structuredSelection);
    }

    public LayerMoveFrontCommand( Map map, List<ILayer> selection ) {
        super(map, selection);
    }

    @Override
    public String getName() {
        return "Move to Front"; //$NON-NLS-1$
    }
    
    @Override
    public void run( IProgressMonitor monitor ) throws Exception {
        for( int i = getSelection().size() -1; i >= 0; i-- ) {
            getMap().sendToFrontLayer((Layer) getSelection().get(i));
        }
    }

    @Override
    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( int i = getSelection().size() - 1; i >= 0; i-- ) {
            final int index = getIndex().get(i);
            getMap().sendToIndexLayer((Layer) getSelection().get(i), index);
        }
    }

}
