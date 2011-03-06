/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.project.ui.operations;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.commands.ChangeCRSCommand;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class SyncProjection implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        ILayer layer = (ILayer) target;
        IMap map = layer.getMap();
        
        final CoordinateReferenceSystem after = layer.getCRS();
        
        MapCommand changeProjection = new ChangeCRSCommand(after);
        map.sendCommandASync( changeProjection );
        
    }

}
