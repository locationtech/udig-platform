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
