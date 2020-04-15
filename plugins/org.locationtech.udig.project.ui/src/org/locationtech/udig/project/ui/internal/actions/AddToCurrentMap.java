/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.actions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ui.ApplicationGIS;

/**
 * This class is a ActionDelegate for AbstractContext menus with RegistryEntry objects When the
 * runWithEvent call is made (by eclipse) wizards should open and allow the use to add the selected
 * items to the Currently selected map.
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class AddToCurrentMap implements IObjectActionDelegate {
    IStructuredSelection current;

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @SuppressWarnings("unchecked")
    public void run( IAction action ) {
        Collection<IGeoResource> resources = AddToNewMap.getResources(current.toList());
        ApplicationGIS.addLayersToMap(ApplicationGIS.getActiveMap(), new ArrayList<IGeoResource>(resources), -1);
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection ) {
        if (!selection.isEmpty()) {
            if (selection instanceof IStructuredSelection) {
                current = (IStructuredSelection) selection;
            }
        }
    }

    /**
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart( IAction action, IWorkbenchPart targetPart ) {
    }
    
}
