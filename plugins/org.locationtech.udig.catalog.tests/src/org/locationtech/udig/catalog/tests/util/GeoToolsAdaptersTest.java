/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.tests.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.util.NameFactory;
import org.junit.Test;
import org.locationtech.udig.catalog.util.GeoToolsAdapters;
import org.opengis.util.ProgressListener;

public class GeoToolsAdaptersTest {
    /**
     * Little progress monitor used for testing
     */
    private final class Monitor implements IProgressMonitor {
        public String task = "not started"; //$NON-NLS-1$

        public double work = Double.NaN;

        public double total = Double.NaN;

        public boolean isCanceled = false;

        public boolean done = false;

        @Override
        public void beginTask(String name, int totalWork) {
            this.task = name;
            this.total = totalWork;
            this.work = 0;
        }

        @Override
        public void done() {
            this.work = total;
            this.done = true;
        }

        @Override
        public void internalWorked(double work) {
            this.work = +work;
        }

        @Override
        public boolean isCanceled() {
            return isCanceled;
        }

        @Override
        public void setCanceled(boolean value) {
            isCanceled = value;
        }

        @Override
        public void setTaskName(String name) {
            this.task = name;
        }

        @Override
        public void subTask(String name) {
            this.task = name;
        }

        @Override
        public void worked(int work) {
            this.work += work;
        }
    }

    @Test
    public void testProgress() {

        Monitor monitor = new Monitor();

        ProgressListener progress = GeoToolsAdapters.progress(monitor);
        progress.setTask(NameFactory.create("go").toInternationalString()); //$NON-NLS-1$
        progress.started();

        assertEquals("test started", 0.0, monitor.work, 0.01); //$NON-NLS-1$
        assertEquals("test started", 100.0, monitor.total, 0.01); //$NON-NLS-1$

        assertFalse(monitor.isCanceled);
        assertEquals("task", "go", monitor.task); //$NON-NLS-1$ //$NON-NLS-2$

        progress.progress(0.5f);
        assertEquals("test working", 50.0, monitor.work, 0.01); //$NON-NLS-1$
        assertEquals("test working", 100.0, monitor.total, 0.01); //$NON-NLS-1$

        progress.progress(1.0f);
        assertEquals("test finished", 100.0, monitor.work, 0.01); //$NON-NLS-1$
        assertEquals("test finished", 100.0, monitor.total, 0.01); //$NON-NLS-1$

        assertEquals("almost done", false, monitor.done); //$NON-NLS-1$
        progress.complete();
        assertEquals("test done", true, monitor.done); //$NON-NLS-1$

    }
}
