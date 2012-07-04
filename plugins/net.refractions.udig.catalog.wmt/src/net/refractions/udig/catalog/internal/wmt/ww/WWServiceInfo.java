/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
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