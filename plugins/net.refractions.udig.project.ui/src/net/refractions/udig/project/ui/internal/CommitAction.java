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
package net.refractions.udig.project.ui.internal;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p>
 * <p>
 * Example Use:
 *
 * <pre><code>
 *  CommitAction x = new CommitAction( ... );
 *  TODO code example
 * </code></pre>
 *
 * </p>
 *
 * @author jones
 * @since 0.3
 */
public class CommitAction implements IEditorActionDelegate {

    private MapPart editor;

    /**
     * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction,
     *      org.eclipse.ui.IEditorPart)
     */
    public void setActiveEditor( IAction action, IEditorPart targetEditor ) {
        editor = (MapPart) targetEditor;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run( IAction action ) {
        try {
            editor.getMap().getEditManagerInternal().commitTransaction();
        } catch (IOException e) {
            // Shouldn't happen but...
            ProjectUIPlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR,
                            "net.refractions.udig.project", 0, "Error commiting transaction", e)); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged( IAction action, ISelection selection ) {
        // do nothing
    }

}
