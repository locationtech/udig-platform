/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wms;

import java.net.URI;
import java.net.URISyntaxException;

import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.wms.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.ows.wms.WMSCapabilities;
import org.geotools.ows.wms.xml.WMSSchema;

class WMSServiceInfo extends IServiceInfo {
    private WMSCapabilities caps = null;

    /** WMSServiceInfo service field */
    private final WMSServiceImpl service;

    WMSServiceInfo( WMSServiceImpl serviceImpl, IProgressMonitor monitor) {
        service = serviceImpl;
        try {
            caps = service.getWMS( monitor ).getCapabilities();
        } catch (Throwable t) {
            t.printStackTrace();
            caps = null;
        }
        

        keywords = caps == null ? null : caps.getService() == null ? null : caps
                .getService().getKeywordList();

        String[] t;
        if (keywords == null) {
            t = new String[2];
        } else {
            t = new String[keywords.length + 2];
            System.arraycopy(keywords, 0, t, 2, keywords.length);
        }
        t[0] = "WMS"; //$NON-NLS-1$
        t[1] = service.getIdentifier().toString();
        keywords = t;
        
        icon = AbstractUIPlugin.imageDescriptorFromPlugin( WmsPlugin.ID, "icons/obj16/wms_obj.gif" ); //$NON-NLS-1$
    }
       
    public String getAbstract() {
        return caps == null ? null : caps.getService() == null ? null : caps
                .getService().get_abstract();
    }
    public String getDescription() {
        return service.getIdentifier().toString();
    }

    public URI getSchema() {
        return WMSSchema.NAMESPACE;
    }

    public URI getSource() {
        try {
            return service.getIdentifier().toURI();
        } catch (URISyntaxException e) {
            // This would be bad 
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }
    public String getTitle() {
        return (caps == null || caps.getService() == null) ? (service.getIdentifier() == null
                ? Messages.WMSServiceImpl_broken 
                : service.getIdentifier().toString()) : caps.getService().getTitle();
    }
    
    public double getMetric() {
        return 0.9;
    }
}
