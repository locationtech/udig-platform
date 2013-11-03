/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command.map;

import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Command moves the layer to the first of the rendering order. In result the layer will be at the
 * back of all other layers.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LayerMoveBackCommand extends AbstractLayerMoveCommand {
    
    public LayerMoveBackCommand( Map map, ILayer layer ) {
        super(map, layer);
    }

    public LayerMoveBackCommand( Map map, IStructuredSelection structuredSelection ) {
        super(map, structuredSelection);
    }

    public LayerMoveBackCommand( Map map, List<ILayer> selection ) {
        super(map, selection);
    }

    @Override
    public String getName() {
        return "Move to Back"; //$NON-NLS-1$
    }
    
    @Override
    public void run( IProgressMonitor monitor ) throws Exception {
        for( ILayer layer : getSelection() ) {
            getMap().sendToBackLayer((Layer) layer);
        }
    }

    @Override
    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( int i = 0; i < getSelection().size(); i++ ) {
            getMap().sendToIndexLayer((Layer) getSelection().get(i), getIndex().get(i));
        }
    }

}
