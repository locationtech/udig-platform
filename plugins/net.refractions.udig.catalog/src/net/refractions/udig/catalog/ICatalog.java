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
package net.refractions.udig.catalog;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import net.refractions.udig.catalog.internal.Messages;
import net.refractions.udig.ui.ErrorManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Interface to capture both the Local Catalog resources and Web Registry Service.
 * <p>
 * Conceptually provides a searchable Catalog of "Spatial Data Sources". Metadata search is
 * arbitrary.
 * </p>
 *
 * @author David Zwiers, Refractions Research
 * @since 0.7.0
 * @see IService
 */
public abstract class ICatalog implements IResolve {
    /**
     * Catalogs do not have a parent so null is returned.
     * <p>
     * We can consider adding a global 'root' parent - but we will wait until we find a need, or if
     * users request.
     * </p>
     *
     * @return null as catalogs do not have a parent
     */
    public IResolve parent( IProgressMonitor monitor ) {
        return null;
    }
    /**
     * Adds the specified entry to this catalog.
     * <p>
     * In some cases the catalog will be backed onto an
     * external server, which may not allow for additions.
     * <p>
     * An IService may belong to more than one Catalog.
     * </p>
     *
     * @param entry
     * @throws UnsupportedOperationException
     */
    public abstract void add( IService service ) throws UnsupportedOperationException;

    /**
     * Removes the specified entry to this catalog. In some cases the catalog will be backed onto a
     * server, which may not allow for deletions.
     *
     * @param service
     * @throws UnsupportedOperationException
     */
    public abstract void remove( IService service ) throws UnsupportedOperationException;

    /**
     * Replaces the specified entry in this catalog.
     * <p>
     * In some cases the catalog will be backed onto a server, which may not
     * allow for deletions.
     * <p>
     * This method can be used for two things:
     * <ul>
     * <li>ResetService (Action): calls this method with id == service.getID() in order
     * to "reset" the IService handle with a fresh one. This can be used to replace a
     * catalog entry that has locked up.
     * <li>This method can also be used to *move* an existing service (the one with the
     * indicated ID) with a new replacement).
     * </ul>
     * <p>
     * This replace method has two differences from a simple *remove( id )* and *add( replacement)*
     * <ul>
     * <li>A difference series of events is generated; letting client code know that they
     * should update the ID they were using to track this resource.
     * <li>An new IForward( ID, replacement.getID()) is left in the catalog as a place holder to
     * order to let any client that was off-line know what happened next time they come to call.
     * </ul>
     * @param id ID of the service to replace, the service with this ID will be removed
     * @param replacement Replacement IService handle; indicating where the service has moved to
     * @throws UnsupportedOperationException
     */
    public abstract void replace( URL id, IService replacement ) throws UnsupportedOperationException;

    /**
     * Will attempt to morph into the adaptee, and return that object. Required adaptations:
     * <ul>
     * <li>ICatalogInfo.class
     * <li>List.class <IService>
     * </ul>
     * May Block.
     *
     * @param adaptee
     * @param monitor May Be Null
     * @return
     * @see ICatalogInfo
     * @see IService
     */
    public abstract <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException;

    /**
     * Acquire info on this Catalog.
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
     */
    public abstract List<IResolve> find( URL resourceId, IProgressMonitor monitor );

    /**
     * Find Service matching this id directly from this Catalog.  This method is guaranteed to be non-blocking.
     *
     * @deprecated This method cannot be guaranteed to be non blocking for external catalogs, please use getById instead
     * @param id used to match resolves
     * @param monitor TODO
     * @return List (possibly empty) of matching Resolves
     */
    public abstract List<IService> findService( URL query );

    /**
     * Look in catalog for exact match with provided id.
     *
     * @param type Type of IResolve if known
     * @param id id used for lookup
     * @param monitor
     * @return Resolve or null if not found
     */
    public abstract <T extends IResolve> T getById( Class<T> type, URL id, IProgressMonitor monitor );

    /**
     * Performs a search on this catalog based on the specified inputs.
     * <p>
     * The pattern uses the following conventions:
     * <ul>
     * <li>
     * <li> use " " to surround a phase
     * <li> use + to represent 'AND'
     * <li> use - to represent 'OR'
     * <li> use ! to represent 'NOT'
     * <li> use ( ) to designate scope
     * </ul>
     * The bbox provided shall be in Lat - Long, or null if the search is not to be contained within
     * a specified area.
     * </p>
     *
     * @param pattern Search pattern (see above)
     * @param bbox The bbox in Lat-Long (ESPG 4269), or null
     * @param monitor for progress, or null if monitoring is not desired
     * @return List matching IResolve
     */
    public abstract List<IResolve> search( String pattern, Envelope bbox, IProgressMonitor monitor )
            throws IOException;

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
     * @param listener
     */
    public abstract void addCatalogListener( IResolveChangeListener listener );

    /**
     * @param listener
     */
    public abstract void removeCatalogListener( IResolveChangeListener listener );

    /**
     * Create an IGeoResource that is will be deleted after the session.  The descriptor object passed in
     * is used to determine the type of resource that is created.  For example if the descriptor object is
     * a FeatureType then a IGeoResource that can resolve to a FeatureStore will be returned.
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
}
