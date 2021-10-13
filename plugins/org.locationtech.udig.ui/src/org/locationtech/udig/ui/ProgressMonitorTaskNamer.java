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
package org.locationtech.udig.ui;

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
