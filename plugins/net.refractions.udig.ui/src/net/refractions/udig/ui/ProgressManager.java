/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
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
