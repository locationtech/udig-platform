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
package org.locationtech.udig.catalog.util;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.ResourceInfo;
import org.geotools.data.ServiceInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.util.SimpleInternationalString;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IServiceInfo;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;
import org.opengis.util.ProgressListener;

/**
 * This class provides support for GeoTools Java 1.4 catalog interfaces.
 * <p>
 * This factory produces Java 5 wrappers around base GeoTools constructs.
 *
 * @author Jody Garnett
 */
public class GeoToolsAdapters {

    static public IGeoResourceInfo info(final ResourceInfo info) {
        return new IGeoResourceInfo() {
            @Override
            public ReferencedEnvelope getBounds() {
                return info.getBounds();
            }

            @Override
            public CoordinateReferenceSystem getCRS() {
                return info.getCRS();
            }

            @Override
            public String getDescription() {
                return info.getDescription();
            }

            @Override
            public String getName() {
                return info.getName();
            }

            @Override
            public URI getSchema() {
                return info.getSchema();
            }

            @Override
            public String getTitle() {
                return info.getTitle();
            }
        };
    }

    static public IServiceInfo info(final ServiceInfo info) {
        return new IServiceInfo() {
            @Override
            public String getAbstract() {
                return info.getDescription();
            }

            @Override
            public String getDescription() {
                return info.getDescription();
            }

            @Override
            public Set<String> getKeywords() {
                return new HashSet<>(info.getKeywords());
            }

            @Override
            public URI getPublisher() {
                return super.getPublisher();
            }

            @Override
            public URI getSchema() {
                return info.getSchema();
            }

            @Override
            public URI getSource() {
                return info.getSource();
            }

            @Override
            public String getTitle() {
                return info.getTitle();
            }
        };
    }

    static public ProgressListener progress(final IProgressMonitor monitor) {
        if (monitor == null)
            return null;
        return new ProgressListener() {
            private int progress;

            private InternationalString task;

            @Override
            public void complete() {
                monitor.done();
            }

            @Override
            public void dispose() {
                task = null;
            }

            @Override
            public void exceptionOccurred(Throwable arg0) {
            }

            @Override
            public boolean isCanceled() {
                return monitor.isCanceled();
            }

            @Override
            public void progress(float amount) {
                int current = (int) (100.0 * amount);
                monitor.worked(current - progress);
                progress = current;
            }

            @Override
            public void setCanceled(boolean arg0) {
                monitor.setCanceled(true);
            }

            @Override
            public void started() {
                monitor.beginTask(task != null ? task.toString() : null, 100);
            }

            @Override
            public void warningOccurred(String arg0, String arg1, String arg2) {
            }

            @Override
            public InternationalString getTask() {
                return task;
            }

            @Override
            public void setTask(InternationalString task) {
                this.task = task;

            }

            @Override
            public float getProgress() {
                return progress;
            }
        };
    }

    static public IProgressMonitor progress(final org.opengis.util.ProgressListener monitor) {
        if (monitor == null)
            return null;
        return new IProgressMonitor() {
            int total;

            int amount;

            @Override
            public void beginTask(String name, int totalWork) {
                amount = 0;
                total = totalWork;
                monitor.setTask(new SimpleInternationalString(name));
                monitor.progress(work());
            }

            float work() {
                return (float) amount / (float) total;
            }

            @Override
            public void done() {
                amount = total;
                monitor.complete();
                monitor.dispose();
            }

            @Override
            public void internalWorked(double work) {
            }

            @Override
            public boolean isCanceled() {
                return monitor.isCanceled();
            }

            @Override
            public void setCanceled(boolean cancel) {
                monitor.setCanceled(cancel);
            }

            @Override
            public void setTaskName(String name) {
                monitor.setTask(new SimpleInternationalString(name));
            }

            @Override
            public void subTask(String name) {
                monitor.setTask(new SimpleInternationalString(name));
            }

            @Override
            public void worked(int work) {
                amount += total;
            }
        };
    }
}
