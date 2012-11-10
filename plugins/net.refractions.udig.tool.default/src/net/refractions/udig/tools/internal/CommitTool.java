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
package net.refractions.udig.tools.internal;

import java.lang.reflect.InvocationTargetException;

import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.ui.tool.AbstractActionTool;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Commit the current transaction
 * @author jgarnett
 * @since 0.6.0
 */
public class CommitTool extends AbstractActionTool {


    /*
     * @see net.refractions.udig.project.ui.tool.ActionTool#run()
     */
    public void run() {
//        context.sendASyncCommand(context.getEditFactory().createCommitCommand());
    	// I don't need confirmation on a commit so rather than use a command I'm going to 
    	// directly execute the command here
    	PlatformGIS.run(new IRunnableWithProgress(){

			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					MapCommand command = context.getEditFactory().createCommitCommand();
					command.setMap(getContext().getMap());
					command.run(monitor);
				} catch (Exception e) {
					ToolsPlugin.log("Exception thrown while committing", e); //$NON-NLS-1$
					Display display = Display.getDefault();
					display.asyncExec(new Runnable() {
						public void run() {
							
							Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
							String message = Messages.CommitTool_Error_message;
							MessageDialog.openError(parent , Messages.CommitTool_error_shell_title, message);
						}
					});
				}
			}
    		
    	});
    }

    /*
     * @see net.refractions.udig.project.ui.tool.Tool#dispose()
     */
    public void dispose() {
    }

}
