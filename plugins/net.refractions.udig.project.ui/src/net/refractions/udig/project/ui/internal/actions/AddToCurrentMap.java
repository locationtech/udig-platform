/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.actions;

import java.util.ArrayList;
import java.util.Collection;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

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