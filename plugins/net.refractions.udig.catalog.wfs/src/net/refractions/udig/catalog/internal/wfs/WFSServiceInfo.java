/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.wfs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.wfs.internal.Messages;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.v1_0_0.xml.WFSSchema;

/**
 * Description of WFSService.
 * 
 * @since 1.2.0
 */
class WFSServiceInfo extends IServiceInfo {
    private final WFSServiceImpl wfsService;
    private WFSDataStore ds;

    WFSServiceInfo( WFSServiceImpl wfsServiceImpl, WFSDataStore resource ) {
        wfsService = wfsServiceImpl;
        this.ds = resource;
        icon = AbstractUIPlugin.imageDescriptorFromPlugin(WfsPlugin.ID,
                "icons/obj16/wfs_obj.16"); //$NON-NLS-1$
    }

    /**
     * Return the service version.
     * 
     * @return Service version
     */
    public String getVersion(){
        return ds.getServiceVersion();
    }
    
    public String getAbstract() {
        return ds.getInfo().getDescription();
    }

    public Set<String> getKeywords() {
        return ds.getInfo().getKeywords();
    }

    public URI getSchema() {
        return WFSSchema.NAMESPACE;
    }

    public String getDescription() {
        return wfsService.getIdentifier().toString();
    }

    public URI getSource() {
        try {
            return wfsService.getIdentifier().toURI();
        } catch (URISyntaxException e) {
            // This would be bad
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
    }

    public String getTitle() {
        String title = ds.getInfo().getTitle();
        if (title == null) {
            title = wfsService.getIdentifier() == null ? Messages.WFSServiceImpl_broken : wfsService.getIdentifier()
                    .toString();
        } else {
            title += " (WFS " + ds.getInfo().getVersion() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return title;
    }
}