/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.locationtech.udig.project.internal.Folder;

/**
 * Action that processes the renaming of folder items in the LegendView.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LegendViewRenameFolderAction extends Action implements ISelectionChangedListener {

    private IStructuredSelection selection;

    private Viewer viewer;

    public LegendViewRenameFolderAction(Viewer viewerSource) {
        this.viewer = viewerSource;
        this.viewer.addSelectionChangedListener(this);

        this.setText(Messages.RenameFolderAction_label);
        this.setEnabled(true);
    }

    @Override
    public void run() {
        if (LegendViewUtils.isFolderSelected(selection)) {
            processRenameFolder();
        }
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        ISelection rawSelection = event.getSelection();
        if (rawSelection instanceof IStructuredSelection) {
            selection = (IStructuredSelection) rawSelection;
        }

    }

    /**
     * Opens a dialog to get user input for folder name and renames folder accordingly. Then
     * refreshes viewer to reflect change.
     */
    private void processRenameFolder() {

        final Runnable runnable = new Runnable() {
            public void run() {
                final Folder folder = (Folder) selection.getFirstElement();
                final InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(),
                        Messages.RenameFolderAction_dialogTitle,
                        Messages.RenameFolderAction_dialogMsg, folder.getName(), null);
                final int folderNameDialogResult = dialog.open();
                if (folderNameDialogResult == Dialog.OK) {
                    folder.setName(dialog.getValue());
                }
                viewer.refresh();
            }
        };

        if (Display.getCurrent() == null) {
            Display.getDefault().syncExec(runnable);
        } else {
            runnable.run();
        }

    }

}
