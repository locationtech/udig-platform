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
package net.refractions.udig.project.internal.commands.selection;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.filter.Filter;

/**
 * Set an arbitrary selection filter on a layer.
 *
 * @author chorner
 * @since 1.0.1
 */
public class SelectCommand extends AbstractCommand implements UndoableMapCommand {
    private ILayer layer;
    private Filter newfilter;
    private Filter oldfilter;

    public SelectCommand( ILayer layer, Filter filter ) {
        this.layer = layer;
        newfilter = filter;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        Layer target = (Layer) layer;
        oldfilter = target.getFilter();
        target.setFilter(newfilter);
    }

    public String getName() {
        return Messages.SelectCommand_name + layer.getName(); 
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        Layer target = (Layer) layer;
        target.setFilter(oldfilter);
    }

}
