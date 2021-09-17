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
package org.locationtech.udig.mapgraphic.internal;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.ITransientResolve;
import org.locationtech.udig.core.internal.CorePlugin;
import org.locationtech.udig.mapgraphic.MapGraphicFactory;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Registers the internal service used to represents
 * map graphics or decorators.
 */
public class MapGraphicService extends IService {
    public static String ID = "mapgraphic:///localhost/mapgraphic"; //$NON-NLS-1$

    /** Dummy url for a MapGraphic */
    public static final URL SERVICE_URL;
    public static final ID SERVICE_ID;
    static {
        URL tmp;
        try {
            tmp = new URL(null, ID, CorePlugin.RELAXED_HANDLER);
        } catch (MalformedURLException e) {
            tmp = null;
            e.printStackTrace();
        }
        SERVICE_URL = tmp;
        SERVICE_ID = new ID(SERVICE_URL);
    }

    /** MapGraphic resource children * */
    private volatile List<MapGraphicResource> members;

    /**
     * Construct <code>MapGraphicService</code>. with package visibility;
     * should only be constructed by MapGraphicServiceExrtension.
     */
    MapGraphicService() {
    }

    /*
     * @see org.locationtech.udig.catalog.IService#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(MapGraphicFactory.class)) {
            return adaptee.cast(MapGraphicFactory.getInstance());
        }
        if (adaptee.isAssignableFrom(ITransientResolve.class)) {
            return adaptee.cast(this);
        }
        return super.resolve(adaptee, monitor);
    }

    @Override
    public MapGraphicServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (MapGraphicServiceInfo) super.getInfo(monitor);
    }
    @Override
    protected synchronized MapGraphicServiceInfo createInfo( IProgressMonitor monitor )
            throws IOException {
        return new MapGraphicServiceInfo();
    }
    /*
     * @see org.locationtech.udig.catalog.IService#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public List<MapGraphicResource> resources( IProgressMonitor monitor ) throws IOException {

        if (members == null) {
            synchronized (MapGraphicFactory.getInstance()) {
                if (members == null) {
                    members = new ArrayList<MapGraphicResource>();
                    List<IConfigurationElement> graphics = MapGraphicFactory.getInstance()
                            .getMapGraphics();

                    if (graphics != null) {
                        for( IConfigurationElement graphic : graphics ) {
                            members.add(new MapGraphicResource(this, graphic));
                        }
                    }
                }
            }
        }

        return members;
    }

    /*
     * @see org.locationtech.udig.catalog.IService#getConnectionParams()
     */
    @Override
    public Map<String, Serializable> getConnectionParams() {
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put(MapGraphicServiceExtension.KEY, SERVICE_URL);
        return params;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null
                && (adaptee.isAssignableFrom(MapGraphicFactory.class)
                        || adaptee.isAssignableFrom(ITransientResolve.class) || super
                        .canResolve(adaptee));
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return Status.CONNECTED;
    }

    @Override
    public void dispose(IProgressMonitor monitor) {
        // built-in
    }
    /*
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return null;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return SERVICE_URL;
    }

    static class MapGraphicServiceInfo extends IServiceInfo {
        /*
         * @see org.locationtech.udig.catalog.IServiceInfo#getTitle()
         */
        @Override
        public String getTitle() {
            return Messages.MapGraphicService_title;
        }

        @Override
        public String getDescription() {
            return Messages.MapGraphicService_description;
        }
    }
}
