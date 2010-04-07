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
package net.refractions.udig.printing.ui.internal;

import java.io.File;

import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.internal.editor.PageEditorInput;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.ui.UDIGEditorInput;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Richard Gould
 * @author Andrea Antonello (www.hydrologis.com)
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

        // copy the page before hacking on it
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
