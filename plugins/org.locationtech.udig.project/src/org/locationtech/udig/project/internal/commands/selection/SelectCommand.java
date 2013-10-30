/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.commands.selection;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;

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
