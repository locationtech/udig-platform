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
package net.refractions.udig.catalog;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.internal.Messages;
import net.refractions.udig.ui.ErrorManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Interface to capture both the Local Catalog resources and Web Registry Service.
 * <p>
 * Conceptually provides a searchable Catalog of "Spatial Data Sources". Metadata search is
 * arbitrary.
 * </p>
 * This class contributes the idea of temporary resources (or scratch layers)
 * <ul>
 * <li>{@link #createTemporaryResource(Object)</li>
 * <li>@link #getTemporaryDescriptorClasses()</li>
 * </ul>
 * Descriptor classes, usually a GeoTools SimpleFeatureType, are used to create a temporary resource
 * that is held in memory. These temporary resources must be saved prior to the application exiting
 * or the contents will be lost.
 * 
 * @author David Zwiers (Refractions Research)
 * @since 0.7.0
 * @version 1.2
 * @see IService
 */
public abstract class ICatalog extends IRepository {    
    /**
     * Aquire info on this Catalog.
     * <p>
     * This is functionally equivalent to: <core>resolve(ICatalogInfo.class,monitor)</code>
     * </p>
     * 
     * @see ICatalog#resolve(Class, IProgressMonitor)
     * @return ICatalogInfo resolve(ICatalogInfo.class,IProgressMonitor monitor);
     */
    public ICatalogInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return resolve(ICatalogInfo.class, monitor);
    }

    /**
     * Find resources for this resourceId from the Catalog.
     * <p>
     * Please note that a List of resources Services, Resources, and friends
     * are returned by this method. While the first result returned
     * may be the most appropriate; you may need to try some of the other
     * values (if for example the first service is unavailable).
     * <p>
     * @param resource used to match resolves
     * @param monitor used to show the progress of the find.
     * @return List (possibly empty) of matching Resolves
     * @see #find(ID, IProgressMonitor)
     */
    public abstract List<IResolve> find( URL resourceId, IProgressMonitor monitor );

    /**
     * Find resources for this resourceId from the Catalog.
     * <p>
     * Please note that a List of resources Services, Resources, and friends
     * are returned by this method. While the first result returned
     * may be the most appropriate; you may need to try some of the other
     * values (if for example the first service is unavailable).
     * <p>
     * @param type filter the list to only contain instances of type
     * @param resource used to match resolves
     * @param monitor used to show the progress of the find.
     * @return List (possibly empty) of matching Resolves
     */
    public <T extends IResolve> List<T> find(Class<T> type, URL resourceId, IProgressMonitor monitor ){
        List<IResolve> resolves = find(resourceId, monitor);
        
        ArrayList<T> result = new ArrayList<T>();
        for( IResolve iResolve : resolves ) {
            if (type.isAssignableFrom(iResolve.getClass())) {
                T desired = type.cast(iResolve);
                result.add(desired);
            }
        }
        
        return result;
    }
    
    /**
     * Indicate class and id.
     * 
     * @return string representing this IResolve
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String classname = getClass().getName();
        String name = classname.substring(classname.lastIndexOf('.') + 1);
        buf.append(name);
        buf.append("("); //$NON-NLS-1$
        buf.append(getIdentifier());
        buf.append(")"); //$NON-NLS-1$
        return buf.toString();
    }

    /**
     * Add a listener to notice when the a resource changes.
     * 
     * @param listener
     */
    public abstract void addCatalogListener( IResolveChangeListener listener );

    /**
     * Remove a listener that was watching for resource changes.
     * 
     * @param listener
     */
    public abstract void removeCatalogListener( IResolveChangeListener listener );
    
    /**
     * Create an IGeoResource that is will be deleted after the session.  The descriptor object passed in
     * is used to determine the type of resource that is created.  For example if the descriptor object is
     * a SimpleFeatureType then a IGeoResource that can resolve to a FeatureStore will be returned.
     *
     * @param descriptor An object whose type is in the {@link #getTemporaryDescriptorClasses()} array.  
     * @return an IGeoResource that is will be deleted after the session.
     * @throws IllegalArgumentException if the descriptor type is not known.
     */
    public abstract IGeoResource createTemporaryResource( Object descriptor ) throws IllegalArgumentException;
    
    /**
     * Returns The list of class names that this catalog can use to create Temporary Resources.
     *
     * @return The list of class names that this catalog can use to create Temporary Resources.
     */
    public abstract String[] getTemporaryDescriptorClasses();
    

    public void dispose(IProgressMonitor monitor) {
            monitor.beginTask(Messages.ICatalog_dispose, 100);
            List< ? extends IResolve> members;
            try {
                members = members(new SubProgressMonitor(monitor,1) );
            } catch (Throwable e) {
                ErrorManager.get().displayException(e, "Error disposing members of catalog: "+getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
                return;
            }
            int steps=(int)((double)99/(double)members.size());
            for( IResolve resolve : members ) {
                try {
                    SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, steps);
                    resolve.dispose( subProgressMonitor);
                    subProgressMonitor.done();
                } catch (Throwable e) {
                    ErrorManager.get().displayException(e, "Error disposing members of catalog: "+getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
                }
            }
        
    }
    
    
    /**
     * This method evaualtes the results of constructServices and returns the list of Services that
     * have already been added to the catalog.
     * <p>
     * Services that have already been added to the catalog will be properly disposed when the
     * applicaiton shuts down; you do not need to call dispose on these services yourself.
     * 
     * @param constructServiceList
     * @return a filtered list of services that exists in the catalog
     */
   public abstract List<IService> checkMembers(List<IService> constructServiceList );
    
   
    /**
     * This method evaualtes the results of constructServices and returns the list of Services that
     * are not listed in the catalogs.
     * <p>
     * The list of services returned have not been added to the catalog and you are responsible for
     * making sure dispose is called.
     * 
     * @param constructServiceList
     * @return a filtered list of services that exists in the catalog
     */
   public abstract List<IService> checkNonMembers(List<IService> constructServiceList );
   
    /**
     * Creates a prioritised list of services that can implement the url, the calling method is
     * responsible for disposing if serviced.
     * <p>
     * This method uses ServiceFactory to get a list of services that think they can implement the
     * url. The ServiceFactory doesn’t guarantee that the services it provides can connect so it is
     * the responsibility of this method to check that they can.
     * 
     * @param url URL indicating resources from a drag and drop (or wizard)
     * @param monitor Used to track the process of connecting
     * @return new ArrayList prioritised list of services
     * @throws IOException
     */
    public abstract List<IService> constructServices(URL url, IProgressMonitor monitor) throws IOException;
    
    /**
     * Takes a list of services and orders them from highest priority to lowest. The
     * priority is worked from the IServiceInfo .getCompleteness() method The priority is determined
     * by the result of the IServiceInfo .getCompleteness(), this method should be overridden in
     * each serviced specific IServiceInfo object to return a value that represents the completes of
     * required metadata to implement the service.
     * 
     * @param services a Map of connection parameters to 
     * @param monitor Used to track the process of connecting
     * @return a ArrayList prioritise list of services
     * @throws IOException 
     */
    public abstract List<IService> constructServices(Map<String, Serializable> params, IProgressMonitor monitor) throws IOException;
}
