/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmsc;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.wmsc.server.TileSet;
import org.locationtech.udig.catalog.wmsc.server.TiledWebMapServer;
import org.locationtech.udig.catalog.wmsc.server.WMSTileSet;

/**
 * GeoResource to represent a WMS-C Tileset.
 *
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.1.0
 */
public class WMSCGeoResourceImpl extends IGeoResource {

    private TileSet tile; // the tile set info from the capabilities document

    private URL identifier; // the unique identifier

    /**
     * Creates a new GeoResource for a given WMSC tile set
     *
     * @param service
     * @param tile
     */
    public WMSCGeoResourceImpl(WMSCServiceImpl service, TileSet tile) {
        this.service = service;
        try {
            this.identifier = new URL(service.getIdentifier().toString() + "#" + tile.getId()); //$NON-NLS-1$
        } catch (Exception ex) {
            this.identifier = service.getIdentifier();
        }

        this.tile = tile;
    }

    @Override
    public URL getIdentifier() {
        return this.identifier;
    }

    @Override
    public IResolve parent(IProgressMonitor monitor) throws IOException {
        return service;
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    @Override
    public <T> boolean canResolve(Class<T> adaptee) {
        if (adaptee == null) {
            return false;
        }

        if (adaptee.isAssignableFrom(TiledWebMapServer.class)
                || adaptee.isAssignableFrom(WMSTileSet.class) || super.canResolve(adaptee)) {
            return true;
        }

        return false;
    }

    @Override
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if (adaptee.isAssignableFrom(TiledWebMapServer.class)) {
            return adaptee.cast(service(monitor).getWMSC());
        }
        if (adaptee.isAssignableFrom(WMSTileSet.class)) {
            return adaptee.cast(tile);
        }
        return super.resolve(adaptee, monitor);
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    @Override
    public Throwable getMessage() {
        return service.getMessage();
    }

    /**
     * @see org.locationtech.udig.catalog.IGeoResource#getStatus()
     */
    @Override
    public Status getStatus() {
        return service.getStatus();
    }

    @Override
    public WMSCGeoResourceInfo getInfo(IProgressMonitor monitor) throws IOException {
        return (WMSCGeoResourceInfo) super.getInfo(monitor);
    }

    @Override
    protected WMSCGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        WMSCServiceImpl tileServer = service(SubMonitor.convert(monitor, 50));
        try {
            tileServer.rLock.lock();
            return new WMSCGeoResourceInfo(this, SubMonitor.convert(monitor, 50));
        } finally {
            tileServer.rLock.unlock();
        }

    }

    @Override
    public WMSCServiceImpl service(IProgressMonitor monitor) throws IOException {
        return (WMSCServiceImpl) super.service(monitor);
    }

    /**
     * @return the WMSC tile set represented by the service
     */
    public TileSet getTileSet() {
        return this.tile;
    }
}
