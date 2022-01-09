/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.NASASource;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.NASASourceManager;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.WMTSourceFactory;
import org.locationtech.udig.core.internal.CorePlugin;

/**
 * Web Map Tile Server support.
 *
 * @author to.srwn
 * @since 1.1.0
 */
public class WMTService extends IService {
    public static String ID = "wmt://localhost/wmt/"; //$NON-NLS-1$

    public static String KEY_PROPERTY_ZOOM_LEVEL_SELECTION_AUTOMATIC = "PROPERTY_ZOOM_LEVEL_SELECTION_AUTOMATIC"; //$NON-NLS-1$

    public static String KEY_PROPERTY_ZOOM_LEVEL_VALUE = "PROPERTY_ZOOM_LEVEL_VALUE"; //$NON-NLS-1$

    /** Related GeoResources * */
    private volatile List<IGeoResource> members;

    private Map<String, Serializable> params;

    private URL url;

    Exception message = null;

    public WMTService(Map<String, Serializable> params) {
        this.params = params;

        if (params != null && params.containsKey(WMTServiceExtension.KEY)) {
            if (params.get(WMTServiceExtension.KEY) instanceof URL) {
                this.url = (URL) params.get(WMTServiceExtension.KEY);
            } else {
                try {
                    this.url = new URL(null, (String) params.get(WMTServiceExtension.KEY),
                            CorePlugin.RELAXED_HANDLER);
                } catch (MalformedURLException exc) {
                    WMTPlugin.log("[WMTService] Could not create url: " //$NON-NLS-1$
                            + params.get(WMTServiceExtension.KEY), exc);
                    this.url = null;
                }
            }
        }
    }

    /**
     * Returns the WMTSouce name of the first WMTGeoResource
     *
     * @return
     */
    public String getName() {
        try {
            List<IGeoResource> resources = resources(null);

            if (!resources.isEmpty()) {
                WMTGeoResource wmtResource = (WMTGeoResource) resources.get(0);

                return wmtResource.getSource().getName();
            }
        } catch (Exception exc) {
        }

        return null;
    }

    @Override
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        return super.resolve(adaptee, monitor);
    }

    @Override
    protected synchronized IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
        if (info == null) {
            synchronized (this) {
                if (info == null) {
                    info = new WMTServiceInfo(this, monitor);
                }
            }
        }
        return info;
    }

    @Override
    public List<IGeoResource> resources(IProgressMonitor monitor) throws IOException {
        if (members == null) {
            synchronized (this) {
                if (members == null) {
                    try {
                        if (WMTSourceFactory.getClassFromUrl(getIdentifier())
                                .equals(NASASource.class.getCanonicalName())) {
                            members = new LinkedList<>();

                            NASASourceManager sourceManager = NASASourceManager.getInstance();
                            sourceManager.buildGeoResources(this, members);
                        } else {
                            return Collections.singletonList((IGeoResource) new WMTGeoResource(this,
                                    WMTGeoResource.DEFAULT_ID));
                        }
                    } catch (Exception exc) {
                        message = exc; // could not "connect"
                    }
                }
            }
        }

        return members;
    }

    public List<IGeoResource> emptyResourcesList(IProgressMonitor monitor) throws IOException {
        members = new LinkedList<>();

        return members;
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    @Override
    public <T> boolean canResolve(Class<T> adaptee) {
        return super.canResolve(adaptee);
    }

    @Override
    public Status getStatus() {
        if (members == null) {
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    @Override
    public Throwable getMessage() {
        return message;
    }

    @Override
    public URL getIdentifier() {
        return url;
    }

    @Override
    public void dispose(IProgressMonitor monitor) {
        super.dispose(monitor);
        if (members != null) {
            members = null;
        }
    }
}
