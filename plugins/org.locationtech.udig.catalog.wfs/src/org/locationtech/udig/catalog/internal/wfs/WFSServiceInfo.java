/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wfs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.wfs.internal.Messages;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.wfs.WFS;


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
        return ds.getInfo().getVersion();
    }
    
    public String getAbstract() {
        return ds.getInfo().getDescription();
    }

    public Set<String> getKeywords() {
        return ds.getInfo().getKeywords();
    }

    public URI getSchema() {
        try {
			return new URI(WFS.NAMESPACE);
		} catch (URISyntaxException e) {
			return null;
		}
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
