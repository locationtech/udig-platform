/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.internal.impl.MapImpl;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.ProjectExplorer;
import org.locationtech.udig.ui.operations.IOp;

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
