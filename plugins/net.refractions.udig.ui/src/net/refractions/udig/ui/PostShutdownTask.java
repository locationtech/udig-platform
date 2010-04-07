/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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