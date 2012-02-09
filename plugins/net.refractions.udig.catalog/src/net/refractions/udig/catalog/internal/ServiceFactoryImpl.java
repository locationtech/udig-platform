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

import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceFactory;
import net.refractions.udig.catalog.ServiceExtension;
import net.refractions.udig.catalog.interceptor.ServiceInterceptor;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Default implementation of IServiceFactory used by the local catalog.
 * <p>
 * This is an internal class and defines no additional API of interest.
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ServiceFactoryImpl extends IServiceFactory {

    /** Lock used to protect map of available services; for the last call? */
    private Lock lock = new ReentrantLock();

    /**
     * Map of ServiceExtension by "id", access control policed by above "lock".
     */
    Map<String, ServiceExtension> registered = null; // lazy creation

    /** @deprecated use createService */
    public List<IService> aquire( final URL id, final Map<String, Serializable> params ) {
        return createService(params);
    }
    /** @deprecated use createService */
    public List<IService> aquire( Map<String, Serializable> params ) {
        return createService(params);
    }
    /** @deprecated use createService */
    public List<IService> acquire( Map<String, Serializable> params ) {
        return createService(params);
    }
    /** @deprecated use createService */
    public List<IService> aquire( final URL target ) {
        return createService(target);
    }
    /** @deprecated use createService */
    public List<IService> acquire( URL target ) {
        return createService(target);
    }

    /** @deprecated use createService */
    public List<IService> acquire( final URL id, final Map<String, Serializable> params ) {
        return createService(params);
    }
    
    /**
     * Check if the service extension is "generic" placeholder
     * where a more specific implementation may exist.
     * <p>
     * An extension point flag should be used; for now we will just
     * check the identifier itself.
     * @param serviceExtId
     * @return true if this service extension is generic
     */
    private boolean isGeneric( String serviceExtId ){
        return serviceExtId.toLowerCase().contains("geotools");
    }
    
    /*
    private <X> Map<String, X> selectGeneric(Map<String, X> map, boolean generic) {
        Map<String,X> selected = new HashMap<String,X>();
        
        for( Map.Entry<String, X> entry : map.entrySet() ) {
            String id = entry.getKey();            
            X value = entry.getValue();
            
            if( isGeneric(id) == generic ){
                selected.put( id, value);
            }
        }
        return selected;
    }
    */
    
    Map<String, ServiceExtension> getRegisteredExtensions() {
        try {
            lock.lock();
            if (registered == null) { // load available
                // we are going to sort the map so that
                // "generic" fallback datastores are selected last
                //                
                registered = new HashMap<String, ServiceExtension>();
                ExtensionPointUtil
                        .process(
                                CatalogPlugin.getDefault(),
                                "net.refractions.udig.catalog.ServiceExtension", new ExtensionPointProcessor(){ //$NON-NLS-1$
                                    public void process( IExtension extension,
                                            IConfigurationElement element ) throws Exception {
                                        // extentionIdentifier used to report any problems;
                                        // in the event of failure we want to be able to report
                                        // who had the problem
                                        //String extensionId = extension.getUniqueIdentifier();
                                        String id = element.getAttribute("id");
                                        ServiceExtension se = (ServiceExtension) element
                                                .createExecutableExtension("class");
                                        if( id == null || id.length()==0){
                                            id = se.getClass().getSimpleName();
                                        }
                                        registered.put(id, se);
                                    }
                                });
            }
            return registered;
        } finally {
            lock.unlock();
        }
    }

    /**
     * List candidate IService handles generated by all ServiceExtentions that think they can handle
     * the provided target drag and drop url.
     * <p>
     * Note: Just because a target is created does *NOT* mean it will actually work. You can check
     * the handles in the usual manner (ask for their info) after you get back this list.
     * </p>
     * 
     * @see net.refractions.udig.catalog.IServiceFactory#acquire(java.net.URL)
     * @param target Target url usually provided by drag and drop code
     * @return List of candidate services
     */
    public List<IService> createService( final URL targetUrl ) {
        final Map<String, Map<String, Serializable>> available = new HashMap<String, Map<String, Serializable>>();
        final Map<String, Map<String, Serializable>> generic = new HashMap<String, Map<String, Serializable>>();
        
        for( Map.Entry<String, ServiceExtension> entry : getRegisteredExtensions().entrySet() ) {
            if (entry == null)
                continue;
            String id = entry.getKey();
            ServiceExtension serviceExtension = entry.getValue();
            try {
                Map<String, Serializable> defaultParams = serviceExtension.createParams(targetUrl);
                if (defaultParams != null) {
                    if( isGeneric(id)){
                        generic.put(id, defaultParams);
                    }
                    else {
                        available.put(id, defaultParams);
                    }
                }
            } catch (Throwable t) {
                if (CatalogPlugin.getDefault().isDebugging()) {
                    IStatus warning = new Status(IStatus.WARNING, CatalogPlugin.ID, id
                            + " could not create params " + targetUrl, t);
                    CatalogPlugin.getDefault().getLog().log(warning);
                }
            }
        }
        List<IService> candidates = new LinkedList<IService>();
        if( !available.isEmpty()){
            for( Map.Entry<String, Map<String, Serializable>> candidateEntry : available.entrySet() ) {
                String extentionIdentifier = candidateEntry.getKey();
                Map<String, Serializable> connectionParameters = candidateEntry.getValue();
                try {
                    List<IService> service = createService(connectionParameters);
                    if (service != null && !service.isEmpty()) {
                        for( IService created : service ){
                            CatalogImpl.runInterceptor(created, ServiceInterceptor.CREATED_ID);
                            candidates.add(created);
                        }
                    }
                } catch (Throwable deadService) {
                    CatalogPlugin.log(extentionIdentifier + " could not create service", deadService); //$NON-NLS-1$
                }
            }
        }
        if( !candidates.isEmpty() && generic.isEmpty()){
            // add generic entries if needed
            for( Map.Entry<String, Map<String, Serializable>> candidateEntry : generic.entrySet() ) {
                String extentionIdentifier = candidateEntry.getKey();
                Map<String, Serializable> connectionParameters = candidateEntry.getValue();
                try {
                    List<IService> service = createService(connectionParameters);
                    if (service != null && !service.isEmpty()) {                    
                        for( IService created : service ){
                            CatalogImpl.runInterceptor(created, ServiceInterceptor.CREATED_ID);
                            candidates.add(created);
                        }
                    }
                } catch (Throwable deadService) {
                    CatalogPlugin.log(extentionIdentifier + " could not create service", deadService); //$NON-NLS-1$
                }
            }
        }
        return candidates;
    }
    
    public List<IService> createService( final Map<String, Serializable> connectionParameters ) {
        final List<IService> services = new LinkedList<IService>();
        
        for( Map.Entry<String, ServiceExtension> entry : getRegisteredExtensions().entrySet() ) {
            String id = entry.getKey();
            if( isGeneric(id)) continue; // skip generic for this passs
            
            ServiceExtension serviceExtension = entry.getValue();
            try {
                // Attempt to construct a service, and add to the list if available.
                IService service = serviceExtension.createService(null, connectionParameters);
                if (service != null) {
                    CatalogImpl.runInterceptor(service, ServiceInterceptor.CREATED_ID);
                    services.add(service);                    
                }
            } catch (Throwable deadService) {
                CatalogPlugin.log(id + " could not create service", deadService); //$NON-NLS-1$
            }
        }
        if( services.isEmpty()){
            for( Map.Entry<String, ServiceExtension> entry : getRegisteredExtensions().entrySet() ) {
                String id = entry.getKey();
                if( !isGeneric(id)) continue; // Do not use generic this pass
                
                ServiceExtension serviceExtension = entry.getValue();
                try {
                    // Attempt to construct a service, and add to the list if available.
                    IService service = serviceExtension.createService(null, connectionParameters);
                    if (service != null) {
                        CatalogImpl.runInterceptor(service, ServiceInterceptor.CREATED_ID);
                        services.add(service);
                    }
                } catch (Throwable deadService) {
                    deadService.printStackTrace();
                    CatalogPlugin.trace(id + " could not create service", deadService); //$NON-NLS-1$
                }
            }    
        }
        return services;
    }

    /** Look up a specific implementation; used mostly for test cases */
    public <E extends ServiceExtension> E serviceImplementation( Class<E> implementation ) {
        for( Map.Entry<String, ServiceExtension> entry : getRegisteredExtensions().entrySet() ) {
            String id = entry.getKey();
            ServiceExtension serviceExtension = entry.getValue();

            if (id == null || serviceExtension == null)
                continue;
            if (implementation.isInstance(serviceExtension)) {
                return implementation.cast(serviceExtension);
            }
        }
        return null;
    }
    /** Look up a specific implementation; used mostly for test cases */
    public ServiceExtension serviceImplementation( String serviceExtensionId ) {
        for( Map.Entry<String, ServiceExtension> entry : getRegisteredExtensions().entrySet() ) {
            String id = entry.getKey();
            ServiceExtension serviceExtension = entry.getValue();

            if (id == null || serviceExtension == null)
                continue;
            if (serviceExtensionId.equalsIgnoreCase(id)) {
                return serviceExtension;
            }
        }
        return null;
    }

    public void dispose( List<IService> list, IProgressMonitor monitor ) {
        if (list == null)
            return;

        if (monitor == null)
            monitor = new NullProgressMonitor();
        monitor.beginTask("dispose", list.size() * 10);
        for( IService service : list ) {
            try {
                service.dispose(new SubProgressMonitor(monitor, 10));
            } catch (Throwable t) {
                CatalogPlugin.trace("Dispose " + service, t);
            }
        }
        monitor.done();
    }
}
