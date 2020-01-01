/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wms;

import static org.locationtech.udig.catalog.internal.wms.Trace.REQUEST;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.ows.GetCapabilitiesRequest;
import org.geotools.data.ows.GetCapabilitiesResponse;
import org.geotools.data.ows.Specification;
import org.geotools.ows.ServiceException;
import org.geotools.ows.wms.Layer;
import org.geotools.ows.wms.WMS1_0_0;
import org.geotools.ows.wms.WMS1_1_0;
import org.geotools.ows.wms.WMS1_1_1;
import org.geotools.ows.wms.WMS1_3_0;
import org.geotools.ows.wms.WMSCapabilities;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.request.GetFeatureInfoRequest;
import org.geotools.ows.wms.request.GetMapRequest;
import org.geotools.ows.wms.response.GetFeatureInfoResponse;
import org.geotools.ows.wms.response.GetMapResponse;
import org.geotools.ows.wms.xml.WMSSchema;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.wms.internal.Messages;
import org.locationtech.udig.catalog.wms.preferences.WmsPreferenceConstants;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;
import org.xml.sax.SAXException;

/**
 * Connect to a WMS.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class WMSServiceImpl extends IService {

    /**
     * <code>WMS_URL_KEY</code> field Magic param key for Catalog WMS persistence.
     */
    public static final String WMS_URL_KEY = "org.locationtech.udig.catalog.internal.wms.WMSServiceImpl.WMS_URL_KEY"; //$NON-NLS-1$
    public static final String WMS_WMS_KEY = "org.locationtech.udig.catalog.internal.wms.WMSServiceImpl.WMS_WMS_KEY"; //$NON-NLS-1$

    private Map<String, Serializable> params;

    private Throwable error;
    private URL url;

    private volatile WebMapServer wms = null;
    protected final Lock rLock = new UDIGDisplaySafeLock();
    private volatile List<IResolve> members;
    private int currentFolderID = 0;

    private static final Lock dsLock = new UDIGDisplaySafeLock();

    /**
     * Construct <code>WMSServiceImpl</code>.
     * 
     * @param url
     * @param params
     */
    public WMSServiceImpl( URL url, Map<String, Serializable> params ) {
        this.params = params;
        this.url = url;
        // System.out.println("WMS "+url);
        if (params.containsKey(WMS_WMS_KEY)) {
            Object obj = params.get(WMS_WMS_KEY);

            if (obj instanceof WebMapServer) {
                this.wms = (WebMapServer) obj;
            }
        }
    }

    public Status getStatus() {
        if( wms == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }
    /**
     * Aquire the actual geotools WebMapServer instance.
     * <p>
     * Note this method is blocking and throws an IOException to indicate such.
     * </p>
     * 
     * @param theUserIsWatching
     * @return WebMapServer instance
     * @throws IOException
     */
    protected WebMapServer getWMS( IProgressMonitor theUserIsWatching ) throws IOException {
        if (wms == null) {
            dsLock.lock();
            try {
                if (wms == null) {
                    try {
                        if (theUserIsWatching != null) {
                            String message = MessageFormat.format(
                                    Messages.WMSServiceImpl_connecting_to, new Object[]{url});
                            theUserIsWatching.beginTask(message, 100);
                        }
                        URL url1 = (URL) getConnectionParams().get(WMS_URL_KEY);
                        if (theUserIsWatching != null)
                            theUserIsWatching.worked(5);
                        wms = new CustomWMS(url1);
                        if (theUserIsWatching != null)
                            theUserIsWatching.done();
                    } catch (IOException persived) {
                        error = persived;
                        throw persived;
                    } catch (Throwable nak) {

                        IOException broken = new IOException(MessageFormat.format(
                                Messages.WMSServiceImpl_could_not_connect, new Object[]{nak
                                        .getLocalizedMessage()}));
                        broken.initCause(nak);
                        error = broken;
                        throw broken;
                    }
                }
            } finally {
                dsLock.unlock();
            }
        }
        return wms;
    }

    @Override
    public WMSServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (WMSServiceInfo) super.getInfo(monitor);
    }
    protected WMSServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        WebMapServer webMapServer = getWMS(monitor);
        if (webMapServer == null) {
            return null; // could not connect
        }
        rLock.lock();
        try {
            return new WMSServiceInfo(monitor);

        } finally {
            rLock.unlock();
        }
    }

    /*
     * @see org.locationtech.udig.catalog.IService#resolve(java.lang.Class,
     * org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null) {
            return null;
        }

        if (adaptee.isAssignableFrom(IServiceInfo.class)) {
            return adaptee.cast(createInfo(monitor));
        }

        if (adaptee.isAssignableFrom(List.class)) {
            return adaptee.cast(members(monitor));
        }

        if (adaptee.isAssignableFrom(WebMapServer.class)) {
            return adaptee.cast(getWMS(monitor));
        }

        return super.resolve(adaptee, monitor);
    }

    /**
     * @see org.locationtech.udig.catalog.IService#getConnectionParams()
     */
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;

        return adaptee.isAssignableFrom(WebMapServer.class) || super.canResolve(adaptee);
    }
    public void dispose( IProgressMonitor monitor ) {
        super.dispose(monitor);
        if( members != null ){
            members = null;
        }
        if( wms != null ){
            wms = null;
        }
    }

    public List<WMSGeoResourceImpl> resources( IProgressMonitor monitor ) throws IOException {
        // seed the potentially null field
        members(monitor);
        List<WMSGeoResourceImpl> children = new ArrayList<WMSGeoResourceImpl>();
        collectChildren(this, children);

        return children;
    }

    private void collectChildren( IResolve resolve, List<WMSGeoResourceImpl> children )
            throws IOException {
        List<IResolve> resolves = resolve.members(new NullProgressMonitor());

        if (resolves.isEmpty() && resolve instanceof WMSGeoResourceImpl) {
            children.add((WMSGeoResourceImpl) resolve);
        } else {
            for( IResolve resolve2 : resolves ) {
                collectChildren(resolve2, children);
            }
        }
    }

    public List<IResolve> members( IProgressMonitor monitor ) throws IOException {

        if (members == null) {
            getWMS(monitor);
            rLock.lock();
            try {
                if (members == null) {
                    getWMS(monitor); // load ds
                    members = new LinkedList<IResolve>();
                    List<Layer> layers = getWMS(monitor).getCapabilities().getLayerList();
                    /*
                     * Retrieved no layers from the WMS - something is wrong, either the WMS doesn't
                     * work, or it has no named layers.
                     */
                    if (layers != null) {
                        for( Layer layer : layers ) {
                            if (layer.getParent() == null) {
                                if (layer.getName() == null) {
                                    members.add(new WMSFolder(this, null, layer));
                                } else {
                                    members.add(new WMSGeoResourceImpl(this, null, layer));
                                }
                            }
                        }
                    }
                }
            } finally {
                rLock.unlock();
            }
        }
        return members;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return error;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return url;
    }

    class WMSServiceInfo extends IServiceInfo {
        WMSServiceInfo( IProgressMonitor monitor ) {
            try {
                caps = getWMS(monitor).getCapabilities();
            } catch (Throwable t) {
                t.printStackTrace();
                caps = null;
            }

            keywords = caps == null ? null : caps.getService() == null ? null : caps.getService()
                    .getKeywordList();

            String[] t;
            if (keywords == null) {
                t = new String[2];
            } else {
                t = new String[keywords.length + 2];
                System.arraycopy(keywords, 0, t, 2, keywords.length);
            }
            t[0] = "WMS"; //$NON-NLS-1$
            t[1] = getIdentifier().toString();
            keywords = t;
            icon = AbstractUIPlugin.imageDescriptorFromPlugin(WmsPlugin.ID,
                    "icons/obj16/wms_obj.gif"); //$NON-NLS-1$
        }
        private WMSCapabilities caps = null;

        public String getAbstract() {
            return caps == null ? null : caps.getService() == null ? null : caps.getService()
                    .get_abstract();
        }

        public String getDescription() {
            return getIdentifier().toString();
        }

        public URI getSchema() {
            return WMSSchema.NAMESPACE;
        }

        public URI getSource() {
            try {
                return getIdentifier().toURI();
            } catch (URISyntaxException e) {
                // This would be bad
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }

        public String getTitle() {
            return (caps == null || caps.getService() == null) ? (getIdentifier() == null
                    ? Messages.WMSServiceImpl_broken
                    : getIdentifier().toString()) : caps.getService().getTitle();
        }
    }

    /**
     * Custom WebMapServer hooked up to tracing events.
     */
    public static class CustomWMS extends WebMapServer {
        /**
         * @throws SAXException
         * @throws ServiceException
         * @param serverURL
         * @throws IOException
         */
        public CustomWMS( URL serverURL ) throws IOException, ServiceException, SAXException {
            super(serverURL, WmsPlugin.getDefault().getPreferenceStore().getInt(
                    WmsPreferenceConstants.WMS_RESPONSE_TIMEOUT));
            
            if (WmsPlugin.isDebugging(REQUEST)) {
                System.out.println("Connection to WMS located at: " + serverURL); //$NON-NLS-1$
            }
            if (getCapabilities() == null) {
                throw new IOException("Unable to parse capabilities document."); //$NON-NLS-1$
            }
        }

        public GetCapabilitiesResponse issueRequest( GetCapabilitiesRequest arg0 )
                throws IOException, ServiceException {
            WmsPlugin.trace("GetCapabilities: " + arg0.getFinalURL(), null); //$NON-NLS-1$
            return super.issueRequest(arg0);
        }

        public GetFeatureInfoResponse issueRequest( GetFeatureInfoRequest arg0 )
                throws IOException, ServiceException {
            WmsPlugin.trace("GetFeatureInfo: " + arg0.getFinalURL(), null); //$NON-NLS-1$
            return super.issueRequest(arg0);
        }

        public GetMapResponse issueRequest( GetMapRequest arg0 ) throws IOException,
                ServiceException {
            WmsPlugin.trace("GetMap: " + arg0.getFinalURL(), null); //$NON-NLS-1$
            return super.issueRequest(arg0);
        }

        protected void setupSpecifications() {
            specs = new Specification[4];
            specs[0] = new WMS1_0_0();
            specs[1] = new WMS1_1_0();
            specs[2] = new WMS1_1_1();
            specs[3] = new WMS1_3_0();
        }
    }

    public int nextFolderID() {
        return currentFolderID++;
    }
}
