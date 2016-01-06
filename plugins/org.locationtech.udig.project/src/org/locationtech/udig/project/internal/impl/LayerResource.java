/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.impl;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.core.internal.ExtensionPointList;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IResourceCachingInterceptor;
import org.locationtech.udig.project.IResourceInterceptor;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Wraps a IGeoResource for a layer.
 * <p>
 * This is to ensure that each item that is resolved to is the same instance regardless of how the
 * IGeoResource's resolve method is implemented.
 * 
 * @author Jesse
 * @since 1.0.0
 */
public class LayerResource extends IGeoResource {

    /**
     * The layer that owns/created the Resource
     */
    private final LayerImpl layer;

    /**
     * The resource that is wrapped by this object
     */
    IGeoResource geoResource;

    private volatile InterceptorsBag interceptors;

    /** Used to sort resource interceptors that are applied prior to the resource being cached. */
    private Sorter preSorter;

    /**
     * Used to sort resource interceptors that are applied after the cache.
     */
    private Sorter postSorter;
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj ) {
        if (obj instanceof LayerResource) {
            LayerResource other = (LayerResource) obj;
            return geoResource.equals(other.geoResource);
        }
        return false;
    }

    /**
     * @see org.locationtech.udig.catalog.IGeoResource#hashCode()
     */
    public int hashCode() {
        return geoResource.hashCode();
    }

    /**
     * Construct <code>LayerImpl.LayerResource</code>.
     */
    public LayerResource( LayerImpl impl, IGeoResource resource ) {
        service = null; // we do not have a parent we are a simple wrapper
        layer = impl;
        this.geoResource = resource;
    }

    /**
     * We can resolve to our layer; or anything our wrapped getResource can do.
     * @param adaptee Requested information
     * @return true if content of requested type (adaptee) is available
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (ILayer.class.isAssignableFrom(adaptee)) {
            return true;
        }

        return geoResource.canResolve(adaptee);
    }

    @Override
    public URL getIdentifier() {
        return geoResource.getIdentifier();
    }

    @Override
    public ID getID() {
        return this.geoResource.getID();
    }
    /**
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return geoResource.getMessage();
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return geoResource.getStatus();
    }

    public String getTitle() {
        return geoResource.getTitle();
    }
    
    /**
     * @see org.locationtech.udig.catalog.IGeoResource#getPersistentProperties()
     */
    public Map<String, Serializable> getPersistentProperties() {
        return geoResource.getPersistentProperties();
    }

    /**
     * @see org.locationtech.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<IResolve> members( IProgressMonitor monitor ) {
        // JG: I am not sure if this is correct; the children may need to be wrapped up as LayerResource themselves
        return geoResource.members(monitor);
    }

    /**
     * @see org.locationtech.udig.catalog.IGeoResource#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public synchronized <T> T resolve( Class<T> adaptee, IProgressMonitor monitor )
            throws IOException {
        if (ILayer.class.isAssignableFrom(adaptee)) {
            return adaptee.cast(layer);
        }
        if (IGeoResource.class == adaptee){
            return adaptee.cast(this); // access to the original
        }
        if (this.geoResource.getClass().isAssignableFrom(adaptee)){
            return adaptee.cast(geoResource); // access to the original
        }
//        T resolve = geoResource.resolve(adaptee, monitor);
//        if (resolve != null) {
//            return resolve;
//        }
        T resource = processResourceCachingStrategy(monitor, adaptee);
        if (resource == null){
            return null; // could not do it
        }
        resource = processPostResourceInterceptors(resource, adaptee);
        return resource;
    }
    @Override
    public IResolve parent( IProgressMonitor monitor ) throws IOException {
        return geoResource.parent(monitor);
    }
    private <T> T processPreResourceInterceptors( T resource, Class<T> requestedType ) {
        List<SafeResourceInterceptor<T>> pre = getPreInterceptors(resource);
        return runInterceptors(requestedType, resource, pre);
    }
    /**
     * This is where all the interceptors actually get applied.
     * 
     * @param <T> type of requested result
     * @param requestedType type of requested result
     * @param origionalResource value of requested type to be wrapped/configured
     * @param pre list of interceptors to "pre" process the resource before use
     * @return resource as modified/wrapped by any applicable interceptors
     */
    private <T> T runInterceptors( Class<T> requestedType, T origionalResource, List<SafeResourceInterceptor<T>> pre ) {
        // initially we start with the origional resource
        T resource = origionalResource;
        for( SafeResourceInterceptor<T> interceptor : pre ) {
            if (resource == null){
                // if the resource is null then we have nothing further we can do
                // (the resource was provided to us as null; or one of the interceptors
                //  has determined we don't have permission/clearance to view the data)
                return null;
            }
            if (isAssignable(resource, interceptor.targetType)){
                // if the interceptor is applicable; run it against the current resource
                resource = interceptor.run(layer, resource, requestedType);
                // The resource returned may be a wrapper (adding functionality) or
                // it may simply configure the resource (say provided the current transaction)
                // or it could return null if it determins that the resource should not be used
                //
            }
            // we need to continue through all the interceptors giving each a chance wrap/configurer
            // Currently all interceptors are considered equal; if needed we can introduce a priority
            // and apply them in a specific order
        }
        return resource; // the final resource
    }

    private <T> List<SafeResourceInterceptor<T>> getPreInterceptors( T resource ) {
        loadInterceptors();
        Set<Entry<IConfigurationElement, SafeResourceInterceptor<?>>> entires = interceptors.pre.entrySet();
        List<SafeResourceInterceptor<T>> result = findValidInterceptors(resource, entires);
        if (preSorter != null) {
            Collections.sort(result, preSorter);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> List<SafeResourceInterceptor<T>> findValidInterceptors( T resource,
            Set<Entry<IConfigurationElement, SafeResourceInterceptor<?>>> entires ) {
        List<SafeResourceInterceptor<T>> result = new ArrayList<SafeResourceInterceptor<T>>();
        for( Entry<IConfigurationElement, SafeResourceInterceptor<?>> entry : entires ) {
            String attribute = entry.getKey().getAttribute("target"); //$NON-NLS-1$
            if (attribute == null) {
                result.add((SafeResourceInterceptor<T>) entry.getValue());
            } else {
                boolean assignableFrom = isAssignable(resource, attribute);
                if (assignableFrom)
                    result.add((SafeResourceInterceptor<T>) entry.getValue());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> boolean isAssignable( T resource, String attribute ) {
        if (attribute == null)
            return true;
        boolean assignableFrom;
        try {
            Class< ? extends Object> class1 = resource.getClass();
            ClassLoader classLoader = class1.getClassLoader();
            if (classLoader == null) {
                // its a library class so use normal class loader
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            Class<T> loadClass = (Class<T>) Class.forName(attribute, true, classLoader);
            assignableFrom = (loadClass).isAssignableFrom(class1);
        } catch (ClassNotFoundException e) {
            return false;
        }
        return assignableFrom;
    }

    /**
     * Carefully checks if the requested information is available.
     * <p>
     * If the information has been asked for before; it is hoped a cached copy is available
     * as dictated by the various interceptors in play.
     * <p>
     * If a cached copy is not available; the geoResource is asked; and the result
     * wrapped up in any preResourceInterceptors before being cached for later.
     * <p>
     * This is the heart of Layer; tread carefully.
     * 
     * @param <T> Type of requested information
     * @param monitor Used to track progress and canel
     * @param requestedType requested information
     * @return the information requested; or null if it is unavailable
     * @throws IOException
     */
    private <T> T processResourceCachingStrategy( IProgressMonitor monitor, Class<T> requestedType )
            throws IOException {
        IResourceCachingInterceptor cachingStrategy = getCachingInterceptors();
        if (cachingStrategy == null) {
            // no caching in use; proceed as normal
            T rawResource = geoResource.resolve(requestedType, monitor);
            if (rawResource == null){                
                return null; // not available!
            }
            T resource = processPreResourceInterceptors(rawResource, requestedType);
            return resource;       
        }
        // we have a caching strategy
        //
        if (cachingStrategy.isCached(layer, geoResource, requestedType)) {
            T resource = cachingStrategy.get(layer, requestedType);
            return resource;
        } else {
            T rawResource;
            if (IGeoResourceInfo.class.isAssignableFrom(requestedType)) {
                rawResource = requestedType.cast(geoResource.getInfo(monitor));
            } else {
                rawResource = geoResource.resolve(requestedType, monitor);
            }
            
            if (rawResource == null){
                return null; // not available!
            }
            T resource = processPreResourceInterceptors(rawResource, requestedType);
            // cache for next time
            cachingStrategy.put(layer, resource, requestedType);
            return resource;
        }
    }

    private IResourceCachingInterceptor getCachingInterceptors() {
        loadInterceptors();
        String string = ProjectPlugin.getPlugin().getPreferenceStore().getString(
                PreferenceConstants.P_LAYER_RESOURCE_CACHING_STRATEGY);
        return interceptors.caching.get(string);
    }
    private <T> T processPostResourceInterceptors( T originalResource, Class<T> requestedType ) {
        // get a list of interceptors willing to work 
        List<SafeResourceInterceptor<T>> interceptors = getPostInterceptors(originalResource);
        
        // allow the interceptors to configure or wrap up the originalResource
        T resource = runInterceptors(requestedType, originalResource, interceptors);
        
        return resource;
    }

    private <T> List<SafeResourceInterceptor<T>> getPostInterceptors( T resource ) {
        loadInterceptors();
        List<SafeResourceInterceptor<T>> findValidInterceptors = findValidInterceptors(resource, interceptors.post
                .entrySet());
        if (postSorter != null) {
            Collections.sort(findValidInterceptors, postSorter);
        }
        return findValidInterceptors;
    }

    @SuppressWarnings("unchecked")
    private void loadInterceptors() {
        if (interceptors == null) {
            synchronized (this) {
                if (interceptors == null) {

                    final Map<IConfigurationElement, SafeResourceInterceptor<?>> pre = new HashMap<IConfigurationElement, SafeResourceInterceptor<?>>();
                    final Map<String, IResourceCachingInterceptor> caching = new HashMap<String, IResourceCachingInterceptor>();
                    final Map<IConfigurationElement, SafeResourceInterceptor<?>> post = new HashMap<IConfigurationElement, SafeResourceInterceptor<?>>();

                    List<IConfigurationElement> list = ExtensionPointList
                            .getExtensionPointList("org.locationtech.udig.project.resourceInterceptor"); //$NON-NLS-1$
                    for( IConfigurationElement element : list ) {
                        try {
                            if (element.getName().equals("cachingStrategy")) { //$NON-NLS-1$
                                IResourceCachingInterceptor strategy = (IResourceCachingInterceptor) element
                                        .createExecutableExtension("class"); //$NON-NLS-1$
                                caching.put(element.getNamespaceIdentifier()
                                        + "." + element.getAttribute("id"), strategy); //$NON-NLS-1$ //$NON-NLS-2$
                            } else {
                                IResourceInterceptor<?> tmp = (IResourceInterceptor<?>) element
                                        .createExecutableExtension("class"); //$NON-NLS-1$

                                SafeResourceInterceptor<?> interceptor = new SafeResourceInterceptor(tmp, element
                                        .getAttribute("target")); //$NON-NLS-1$
                                String order = element.getAttribute("order"); //$NON-NLS-1$
                                if ("PRE".equals(order)) { //$NON-NLS-1$
                                    pre.put(element, interceptor);
                                } else if ("POST".equals(order)) { //$NON-NLS-1$
                                    post.put(element, interceptor);
                                }
                            }
                        } catch (CoreException e) {
                            ProjectPlugin
                                    .log(
                                            "Failed to load resource interceptor:" + element.getNamespaceIdentifier() + "." + element.getAttribute("id"), e); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                        }
                    }

                    interceptors = new InterceptorsBag(pre, caching, post);
                }
            }
        }
    }
    @Override
    protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        return resolve(IGeoResourceInfo.class, monitor);
    }

    public LayerImpl getLayer() {
        return layer;
    }

    /**
     * ONLY FOR TESTING
     * 
     * @param <T>
     * @param comparator the comparator for sorting
     * @param sortPre if true the preResourceInterceptors will be sorted other wise the
     *        postResourceInterceptors will be sorted.
     */
    public void testingOnly_sort( Comparator<IResourceInterceptor< ? extends Object>> comparator,
            boolean sortPre ) {
        if (sortPre)
            preSorter = new Sorter(comparator);
        else
            postSorter = new Sorter(comparator);

    }
    /**
     * Data structure of the interceptors currently in play.
     */
    private static class InterceptorsBag {
        final Map<IConfigurationElement, SafeResourceInterceptor<?>> pre;
        final Map<String, IResourceCachingInterceptor> caching;
        final Map<IConfigurationElement, SafeResourceInterceptor<?>> post;
        public InterceptorsBag( final Map<IConfigurationElement, SafeResourceInterceptor<?>> pre,
                final Map<String, IResourceCachingInterceptor> caching,
                final Map<IConfigurationElement, SafeResourceInterceptor<?>> post ) {
            super();
            this.pre = pre;
            this.caching = caching;
            this.post = post;
        }
    }
    /** Used to compare wrappers */
    private static class Sorter implements Comparator<SafeResourceInterceptor< ? extends Object>> {

        private Comparator<IResourceInterceptor< ? extends Object>> wrapped;

        public Sorter( Comparator<IResourceInterceptor< ? extends Object>> comparator ) {
            wrapped = comparator;
        }

        public int compare( SafeResourceInterceptor< ? extends Object> o1, SafeResourceInterceptor< ? extends Object> o2 ) {
            return wrapped.compare(o1.interceptor, o2.interceptor);
        }
    }

    /**
     * Safety wrapper; willing to log exceptions (when running the wrapped interceptor).
     * 
     * @author Jesse
     * @since 1.1.0
     */
    private static class SafeResourceInterceptor<T> implements IResourceInterceptor<T> {
        public String targetType;
        private IResourceInterceptor<T> interceptor;
        
        public SafeResourceInterceptor( IResourceInterceptor<T> interceptor, String targetType ) {
            this.interceptor = interceptor;
            this.targetType = targetType;
        }
        /**
         * Safely calls the wrapped resource ResourceInterceptor; logging any errors thrown.
         */
        public T run( ILayer layer, T resource, Class< ? super T> requestedType ) {
            try {
                return interceptor.run(layer, resource, requestedType);
            } catch (Throwable t) {
                ProjectPlugin.log("Exception when running interceptor: " + interceptor, t); //$NON-NLS-1$
                return resource;
            }
        }

        @Override
        public String toString() {
            return interceptor.toString();
        }
    }

    @Override
    public IService service( IProgressMonitor monitor ) throws IOException {
        return geoResource.service(monitor);
    }
}
