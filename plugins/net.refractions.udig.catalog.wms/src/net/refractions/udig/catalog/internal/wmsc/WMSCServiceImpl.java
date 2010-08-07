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
package net.refractions.udig.catalog.internal.wmsc;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.wms.WmsPlugin;
import net.refractions.udig.catalog.wmsc.server.Capability;
import net.refractions.udig.catalog.wmsc.server.TiledWebMapServer;
import net.refractions.udig.catalog.wmsc.server.VendorSpecificCapabilities;
import net.refractions.udig.catalog.wmsc.server.WMSCCapabilities;
import net.refractions.udig.catalog.wmsc.server.WMSTileSet;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A WMS-C Service. See Specifications:
 * http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation#GetCapabilities_Responses
 * 
 * @author Emily Gouge (Refractions Research, Inc.)
 * @since 1.1.0
 */
public class WMSCServiceImpl extends IService {

    private static final String CAPABILITIES_KEY = "net.refractions.udig.catalog.internal.wms.WMSCServiceImpl.capabilities"; //$NON-NLS-1$
    /**
     * <code>WMS_URL_KEY</code> field Magic param key for Catalog WMSC persistence.
     */
    public static final String WMSC_URL_KEY = "net.refractions.udig.catalog.internal.wms.WMSCServiceImpl.WMS_URL_KEY"; //$NON-NLS-1$
    public static final String WMSC_WMS_KEY = "net.refractions.udig.catalog.internal.wms.WMSCServiceImpl.WMS_WMS_KEY"; //$NON-NLS-1$

    private Map<String, Serializable> params;
    private URL url;

    private TiledWebMapServer wmsc;
    private Throwable msg;

    private List<IResolve> members;

    protected final Lock rLock = new UDIGDisplaySafeLock();
    private static final Lock dsLock = new UDIGDisplaySafeLock();

    /**
     * Creates a new service.
     * 
     * @param params
     */
    WMSCServiceImpl( Map<String, Serializable> params ) {
        this((URL) params.get(WMSC_URL_KEY), params);
    }

    WMSCServiceImpl( URL url, Map<String, Serializable> params ) {
        this.params = params;
        this.url = url;
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }
    @Override
    public WMSCServieInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (WMSCServieInfo) super.getInfo(monitor);
    }
    @Override
    protected WMSCServieInfo createInfo( IProgressMonitor monitor ) throws IOException {
        TiledWebMapServer tileServer = getWMSC();
        if (tileServer == null) {
            return null; // could not connect
        }
        rLock.lock();
        try {
            return new WMSCServieInfo(this);
        } finally {
            rLock.unlock();
        }
    }

    /**
     * For a WMS-C service this returns a list GeoResources representing the possible tilesets.
     */
    @Override
    public List<IResolve> members( IProgressMonitor monitor ) throws IOException {
        if (members == null) {
            getWMSC();
            rLock.lock();
            try {
                if (members == null) {
                    members = new LinkedList<IResolve>();

                    WMSCCapabilities capabilities = getWMSC().getCapabilities();
                    Capability capability = capabilities.getCapability();
                    VendorSpecificCapabilities vendorCapabilities = capability.getVSCapabilities();
                    if( vendorCapabilities != null ){
                        List<WMSTileSet> tiles = vendorCapabilities.getTiles();
                        
                        /*
                         * Retrieved no layers from the WMS - something is wrong, either the WMS doesn't
                         * work, or it has no named layers.
                         */
                        if (tiles != null) {
                            for( WMSTileSet tileset : tiles ) {
                                // add the server to this tileset
                                tileset.setServer(getWMSC());
                                members.add(new WMSCGeoResourceImpl(this, tileset));
                            }
                            this.msg = null;
                        }
                        else {
                            this.msg = new IllegalStateException("VendorCapabilities does not contain titles");
                        }
                    }
                    else {
                        this.msg = new IllegalStateException("VendorCapabilities not available");
                    }
                }
            } finally {
                rLock.unlock();
            }
        }
        return members;
    }

    @Override
    public List<WMSCGeoResourceImpl> resources( IProgressMonitor monitor ) throws IOException {
        members(monitor);
        List<WMSCGeoResourceImpl> children = new ArrayList<WMSCGeoResourceImpl>();
        for( Iterator<IResolve> iterator = members.iterator(); iterator.hasNext(); ) {
            children.add((WMSCGeoResourceImpl) iterator.next());

        }
        return children;
    }

    /**
     * @return the unique resource identifier
     */
    public URL getIdentifier() {
        return url;
    }
    public Throwable getMessage() {
        return msg;
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee.isAssignableFrom(TiledWebMapServer.class) || super.canResolve(adaptee);
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee.isAssignableFrom(TiledWebMapServer.class)) {
            return adaptee.cast(getWMSC());
        }
        return super.resolve(adaptee, monitor);
    }

    /**
     * This method will return a TiledWebMapServer based on either: connecting to the server; or a
     * cached capabilities document.
     * 
     * @return the link to the actual server
     */
    public TiledWebMapServer getWMSC() {
        if (wmsc == null) {
            dsLock.lock();
            try {
                if (msg != null) {
                    throw (IOException) msg;
                }
                if (wmsc == null) {
                    Serializable serializable = getPersistentProperties().get(CAPABILITIES_KEY);
                    if (serializable != null) {
                        try {
                            String xml = (String) serializable;

                            // NOTE: this constructor will check
                            // the updateSequence number and compare it to any
                            // capabilities it can fetch from the server
                            wmsc = new TiledWebMapServer(this.url, xml, true);
                        } catch (Exception e) {
                            WmsPlugin.log("Restore from cached capabilities failed", e); //$NON-NLS-1$
                            // we are going to continue by trying to connect to the real thing
                        }
                    }
                    if (wmsc == null) {
                        // we could not reconstruct from our cached capabilities?

                        // this constructor will grab the capabilities when
                        // first needed
                        wmsc = new TiledWebMapServer(this.url);
                        String xml = wmsc.getCapabilitiesXml();
                        getPersistentProperties().put(CAPABILITIES_KEY, xml);
                    }
                    msg = null; // we connected just fine this time
                }
            } catch (IOException couldNotConnext) {
                msg = couldNotConnext;
            } finally {
                dsLock.unlock();
            }
        }
        return wmsc;
    }

    /**
     * @return Status of the resource
     */
    public Status getStatus() {
        if (msg != null) {
            return Status.BROKEN;
        }
        if (wmsc == null) {
            return Status.NOTCONNECTED;
        }
        return Status.CONNECTED;
    }

}
