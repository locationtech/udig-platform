/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wms;

import java.net.URI;
import java.net.URISyntaxException;

import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.wms.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.ows.WMSCapabilities;
import org.geotools.data.wms.xml.WMSSchema;

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
}