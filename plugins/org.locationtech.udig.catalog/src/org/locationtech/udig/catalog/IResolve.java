/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.catalog;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Represents external services; you can contact the external service using an API of your choice.
 * <p>
 * Quick example:
 * 
 * <pre>
 * <code>
 * if( resolve.canResolve( FeatureSource.class ) ){
 *     FeatureSource source = resolve.resolve( FeatureSource.class, new NullProgressMonitor() );
 *     FeatureCollection features = source.getFeatures();
 *     ...
 * }
 * </code>
 * </pre>
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.7.0
 * @see IAdaptable
 */
public interface IResolve {

    /**
     * Will attempt to morph into the adaptee, and return that object.
     * <p>
     * Required adaptions will be listed in Abstract Classes, along with the method they will call. IResolve
     * implementations are encouraged to follow this practice - documenting what adapters are required.
     * </p>
     * The extensible interface pattern also demands that the set of adapters be open-ended; we have provided an
     * extention point to let others teach the system new tricks at configuration time. </p> Here is a code example that
     * every implementation needs to follow in order to make use of the IResolveManager:
     * <ul>
     * <li>TYPE - an example required class (like URL.class)
     * <li>METHOD - an example method that will produce the adapter
     * </ul>
     * 
     * <pre>
     * <code>
     * public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
     *     if (monitor == null)
     *         monitor = new NullProgressMonitor();
     *     if (adaptee == null)
     *         throw new NullPointerException("No adaptor specified" );
     *     
     *     if (adaptee.isAssignableFrom(TYPE.class)) {
     *         return adaptee.cast(METHOD(monitor));
     *     }
     *     ...
     *     IResolveManager rm = CatalogPlugin.getDefault().getResolveManager();
     *     if (rm.canResolve(this, adaptee)) {
     *         return rm.resolve(this, adaptee, monitor);
     *     }
     *     return null; // could not find adapter
     * }</code>
     * </pre>
     * 
     * May Block.
     * 
     * @param adaptee
     * @param monitor
     *            May Be Null
     * @return Instance of type adaptee, or null if adaptee is unsuported.
     * @throws IOException
     *             if result was unavailable due to a technical problem
     */
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException;

    /**
     * Required adaptions will be listed in Abstract Classes under the resolve() method.
     * <p>
     * Restrictions on implementations:
     * <ul>
     * <li>May not Block
     * <li>*MUST NOT* throw any exceptions (be sure to check for null!)
     * <li>Must delegate to ResolveManger - to recognize adapters contributed by others
     * </ul>
     * When defining a new AbstractClass you are also asked to please list the required adaptations in your javadocs.
     * <p>
     * The following code example shows intended practice:
     * 
     * <pre>
     * <code>
     * public <T> boolean canResolve( Class<T> adaptee ){
     *    return adaptee != null && (
     *               adaptee.isAssignableFrom(TYPE.class) ||
     *               CatalogPlugin.getDefault().getResolveManager().canResolve(this, adaptee)
     *           );
     * }
     * </code>
     * </pre>
     * 
     * @see IResolve#resolve(Class, IProgressMonitor);
     * @return true if a resolution for adaptee is avaialble
     */
    public <T> boolean canResolve(Class<T> adaptee);

    /**
     * The parent of this handle, may be null if parent unknown.
     * 
     * @param monitor
     *            used to provide feedback during parent lookup
     * @return Parent IResolve, null if unknown
     * @throws IOException
     *             in the event of a technical problem
     */
    public IResolve parent(IProgressMonitor monitor) throws IOException;

    /**
     * Contents of this handle, Collections.EMPTY_LIST iff this is a leaf.
     * 
     * @param monitor
     *            Monitor used to provide feedback during member lookup
     * @return List, possibly empty, of members. Will be EMPTY_LIST if this is a leaf.
     * @throws IOException
     *             in the event of a technical problem
     */
    public List<IResolve> members(IProgressMonitor monitor) throws IOException;

    public enum Status {
        /** Status constant indicates a live connection in use */
        CONNECTED,

        /** Status constant indicates a connection that is not in use */
        NOTCONNECTED,

        /** Status constant indicates a connection that is broken */
        BROKEN,

        /** Indicates that user does not have access to resource */
        RESTRICTED_ACCESS,

        /**
         * Handle has been disposed (any any oustanding resources reclaimed). See {@link IResolve#dispose()} for
         * details.
         */
        DISPOSED;
    };

    /**
     * Status information for this service.
     * <p>
     * In the future this may be extended into a bit mask of connection status.
     * </p>
     * 
     * @return Status of the resource
     */
    public Status getStatus();

    /**
     * Text description for status of this resource.
     * <p>
     * For a BROKEN status this will contain the error message, null will be returned if there is nothing interesting to
     * report.
     * <p>
     * <p>
     * Not the Exception is expected to be in human readable, terms.
     * </p>
     * 
     * @return Text describing service status
     */
    public Throwable getMessage();

    /**
     * A unique resource identifier ... this should be unique for each service. Must Not Block.
     * <p>
     * Creating an "invalid" URL for local content is tricky try the following:
     * 
     * <pre>
     * <code>
     * new URL(null, getID().toString(), CorePlugin.RELAXED_HANDLER);
     * </code>
     * </pre>
     * 
     * @return ID for this IResolve, should not be null.
     */
    public abstract URL getIdentifier();

    /**
     * A unique resource ID ... this should be unique for each service. Must Not Block.
     * <p>
     * This method is similar to new ID( getIdentifier() ) but is a lot easier to maintain. We are using a strongly
     * typed ID object since there are so many restrictions and corner cases when working with URLs, URIs (or even
     * Strings across platforms).
     * </p>
     * 
     * @return ID for this object, will not be null.
     */
    public abstract ID getID();

    /**
     * Non blocking method to retrieve a title - often used as a label to represent this resource in a user interface.
     * <p>
     * Retrieves any readily available title. This method must not block. The general algorithm for this is to check any
     * title cache, and then check for the presents of an Info object to retrieve the title from. An Info object must
     * not be created in this method. If no title is readily available, return null to indicate that a title must be
     * generated.
     * 
     * @return title or <code>null</code> if none is readily available
     */
    public String getTitle();

    /**
     * Clean up after acquired resources - the handle will not function after being disposed.
     * <p>
     * Please ensure that {@link #getStatus()} returns {@link Status#DISPOSED} after this method
     * is called.
     * 
     * @param monitor
     */
    public void dispose(IProgressMonitor monitor);

}