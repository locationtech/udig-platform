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
 * Encapsulates a task that needs to be run at shutdown but before the workbench has been shutdown.  
 * It can be submitted to the {@link ShutdownTaskList}.
 * Methods are NOT called in the Display thread.  
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface PreShutdownTask {

    /**
     * Called before shutdown is complete.  if !forced then the shutdown can be canceled if false is returned.
     *
     * @param monitor monitor for showing progress of task.  The beginTask method will set the task name but the number of steps is ignored.
     * @param workbench workbench that is shutting down
     * @param forced if the shutdown is forced.  If it is forced then shutdown cannot be canceled
     * @return true if shutdown is permitted or false if not.  ignored if forced==true.
     * @throws Exception if an exception is thrown it will be passed to {@link #handlePreShutdownException(Throwable, boolean)}
     */
    boolean preShutdown( IProgressMonitor monitor, IWorkbench workbench, boolean forced ) throws Exception;

    /**
     * called if {@link #preShutdown(IProgressMonitor, IWorkbench, boolean)} throws an exception.
     *
     * @param t the exception.
     * @param forced  if the shutdown is forced.  If it is forced then shutdown cannot be canceled
     * @return true if shutdown is permitted or false if not.  ignored if forced==true.
     */
    boolean handlePreShutdownException( Throwable t, boolean forced );
    
    /**
     * Returns the number of steps {@link #preShutdown(IProgressMonitor, IWorkbench, boolean)} will use.  
     * This is called only once just before all shutdown tasks are run.
     *
     * @return the number of steps {@link #preShutdown(IProgressMonitor, IWorkbench, boolean)} will use.
     */
    int getProgressMonitorSteps();

}
