/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.printing.ui.internal;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.printing.model.Page;
import org.locationtech.udig.printing.ui.internal.editor.PageEditorInput;
import org.locationtech.udig.project.ui.UDIGEditorInput;

/**
 * creates a pdf file from the printing page.
 * 
 * @author Richard Gould
 * @author Andrea Antonello (www.hydrologis.com)
 * @author Frank Gasdorf
 */
public class PrintAction extends Action implements IEditorActionDelegate {

    public PrintAction() {
        super();
    }

    public void run() {
        Page page = null;
        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        UDIGEditorInput editorInput = (UDIGEditorInput) workbenchWindow.getActivePage()
                .getActiveEditor().getEditorInput();
        if (editorInput instanceof PageEditorInput) {
            page = (Page) ((PageEditorInput) editorInput).getProjectElement();
        }
        if (page == null) {
            throw new RuntimeException(Messages.PrintAction_pageError);
        }

        FileDialog fileDialog = new FileDialog(workbenchWindow.getShell(), SWT.SAVE);
        fileDialog.setOverwrite(true);
        String path = fileDialog.open();

        File outFile = null;
        if (path == null || path.length() < 1) {
            return;
        } else {
            if (!path.endsWith(".pdf") && !path.endsWith(".PDF")) {
                path = path + ".pdf";
            }
            outFile = new File(path);
        }

        // Workaround for Windows systems to check, if there is a lock
        boolean fileIsLocked = !outFile.renameTo(outFile);
        
        if (fileIsLocked) {
            MessageDialog.open(MessageDialog.ERROR, workbenchWindow.getShell(), Messages.PrintAction_errorDialogTitle, 
                    MessageFormat.format(Messages.PrintAction_errorDialogLockMessage, outFile.getAbsolutePath()), SWT.NONE);
            return;
        }

        
        // copy the page before modifying it
        final Page copy = (Page) EcoreUtil.copy((EObject) page);
        final PdfPrintingEngine engine = new PdfPrintingEngine(copy, outFile);

        Job job = new Job(Messages.PrintAction_jobTitle){
            protected IStatus run( IProgressMonitor monitor ) {

                engine.setMonitor(monitor);

                boolean printToPdf = engine.printToPdf();
                if (printToPdf) {
                    return Status.OK_STATUS;
                } else {
                    return Status.CANCEL_STATUS;
                }
            }
        };

        if (job.isSystem())
            job.setSystem(false);

        job.schedule();
    }

    /**
     * TODO summary sentence for setActiveEditor ...
     * 
     * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
     * @param action
     * @param targetEditor
     */
    public void setActiveEditor( IAction action, IEditorPart targetEditor ) {
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @param action
     */
    public void run( IAction action ) {
        run();
    }

    /**
     * TODO summary sentence for selectionChanged ...
     * 
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     * @param action
     * @param selection
     */
    public void selectionChanged( IAction action, ISelection selection ) {
    }
}
