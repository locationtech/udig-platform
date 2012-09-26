/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.project.command.map;

import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;

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
            //TODO - Fix this
//            getMap().sendToBackLayer((Layer) layer);
        }
    }

    @Override
    public void rollback( IProgressMonitor monitor ) throws Exception {
        for( int i = 0; i < getSelection().size(); i++ ) {
            //TODO - Fix this
//            getMap().sendToIndexLayer((Layer) getSelection().get(i), getIndex().get(i));
        }
    }

}
