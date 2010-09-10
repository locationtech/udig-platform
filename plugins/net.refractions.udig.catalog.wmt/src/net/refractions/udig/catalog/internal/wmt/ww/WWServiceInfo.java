
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