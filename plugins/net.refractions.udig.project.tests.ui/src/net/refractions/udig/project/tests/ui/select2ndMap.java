/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.project.tests.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.impl.MapImpl;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ProjectExplorer;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class select2ndMap implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        List<MapImpl> maps = ApplicationGIS.getActiveProject().getElements(MapImpl.class);
        Set<Map> selection=new HashSet<Map>();
        selection.add(maps.get(1));
        ProjectExplorer.getProjectExplorer().setSelection(selection, true);
    }

}
