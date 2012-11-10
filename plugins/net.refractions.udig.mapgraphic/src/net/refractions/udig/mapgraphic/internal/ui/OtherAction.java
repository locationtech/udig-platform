/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.mapgraphic.internal.ui;


import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.mapgraphic.MapGraphicChooserDialog;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Opens a dialog with all the MapGraphics
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class OtherAction implements IWorkbenchWindowActionDelegate {

    public void init( IWorkbenchWindow window ) {
    }

    public void run( IAction action ) {
        MapGraphicChooserDialog d = new MapGraphicChooserDialog(Display.getCurrent().getActiveShell(), true);
        d.open();
        
        List<IGeoResource> resources = new ArrayList<IGeoResource>();
        for( IGeoResource geoResource : d.getSelectedResources() ) {
            resources.add(geoResource);
        }
        ApplicationGIS.addLayersToMap(null, resources, -1);


    }

    public void selectionChanged( IAction action, ISelection selection ) {
    }

    public void dispose() {
    }
    
}
