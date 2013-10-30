/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import net.refractions.udig.core.IProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Provides a consistent way to show progress of blocking operations.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class ProgressManager implements IProvider<IProgressMonitor>{
    private ProgressManager() {
    }
    private static final ProgressManager instance=new ProgressManager();
    
    /**
     * Returns the singleton instance
     *
     * @return the singleton instance
     */
    public static ProgressManager instance(){
        return instance;
    }

    /**
     * Requires no parameters
     */
    public IProgressMonitor get(Object... params) {
        return new NullProgressMonitor();
    }

}
