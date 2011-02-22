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
package net.refractions.udig.catalog.internal;

import static net.refractions.udig.catalog.IResolve.Status.CONNECTED;
import static net.refractions.udig.catalog.IResolve.Status.NOTCONNECTED;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ICatalog;
import net.refractions.udig.catalog.ICatalogInfo;
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
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Implementation of an in memory catalog.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
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

    /**
     * @see net.refractions.udig.catalog.ICatalog#removeCatalogListener(net.refractions.udig.catalog.ICatalog.ICatalogListener)
     * @param listener
     */
    public void removeCatalogListener( IResolveChangeListener listener ) {
        catalogListeners.remove(listener);
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#add(net.refractions.udig.catalog.IService)
     * @param entry
     * @throws UnsupportedOperationException
     */
    public void add( IService entry ) throws UnsupportedOperationException {
        if (entry == null || entry.getIdentifier() == null)
            throw new NullPointerException("Cannot have a null id"); //$NON-NLS-1$
        if( !findService(entry.getIdentifier()).isEmpty() )
            return;
        services.add(entry);
        IResolveDelta deltaAdded = new ResolveDelta(entry, IResolveDelta.Kind.ADDED);
        IResolveDelta deltaChanged = new ResolveDelta(this, Collections.singletonList(deltaAdded));
        fire(new ResolveChangeEvent(CatalogImpl.this, IResolveChangeEvent.Type.POST_CHANGE,
                deltaChanged));
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#remove(net.refractions.udig.catalog.IService)
     * @param entry
     * @throws UnsupportedOperationException
     */
    public void remove( IService entry ) throws UnsupportedOperationException {
        if (entry == null || entry.getIdentifier() == null)
            throw new NullPointerException("Cannot have a null id"); //$NON-NLS-1$
        IResolveDelta deltaRemoved = new ResolveDelta(entry, IResolveDelta.Kind.REMOVED);
        IResolveDelta deltaChanged = new ResolveDelta(this, Collections.singletonList(deltaRemoved));
        fire(new ResolveChangeEvent(CatalogImpl.this, IResolveChangeEvent.Type.PRE_DELETE,
                deltaChanged));
        services.remove(entry);
        fire(new ResolveChangeEvent(CatalogImpl.this, IResolveChangeEvent.Type.POST_CHANGE,
                deltaRemoved));
    }

    /**
     * This implementation will store a IForward handle
     * at the indicated *id*, recording the fact that entry
     * has now moved.
     * <p>
     * If the id == entry.getIdentifier() this method is being used to
     * "reset" the current <b>id</b> service handle.
     */
    public void replace( URL id, IService entry ) throws UnsupportedOperationException {
        if (entry == null || entry.getIdentifier() == null || id == null){
            throw new NullPointerException("Cannot have a null id"); //$NON-NLS-1$
        }
        final IService service = getServiceById(id);
        List<IResolveDelta> changes = new ArrayList<IResolveDelta>();
        List<IResolveDelta> childChanges = new ArrayList<IResolveDelta>();
        try {
        	List< ? extends IGeoResource> newChildren = entry.resources(null);
        	List< ? extends IGeoResource> oldChildren = service.resources(null);
            if( oldChildren!=null )
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
        changes.add(new ResolveDelta(service, entry, childChanges));

        IResolveDelta deltas = new ResolveDelta(this, changes);
        IResolveChangeEvent event = new ResolveChangeEvent(this,
                IResolveChangeEvent.Type.PRE_DELETE, deltas);
        fire(event);
        services.remove(service);

        PlatformGIS.run(new IRunnableWithProgress(){

            public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
                try{
                    service.dispose( monitor );
                } catch (Throwable e) {
                    CatalogPlugin.log("error disposing of: "+service.getIdentifier(), e); //$NON-NLS-1$
                }
            }

        });

        services.add(entry);
        event = new ResolveChangeEvent(this,
                IResolveChangeEvent.Type.POST_CHANGE, deltas);

        if( !id.equals( entry.getIdentifier())){
            // the service has actually moved
            IService moved = new MovedService( id, entry.getIdentifier() );
            services.add( moved );
        }
        fire(event);
    }

    /**
     * Quick search by url match.
     * @param query
     *
     * @see net.refractions.udig.catalog.ICatalog#search(org.geotools.filter.Filter)
     * @return List<IResolve>
     * @throws IOException
     */
    public List<IResolve> find( URL query, IProgressMonitor monitor ) {
        Set<IResolve> found = new LinkedHashSet<IResolve>();

        // first pass 1.1- use urlEquals on CONNECTED service for subset check
        for( IService service : services ) {
            if( service.getStatus() != CONNECTED ) continue; // skip non connected service
            URL identifier = service.getIdentifier();
            if ( URLUtils.urlEquals(query, identifier, true)) {
                if( matchedService(query, identifier)){
            		found.add(service);
            		found.addAll( friends( service ));
                    break;
            	}
            	else {
                    IResolve res = getChildById(service, query, monitor);
                    if( res!=null ){
                        found.add(res);
                		found.addAll( friends( res));
                        break;
                    }
                }
            }
        }
        // first pass 1.2 - use urlEquals on unCONNECTED service for subset check
        for( IService service : services ) {
            if( service.getStatus() == CONNECTED ) continue; // already checked in pass 1.1
            URL identifier = service.getIdentifier();
            if ( URLUtils.urlEquals(query, identifier, true)) {
                if( service.getStatus() != NOTCONNECTED ) continue; // look into notconnected service that "match"
                if( matchedService(query, identifier) ){
                    found.add(service);
                    found.addAll( friends( service ));
                    break;
                }
                else {
                    IResolve res = getChildById(service, query, monitor);
                    if( res!=null ){
                        found.add(res);
                        found.addAll( friends( res));
                        break;
                    }
                }
            }
        }
        // first pass 1.3 - use urlEquals on BROKEN or RESTRICTED_ACCESS service for subset check
        // the hope here is that a "friend" will still have data! May be tough for friends
        // to negotiate a match w/ a broken services - but there is still hope...
        for( IService service : services ) {
            if( service.getStatus() == CONNECTED || service.getStatus() == NOTCONNECTED) {
                continue; // already checked in pass 1.1-1.2
            }
            URL identifier = service.getIdentifier();
            if ( URLUtils.urlEquals(query, identifier, true)) {
                if( matchedService(query, identifier) ){
                    found.add(service);
                    found.addAll( friends( service ));
                    break;
                }
                else {
                    IResolve res = getChildById(service, query, monitor);
                    if( res!=null ){
                        found.add(res);
                        found.addAll( friends( res));
                        break;
                    }
                }
            }
        }
        // second pass - deep check for georesource on connected services
        // these are resources that dont match the server#membername pattern
        //
        // This code removed for udig 1.1 release
        /*
        if( found.isEmpty() && query.getRef()!=null ){
            // we have not found anything; and we are looking for a georesource
            // search connected resources ...
            for( IService service : services ) {
                if( service.getStatus() == CONNECTED ){
                    IResolve res = getChildById(service, query, monitor);
                    if( res!=null ){
                        found.add(res);
                        found.addAll( friends( res));
                        break;
                    }
                }
            }
        }
        */
        // thirdpass - deep check for georesource on unconnected services
        //
        // This code removed for udig 1.1 release
        /*
        if( found.isEmpty() && query.getRef()!=null ){
            if( false ){
               CatalogPlugin.log("Warning Deep search in catalog is occurring this is VERY expensive", new Exception("JUST A WARNING")); //$NON-NLS-1$ //$NON-NLS-2$
            }
            // we have not found anything; and we are looking for a georesource
            // search not connected resources ...
            for( IService service : services ) {
                if( service.getStatus() == NOTCONNECTED ){
                    IResolve res = getChildById(service, query, monitor);
                    if( res!=null ){
                        found.add(res);
                        found.addAll( friends( res));
                        break;
                    }
                }
            }
        }
        */
        return new ArrayList<IResolve>( found );
    }

    /**
     * Check if the provided query is a child of identifier.
     *
     * @param query
     * @param identifier
     * @return true if query may be a child of identifier
     */
	private boolean matchedService(URL query, URL identifier) {
		return query.getRef()==null && URLUtils.urlEquals(query, identifier, false);
	}

    /**
     * Returns a list of friendly resources working with related data.
     * <p>
     * This method is used internally to determine resource handles that
     * offer different entry points to the same information.
     * </p>
     * A friend can be found via:
     * <ul>
     * <li>Making use of a CSW2.0 association
     * <li>URL Pattern matching for well known cases like GeoServer and MapServer
     * <li>Service Metadata, for example WMS resourceURL referencing a WFS FeatureType
     * </ul>
     * All of these handles will be returned from the find( URL, monitor ) method.
     * </ul>
     * @param handle
     * @return List of frends, possibly empty
     */
    public List<IResolve> friends( final IResolve handle ){
    	if( false ){
    		return Collections.emptyList();
    	}
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
                    		if( target != null ){
                    			// perform target check
                    			if( target.equals(target.getClass().toString()) ){
                    				return;
                    			}
                    		}
                    		if( contain != null ){
                    			String uri = handle.getIdentifier().toExternalForm();
                    			if( !uri.contains( contain )){
                    				return;
                    			}
                    		}

                    		IFriend friendly = (IFriend) element.createExecutableExtension("class"); //$NON-NLS-1$
                    		friends.addAll( friendly.friendly(handle, null) );
                    	}
                    	catch(Throwable t) {
                    		CatalogPlugin.log(t.getLocalizedMessage(),t);
                    	}
                    }
                });
        return friends;
    }

    /**
     * Quick search by url match.
     *
     * @see net.refractions.udig.catalog.ICatalog#search(org.geotools.filter.Filter)
     * @param query
     * @return List<IResolve>
     * @throws IOException
     * @deprecated use getServiceById
     */
    public List<IService> findService( URL query ) {
    	IService service = getServiceById( query );
    	if( service == null )
    		return Collections.emptyList();
    	else
    		return Collections.singletonList( service );
    }

    /**
     * Utility method to quick hunt for matching service.
     * <p>
     * We are depending on the provided ID being unique for this catalog;
     * please note that in the event the ID locates an IForward instances
     * we will find and return the replacement.
     * <p>
     * @param ID Identification of service to find
     * @return Found service handle or null
     */
    public IService getServiceById( final URL ID ){
        if( ID == null || ID.getRef() != null ) return null;
        for( IService service : services ) {
            if ( URLUtils.urlEquals( ID, service.getIdentifier(), false) ) {
            	return service;
            }
        }
        return null;
    }

    @Override
    public <T extends IResolve> T getById(Class<T> type, final URL ID, IProgressMonitor monitor) {

    	IProgressMonitor monitor2=monitor;;
        if( monitor2 == null) monitor2 = new NullProgressMonitor();
    	if( ID == null ) return null;

    	if( IService.class.isAssignableFrom( type )){
    		monitor2.beginTask(Messages.CatalogImpl_monitorTask,1);
    		IService service = getServiceById( ID );
    		monitor2.done();
    		return type.cast(service);
    	}

    	if( IResolve.class.isAssignableFrom( type ) ){
            for( IService service : services ) {
            	if( URLUtils.urlEquals( ID, service.getIdentifier(), true ) ){
            		IResolve child = getChildById( service, ID, monitor2 );
            		if( child != null ) return type.cast( child );
            	}
            }
    	}
    	return null;
    }

    /**
     * Utility method that will search in the provided handle for an ID match; especially good for
     * nested content such as folders or WMS layers.
     *
     * <h4>Old Comment</h4>
     * The following comment was origional included in the source code: we are not sure it should
     * be belived ... we will do our best to search CONNECTED services first, nut NOTCONNECTED
     * is included in our search.
     * <quote>
     * Although the following is a 'blocking' call, we have deemed it safe based on
     * the following reasons:
     * <ul>
     * <li>This will only be called for Identifiers which are well known.
     * <li>The Services being checked have already been screened, and only a
     * limited number of services (usually 1) will be called.
     *    <ol>
     *    <li>The Id was acquired from the catalog ... and this is a look-up, in which case the uri exists.
     *    <li>The Id was persisted.
     *    </ol>
     * </ul>
     * In the future this will also be free, as we plan on caching the equivalent of a getCapabilities
     * document between runs (will have to be updated too as the app has time).
     * </quote>
     * Repeate the following comment is out of date since people are using this method to look
     * for entries that have not been aded to the catalog yet.
     */
    public IResolve getChildById( IResolve handle, final URL ID, IProgressMonitor monitor) {
    	IProgressMonitor monitor2=monitor;
        if( monitor2 == null) monitor2 = new NullProgressMonitor();

    	if ( URLUtils.urlEquals( handle.getIdentifier(), ID, false ) ) {
    		return handle;
    	}
		try {
			List<? extends IResolve> children = handle.members(monitor2);
			if( children == null || children.isEmpty() ) return null;

			monitor2.beginTask(Messages.CatalogImpl_monitorTask2, children.size());
			for( IResolve child : children ){
				IResolve found = getChildById( child, ID, null );
				if( found != null ) return found;
			}
		} catch (IOException e) {
			CatalogPlugin.log( "Could not search children of "+handle.getIdentifier(), e); //$NON-NLS-1$
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
        IProgressMonitor monitor = monitor2;
    	if (monitor==null)
    		monitor=new NullProgressMonitor();
        if ((pattern == null || "".equals(pattern.trim())) //$NON-NLS-1$
        		&& (bbox==null || bbox.isNull())) {
            return new LinkedList<IResolve>();
        }

        AST ast=null;
        if ( pattern!=null && !"".equals(pattern.trim())) //$NON-NLS-1$
        ast= ASTFactory.parse(pattern);

        // TODO check cuncurrency issues here

        List<IResolve> result = new LinkedList<IResolve>();
        HashSet<IService> tmp = new HashSet<IService>();
        tmp.addAll(this.services);
        try{
        	monitor.beginTask(Messages.CatalogImpl_finding,tmp.size()*10);
        Iterator<IService> services = tmp.iterator();
        if (services != null) {
            while( services.hasNext() ) {
                IService service = services.next();
                if (check(service, ast)) {
                    result.add(service);
                }
                Iterator< ? extends IGeoResource> resources;
            	SubProgressMonitor submonitor=new SubProgressMonitor(monitor, 10);
                try {
                    List< ? extends IGeoResource> t = service.resources(submonitor);
                    resources = t == null ? null : t.iterator();
                    while( resources != null && resources.hasNext() ) {
                        IGeoResource resource = resources.next();
                        if (check(resource, ast, bbox)) {
                            result.add(resource);
                        }
                    }
                } catch (IOException e) {
                    CatalogPlugin.log(null, e);
                }finally{
                	submonitor.done();
                }
            }
        }
        return result;
        }finally{
        	monitor.done();
        }
    }

    /* check the fields we catre about */
    protected static boolean check( IService service, AST pattern ) {
        if( pattern==null ){
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
                String[] keys = info.getKeywords();
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
    	if( pattern==null )
    		return true;
        IGeoResourceInfo info;
        try {
            info = (resource == null ? null : resource.getInfo(null));
        } catch (IOException e) {
            CatalogPlugin.log(null, e);
            info = null;
        }
        boolean t = false;
        if (info != null) {
            if (info.getTitle() != null)
                t = pattern.accept(info.getTitle());
            if (!t && info.getName() != null)
                t = pattern.accept(info.getName());
            if (!t && info.getKeywords() != null) {
                String[] keys = info.getKeywords();
                for( int i = 0; !t && i < keys.length; i++ )
                    if (keys[i] != null)
                        t = pattern.accept(keys[i]);
            }
            if (!t && info.getSchema() != null)
                t = pattern.accept(info.getSchema().toString());
            if (!t && info.getDescription() != null)
                t = pattern.accept(info.getDescription());
        }
        return t;
    }

    protected static boolean check( IGeoResource resource, AST pattern, Envelope bbox ) {
        if (!check(resource, pattern))
            return false;
        if (bbox == null || bbox.isNull())
            return true; // no checking here
        try {
            return bbox.intersects(resource.getInfo(null).getBounds());
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

        for( IResolveChangeListener listener:copy) {
            try {
                listener.changed(event);
            } catch (Throwable die) {
                CatalogPlugin.log(null, new Exception(die));
            }
        }
    }

    /**
     * safely makes a copy of the listeners
     */
    private HashSet<IResolveChangeListener> getListenersCopy() {
        HashSet<IResolveChangeListener> copy;
        synchronized (catalogListeners) {
            copy=new HashSet<IResolveChangeListener>(catalogListeners);
        }
        return copy;
    }

    /**
     * @see net.refractions.udig.catalog.ICatalog#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @SuppressWarnings(value={"unchecked"})
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor2 ) {
        IProgressMonitor monitor ;
    	if (monitor2==null)
    		monitor=new NullProgressMonitor();
        else
            monitor = monitor2;
    	try{
    	    if (adaptee == null)
    	        return null;
    	monitor.beginTask(Messages.CatalogImpl_resolving+adaptee.getSimpleName(), 2);
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
    	}finally{
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
    	if( monitor==null )
    		monitor=new NullProgressMonitor();
    	monitor.beginTask(Messages.CatalogImpl_finding, 1);
    	monitor.done();
        return new LinkedList<IResolve>(services);
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

    @Override
    public IGeoResource createTemporaryResource( Object descriptor ) {
        List<IConfigurationElement> list = ExtensionPointList.getExtensionPointList(TEMPORARY_RESOURCE_EXT_ID);
        for( IConfigurationElement element : list ) {
            try{
                Class<?> c=descriptor.getClass().getClassLoader().loadClass(element.getAttribute("descriptorClass")); //$NON-NLS-1$
                if( c.isAssignableFrom(descriptor.getClass()) ){
                    TemporaryResourceFactory fac=(TemporaryResourceFactory) element.createExecutableExtension("factory"); //$NON-NLS-1$
                    return fac.createResource(descriptor);
                }
            } catch (ClassNotFoundException e) {
                //thats fine.  Lets allow tracing to get this.
                CatalogPlugin.trace("Trying to match classes", e); //$NON-NLS-1$
            } catch (Exception e) {
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        throw new IllegalArgumentException(descriptor.getClass()+" is not a legal descriptor type.  If must be one of " + //$NON-NLS-1$
                String.valueOf(getTemporaryDescriptorClasses()));
    }


    @Override
    public synchronized String[] getTemporaryDescriptorClasses() {
        if( descriptors==null ){
            List<IConfigurationElement> list = ExtensionPointList.getExtensionPointList(TEMPORARY_RESOURCE_EXT_ID);
            ArrayList<String> temp = new ArrayList<String>();
            for( IConfigurationElement element : list ) {
                try{
                    String desc = element.getAttribute("descriptorClass"); //$NON-NLS-1$
                    if( desc!=null )
                        temp.add(desc);
                }catch(Exception e){
                    CatalogPlugin.log("", e); //$NON-NLS-1$
                }
            }
            descriptors=temp.toArray(new String[temp.size()]);
        }
        int i = 0;
        if( descriptors!=null )
            i=descriptors.length;
        String[] k=new String[i];
        if( descriptors!=null )
            System.arraycopy(descriptors, 0, k, 0, k.length);
        return k;
   }

	public void loadFromFile(File catalogLocation, IServiceFactory factory) {
		try {
			FileInputStream input=new FileInputStream(catalogLocation);
			IPreferencesService preferencesService = Platform.getPreferencesService();
			IExportedPreferences paramsNode = preferencesService.readPreferences(input);

			ServiceParameterPersister persister=new ServiceParameterPersister(this, factory, catalogLocation);

			persister.restore(findParameterNode(paramsNode));
		} catch (Throwable e) {
			// ok maybe it is an from an older version of uDig so try the oldCatalogRef
			try{
		        IPreferencesService prefs = Platform.getPreferencesService();
		        IEclipsePreferences root = prefs.getRootNode();
		        Preferences node = root.node(InstanceScope.SCOPE).node(CatalogPlugin.ID + ".services"); //$NON-NLS-1$
		        ServiceParameterPersister persister=new ServiceParameterPersister(this, factory);
		        persister.restore(node);
			}catch(Throwable e2){
				CatalogPlugin.log("Unable to load services", e); //$NON-NLS-1$
			}
		}
	}

    private Preferences findParameterNode(IExportedPreferences paramsNode) throws BackingStoreException {
    	String[] name = paramsNode.childrenNames();

    	Preferences plugin = paramsNode.node(name[0]);
    	name = plugin.childrenNames();

    	return plugin.node(name[0]);
	}

	public void saveToFile(File catalogLocation, IServiceFactory factory, IProgressMonitor monitor) {
		try{
        	Preferences toSave = Platform.getPreferencesService().getRootNode().node(CatalogPlugin.ID).node("LOCAL_CATALOG_SERVICES");
	        if (services!=null) {
	            ServiceParameterPersister persister=new ServiceParameterPersister(this, factory, catalogLocation);

	            persister.store(monitor, toSave, services);
	        }

	        FileOutputStream out = new FileOutputStream(catalogLocation);
	        Platform.getPreferencesService().exportPreferences((IEclipsePreferences) toSave, out, null);
	        toSave.clear();

        }catch( Throwable t ){
        	CatalogPlugin.log("Error saving services for the local catalog",t);  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        }
    }

}
