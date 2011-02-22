package net.refractions.udig.core.jts;

import org.eclipse.core.runtime.IProgressMonitor;

import com.vividsolutions.jts.util.Progress;

/**
 * Adapter allowing the use of a ProgressMonitor with JTS operations.
 *
 * @author Jody Garnett
 * @since 1.1.0
 */
public class JTSProgressMonitor implements Progress {
    private IProgressMonitor monitor;
    public JTSProgressMonitor(IProgressMonitor monitor){
        this.monitor = monitor;
    }
    public void begin( String name, int total ) {
        monitor.beginTask( name, total );
    }
    public void done() {
        monitor.done();
    }
    public boolean isCanceled() {
        return monitor.isCanceled();
    }
    public void setCanceled( boolean stop ) {
        monitor.setCanceled( stop );
    }
    public void setTask( String task ) {
        monitor.setTaskName( task );
    }
    public void worked( int amount ) {
        monitor.worked( amount );
    }
}
