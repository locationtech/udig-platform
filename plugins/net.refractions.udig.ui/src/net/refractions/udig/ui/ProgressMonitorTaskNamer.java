package net.refractions.udig.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * A SubprogressMonitor that also changes the current task name when begin Task is called.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ProgressMonitorTaskNamer extends SubProgressMonitor{

    public ProgressMonitorTaskNamer( IProgressMonitor monitor, int ticks) {
        super(monitor, ticks);
    }
    
    @Override
    public void beginTask( String name, int totalWork ) {
        super.beginTask(name, totalWork);
        setTaskName(name);
    }
    
}