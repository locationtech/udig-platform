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

import java.awt.print.PrinterJob;

import net.refractions.udig.printing.model.Page;
import net.refractions.udig.printing.ui.internal.editor.PageEditorInput;
import net.refractions.udig.project.ui.UDIGEditorInput;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * Provides ...TODO summary sentence
 * <p>
 * TODO Description
 * </p><p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p><p>
 * Example Use:<pre><code>
 * PrintAction x = new PrintAction( ... );
 * TODO code example
 * </code></pre>
 * </p>
 * @author Richard Gould
 * @since 0.3
 */
public class PrintAction extends Action implements IEditorActionDelegate {

    public PrintAction (){
        super();
    }

    public void run() {
    	Page page = null;
        UDIGEditorInput editorInput = (UDIGEditorInput) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
        if (editorInput instanceof PageEditorInput) {
            page = (Page) ((PageEditorInput) editorInput).getProjectElement();
        }
        if (page == null) {
            throw new RuntimeException(Messages.PrintAction_pageError);
        }

        final String jobName = page.getName();

        final PrintingEngine engine = new PrintingEngine(page);

        Job job = new Job(Messages.PrintAction_jobTitle){
            protected IStatus run( IProgressMonitor monitor ) {

                engine.setMonitor(monitor);

                PrinterJob printerJob = PrinterJob.getPrinterJob();

                engine.setPrinterJob(printerJob);

                if (printerJob.printDialog()) {
                    try {
                    	printerJob.setPageable(engine);
                    	printerJob.setJobName(jobName);
                        printerJob.print();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return Status.OK_STATUS;
            }
        };

        if( job.isSystem() )
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
