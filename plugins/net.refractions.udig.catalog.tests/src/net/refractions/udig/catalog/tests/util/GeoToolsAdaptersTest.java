package net.refractions.udig.catalog.tests.util;

import net.refractions.udig.catalog.util.GeoToolsAdapters;
import net.refractions.udig.ui.ProgressMonitorTaskNamer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.util.ProgressListener;
import org.junit.Test;
import static org.junit.Assert.*;

public class GeoToolsAdaptersTest {

    private final class Monitor implements IProgressMonitor {
        private String task;
        private double work;
        private double total;
        private boolean isCanceled = false;
        public void beginTask( String name, int totalWork ) {
            this.task = name;
            this.total = totalWork;
            this.work = 0;
        }
        public void done() {
            this.work = total;
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

        IProgressMonitor monitor = new Monitor();
        ProgressListener progress = GeoToolsAdapters.progress(monitor);
        assertFalse( progress.isCanceled() );
        assertEquals( progress.getProgress(), 0.0, 0.1f );
        
        monitor.beginTask("test", 100 );
        assertFalse( progress.isCanceled() );
        assertEquals( progress.getProgress(), 0.0, 0.01f );
        //assertEquals( "test", progress.get());
        
        monitor.setTaskName("skip");
        monitor.worked(50);
        assertEquals( 0.5, progress.getProgress(), 0.01 );
        //assertEquals( "skip", progress.getDescription());

        monitor.setTaskName("hop");
        monitor.worked(25);
        assertEquals( 0.75, progress.getProgress(), 0.01 );
        //assertEquals( "hop", progress.getDescription());

        monitor.setTaskName("jump");
        monitor.worked(25);
        assertEquals( 1.00, progress.getProgress(), 0.01f );
        //assertEquals( "jump", progress.getDescription());
    }
}
