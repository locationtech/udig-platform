/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.catalog.cgdi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wms.WebMapServer;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.jdom.Element;
import org.jdom.Namespace;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Placeholder for results from a CGDI search.
 *
 * @author dzwiers
 * @since 0.6.0
 */
public class CGDIResource extends IGeoResource {

    private CGDIResource() {/* not used */
    }

    private IGeoResourceInfo info = null;
    class CGDIResourceInfo extends IGeoResourceInfo {
        CGDIResourceInfo( Element layer, Namespace context ) {
            Element server = layer.getChild("Server", context); //$NON-NLS-1$
            if (server != null) {
                server = server.getChild("OnlineResource", context); //$NON-NLS-1$
                if (server != null) {
                    String tmp = server
                            .getAttributeValue(
                                    "href", Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    if (tmp != null)
                        try {
                            serverURL = new URL(tmp);
                        } catch (MalformedURLException e) {
                            error = e;
                            CgdiPlugin.log(null, e);
                        }
                }
            }
            name = layer.getChildText("Name", context); //$NON-NLS-1$
            title = layer.getChildText("Title", context); //$NON-NLS-1$
            description = layer.getChildText("Abstract", context); //$NON-NLS-1$

            String tmp = layer.getChildText("SRS", context); //$NON-NLS-1$
            CoordinateReferenceSystem crs = null;
            try {
                crs = CRS.decode(tmp);
            } catch (FactoryException e) {
                e.printStackTrace();
            }

            Element extension = layer.getChild("Extension", context); //$NON-NLS-1$
            if (extension != null) {
                Element llbbox = layer.getChild("LatLongBoundingBox", context); //$NON-NLS-1$
                if (llbbox != null) {
                    double minx = Double
                            .valueOf(llbbox.getAttributeValue("minx", context)).doubleValue(); //$NON-NLS-1$
                    double maxx = Double
                            .valueOf(llbbox.getAttributeValue("maxx", context)).doubleValue(); //$NON-NLS-1$
                    double miny = Double
                            .valueOf(llbbox.getAttributeValue("miny", context)).doubleValue(); //$NON-NLS-1$
                    double maxy = Double
                            .valueOf(llbbox.getAttributeValue("maxy", context)).doubleValue(); //$NON-NLS-1$
                    bounds = new ReferencedEnvelope(new Envelope(minx, maxx, miny, maxy), crs);
                }
            }
        }
    }
    private String name = null;
    /**
     * Construct <code>CGDIResource</code>.
     *
     * @param layer
     * @param context
     * @param catalog
     */
    public CGDIResource( Element layer, Namespace context, CGDILayerCatalog catalog ) {
        this.catalog = catalog;
        if (layer != null) {
            info = new CGDIResourceInfo(layer, context);
            name = info.getName();
        }
        try {
            identifier = new URL(serverURL.toString() + "#" + name); //$NON-NLS-1$
        } catch (Throwable e) {
            error = e;
            CgdiPlugin.log(null, e);
            identifier=serverURL;
        }
    }
    private CGDILayerCatalog catalog = null;
    URL serverURL = null;

    private IGeoResource real = null;
    private IService service = null;
    Throwable error = null;
    private URL identifier;

    private void loadReal( IProgressMonitor monitor ) throws IOException {
        getInfo(monitor); // load me
        if (info == null || serverURL == null || info.getName() == null)
            return;

        URL id = getIdentifier();
        if (id == null)
            return;

        if (service == null) {
            List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(
                    serverURL);
            Iterator<IService> srv = services.iterator();
            while( srv.hasNext() ) {
                IService s = srv.next();
                if (s.canResolve(WebMapServer.class)) {
                    service = s;
                }
            }

            if (service == null) {
                return;
            }
        }
        if (real == null) {
            CatalogPlugin.getDefault().getLocalCatalog().add(service);
            // search for the child
            List< ? extends IGeoResource> resL = service.resources(monitor);
            if (resL == null) {
                return;
            }
            Iterator< ? extends IGeoResource> res = resL.iterator();
            while( res.hasNext() && real == null ) {
                IGeoResource restmp = res.next();
                if (restmp != null && restmp.getIdentifier() != null
                        && info.getName().equals(restmp.getIdentifier().getRef()))
                    real = restmp;
            }
        }
        IResolveDelta delta = new ResolveDelta(this, real, null);
        catalog.fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
    }
    /*
     * @see net.refractions.udig.catalog.IGeoResource#getInfo()
     */
    public IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (real != null)
            return real.getInfo(monitor);
        return info;
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#getIdentifier()
     */
    public URL getIdentifier() {
        if (real != null)
            return real.getIdentifier();
        return identifier;
    }

    /**
     * TODO summary sentence for getService ...
     *
     * @param monitor
     * @return x
     * @throws IOException
     */
    public IService getService( IProgressMonitor monitor ) throws IOException {
        if (service == null)
            loadReal(monitor);
        if (service != null)
            return service;
        return null;
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#getStatus()
     */
    public Status getStatus() {
        if (real != null)
            return real.getStatus();
        return error != null ? Status.BROKEN : Status.NOTCONNECTED;
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#getStatusMessage()
     */
    public Throwable getMessage() {
        return real == null ? error : real.getMessage();
    }
    /*
     * Required adaptions: <ul> <li>IGeoResourceInfo.class <li>IService.class </ul>
     *
     * @see net.refractions.udig.catalog.IGeoResource#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast(info);
        if (adaptee.isAssignableFrom(IService.class))
            return adaptee.cast(getService(monitor));
        if (adaptee.isAssignableFrom(WebMapServer.class)) {
            return getService(monitor).resolve(adaptee, monitor);
        }
        if (adaptee.isAssignableFrom(WFSDataStore.class)) {
            return getService(monitor).resolve(adaptee, monitor);
        }
        return super.resolve(adaptee, monitor);
    }
    @Override
    public IService service( IProgressMonitor monitor ) throws IOException {
        return getService(monitor);
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;
        if (adaptee.isAssignableFrom(WebMapServer.class)) {
            URL id = getIdentifier();
            String url = id.toExternalForm();
            return url.toUpperCase().indexOf("SERVICE=WMS") != -1; //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(WFSDataStore.class)) {
            URL id = getIdentifier();
            String url = id.toExternalForm();
            return url.toUpperCase().indexOf("SERVICE=WFS") != -1; //$NON-NLS-1$
        }
        return (adaptee.isAssignableFrom(IGeoResourceInfo.class) || adaptee
                .isAssignableFrom(IService.class))
                || super.canResolve(adaptee);
    }
}
