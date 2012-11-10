/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbench;

/**
 * Encapsulates a task that needs to be run after the workbench has shutdown.  
 * It can be submitted to the {@link ShutdownTaskList} object .
 * Methods are NOT called in the Display thread.  
 * 
 * @author Jesse
 * @since 1.1.0
 */public interface PostShutdownTask {

    /**
     * Called after shutdown is complete, at this point it is too late to cancel.
     * @param monitor the progress monitor to use.
     * @param workbench workbench that is shutting down
     */
    void postShutdown(  IProgressMonitor monitor, IWorkbench workbench ) throws Exception ;

    /**
     * Called if {@link #postShutdown(IWorkbench)} throws an exception
     *
     * @param t the exception
     */
    void handlePostShutdownException(Throwable t );
    
    /**
     * Returns the number of steps {@link #postShutdown(IProgressMonitor, IWorkbench)} will use.  
     * This is called only once just before all shutdown tasks are run.
     *
     * @return the number of steps {@link #postShutdown(IProgressMonitor, IWorkbench)} will use.
     */
    public int getProgressMonitorSteps();
}