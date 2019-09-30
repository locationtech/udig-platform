/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.util;

import org.locationtech.udig.catalog.util.GeoToolsAdapters;
import org.locationtech.udig.ui.ProgressMonitorTaskNamer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.util.NameFactory;
import org.opengis.util.ProgressListener;
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
        progress.setTask(NameFactory.create("go").toInternationalString());
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
