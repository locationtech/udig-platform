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
