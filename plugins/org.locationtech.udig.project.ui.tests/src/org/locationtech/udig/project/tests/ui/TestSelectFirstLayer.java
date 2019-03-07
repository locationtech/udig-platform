/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.tests.ui;

import java.util.List;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.EditManager;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.internal.LayersView;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.junit.Ignore;

/**
 * Selects the first layer in the map
 * @author Jesse
 * @since 1.1.0
 */
@Ignore
public class TestSelectFirstLayer extends ActionDelegate implements IWorkbenchWindowActionDelegate {

    @Override
    public void run( IAction action ) {
        IMap map = ApplicationGIS.getActiveMap();
        
        List<ILayer> layers = map.getMapLayers();
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        activePage.activate(activePage.findView(LayersView.ID));
        ((EditManager)map.getEditManager()).setSelectedLayer((Layer) layers.get(layers.size()-1));
    }
    
    public void init( IWorkbenchWindow window ) {
    }

}
