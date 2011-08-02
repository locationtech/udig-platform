package net.refractions.udig.catalog.memory;

import static net.refractions.udig.catalog.IResolve.Status.CONNECTED;
import static net.refractions.udig.catalog.IResolve.Status.NOTCONNECTED;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;

import com.vividsolutions.jts.geom.Envelope;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalogInfo;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.ISearch;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.CatalogInfoImpl;
import net.refractions.udig.catalog.internal.Messages;
import net.refractions.udig.catalog.util.AST;
import net.refractions.udig.catalog.util.ASTFactory;

/**
 * A very simple catalog to hold search results in memory.
 * 
 * @author Jody Garnett
 * @since 1.2.4
 */
public class MemoryCatalog extends ISearch {

    ICatalogInfo info;
    List<IService> services;
    private ID id;
    private String title;

    /**
     * Create a catalog; id often determined from search results.
     * <p>
     * Example:
     * 
     * <pre>
     * IRepository local = CatalogPlugin.getDefault().getLocalCatalog();
     * MemoryCatalog localFile = new ID(local.getID(), &quot;file&quot;);
     * </pre>
     * 
     * @param id
     */
    public MemoryCatalog( ID id, String title, List<IService> services ) {
        this.id = id;
        this.title = title;
        this.services = services;
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        return false;
    }

    @Override
    public Status getStatus() {
        return Status.CONNECTED;
    }

    @Override
    public Throwable getMessage() {
        return null;
    }

    @Override
    public URL getIdentifier() {
        return id.toURL();
    }

    @Override
    public ID getID() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null) return null;

        if (monitor == null) monitor = new NullProgressMonitor();

        try {
            if (adaptee.isInstance(this)) {
                return adaptee.cast(this);
            }
            if (adaptee.isInstance(info)) {
                return adaptee.cast(info);
            }
        } finally {
            monitor.worked(1);
            monitor.done();
        }
        return null;
    }

    @Override
    public List<IResolve> find( ID resourceId, IProgressMonitor monitor ) {
        if (monitor == null) monitor = new NullProgressMonitor();

        URL query = resourceId.toURL();
        Set<IResolve> found = new LinkedHashSet<IResolve>();

        monitor.beginTask("find " + resourceId, services.size());
        for( IService service : services ) {
            ID id = service.getID();
            URL identifier = service.getIdentifier();

            if (URLUtils.urlEquals(query, identifier, true)) {
                if (query.getRef() == null && URLUtils.urlEquals(query, identifier, false)) {
                    found.add(service);
                } else {
                    IResolve res = getChildById(service, resourceId, monitor);
                    if (res != null) {
                        found.add(res);
                    }
                }
            }
            monitor.worked(1);
        }
        monitor.done();
        return new ArrayList<IResolve>(found);
    }

    @Override
    public <T extends IResolve> T getById( Class<T> type, ID id, IProgressMonitor monitor ) {
        return null;
    }

    IResolve getChildById( IResolve handle, final ID id, IProgressMonitor monitor ) {

        if (monitor == null) monitor = new NullProgressMonitor();

        if (id.equals(handle.getID())) {
            // rough match
            return handle;
        }

        try {
            List< ? extends IResolve> children = handle.members(monitor);

            if (children == null || children.isEmpty()) {
                return null;
            }
            for( IResolve child : children ) {
                IResolve found = getChildById(child, id, null);
                if (found != null) {
                    return found;
                }
            }
        } catch (IOException e) {
            CatalogPlugin.log("Could not search children of " + handle.getIdentifier(), e); //$NON-NLS-1$
        }
        return null;
    }

    @Override
    public List<IResolve> search( String pattern, Envelope bbox, IProgressMonitor monitor )
            throws IOException {
        if (CatalogPlugin.getDefault().isDebugging()) {
            if (Display.getCurrent() != null) {
                throw new IllegalStateException("search called from display thread");
            }
        }
        if (monitor == null) monitor = new NullProgressMonitor();
        
        List<IResolve> result = new LinkedList<IResolve>();
        if ((pattern == null || pattern.trim().length() == 0) && (bbox == null || bbox.isNull())) {
            // nothing to find
            return result;
        }
        AST ast = ASTFactory.parse(pattern);
        if (ast == null) {
            return result;
        }
        HashSet<IService> searchScope = new HashSet<IService>();
        searchScope.addAll(this.services);
        try {
            monitor.beginTask(Messages.CatalogImpl_finding, searchScope.size() * 10);
            SERVICE: for( IService service : searchScope ) {
                ID serviceID = service.getID();
                if (check(service, ast)) {
                    result.add(service);
                }
                // Iterator< ? extends IGeoResource> resources;
                SubProgressMonitor submonitor = new SubProgressMonitor(monitor, 10);
                try {
                    List< ? extends IGeoResource> members = service.resources(submonitor);
                    if (members == null) {
                        continue SERVICE;
                    }
                    for( IGeoResource resource : members ) {
                        ID resoruceID = resource.getID();
                        try {
                            if (check(resource, ast, bbox)) {
                                result.add(resource);
                            }
                        } catch (Throwable t) {
                            CatalogPlugin.log("Could not search in resource:" + resoruceID, t);
                        }
                    }
                } catch (IOException e) {
                    CatalogPlugin.log("Could not search in service:" + serviceID, e);
                } finally {
                    submonitor.done();
                }
                Thread.yield(); // allow other threads to have a go... makes search view more
                                // responsive
            }
            return result;
        } finally {
            monitor.done();
        }
    }

    /* check the fields we catre about */
    protected static boolean check( IService service, AST pattern ) {
        if (pattern == null) {
            return false;
        }
        IServiceInfo info;
        try {
            info = service == null ? null : service.getInfo(null);
        } catch (IOException e) {
            info = null;
            CatalogPlugin.log(null, e);
        }
        boolean t = false;
        if (info != null) {
            if (info.getTitle() != null)
                t = pattern.accept(info.getTitle());
            if (!t && info.getKeywords() != null) {
                String[] keys = info.getKeywords().toArray(new String[0]);
                for( int i = 0; !t && i < keys.length; i++ )
                    if (keys[i] != null)
                        t = pattern.accept(keys[i]);
            }
            if (!t && info.getSchema() != null)
                t = pattern.accept(info.getSchema().toString());
            if (!t && info.getAbstract() != null)
                t = pattern.accept(info.getAbstract());
            if (!t && info.getDescription() != null)
                t = pattern.accept(info.getDescription());
        }
        return t;
    }
    /* check the fields we catre about */
    protected static boolean check( IGeoResource resource, AST pattern ) {
        if (pattern == null) {
            return true;
        }
        IGeoResourceInfo info;
        try {
            info = (resource == null ? null : resource.getInfo(null));
        } catch (IOException e) {
            CatalogPlugin.log(null, e);
            info = null;
        }
        if (info == null) {
            return false;
        }
        if (pattern.accept(info.getTitle())) {
            return true;
        }
        if (pattern.accept(info.getName())) {
            return true;
        }
        if (info.getKeywords() != null) {
            for( String key : info.getKeywords() ) {
                if (pattern.accept(key)) {
                    return true;
                }
            }
        }
        if (info.getSchema() != null && pattern.accept(info.getSchema().toString())) {
            return true;
        }
        if (pattern.accept(info.getDescription())) {
            return true;
        }
        return false;
    }

    protected static boolean check( IGeoResource resource, AST pattern, Envelope bbox ) {
        if (!check(resource, pattern)) {
            return false;
        }
        if (bbox == null || bbox.isNull()) {
            return true; // no checking here
        }
        try {
            ReferencedEnvelope bounds = resource.getInfo(null).getBounds();
            if (bounds == null) {
                return true; // bounds are unknown!
            }
            return bbox.intersects(bounds);
        } catch (Throwable e) {
            CatalogPlugin.log(null, e);
            return false;
        }
    }
}
