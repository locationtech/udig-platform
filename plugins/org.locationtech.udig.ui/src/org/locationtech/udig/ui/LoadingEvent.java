/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.ui;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Parameter Object for notifying loading listeners
 * @author Jesse
 * @since 1.1.0
 */
class LoadingEvent {
    final boolean canceled;
    final IProgressMonitor monitor;
    final boolean loading;
    public LoadingEvent( final boolean canceled, final IProgressMonitor monitor, final boolean loading ) {
        this.canceled = canceled;
        this.monitor = monitor;
        this.loading = loading;
    }
}
