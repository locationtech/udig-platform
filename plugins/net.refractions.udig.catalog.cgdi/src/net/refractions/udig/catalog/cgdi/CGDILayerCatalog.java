/*
 * Created on 6-Jan-2005
 */
package net.refractions.udig.catalog.cgdi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import com.vividsolutions.jts.geom.Envelope;

/**
 * @author dzwiers This catalog is built to connect to a single server instance. To the best of my
 *         knowledge, the server is not based on a specification. The server can be found at
 *         'http://geodiscover.cgdi.ca/ceonetWeb/biz' and a web interface at
 *         'http://geodiscover.cgdi.ca/ceonetWeb/doc?dispatchServlet=/biz&servletName=biz
 *         Servlet&service=searchForWebServiceData'. This returns a WebContextDocument ... so WMS
 *         layers.
 */
public class CGDILayerCatalog extends ICatalog {

    private ListenerList catalogListeners;
    URL url;
    private ICatalogInfo info = null;
    private Throwable msg = null;

    /**
     * Construct <code>CGDILayerCatalog</code>.
     */
    public CGDILayerCatalog() {
        catalogListeners = new ListenerList();
        try {
            url = new URL("http://geodiscover.cgdi.ca/ceonetWeb/biz"); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            msg = e;
            CgdiPlugin.log(null, e);
        }
        info = new CGDILayerCatalogInfo();
    }

    /**
     * Construct <code>CGDILayerCatalog</code>.
     *
     * @param url
     */
    public CGDILayerCatalog( URL url ) {
        catalogListeners = new ListenerList();
        this.url = url;
        info = new CGDILayerCatalogInfo();
    }

    /**
     * TODO summary sentence for addCatalogListener ...
     *
     * @param listener
     */
    public void addCatalogListener( IResolveChangeListener listener ) {
        catalogListeners.add(listener);
    }

    /**
     * TODO summary sentence for removeCatalogListener ...
     *
     * @param listener
     */
    public void removeCatalogListener( IResolveChangeListener listener ) {
        catalogListeners.remove(listener);
    }

    /**
     *
     */
    public void add( IService entry ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Write support is not provided."); //$NON-NLS-1$
    }

    /**
     *
     */
    public void remove( IService entry ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Write support is not provided."); //$NON-NLS-1$
    }

    /**
     *
     */
    public void replace( URL id, IService entry ) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Write support is not provided."); //$NON-NLS-1$
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
     * Looks for services based on the pattern ... Ignores the bbox. This uses the searchForServices
     * action.
     */
    public List<IResolve> search( String pattern, Envelope bbox, IProgressMonitor monitor )
            throws IOException {

        String keys = print(ASTFactory.parse(pattern));

        // create url
        String url1 = this.url.toString();
        // level of detail = brief (only real option)

        // http://geodiscover.cgdi.ca/ceonetWeb/biz?request=searchForWebServiceData
        // &language=en&northCoord=80&southCoord=49&eastCoord=-126&westCoord=-60
        // &width=500&height=500&keywords=landsat&subject=&srs=&version=&useBaseMap=
        // &filterCascade=

        url1 += "?request=searchForWebServiceData&language=en"; //$NON-NLS-1$

        if (bbox == null || bbox.isNull()) {
            // defaults
            bbox = new Envelope(-180, 180, -90, 90);
        }
        url1 += "&northCoord=" + bbox.getMaxY() + "&southCoord=" + bbox.getMinY() + "&eastCoord=" + bbox.getMinX() + "&westCoord=" + bbox.getMaxX(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        url1 += "&width=500&height=500"; //$NON-NLS-1$
        url1 += "&keywords=" + keys; //$NON-NLS-1$
        url1 += "&subject=&srs=&version=&useBaseMap=&filterCascade="; //$NON-NLS-1$

        URL request = null;
        try {
            // send url
            request = new URL(url1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // should not happen as we are only adding a query to the url
            return null;
        }

        // parse url
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(request);
        } catch (JDOMException e1) {
            msg = e1;
            CgdiPlugin.log(null, e1);
            throw new IOException(e1.getMessage());
        } catch (IOException e1) {
            msg = e1;
            CgdiPlugin.log("", e1); //$NON-NLS-1$
            throw e1;
        }

        // translate doc into IGeoResource instances
        Element root = doc.getRootElement();
        Namespace context = Namespace.getNamespace("http://www.opengis.net/context"); //$NON-NLS-1$
        Element layerList = root.getChild("LayerList", context); //$NON-NLS-1$
        if (layerList == null)
            return null;
        List layers = layerList.getChildren("Layer", context); //$NON-NLS-1$

        if (layers != null && !layers.isEmpty()) {
            List<IResolve> resources = new LinkedList<IResolve>();
            Iterator i = layers.iterator();
            while( i.hasNext() ) {
                resources.add(new CGDIResource((Element) i.next(), context, this));
            }
            return resources;
        }

        return null;
    }

    class CGDILayerCatalogInfo extends ICatalogInfo {
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
            return Messages.CGDILayerCatalog_description;
        }

        /**
         * @see net.refractions.udig.catalog.ICatalogInfo#getSource()
         * @return x
         */
        public URL getSource() {
            return url;
        }
    }

    /*
     * Required adaptions: <ul> <li>ICatalogInfo.class <li>List.class <IService> </ul>
     *
     * @see net.refractions.udig.catalog.ICatalog#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) {
        if (adaptee == null)
            return null;
        if (adaptee.isAssignableFrom(ICatalogInfo.class))
            return adaptee.cast(info);
        if (adaptee.isAssignableFrom(List.class))
            return adaptee.cast(members(monitor));
        return null;
    }

    /*
     * @see net.refractions.udig.catalog.ICatalog#find(java.net.URL)
     */
    public List<IResolve> find( URL id, IProgressMonitor monitor ) {
        return new LinkedList<IResolve>();
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
     * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<IResolve> members( IProgressMonitor monitor ) {
        return new LinkedList<IResolve>();
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return msg == null ? Status.CONNECTED : Status.BROKEN;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return msg;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return url;
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
		return null;
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
}
