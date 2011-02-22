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
package net.refractions.udig.project.ui.internal.actions;

import java.util.Iterator;

import net.refractions.udig.project.IProject;
import net.refractions.udig.project.IProjectElement;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.ProjectExplorer;
import net.refractions.udig.project.ui.internal.UDIGEditorInputDescriptor;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Closes the selected projects
 *
 * @author jeichar
 * @since 0.6.0
 */
public class CloseProject implements IWorkbenchWindowActionDelegate {
    private IStructuredSelection selection;

    /**
     * @see org.eclipse.ui.actions.ActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action ) {
        for( Iterator iter = selection.iterator(); iter.hasNext(); ) {
            IProject project = (IProject) iter.next();
            for( IProjectElement element : project.getElements() ) {
                for( UDIGEditorInputDescriptor desc : ApplicationGIS.getEditorInputs(element) ) {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                            .getActivePage();
                    IEditorPart editor = page.findEditor(desc.createInput(element));
                    if (editor != null)
                        page.closeEditor(editor, false);
                }
            }
            ProjectExplorer explorer = ProjectExplorer.getProjectExplorer();
            explorer.collapseToLevel(project, 1);
        }
    }

    /**
     * @see org.eclipse.ui.actions.ActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection ) {
        if (selection instanceof IStructuredSelection)
            this.selection = (IStructuredSelection) selection;
        else
            this.selection = new StructuredSelection();
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init( IWorkbenchWindow window ) {
    }
}
