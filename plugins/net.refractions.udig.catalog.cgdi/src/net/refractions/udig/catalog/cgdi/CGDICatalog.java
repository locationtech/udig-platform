/*
 * Created on 6-Jan-2005
 */
package net.refractions.udig.catalog.cgdi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ICatalogInfo;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.cgdi.internal.Messages;
import net.refractions.udig.catalog.util.AST;
import net.refractions.udig.catalog.util.ASTFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.ui.PlatformUI;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author dzwiers This catalog is built to connect to a single server instance. To the best of my
 *         knowledge, the server is not based on a specification. The server can be found at
 *         'http://geodiscover.cgdi.ca/ceonetWeb/biz' and a web interface at
 *         'http://geodiscover.cgdi.ca/ceonetWeb/doc?dispatchServlet=/biz&servletName=biz
 *         Servlet&service=searchForService'.
 */
public class CGDICatalog extends ICatalog {

    /**
     * @param listener
     */
    public void addCatalogListener( IResolveChangeListener listener ) {
        catalogListeners.add(listener);
    }

    /**
     * @param listener
     */
    public void removeCatalogListener( IResolveChangeListener listener ) {
        catalogListeners.remove(listener);
    }
    /*
     * The following constants can be used to configure the Catalog either during creation, or at
     * runtime. These represent masks of which service types to search.
     */
    /** <code>UNDEFINED</code> field */
    public static final int UNDEFINED = 0;
    /** <code>WEB_FEATURE_SERVICES</code> field */
    public static final int WEB_FEATURE_SERVICES = 1;
    /** <code>WEB_MAP_SERVICES</code> field */
    public static final int WEB_MAP_SERVICES = 2;
    /** <code>WEB_COVERAGE_SERVICES</code> field */
    public static final int WEB_COVERAGE_SERVICES = 4;
    /** <code>WEB_REGISTRY_SERVICES</code> field */
    public static final int WEB_REGISTRY_SERVICES = 8;

    /** <code>ALL_SERVICES</code> field */
    public static final int ALL_SERVICES = 15;

    private ListenerList catalogListeners;
    URL url;
    private int serviceTypes = ALL_SERVICES;
    private ICatalogInfo info = null;

    /**
     * Construct <code>CGDICatalog</code>.
     */
    public CGDICatalog() {
        catalogListeners = new ListenerList();
        try {
            url = new URL("http://geodiscover.cgdi.ca/ceonetWeb/biz"); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            msg = e;
            CgdiPlugin.log(null, e);
        }
        info = new CDGICatalogMetadata();
    }

    /**
     * Construct <code>CGDICatalog</code>.
     *
     * @param url
     */
    public CGDICatalog( URL url ) {
        catalogListeners = new ListenerList();
        this.url = url;
        info = new CDGICatalogMetadata();
    }

    /**
     * TODO summary sentence for addListener ...
     *
     * @param listener
     */
    public void addListener( IResolveChangeListener listener ) {
        catalogListeners.add(listener);
    }

    /**
     * TODO summary sentence for removeListener ...
     *
     * @param listener
     */
    public void removeListener( IResolveChangeListener listener ) {
        catalogListeners.remove(listener);
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#add(net.refractions.udig.catalog.IService)
     */
    public void add( IService entry ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Write support is not provided."); //$NON-NLS-1$
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#remove(net.refractions.udig.catalog.IService)
     */
    public void remove( IService entry ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Write support is not provided."); //$NON-NLS-1$
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#replace(java.net.URL,
     *      net.refractions.udig.catalog.IService)
     */
    public void replace( URL id, IService entry ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Write support is not provided."); //$NON-NLS-1$
    }

    /**
     * Calls search ... this server only returns services directly
     */
    public List<IResolve> members( IProgressMonitor monitor ) throws IOException {
        return search("", null, monitor); //$NON-NLS-1$
    }

    /**
     * Returns the empty list ... not georesources here
     */
    public List<IResolve> find( URL id, IProgressMonitor monitor ) {
        return new LinkedList<IResolve>(); // this does not find geoResources
    }

    private String print( AST ast ) {
        if (ast.type() == AST.LITERAL) {
            return ast.toString() == null ? null : "%22" + ast.toString() + "%22"; //$NON-NLS-1$ //$NON-NLS-2$
        }

        String left = ast.getLeft() == null ? null : print(ast.getLeft());
        String right = ast.getRight() == null ? null : print(ast.getRight());
        switch( ast.type() ) {
        case AST.AND:
            return (left == null) ? ((right == null) ? null : right) : (right == null)
                    ? left
                    : left + " + " + right; //$NON-NLS-1$
        case AST.NOT:
            return (left == null) ? null : " - " + left; //$NON-NLS-1$
        case AST.OR:
        default:
            return (left == null) ? ((right == null) ? null : right) : (right == null)
                    ? left
                    : left + " " + right; //$NON-NLS-1$
        }
    }

    /**
     *
     */
    public List<IResolve> search( String pattern, Envelope bbox, IProgressMonitor monitor )
            throws IOException {
        List<IResolve> r = new LinkedList<IResolve>();

        AST ast = ASTFactory.parse(pattern);
        String keys = print(ast);

        if ((serviceTypes & WEB_FEATURE_SERVICES) == WEB_FEATURE_SERVICES) {
            List<IResolve> t = search(keys, WEB_FEATURE_SERVICES, "CgdiFeatureServices"); //$NON-NLS-1$
            if (t != null)
                r.addAll(t);
        }

        if ((serviceTypes & WEB_COVERAGE_SERVICES) == WEB_COVERAGE_SERVICES) {
            List<IResolve> t = search(keys, WEB_COVERAGE_SERVICES, "CgdiCoverageServices"); //$NON-NLS-1$
            if (t != null)
                r.addAll(t);
        }

        if ((serviceTypes & WEB_MAP_SERVICES) == WEB_MAP_SERVICES) {
            List<IResolve> t = search(keys, WEB_MAP_SERVICES, "CgdiMapServices"); //$NON-NLS-1$
            if (t != null)
                r.addAll(t);
        }

        if ((serviceTypes & WEB_REGISTRY_SERVICES) == WEB_REGISTRY_SERVICES) {
            List<IResolve> t = search(keys, WEB_REGISTRY_SERVICES, "CgdiRegistryServices"); //$NON-NLS-1$
            if (t != null)
                r.addAll(t);
        }

        return r;
    }
    List<IResolve> search( String keys, int type, String servType ) throws IOException {

        // create url
        String url1 = this.url.toString();
        // level of detail = brief (only real option)

        // http://geodiscover.cgdi.ca/ceonetWeb/biz?request=searchForService&language=en
        // &numResultsPerPage=&page=&levelOfDetail=brief&sortOrder=alphabetic&keywords=
        // &serviceType=CgdiFeatureServices

        url1 = url1 + "?request=searchForService&language=en&numResultsPerPage=1000&page=1" + //$NON-NLS-1$
                "&levelOfDetail=brief&sortOrder=alphabetic&keywords=" + keys + //$NON-NLS-1$
                "&serviceType=" + servType; //$NON-NLS-1$
        URL request = null;
        try {
            // send url
            request = new URL(url1);
        } catch (MalformedURLException e) {
            msg = e;
            CgdiPlugin.log(null, e);
        }

        // parse url
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(request);
        } catch (IOException e1) {
            msg = e1;
            CgdiPlugin.log(null, e1);
            throw e1;
        } catch (JDOMException e1) {
            msg = e1;
            CgdiPlugin.log(null, e1);
            throw new IOException(e1.getMessage());
        }

        // translate doc into IService instances
        Element root = doc.getRootElement();
        Element results = root.getChild("searchResults"); //$NON-NLS-1$
        List<Element> entryList = results.getChildren("entry"); //$NON-NLS-1$

        if (entryList != null && !entryList.isEmpty()) {
            List<IResolve> services = new LinkedList<IResolve>();
            Iterator<Element> i = entryList.iterator();
            while( i.hasNext() ) {
                services.add(new CGDIService(i.next(), type, this));
            }
            return services;
        }

        return null;
    }

    /**
     * @see net.refractions.udig.catalog.ICatalogInfo#getSource()
     * @return x
     */
    public URL getIdentifier() {
        return url;
    }

    class CDGICatalogMetadata extends ICatalogInfo {
        CDGICatalogMetadata() {
            super();
        }
        /**
         * @see net.refractions.udig.catalog.ICatalogInfo#getTitle()
         * @return x
         */
        public String getTitle() {
            return url.toString();
        }

        /**
         * @see net.refractions.udig.catalog.ICatalogInfo#getKeywords()
         * @return x
         */
        public String[] getKeywords() {
            return new String[]{"CGDI", //$NON-NLS-1$
                    "Catalog", //$NON-NLS-1$
                    "Discovery" //$NON-NLS-1$
            };
        }

        /**
         * @see net.refractions.udig.catalog.ICatalogInfo#getDescription()
         * @return x
         */
        public String getDescription() {
            return Messages.CGDICatalog_description;
        }

        /**
         * @see net.refractions.udig.catalog.ICatalogInfo#getSource()
         * @return x
         */
        public URL getSource() {
            return url;
        }
    }
    /**
     * @return Returns the serviceTypes.
     */
    public int getServiceTypes() {
        return serviceTypes;
    }
    /**
     * @param serviceTypes The serviceTypes to set.
     */
    public void setServiceTypes( int serviceTypes ) {
        this.serviceTypes = serviceTypes;
    }

    /*
     * <ul> <li>ICatalogInfo.class <li>List.class <IService> </ul>
     *
     * @see net.refractions.udig.catalog.ICatalog#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(ICatalogInfo.class))
            return adaptee.cast(info);
        if (adaptee.isAssignableFrom(List.class))
            return adaptee.cast(members(monitor));
        return null;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;
        return (adaptee.isAssignableFrom(ICatalogInfo.class) || adaptee
                .isAssignableFrom(List.class));
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return msg == null ? Status.CONNECTED : Status.BROKEN;
    }
    private Throwable msg;
    /*
     * @see net.refractions.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return msg;
    }

    void fire( IResolveChangeEvent event ) {
        Object[] listeners = catalogListeners.getListeners();
        if (listeners.length == 0)
            return;

        for( int i = 0; i < listeners.length; ++i ) {
            try {
                ((IResolveChangeListener) listeners[i]).changed(event);
            } catch (Throwable die) {
                CgdiPlugin.log(null, new Exception(die));
            }
        }
    }

	@Override
	public List<IService> findService(URL query) {
		return new ArrayList<IService>();
	}
	@Override
	public <T extends IResolve> T getById(Class<T> type, URL id, IProgressMonitor monitor) {
		return null;
	}
    @Override
    public IGeoResource createTemporaryResource( Object descriptor ) throws IllegalArgumentException {
        throw new IllegalArgumentException("This catalog does not create Temporary Resources"); //$NON-NLS-1$
    }

    @Override
    public String[] getTemporaryDescriptorClasses() {
        return new String[0];
    }

    @Override
    public void dispose( IProgressMonitor monitor ) {
        catalogListeners.clear();
    }
}
