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