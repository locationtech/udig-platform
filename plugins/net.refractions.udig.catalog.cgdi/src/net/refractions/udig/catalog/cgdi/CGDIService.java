/*
 * Created on 8-Jan-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package net.refractions.udig.catalog.cgdi;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.OperationNotSupportedException;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wms.WebMapServer;
import org.jdom.Element;

/**
 * summary sentence.
 * <p>
 * Paragraph ...
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p>
 * <p>
 * Example:
 *
 * <pre><code>
 *   CGDIService x = new CGDIService( ... );
 *   TODO code example
 * </code></pre>
 *
 * </p>
 *
 * @author dzwiers
 * @since 0.6.0
 */
public class CGDIService extends IService {

    private CGDIService() {/* not used */
    }
    private Throwable message = null; // the exception as a string

    URL connectionURL;
    String name;
    private int type = CGDICatalog.UNDEFINED;
    private Element entry = null;

    private IService service = null;
    private CGDICatalog catalog = null;

    /**
     * Construct <code>CGDIService</code>.
     *
     * @param entry jDom Element of the 'entry' from a search
     * @param type
     * @param catalog
     */
    public CGDIService( Element entry, int type, CGDICatalog catalog ) {
        this.catalog = catalog;
        try {
            connectionURL = new URL(entry.getChildTextTrim("accessUrl")); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            message = e;
            CgdiPlugin.log(null, e);
        }
        if (type == CGDICatalog.UNDEFINED) {
            String url = entry.getChildTextTrim("accessUrl").toUpperCase(); //$NON-NLS-1$
            if (url.indexOf("SERVICE=WFS") > -1) { //$NON-NLS-1$
                // WFS
                type = CGDICatalog.WEB_FEATURE_SERVICES;
            } else {
                if (url.indexOf("SERVICE=WMS") > -1) { //$NON-NLS-1$
                    // WMS
                    type = CGDICatalog.WEB_MAP_SERVICES;
                } else {
                    // TODO worry when we have more about WCS/WRS
                    type = CGDICatalog.UNDEFINED;
                }
            }
        } else {
            this.type = type;
        }
        name = entry.getChildTextTrim("name"); //$NON-NLS-1$
        this.entry = entry;
    }

    public void dispose( IProgressMonitor monitor ) {
        if (service == null)
            return;

        service.dispose(monitor);
    }

    /*
     * @see net.refractions.udig.catalog.IService#getStatus()
     */
    public Status getStatus() {
        if (service != null)
            return service.getStatus();
        return message == null ? Status.CONNECTED : Status.BROKEN;
    }

    /*
     * @see net.refractions.udig.catalog.IService#getStatusMessage()
     */
    public Throwable getMessage() {
        if (service != null)
            return service.getMessage();
        return message;
    }

    private void loadService() throws OperationNotSupportedException {
        serviceInfo = null;

        List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(
                connectionURL);
        Iterator<IService> srv = services.iterator();

        switch( type ) {
        case CGDICatalog.WEB_FEATURE_SERVICES:

            while( srv.hasNext() ) {
                IService s = srv.next();
                if (s.canResolve(WFSDataStore.class)) {
                    service = s;
                }
            }
            break;
        case CGDICatalog.WEB_MAP_SERVICES:

            while( srv.hasNext() ) {
                IService s = srv.next();
                if (s.canResolve(WebMapServer.class)) {
                    service = s;
                }
            }
            break;
        default:
            List l = CatalogPlugin.getDefault().getServiceFactory().createService(connectionURL);
            if (l == null || l.size() < 1)
                throw new OperationNotSupportedException("Cannot yet support that format"); //$NON-NLS-1$
            service = ((IService) l.iterator().next());
        }

        CatalogPlugin.getDefault().getLocalCatalog().add(service);

        IResolveDelta delta = new ResolveDelta(this, service, null);
        catalog.fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified" ); //$NON-NLS-1$
        }

        if (service != null) {
            return service.resolve(adaptee, monitor);
        }
        if (adaptee.isAssignableFrom(IServiceInfo.class)) {
            return adaptee.cast(this.getInfo(monitor));
        }
        try {
            loadService();
        } catch (OperationNotSupportedException e) {
            message = e;
            CgdiPlugin.log("Could not connect to service", e); //$NON-NLS-1$
        }
        if (service != null) {
            return service.resolve(adaptee, monitor);
        }
        return super.resolve(adaptee, monitor);
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;
        if (service != null)
            return service.canResolve(adaptee);
        return super.canResolve(adaptee);
    }

    /**
     * generates the connection params ... may cause the service to be added locally
     */
    public Map<String, Serializable> getConnectionParams() {
        if (service == null)
            try {
                loadService();
            } catch (OperationNotSupportedException e) {
                e.printStackTrace();
                return null;
            }
        return service.getConnectionParams();
    }

    /*
     * @see net.refractions.udig.catalog.IService#getIdentifier()
     */
    public URL getIdentifier() {
        if (service != null)
            return service.getIdentifier();
        return connectionURL;
    }

    /**
     * This will create a IService in the local catalog .. and return it's resources. The Id will be
     * identical, and you aught to be capable of keeping this handle.
     */
    public List< ? extends IGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        if (service == null)
            try {
                loadService();
            } catch (OperationNotSupportedException e) {
                message = e;
                CgdiPlugin.log(null, e);
            }
        return service.resources(monitor);
    }

    /*
     * @see net.refractions.udig.catalog.IService#getInfo()
     */
    public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (service != null) {
            if (serviceInfo == null) {
                serviceInfo = service.getInfo(monitor);
                IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
                ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                        .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE,
                                delta));
            }
            return serviceInfo;
        }
        if (serviceInfo == null) {
            serviceInfo = new CGDIServiceInfo(entry);
            IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                    .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
        }
        return serviceInfo;

    }
    private IServiceInfo serviceInfo = null;

    class CGDIServiceInfo extends IServiceInfo {

        CGDIServiceInfo( Element entry ) {
            title = name;
            source = connectionURL;

            Element metadata = entry.getChild("serviceDetails"); //$NON-NLS-1$
            if (metadata == null)
                return;
            Element idinfo = metadata.getChild("citation"); //$NON-NLS-1$
            if (idinfo == null)
                return;

            Element kw = idinfo.getChild("acronym"); //$NON-NLS-1$
            if (kw != null) {
                keywords = new String[4];
                keywords[0] = getTypeStr();
                keywords[1] = "CGDI"; //$NON-NLS-1$
                keywords[2] = name;
                keywords[3] = kw.getTextTrim();
            } else {
                keywords = new String[3];
                keywords[0] = getTypeStr();
                keywords[1] = "CGDI"; //$NON-NLS-1$
                keywords[2] = name;
            }

            description = idinfo.getChildTextTrim("description"); //$NON-NLS-1$

            _abstract = idinfo.getChildTextTrim("fullName"); //$NON-NLS-1$
        }
    }

    /**
     * TODO summary sentence for getTypeStr ...
     *
     * @return x
     */
    protected String getTypeStr() {
        switch( type ) {
        case CGDICatalog.WEB_FEATURE_SERVICES:
            return "WFS"; //$NON-NLS-1$
        case CGDICatalog.WEB_MAP_SERVICES:
            return "WMS"; //$NON-NLS-1$
        case CGDICatalog.WEB_COVERAGE_SERVICES:
            return "WCS"; //$NON-NLS-1$
        case CGDICatalog.WEB_REGISTRY_SERVICES:
            return "WRS"; //$NON-NLS-1$
        default:
            return ""; //$NON-NLS-1$
        }
    }
}
