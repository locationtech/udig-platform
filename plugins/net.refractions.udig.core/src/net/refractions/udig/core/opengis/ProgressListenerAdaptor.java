/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.core.opengis;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.util.InternationalString;
import org.opengis.util.ProgressListener;

public class ProgressListenerAdaptor implements ProgressListener {
    private String description;
    private int progress;
    private InternationalString task;
    private IProgressMonitor monitor;
    public ProgressListenerAdaptor(IProgressMonitor monitor){
        this.monitor = monitor; 
    }
    public void complete() {
        monitor.done();
    }
    public void dispose() {
        description = null;
    }
    public void exceptionOccurred(Throwable arg0) {             
    }
    public String getDescription() {
        return description;
    }
    public boolean isCanceled() {
        return monitor.isCanceled();
    }
    public void progress(float amount) {
        int current = (int)(100.0 * amount);
        monitor.worked( current - progress );
        progress = current;
    }

    public void setCanceled(boolean arg0) {
        monitor.setCanceled(true);
    }

    public void setDescription(String text) {
        description = text;
    }

    public void started() {
        monitor.beginTask( description, 100);
    }

    public void warningOccurred(String arg0, String arg1, String arg2) {                
    }
    public InternationalString getTask() {
        return task;
    }
    public void setTask(InternationalString task) {
        this.task=task;
        
    }
    public float getProgress() {
        return progress;
    }           
}