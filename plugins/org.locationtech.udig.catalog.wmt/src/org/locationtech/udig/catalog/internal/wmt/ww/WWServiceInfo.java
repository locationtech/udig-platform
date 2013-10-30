/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.internal.wmt.ww;

import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.wmt.WMTPlugin;

import org.eclipse.core.runtime.IProgressMonitor;

class WWServiceInfo extends IServiceInfo {
    /** WMSServiceInfo service field */
    private final WWService service;

    WWServiceInfo(WWService serviceImpl, IProgressMonitor monitor) {
        service = serviceImpl;
        try {
            this.title = service.getLayerSet(monitor).getName();
        } catch (Exception e) {
            WMTPlugin.log("[WWServiceInfo] Failed getting LayerSet ", e); //$NON-NLS-1$
        }
    }

}