package net.refractions.udig.catalog.tests.util;

import net.refractions.udig.catalog.util.GeoToolsAdapters;
import net.refractions.udig.ui.ProgressMonitorTaskNamer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.util.ProgressListener;
import org.junit.Test;
import static org.junit.Assert.*;

public class GeoToolsAdaptersTest {
    /** Little progress monitor used for testing */
    private final class Monitor implements IProgressMonitor {
        public String task = "not started";
        public double work = Double.NaN;
        public double total = Double.NaN;
        public boolean isCanceled = false;
        public boolean done = false;
        
        public void beginTask( String name, int totalWork ) {
            this.task = name;
            this.total = totalWork;
            this.work = 0;
        }
        public void done() {
            this.work = total;
            this.done = true;
        }
        public void internalWorked( double work ) {
            this.work = +work;
        }
        public boolean isCanceled() {
            return isCanceled;
        }
        public void setCanceled( boolean value ) {
            isCanceled = value;
        }
        public void setTaskName( String name ) {
            this.task = name;
        }
        public void subTask( String name ) {
            this.task = name;
        }
        public void worked( int work ) {
            this.work += work;
        }
    }

    @Test
    public void testProgress() {

        Monitor monitor = new Monitor();
        
        ProgressListener progress = GeoToolsAdapters.progress(monitor);
        progress.setDescription("go");
        progress.started();
        
        assertEquals("test started", 0.0, monitor.work, 0.01 );
        assertEquals("test started", 100.0, monitor.total, 0.01 );
        
        assertFalse( monitor.isCanceled );
        assertEquals("task", "go", monitor.task );
        
        progress.progress( 0.5f );
        assertEquals("test working", 50.0, monitor.work, 0.01 );
        assertEquals("test working", 100.0, monitor.total, 0.01 );
        
        progress.progress( 1.0f );
        assertEquals("test finished", 100.0, monitor.work, 0.01 );
        assertEquals("test finished", 100.0, monitor.total, 0.01 );
        
        assertEquals("almost done", false, monitor.done );       
        progress.complete();
        assertEquals("test done", true, monitor.done );
        
    }
}
