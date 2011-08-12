/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004-2011, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal;

import static net.refractions.udig.catalog.IResolve.Status.CONNECTED;
import static net.refractions.udig.catalog.IResolve.Status.NOTCONNECTED;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ICatalogInfo;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveChangeListener;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.ServiceParameterPersister;
import net.refractions.udig.catalog.TemporaryResourceFactory;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.interceptor.GeoResourceInterceptor;
import net.refractions.udig.catalog.interceptor.ServiceInterceptor;
import net.refractions.udig.catalog.moved.MovedService;
import net.refractions.udig.catalog.util.AST;
import net.refractions.udig.catalog.util.ASTFactory;
import net.refractions.udig.catalog.util.IFriend;
import net.refractions.udig.core.WeakHashSet;
import net.refractions.udig.core.internal.ExtensionPointList;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.ui.PlatformGIS;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Local Catalog implementation (tracking known services, persisted by XML currently).
 * 
 * @author David Zwiers (Refractions Research)
 * @author Jody Garnett
 * @since 0.6
 * @version 1.2
 */
public class CatalogImpl extends ICatalog {
    private static final String TEMPORARY_RESOURCE_EXT_ID = "net.refractions.udig.catalog.temporaryResource"; //$NON-NLS-1$

    /** All services known to the local catalog */
    private final Set<IService> services = new CopyOnWriteArraySet<IService>();
    /** Information about this catalog */
    private ICatalogInfo metadata;

    private final Set<IResolveChangeListener> catalogListeners;

    /** @see getTemporaryDescriptorClasses */
    private String[] descriptors;

    public CatalogImpl() {
        CatalogInfoImpl metadata = new CatalogInfoImpl();
        metadata.setTitle(Messages.CatalogImpl_localCatalog_title);
        try {
            metadata.setSource(new URL("http://localhost")); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            // do nothing
        }

        this.metadata = metadata;
        catalogListeners = Collections.synchronizedSet(new WeakHashSet<IResolveChangeListener>());
    }

    public CatalogImpl( ICatalogInfo metadata ) {
        this();
        this.metadata = metadata;
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#addCatalogListener(net.refractions.udig.catalog.ICatalog.ICatalogListener)
     * @param listener
     */
    public void addCatalogListener( IResolveChangeListener listener ) {
        catalogListeners.add(listener);
    }
    public void addListener( IResolveChangeListener listener ) {
        catalogListeners.add(listener);
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#removeCatalogListener(net.refractions.udig.catalog.ICatalog.ICatalogListener)
     * @param listener
     */
    public void removeCatalogListener( IResolveChangeListener listener ) {
        catalogListeners.remove(listener);
    }
    public void removeListener( IResolveChangeListener listener ) {
        catalogListeners.remove(listener);
    }

    public IService add( IService service ) throws UnsupportedOperationException {
        if (service == null || service.getIdentifier() == null)
            throw new NullPointerException("Cannot have a null id"); //$NON-NLS-1$
        ID id = service.getID();

        IService found = getById(IService.class, id, new NullProgressMonitor());

        if (found != null) {
            // clean up the service that was passed in
            try {
                service.dispose(new NullProgressMonitor());
            } catch (Throwable t) {
                CatalogPlugin.trace("dispose " + id, t);
            }
            return found;
        }

        services.add(service);
        runInterceptor(service, ServiceInterceptor.ADDED_ID);
        
        IResolveDelta deltaAdded = new ResolveDelta(service, IResolveDelta.Kind.ADDED);
        IResolveDelta deltaChanged = new ResolveDelta(this, Collections.singletonList(deltaAdded));
        fire(new ResolveChangeEvent(CatalogImpl.this, IResolveChangeEvent.Type.POST_CHANGE,
                deltaChanged));
        
        return service;
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#remove(net.refractions.udig.catalog.IService)
     * @param entry
     * @throws UnsupportedOperationException
     */
    public void remove( IService entry ) throws UnsupportedOperationException {
        if (entry == null || entry.getIdentifier() == null){
            throw new NullPointerException("Cannot have a null id"); //$NON-NLS-1$
        }
        IResolveDelta deltaRemoved = new ResolveDelta(entry, IResolveDelta.Kind.REMOVED);
        IResolveDelta deltaChanged = new ResolveDelta(this, Collections.singletonList(deltaRemoved));
        fire(new ResolveChangeEvent(CatalogImpl.this, IResolveChangeEvent.Type.PRE_DELETE,
                deltaChanged));
        
        services.remove(entry);
        runInterceptor(entry, ServiceInterceptor.REMOVED_ID);
        
        fire(new ResolveChangeEvent(CatalogImpl.this, IResolveChangeEvent.Type.POST_CHANGE,
                deltaRemoved));
    }
    public void replace( ID id, IService replacement ) throws UnsupportedOperationException {
        if (replacement == null || replacement.getIdentifier() == null || id == null) {
            throw new NullPointerException("Cannot have a null id"); //$NON-NLS-1$
        }
        final IService service = getServiceById(id);
        List<IResolveDelta> changes = new ArrayList<IResolveDelta>();
        List<IResolveDelta> childChanges = new ArrayList<IResolveDelta>();
        try {
            List< ? extends IGeoResource> newChildren = replacement.resources(null);
            List< ? extends IGeoResource> oldChildren = service.resources(null);
            if (oldChildren != null)
                for( IGeoResource oldChild : oldChildren ) {
                    String oldName = oldChild.getIdentifier().toString();

                    for( IGeoResource child : newChildren ) {
                        String name = child.getIdentifier().toString();
                        if (oldName.equals(name)) {
                            childChanges.add(new ResolveDelta(child, oldChild,
                                    IResolveDelta.NO_CHILDREN));
                            break;
                        }
                    }
                }
        } catch (IOException ignore) {
            // no children? Not a very good entry ..
        }
        changes.add(new ResolveDelta(service, replacement, childChanges));

        IResolveDelta deltas = new ResolveDelta(this, changes);
        IResolveChangeEvent event = new ResolveChangeEvent(this,
                IResolveChangeEvent.Type.PRE_DELETE, deltas);
        fire(event);
        services.remove(service);
        runInterceptor(service, ServiceInterceptor.REMOVED_ID);

        PlatformGIS.run(new IRunnableWithProgress(){

            public void run( IProgressMonitor monitor ) throws InvocationTargetException,
                    InterruptedException {
                try {
                    service.dispose(monitor);
                } catch (Throwable e) {
                    CatalogPlugin.log("error disposing of: " + service.getIdentifier(), e); //$NON-NLS-1$
                }
            }

        });

        services.add(replacement);
        runInterceptor(replacement, ServiceInterceptor.ADDED_ID);
        event = new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, deltas);

        if (!id.equals(replacement.getIdentifier())) {
            // the service has actually moved
            IService moved = new MovedService(id, replacement.getID());
            services.add(moved);
            runInterceptor(moved, ServiceInterceptor.ADDED_ID);
        }
        fire(event);
    }
    //
    // Registration: creation
    //
    
    /**
     * This is the preferred way to connect to a service using a URL.
     * <p>
     * If the service is already in the catalog it will be returned; if not we will
     * connect to the service (add it to the catalog for safekeeping and cleanup)
     * and return you the result.
     * <p>
     * The catalog takes responsibility for cleaning up after the service (ie call dispose())
     * so you are free to continue with your work.
     * @param url
     * @param monitor
     * @return Service used to access the provided url
     */
    public IService acquire( URL url, IProgressMonitor monitor ) throws IOException {
        List<IService> possible = new ArrayList<IService>();
        IService createdService = null;
        
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask("acquire", 100);
        monitor.subTask("acquire services");

        monitor.worked(10);
        try {

            possible = constructServices(url, monitor);

            if (possible.isEmpty()) {
                throw new IOException("Unable to connect to any service supporting " + url);
            }
            
            createdService = possible.get(0);
            
            try {
                
                add(createdService);// TODO don't clean this one up!
                return createdService;
            } catch (Throwable t) {
                // usually indicates an IOException as the service is unable to connect
                CatalogPlugin.trace("trouble connecting to " + createdService.getID(), t);
            }

        } finally {
            List<IService> members = checkMembers(possible);
            
            for( Iterator<IService> iterator = members.iterator(); iterator.hasNext(); ) {
                IService service = iterator.next();

                if (service.equals(createdService))
                    continue;

                service.dispose(new SubProgressMonitor(monitor, 10));
            }
            monitor.done();
        }
        return null; // unable to connect
    }

    /**
     * This is the preferred way to connect to a service using connection parameters.
     * <p>
     * If the service is already in the catalog it will be returned; if not we will
     * connect to the service (add it to the catalog for safekeeping and cleanup)
     * and return you the result.
     * <p>
     * The catalog takes responsibility for cleaning up after the service (ie call dispose())
     * so you are free to continue with your work.
     * @param connectionParameters
     * @param monitor
     * @return Service used to access the provided connectionParameters
     */
    public IService acquire( Map<String, Serializable> connectionParameters,
            IProgressMonitor monitor ) throws IOException {
        
        List<IService> possible = new ArrayList<IService>();
        IService createdService = null;
        
        if (monitor == null) monitor = new NullProgressMonitor();

        monitor.beginTask("acquire", 100);
        monitor.subTask("acquire services");

        try {

            possible = constructServices(connectionParameters, monitor);

            if (possible.isEmpty()) {
                throw new IOException("Unable to connect to any service ");
            }

            createdService = possible.get(0);

            try {
                add(createdService);// TODO don't clean this one up!
                return createdService;
            } catch (Throwable t) {
                // usually indicates an IOException as the service is unable to connect
                CatalogPlugin.trace("trouble connecting to " + createdService.getID(), t);
            }

        } finally {
            List<IService> members = checkMembers(possible);
            
            for( Iterator<IService> iterator = members.iterator(); iterator.hasNext(); ) {
                IService service = iterator.next();

                if (service.equals(createdService))
                    continue;

                service.dispose(new SubProgressMonitor(monitor, 10));
            }
            
            monitor.done();
        }
        return null;
    }

    //
    // Search Implementations
    //
    public List<IResolve> find( ID id, IProgressMonitor monitor ) {
        URL query = id.toURL();
        Set<IResolve> found = new LinkedHashSet<IResolve>();

        ID id1 = new ID(query);

        // first pass 1.1- use urlEquals on CONNECTED service for subset check
        for( IService service : services ) {
            if (service.getStatus() != CONNECTED)
                continue; // skip non connected service
            URL identifier = service.getIdentifier();
            if (URLUtils.urlEquals(query, identifier, true)) {
                if (matchedService(query, identifier)) {
                    found.add(service);
                    found.addAll(friends(service));
                } else {
                    IResolve res = getChildById(service, id1, true, monitor);
                    if (res != null) {
                        found.add(res);
                        found.addAll(friends(res));
                    }
                }
            }
        }
        // first pass 1.2 - use urlEquals on unCONNECTED service for subset check
        for( IService service : services ) {
            if (service.getStatus() == CONNECTED)
                continue; // already checked in pass 1.1
            URL identifier = service.getIdentifier();
            if (URLUtils.urlEquals(query, identifier, true)) {
                if (service.getStatus() != NOTCONNECTED)
                    continue; // look into not connected service that "match"
                if (matchedService(query, identifier)) {
                    found.add(service);
                    found.addAll(friends(service));
                } else {
                    IResolve res = getChildById(service, id1, true, monitor);
                    if (res != null) {
                        found.add(res);
                        found.addAll(friends(res));
                    }
                }
            }
        }
        // first pass 1.3 - use urlEquals on BROKEN or RESTRICTED_ACCESS service for subset check
        // the hope here is that a "friend" will still have data! May be tough for friends
        // to negotiate a match w/ a broken services - but there is still hope...
        for( IService service : services ) {
            if (service.getStatus() == CONNECTED || service.getStatus() == NOTCONNECTED) {
                continue; // already checked in pass 1.1-1.2
            }
            URL identifier = service.getIdentifier();
            if (URLUtils.urlEquals(query, identifier, true)) {
                if (matchedService(query, identifier)) {
                    found.add(service);
                    found.addAll(friends(service));
                } else {
                    IResolve res = getChildById(service, id1, true, monitor);
                    if (res != null) {
                        found.add(res);
                        found.addAll(friends(res));
                    }
                }
            }
        }
        // second pass - deep check for georesource on connected services
        // these are resources that dont match the server#membername pattern
        //
        // This code removed for udig 1.1 release
        /*
         * if( found.isEmpty() && query.getRef()!=null ){ // we have not found anything; and we are
         * looking for a georesource // search connected resources ... for( IService service :
         * services ) { if( service.getStatus() == CONNECTED ){ IResolve res = getChildById(service,
         * query, monitor); if( res!=null ){ found.add(res); found.addAll( friends( res)); break; }
         * } } }
         */
        // thirdpass - deep check for georesource on unconnected services
        //
        // This code removed for udig 1.1 release
        /*
         * if( found.isEmpty() && query.getRef()!=null ){ if( false ){
         * CatalogPlugin.log("Warning Deep search in catalog is occurring this is VERY expensive",
         * new Exception("JUST A WARNING")); //$NON-NLS-1$ //$NON-NLS-2$ } // we have not found
         * anything; and we are looking for a georesource // search not connected resources ... for(
         * IService service : services ) { if( service.getStatus() == NOTCONNECTED ){ IResolve res =
         * getChildById(service, query, monitor); if( res!=null ){ found.add(res); found.addAll(
         * friends( res)); break; } } } }
         */
        return new ArrayList<IResolve>(found);
    }

    /**
     * Quick search by url match.
     * 
     * @param query
     * @see net.refractions.udig.catalog.ICatalog#search(org.opengis.filter.Filter)
     * @return List<IResolve>
     * @throws IOException
     */
    public List<IResolve> find( URL query, IProgressMonitor monitor ) {
        return find(new ID(query), monitor);
    }

    /**
     * Check if the provided query is a child of identifier.
     * 
     * @param query
     * @param identifier
     * @return true if query may be a child of identifier
     */
    private boolean matchedService( URL query, URL identifier ) {
        return query.getRef() == null && URLUtils.urlEquals(query, identifier, false);
    }

    /**
     * Returns a list of friendly resources working with related data.
     * <p>
     * This method is used internally to determine resource handles that offer different entry
     * points to the same information.
     * </p>
     * A friend can be found via:
     * <ul>
     * <li>Making use of a CSW2.0 association
     * <li>URL Pattern matching for well known cases like GeoServer and MapServer
     * <li>Service Metadata, for example WMS resourceURL referencing a WFS SimpleFeatureType
     * </ul>
     * All of these handles will be returned from the find( URL, monitor ) method. </ul>
     * 
     * @param handle
     * @return List of frends, possibly empty
     */
    public List<IResolve> friends( final IResolve handle ) {
        final List<IResolve> friends = new ArrayList<IResolve>();
        ExtensionPointUtil.process(CatalogPlugin.getDefault(),
                "net.refractions.udig.catalog.friendly", new ExtensionPointProcessor(){ //$NON-NLS-1$
                    /**
                     * Lets find our friends.
                     */
                    public void process( IExtension extension, IConfigurationElement element )
                            throws Exception {
                        try {
                            String target = element.getAttribute("target"); //$NON-NLS-1$
                            String contain = element.getAttribute("contain"); //$NON-NLS-1$
                            if (target != null) {
                                // perform target check
                                if (target.equals(target.getClass().toString())) {
                                    return;
                                }
                            }
                            if (contain != null) {
                                String uri = handle.getIdentifier().toExternalForm();
                                if (!uri.contains(contain)) {
                                    return;
                                }
                            }

                            IFriend friendly = (IFriend) element.createExecutableExtension("class"); //$NON-NLS-1$
                            friends.addAll(friendly.friendly(handle, null));
                        } catch (Throwable t) {
                            CatalogPlugin.log(t.getLocalizedMessage(), t);
                        }
                    }
                });
        return friends;
    }

    /**
     * Utility method to quick hunt for matching service.
     * <p>
     * We are depending on the provided ID being unique for this catalog; please note that in the
     * event the ID locates an IForward instances we will find and return the replacement.
     * <p>
     * 
     * @param id Identification of service to find
     * @return Found service handle or null
     */
    private IService getServiceById( final ID id ) {
        if (id == null)
            return null;
        for( IService service : services ) {
            if (id.equals(service.getID())) {
                return service;
            }
        }
        return null;
    }

    public <T extends IResolve> T getById( Class<T> type, final ID id, IProgressMonitor monitor ) {

        IProgressMonitor monitor2 = monitor;;
        if (monitor2 == null)
            monitor2 = new NullProgressMonitor();
        if (id == null)
            return null;

        if (IService.class.isAssignableFrom(type)) {
            monitor2.beginTask(Messages.CatalogImpl_monitorTask, 1);
            IService service = getServiceById(id);
            monitor2.done();
            return type.cast(service);
        }

        URL url = id.toURL();
        if (IResolve.class.isAssignableFrom(type)) {
            for( IService service : services ) {
                if (URLUtils.urlEquals(url, service.getIdentifier(), true)) {
                    IResolve child = getChildById(service, id, false, monitor2);
                    if (child != null)
                        return type.cast(child);
                }
            }
        }
        return null;
    }

    // @Override
    // public <T extends IResolve> T getById(Class<T> type, final URL id, IProgressMonitor monitor)
    // {
    // return getById(type, new ID(id), monitor);
    // }

    /**
     * Utility method that will search in the provided handle for an ID match; especially good for
     * nested content such as folders or WMS layers.
     * <p>
     * <h4>Old Comment</h4> The following comment was original included in the source code: we are
     * not sure it should be believed ... we will do our best to search CONNECTED services first,
     * nut NOTCONNECTED is included in our search. <quote> Although the following is a 'blocking'
     * call, we have deemed it safe based on the following reasons:
     * <ul>
     * <li>This will only be called for Identifiers which are well known.
     * <li>The Services being checked have already been screened, and only a limited number of
     * services (usually 1) will be called.
     * <ol>
     * <li>The Id was acquired from the catalog ... and this is a look-up, in which case the uri
     * exists.
     * <li>The Id was persisted.
     * </ol>
     * </ul>
     * In the future this will also be free, as we plan on caching the equivalent of a
     * getCapabilities document between runs (will have to be updated too as the app has time).
     * </quote> Repeat the following comment is out of date since people are using this method to
     * look for entries that have not been added to the catalog yet.
     * 
     * @param roughMatch an ID consists of a URL and other info like a typeQualifier if roughMatch
     *        is true then the extra information is ignored during search
     */
    public IResolve getChildById( IResolve handle, final ID id, boolean roughMatch,
            IProgressMonitor monitor ) {
        IProgressMonitor monitor2 = monitor;
        if (monitor2 == null)
            monitor2 = new NullProgressMonitor();

        if (roughMatch) {
            if (new ID(id.toURL()).equals(new ID(handle.getIdentifier()))) {
                return handle;
            }
        } else {
            if (id.equals(handle.getID())) {
                return handle;
            }
        }
        try {
            List< ? extends IResolve> children = handle.members(monitor2);
            if (children == null || children.isEmpty())
                return null;

            monitor2.beginTask(Messages.CatalogImpl_monitorTask2, children.size());
            for( IResolve child : children ) {
                IResolve found = getChildById(child, id, roughMatch, null);
                if (found != null)
                    return found;
            }
        } catch (IOException e) {
            CatalogPlugin.log("Could not search children of " + handle.getIdentifier(), e); //$NON-NLS-1$
        }
        return null;
    }

    /**
     * Performs a search on this catalog based on the specified inputs. The pattern uses the
     * following conventions: use " " to surround a phase use + to represent 'AND' use - to
     * represent 'OR' use ! to represent 'NOT' use ( ) to designate scope The bbox provided shall be
     * in Lat - Long, or null if the search is not to be contained within a specified area.
     * 
     * @see net.refractions.udig.catalog.ICatalog#search(java.lang.String,
     *      com.vividsolutions.jts.geom.Envelope)
     * @param pattern
     * @param bbox used for an intersection test
     * @return
     */
    public synchronized List<IResolve> search( String pattern, Envelope bbox,
            IProgressMonitor monitor2 ) {
        
        if( CatalogPlugin.getDefault().isDebugging() ){
            if( Display.getCurrent() != null ){
                throw new IllegalStateException("search called from display thread");
            }
        }
        
        IProgressMonitor monitor = monitor2;
        if (monitor == null)
            monitor = new NullProgressMonitor();
        if ((pattern == null || "".equals(pattern.trim())) //$NON-NLS-1$
                && (bbox == null || bbox.isNull())) {
            return new LinkedList<IResolve>();
        }
        List<IResolve> result = new LinkedList<IResolve>();
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
                //Iterator< ? extends IGeoResource> resources;
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
                Thread.yield(); // allow other threads to have a go... makes search view more responsive
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

    /**
     * Fire a resource changed event, these may be batched into one delta for performance.
     * 
     * @param resoruce IGeoResource undergoing change
     * @param mask of IDelta constants indicating change
     * @throws IOException protected void fireResourceEvent( IGeoResource resource,
     *         IResolveDelta.Kind kind ) throws IOException { Object[] listeners =
     *         catalogListeners.getListeners(); if( listeners.length == 0 ) return;
     *         GeoReferenceDelta rDelta = new GeoReferenceDelta( resource, kind ); ServiceDelta
     *         sDelta = new ServiceDelta( resource.getService(null), IDelta.Kind.NO_CHANGE,
     *         Collections.singletonList( rDelta ) ); CatalogDelta cDelta = new CatalogDelta(
     *         Collections.singletonList( (IDelta)sDelta ) ); fire( new CatalogChangeEvent(
     *         resource, ICatalogChangeEvent.Type.POST_CHANGE, cDelta ) ); }
     */
    public void fire( IResolveChangeEvent event ) {
        if (catalogListeners.size() == 0)
            return;

        HashSet<IResolveChangeListener> copy;
        copy = getListenersCopy();

        for( IResolveChangeListener listener : copy ) {
            try {
                listener.changed(event);
            } catch (Throwable die) {
                CatalogPlugin.log("Catalog event could not be delivered to "
                        + listener.getClass().getSimpleName() + ":" + listener.toString(), die);
                die.printStackTrace();
            }
        }
    }

    /**
     * safely makes a copy of the listeners
     */
    private HashSet<IResolveChangeListener> getListenersCopy() {
        HashSet<IResolveChangeListener> copy;
        synchronized (catalogListeners) {
            copy = new HashSet<IResolveChangeListener>(catalogListeners);
        }
        return copy;
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @SuppressWarnings(value={"unchecked" )
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor2 ) {
        IProgressMonitor monitor;
        if (monitor2 == null)
            monitor = new NullProgressMonitor();
        else
            monitor = monitor2;
        try {
            if (adaptee == null)
                return null;
            monitor.beginTask(Messages.CatalogImpl_resolving + adaptee.getSimpleName(), 2);
            monitor.worked(1);
            if (adaptee.isAssignableFrom(CatalogImpl.class))
                return adaptee.cast(this);
            if (adaptee.isAssignableFrom(CatalogInfoImpl.class))
                return adaptee.cast(metadata);
            if (adaptee.isAssignableFrom(services.getClass()))
                return adaptee.cast(services);
            if (adaptee.isAssignableFrom(List.class))
                return adaptee.cast(new LinkedList<IService>(services));
            if (adaptee.isAssignableFrom(catalogListeners.getClass()))
                return adaptee.cast(getListenersCopy());
        } finally {
            monitor.worked(1);
            monitor.done();
        }
        return null;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        Object value = resolve(adaptee, null);
        return value != null;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<IResolve> members( IProgressMonitor monitor2 ) {
        IProgressMonitor monitor = monitor2;
        if (monitor == null)
            monitor = new NullProgressMonitor();
        monitor.beginTask(Messages.CatalogImpl_finding, 1);
        monitor.done();
        return new LinkedList<IResolve>(services);
    }

    public String getTitle() {
        return metadata.getTitle();
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return Status.CONNECTED;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return null;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return metadata.getSource();
    }

    public ID getID() {
        return new ID(getIdentifier());
    }

    @Override
    public IGeoResource createTemporaryResource( Object descriptor ) {
        List<IConfigurationElement> list = ExtensionPointList
                .getExtensionPointList(TEMPORARY_RESOURCE_EXT_ID);
        for( IConfigurationElement element : list ) {
            try {
                String attribute = element.getAttribute("descriptorClass"); //$NON-NLS-1$
                Class< ? > c = descriptor.getClass().getClassLoader().loadClass(attribute);
                if (c.isAssignableFrom(descriptor.getClass())) {
                    TemporaryResourceFactory fac = (TemporaryResourceFactory) element
                            .createExecutableExtension("factory"); //$NON-NLS-1$
                    return fac.createResource(descriptor);
                }
            } catch (ClassNotFoundException e) {
                // thats fine. Lets allow tracing to get this.
                CatalogPlugin.trace("Trying to match classes", e); //$NON-NLS-1$
            } catch (Exception e) {
                throw (RuntimeException) new RuntimeException().initCause(e);
            }
        }
        throw new IllegalArgumentException(descriptor.getClass()
                + " is not a legal descriptor type.  If must be one of " + //$NON-NLS-1$
                String.valueOf(getTemporaryDescriptorClasses()));
    }

    @Override
    public synchronized String[] getTemporaryDescriptorClasses() {
        if (descriptors == null) {
            List<IConfigurationElement> list = ExtensionPointList
                    .getExtensionPointList(TEMPORARY_RESOURCE_EXT_ID);
            ArrayList<String> temp = new ArrayList<String>();
            for( IConfigurationElement element : list ) {
                try {
                    String desc = element.getAttribute("descriptorClass"); //$NON-NLS-1$
                    if (desc != null)
                        temp.add(desc);
                } catch (Exception e) {
                    CatalogPlugin.log("", e); //$NON-NLS-1$
                }
            }
            descriptors = temp.toArray(new String[temp.size()]);
        }
        int i = 0;
        if (descriptors != null)
            i = descriptors.length;
        String[] k = new String[i];
        if (descriptors != null)
            System.arraycopy(descriptors, 0, k, 0, k.length);
        return k;
    }

    public void loadFromFile( File catalogLocation, IServiceFactory factory ) {
        try {
            FileInputStream input = new FileInputStream(catalogLocation);
            IPreferencesService preferencesService = Platform.getPreferencesService();
            IExportedPreferences paramsNode = preferencesService.readPreferences(input);

            ServiceParameterPersister persister = new ServiceParameterPersister(this, factory,
                    catalogLocation);

            persister.restore(findParameterNode(paramsNode));
        } catch (Throwable e) {
            // ok maybe it is an from an older version of uDig so try the oldCatalogRef
            try {
                IPreferencesService prefs = Platform.getPreferencesService();
                IEclipsePreferences root = prefs.getRootNode();
                Preferences node = root.node(InstanceScope.SCOPE).node(
                        CatalogPlugin.ID + ".services"); //$NON-NLS-1$
                ServiceParameterPersister persister = new ServiceParameterPersister(this, factory);
                persister.restore(node);
            } catch (Throwable e2) {
                CatalogPlugin.log("Unable to load services", e); //$NON-NLS-1$
            }
        }
    }

    private Preferences findParameterNode( IExportedPreferences paramsNode )
            throws BackingStoreException {
        String[] name = paramsNode.childrenNames();

        Preferences plugin = paramsNode.node(name[0]);
        name = plugin.childrenNames();

        return plugin.node(name[0]);
    }
    /**
     * Save this local catalog to the provided file.
     * 
     * @param catalogLocation
     * @param factory
     * @param monitor
     */
    public void saveToFile( File catalogLocation, IServiceFactory factory, IProgressMonitor monitor ) {
        try {
            Preferences toSave = Platform.getPreferencesService().getRootNode().node(
                    CatalogPlugin.ID).node("LOCAL_CATALOG_SERVICES"); //$NON-NLS-1$
            if (services != null) {
                ServiceParameterPersister persister = new ServiceParameterPersister(this, factory,
                        catalogLocation.getParentFile());

                persister.store(monitor, toSave, services);
            }

            FileOutputStream out = new FileOutputStream(catalogLocation);
            Platform.getPreferencesService().exportPreferences((IEclipsePreferences) toSave, out,
                    null);
            toSave.clear();

        } catch (Throwable t) {
            CatalogPlugin.log("Error saving services for the local catalog", t); //$NON-NLS-1$ 
        }
    }
    //
    // Interceptors
    //
    /**
     * Run the service interceptors for the indicated activity.
     * 
     * @param activity ADDED_ID, CREATED_ID, REMOVED_ID
     */
    public static void runInterceptor( IService service, String activity ) {
        if (!ServiceInterceptor.ADDED_ID.equals(activity)
                && !ServiceInterceptor.REMOVED_ID.equals(activity)
                && !ServiceInterceptor.CREATED_ID.equals(activity)) {
            return; // no activities defined
        }
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(ServiceInterceptor.EXTENSION_ID);

        for( IConfigurationElement element : interceptors ) {
            if (!activity.equals(element.getName())) {
                continue;
            }
            try {
                ServiceInterceptor interceptor = (ServiceInterceptor) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
                interceptor.run(service);
            } catch (Exception e) {
                CatalogPlugin.trace( activity +" "+element.getAttribute("class")+":"+e, e); //$NON-NLS-1$
            }
        }
    }
    
    /**
     * Run the resource interceptors for the indicated activity.
     * 
     * @param activity ADDED_ID, CREATED_ID, REMOVED_ID
     */
    public static void runInterceptor( IGeoResource resource, String activity ) {
        if (!GeoResourceInterceptor.ADDED_ID.equals(activity)
                && !GeoResourceInterceptor.REMOVED_ID.equals(activity)) {
            return; // no activities defined
        }
        List<IConfigurationElement> interceptors = ExtensionPointList
                .getExtensionPointList(ServiceInterceptor.EXTENSION_ID);

        for( IConfigurationElement element : interceptors ) {
            if (!activity.equals(element.getName())) {
                continue;
            }
            try {
                GeoResourceInterceptor interceptor = (GeoResourceInterceptor) element
                        .createExecutableExtension("class"); //$NON-NLS-1$
                interceptor.run(resource);
            } catch (Exception e) {
                CatalogPlugin.trace( activity +" "+element.getAttribute("class")+":"+e, e); //$NON-NLS-1$
            }
        }
    }
    
    /**
     * Takes a list of IServices and prioritises them by the percentage of metadata available. This
     * method relies on IServiceInfo.getMetric() for metadata metric calculations. Subclasses are
     * encouraged to override IServiceInfo getMetric() to calculate required metadata for each
     * services.
     * 
     * @param services A list of IServices to be prioritised.
     * @param monitor Used to track the process of connecting
     * @return
     * @see #IserviceInfo.getMetric()
     */
    private List<IService> prioritise( List<IService> services, IProgressMonitor monitor ) {
        
        //If there is less than 2 IService there is no sorting required. 
        if(services.size() < 2){
            return services;
        }
        
        final IProgressMonitor monitor2 = new SubProgressMonitor(monitor, 60);

        class IServiceComparator implements Comparator<IService> {
            
            public int compare( IService o1, IService o2 ) {
                try {
                    IServiceInfo info1 = o1.getInfo(new SubProgressMonitor(monitor2, 1));
                    IServiceInfo info2 = o2.getInfo(new SubProgressMonitor(monitor2, 1));

                    if (info1.getMetric() > info2.getMetric()) {
                        return 1;
                    }else if(info1.getMetric() < info2.getMetric()){
                       return -1;
                    }else{
                       return 0;
                    }
                    
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return 0;
            }
        }
       
        Comparator<IService> comparator = new IServiceComparator();
        Collections.sort(services, comparator);

        return services;
    }

    @Override
    public List<IService> checkMembers( List<IService> constructServiceList ) {
        List<IService> catalogServices = new ArrayList<IService>();

        for( IService service : constructServiceList ) {

            ID id = service.getID();
            IService found = getById(IService.class, id, new NullProgressMonitor());

            if (!(found == null)) {
                catalogServices.add(service);
            }

        }

        return catalogServices;
    }

    @Override
    public List<IService> checkNonMembers( List<IService> constructServiceList ) {
        List<IService> catalogServices = new ArrayList<IService>();

        for( IService service : constructServiceList ) {

            ID id = service.getID();
            IService found = getById(IService.class, id, new NullProgressMonitor());

            if (found == null) {
                catalogServices.add(service);
            }
        }

        return catalogServices;
    }

    @Override
    public List<IService> constructServices(URL url, IProgressMonitor monitor )
            throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        
        if (url == null){
            return null;
        }
        
        int urlProcessCount = 0;
        
        List<IService> availableServices = new ArrayList<IService>();//services already in catalog

        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();

        monitor.beginTask("Check", 1);
        monitor.subTask("Check available services");

        try {
            List<IService> possible = factory.createService(url);
            monitor.worked(urlProcessCount);

            IProgressMonitor monitor3 = new SubProgressMonitor(monitor, 60);
            monitor3.beginTask("connect", possible.size() * 10);

            for( Iterator<IService> iterator = possible.iterator(); iterator.hasNext(); ) {
                IService service = iterator.next();

                if (service == null) continue;

                monitor3.subTask("connect " + service.getID());
                try {
                    // try connecting
                    IServiceInfo info = service.getInfo(new SubProgressMonitor(monitor3, 10));

                    if (info == null) {
                        CatalogPlugin.trace("unable to connect to " + service.getID(), null);
                        continue; // skip unable to connect
                    }

                    availableServices.add(service);
                } catch (Throwable t) {
                    // usually indicates an IOException as the service is unable to connect
                    CatalogPlugin.trace("trouble connecting to " + service.getID(), t);
                }
            }
            monitor3.done();
        } finally {
            monitor.done();
        }
        return prioritise(availableServices, monitor); //return a prioritise list
    }

    @Override
    public List<IService> constructServices( Map<String, Serializable> params,
            IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        List<IService> availableServices = new ArrayList<IService>();//services already in catalog

        IServiceFactory factory = CatalogPlugin.getDefault().getServiceFactory();

        try {

            if (params != null && !params.isEmpty()) {
                Set<IService> results = new HashSet<IService>(factory.createService(params));
                for( IService service : results ) {
                    
                    IServiceInfo info = service.getInfo(new SubProgressMonitor(monitor, 10));
                    
                    if (info == null) {
                        CatalogPlugin.trace("unable to connect to " + service.getID(), null);
                        continue; // skip unable to connect
                    }

                    availableServices.add(service);
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            monitor.done();
        }
        
        return prioritise(availableServices, monitor); //return a prioritise list
    }
}