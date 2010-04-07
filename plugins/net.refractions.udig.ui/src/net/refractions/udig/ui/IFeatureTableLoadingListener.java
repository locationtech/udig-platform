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

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A listener that is notified when the FeatureTable begins loading features and stops loading features.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public interface IFeatureTableLoadingListener {
    /**
     * Called when the {@link FeatureTableControl} starts loading features.  The progress monitor is only for cancelling the
     * loading any other method will throw a {@link UnsupportedOperationException}
     *
     * @param monitor monitor that is being updated by the job loading the features.  
     */
    public void loadingStarted(IProgressMonitor monitor);
    /**
     * Called when the loading stops.  Either canceled or finished
     * @param canceled if true the loading was canceled.
     *
     */
    public void loadingStopped(boolean canceled);
}
