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
package org.locationtech.udig.catalog;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.internal.Messages;
import org.locationtech.udig.ui.ErrorManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * Represents a geo spatial service handle. Follows the same design as IResource.
 * <p>
 * Represents a spatial service, which may be lazily loaded. The existance of this object does not
 * ensure that the advertized data is guaranteed to exist, nor does this interface guarantee that
 * the service exists based on this object's existance. We should also note the resource management
 * is left to the user, and that resolve() is not guaranteed to return the same instance object from
 * two subsequent calls, but may. This is merely a handle to some information about a service, and a
 * method of aquiring an instance of the service ...
 * </p>
 * <p>
 * NOTE: This may be the result of communications with a metadata service, and as such this service
 * described may not be running right now. Remember to check the service status.
 * </p>
 * <h2>Implementing an IService</h2> Implement the abstract methods and you are good to go. <h2>
 * Extending an IService</h2> You may want to implement your own IService in order to provide a
 * handle for a new kind of <i>API</i>.
 * <ol>
 * <li>New method:
 * 
 * <pre>
 * <code>
 *  public API getAPI( ProgressMonitor monitor){
 *      if (monitor == null) monitor = new NullProgressMonitor();
 *      
 *      monitor.beingTask("Connect to API",2);
 *      try {            
 *          String server = getConnectionParams().get("server");
 *          monitor.worked(1);
 *          return new API( s );
 *      }
 *      finally {
 *          monitor.done();
 *      }
 *  }
 * </pre>
 * 
 * </code> (note the use of NullProgressMonitor)</li>
 * <li>Optional: Customize resolve method to advertise your new API "dynamically"
 * 
 * <pre>
 * <code>
 * public &lt;T&gt; boolean canResolve( Class&lt;T&gt; adaptee ) {
 *     return adaptee != null
 *             &amp;&amp; (adaptee.isAssignableFrom(API.class) || super.canResolve(adaptee));
 * }
 * public &lt;T&gt; T resolve( Class&lt;T&gt; adaptee, IProgressMonitor monitor ) throws IOException {
 *     if (monitor == null) monitor = new NullProgressMonitor(); 
 *     if (adaptee == null)
 *         throw new NullPointerException("No adaptor specified" );
 *         
 *     if (adaptee.isAssignableFrom(API.class)) {
 *         return adaptee.cast(getAPI(monitor));
 *     }
 *     return super.resolve(adaptee, monitor);
 * }
 * </code>
 * </pre>
 * 
 * (note the call to super)</li>
 * <li>Optional: cache your API as the "connection"
 * 
 * <pre>
 * <code>
 *  API api = null;
 *  Throwable msg = null;
 *  public synchronized API getAPI( ProgressMonitor monitor){
 *      if( api != null ) return api;
 *      
 *      if (monitor == null) monitor = new NullProgressMonitor();
 *      
 *      monitor.beingTask("Connect to API",2);
 *      try {            
 *          String server = getConnectionParams().get("server");
 *          monitor.worked(1);
 *          api = new API( s );
 *          monitor.worked(1);
 *          return api;
 *      }
 *      finally {
 *          monitor.done();
 *      }
 *  }
 *  public Status getStatus() {
 *      return msg != null? Status.BROKEN : api == null? Status.NOTCONNECTED : Status.CONNECTED;
 *  }
 *  public Throwable getMessage(){
 *      return msg;
 *  }
 *  public synchronized void dispose( ProgressMonitor monitor ){
 *      if( api != null ){
 *           api.dispose();
 *           api = null;
 *      }
 *      if( msg != null ) msg = null;
 *  }
 * </code>
 * </pre>
 * 
 * (Note the use of getMessage and getStatus)</li>
 * </ol>
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 * @version 1.2
 * @see IServiceInfo
 * @see IServiceFactory
 */
public abstract class IService implements IResolve {
    /**
     * Placeholder used to mark info field as unavailable; often the case
     * where we were unable to connect to a service using createInfo.
     */
    protected static IServiceInfo INFO_UNAVAILABLE = new IServiceInfo();
    
    /**
     * Use when implementing {@link #getStatus()}.
     */
    protected boolean isDisposed = false;
    /**
     * Used to save persisted properties; please see {@link ServiceParameterPersister} for details.
     */
    private Map<String, Serializable> properties = Collections.synchronizedMap(new HashMap<String, Serializable>());
    
    /**
     * Used to save persisted properties for child resources; please see {@link ServiceParameterPersister} for details.
     * <p>
     * IGeoResource makes use of {@link #getPersistentProperties(ID)} for direct access to these maps.
     */
    Map<ID,Map<String, Serializable>> resourceProperties =  Collections.synchronizedMap(new HashMap<ID,Map<String, Serializable>>() );
    
    /**
     * This is a protected field; that is laziy created when getInfo is called.
     */
    protected volatile IServiceInfo info = null;

    /**
     * Will attempt to morph into the adaptee, and return that object. Harded coded to capture the
     * IService contract.
     * <p>
     * That is we *must* resolve the following:
     * <ul>
     * <li>IService: this
     * <li>IServiceInfo.class: getInfo
     * <li>ICatalog.class: parent
     * </ul>
     * Recomended adaptations:
     * <ul>
     * <li>ImageDescriptor.class: for a custom icon
     * <li>List.class: members
     * </ul>
     * May Block.
     * <p>
     * Example implementation:
     * 
     * <pre>
     * <code>
     * public &lt;T&gt; T resolve( Class&lt;T&gt; adaptee, IProgressMonitor monitor ) throws IOException {
     *     if (monitor == null) monitor = new NullProgressMonitor(); 
     *     if (adaptee == null)
     *         throw new NullPointerException("No adaptor specified" );
     *         
     *     if (adaptee.isAssignableFrom(API.class)) {
     *         return adaptee.cast(getAPI(monitor));
     *     }
     *     return super.resolve(adaptee, monitor);
     * }
     * </code>
     * </pre>
     * 
     * @param adaptee
     * @param monitor
     * @return instance of adaptee, or null if unavailable (IServiceInfo and List<IGeoResource> must
     *         be supported)
     * @see IServiceInfo
     * @see IGeoResource
     * @see IResolve#resolve(Class, IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(IServiceInfo.class)) {
            return adaptee.cast(getInfo(monitor));
        }
        if (adaptee.isAssignableFrom(IService.class)) {
            monitor.done();
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(ICatalog.class)) {
            return adaptee.cast(parent(monitor));
        }
        IResolveManager rm = CatalogPlugin.getDefault().getResolveManager();
        if (rm.canResolve(this, adaptee)) {
            return rm.resolve(this, adaptee, monitor);
        }
        return null; // no adapter found (check to see if ResolveAdapter is registered?)
    }

    /**
     * Harded coded to capture the IService contract.
     * <p>
     * That is we *must* resolve the following:
     * <ul>
     * <li>IService: this
     * <li>IServiceInfo.class: getInfo
     * <li>List.class: members
     * <li>ICatalog.class: parent
     * </ul>
     * <p>
     * Here is an implementation example (for something that can adapt to DataStore):
     * 
     * <pre>
     * <code>
     * public &lt;T&gt; boolean canResolve( Class&lt;T&gt; adaptee ) {
     *     return adaptee != null
     *             &amp;&amp; (adaptee.isAssignableFrom(DataStore.class) || super.canResolve(adaptee));
     * }
     * </code>
     * </pre>
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null && (adaptee.isAssignableFrom(IService.class) || // this
                adaptee.isAssignableFrom(IServiceInfo.class) || // getInfo
                adaptee.isAssignableFrom(List.class) || // members
                adaptee.isAssignableFrom(ICatalog.class) || // parent
                CatalogPlugin.getDefault().getResolveManager().canResolve(this, adaptee));
    }

    /**
     * Returns LocalCatalog by defaul, subclass must override iff a custom catalog is used.
     */
    public ICatalog parent( IProgressMonitor monitor ) {
        ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
        if (localCatalog.getById(IService.class, getID(), monitor) != null) {
            return localCatalog;
        } else {
            return null;
        }
    }
    /**
     * List of concrete resources (IE with data) managed by this service.
     * <p>
     * Many file based services will just contain a single IGeoResource. Services that arrange their
     * data into folders will (like WMS) will return a list of all the actual data made available by
     * the service.
     * </p>
     * The IService.resources() method is the inverse of GeoResource.service().
     */
    public abstract List< ? extends IGeoResource> resources( IProgressMonitor monitor )
            throws IOException;

    /**
     * The contents of this IService presented as a list of members.
     * <p>
     * For many simple services this will be the same as the the resources, however some services
     * may choose to organize their resources into folders or schemas, or even a tree (as is the
     * case for WMS layers).
     */
    public List<IResolve> members( IProgressMonitor monitor ) throws IOException {
        List<IResolve> resolves = new ArrayList<IResolve>(resources(monitor));
        return resolves;
    }

    /**
     * Responsible for creation of an appropriate IServiceInfo object.
     * 
     * @return IServiceInfo resolve(IServiceInfo.class, IProgressMonitor monitor);
     * @throws IOException
     */
    protected abstract IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException;

    /**
     * Information about this service.
     * <p>
     * Subclasses are encouraged to overrride this to type narrow to the specific ServiceInfo class
     * returned.
     * </p>
     * Example:
     * <pre><code>
     *     public CSVServiceInfo getInfo(IProgressMonitor monitor) throws IOException {
     *     		return (CSVServiceInfo) super.getInfo(monitor);
     *     }
     *     protected CSVServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
     *     	return new CSVServiceInfo( this );
     *     }</code>
     * </pre>
     * <p>
     * Please do call super.getInfo( monitor ) as the base IService implementation provides a cache
     * so createInfo( monitor ) is only called once.
     * </p>
     * @see IService#INFO_UNAVAILABLE
     * @return IServiceInfo resolve(IServiceInfo.class,IProgressMonitor monitor);
     * @see IService#resolve(Class, IProgressMonitor)
     */
    public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if( isDisposed ){
            return null; 
        }
        if (info == null) { // lazy creation
            synchronized (this) {
                // support concurrent access
                // however createInfo implementors should know about this
                if (info == null) {
                    if (Display.getCurrent() != null) {
                        throw new IllegalStateException("Lookup of getInfo not available from the display thread"); //$NON-NLS-1$
                    }
                    info = createInfo(monitor);

                    if( info == null ){
                        info = INFO_UNAVAILABLE;
                    }
                    else {
                        // broadcast the change - code taken from ArcServiceImpl

                        // this delta describes what has changed
                        /*
                        IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);

                        // fire the change
                        CatalogImpl localCatalog = (CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog();
                        localCatalog.fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE, delta));
                         */
                    }
                }
            }
        }
        if (info == INFO_UNAVAILABLE) {
            return null; // info was not available
        }
        return info;        
    }

    public ID getID() {
        return new ID(getIdentifier());
    }

    /**
     * hide user password from the layer ID if it exists and returns
     * ID as String.
     * 
     * @param layer
     * @return
     */
    public String getDisplayID() {
        String userInfo = getIdentifier().getUserInfo();
        if (userInfo != null) {
            userInfo = userInfo.substring(0, userInfo.indexOf(":")+1);
            userInfo = userInfo.concat("******");
            return new ID(getIdentifier().toString().replace(getIdentifier().getUserInfo(), userInfo), null).toString();
        }
        return getID().toString();
    }
	
    /**
     * Map of parameters used to create this entry. There is no guarantee that these params created
     * a usable service (@see getStatus() ). These params may have been modified within the factory
     * during creation. This method is intended to be used for cloning (@see IServiceFactory) or for
     * persistence between sessions.
     * <p>
     * <b>IMPORTANT:</b> Because of the serialization currently used only types that can be
     * reconstructed from their toString() representation can be used. For example:
     * 
     * <pre>
     * <code>
     * valueThatIsSaved=url.toString().
     * URL restoredValue=new URL(valueThatIsSaved);
     * </code>
     * </pre>
     * 
     * Also only classes that this plugin can load can be loaded so custom classes from downstream
     * plugins cannot be used. It is recommended that only "normal" types be used like Integer, URL,
     * Float, etc...
     * </p>
     * This restriction will be lifted in the future. (Except for the loading issue that is a design
     * issue that we will live with.)
     * 
     * @see IServiceFactory
     * @return
     */
    public abstract Map<String, Serializable> getConnectionParams();

    /**
     * Map of servies's persistent properties (may be empty).
     * 
     * @see ServiceParameterPersister for restrictions of values that can be stored
     * @return The map containing the persistent properties for this service.
     */
    public Map<String, Serializable> getPersistentProperties() {
        return properties;
    }
    /**
     * Map of indicated resource's persistent properties. Returns an empty map if
     * this resource has no persistent properties.
     * 
     * @see ServiceParameterPersister for restrictions of values that can be stored
     * @param child ID of child properties requested
     * @return The map containing the persistent properties where the key is the
     *         {@link QualifiedName} of the property and the value is the {@link String} value of
     *         the property.
     */
    public synchronized Map<String, Serializable> getPersistentProperties(ID child) {
    	if( resourceProperties.containsKey(child)){
    		Map<String, Serializable> properties = resourceProperties.get(child);
    		return properties;
    	}
    	else {
    		Map<String, Serializable> properties = Collections.synchronizedMap(new HashMap<String, Serializable>());
    		resourceProperties.put(child, properties);
    		return properties;
    	}
    }

    /**
     * Retrieves the title from the IService cache, or from the ServiceInfo object iff it is
     * present. Returns null if either of these are not available. If the title is fetched from
     * ServiceInfo, it is added to the cache before returning.
     * 
     * @returns the service title or null if non is readily available
     */
    public String getTitle() {
        String title = null;
        if (info != null) {
            // we are connected and can use the real title
            title = info.getTitle();

            // cache title for when we are next offline
            getPersistentProperties().put("title", title); //$NON-NLS-1$
        }
        if (title == null) {
            Serializable s = properties.get("title"); //$NON-NLS-1$
            title = (s != null ? s.toString() : null);
        }
        return title;
    }

    /**
     * This should represent the identifier
     * 
     * @see Object#equals(java.lang.Object)
     * @param obj
     * @return
     */
    public final boolean equals( Object obj ) {
        if (obj != null && obj instanceof IService) {
            IService service = (IService) obj;
            if (getID() != null && service.getID() != null)
                return getID().equals(service.getID());
        }
        return false;
    }
    /**
     * This should represent the identified
     * 
     * @see Object#hashCode()
     * @return
     */
    public final int hashCode() {
        int value = 31;

        if (getID() != null)
            value += 31 + getID().hashCode();
        value += 31 + getClass().getName().hashCode();
        return value;
    }

    /**
     * Indicate class and id.
     * 
     * @return string representing this IResolve
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        String classname = getClass().getSimpleName();
        buf.append(classname);
        buf.append("("); //$NON-NLS-1$
        ID ref = getID();
        buf.append( ref.labelServer() );
        buf.append(")"); //$NON-NLS-1$
        return buf.toString();
    }
    
    /**
     * Quick partial implementation for IService implementors.
     * <p>
     * Example use:<pre> public Status getStatus() {
     *    if( ds == null ){
     *        return super.getStatus(); // check isDisposed and getMessage()
     *    }
     *    return Status.CONNECTED;
     * }</pre>
     * @return DIPOSED, NOTCONNECTED or BROKEN (based on getMessage() being non null)
     */
    @Override
    public Status getStatus() {
        if( isDisposed ){
            return Status.DISPOSED;
        }
        if( getMessage() != null ){
            return Status.BROKEN;
        }
        return Status.NOTCONNECTED;
    }
    protected void finalize() throws Throwable {
        // clean up connection
        if( !isDisposed ){
            CatalogPlugin.trace( getClass().getName()+" being cleaned up by fianlize, without prior call to dispose", null );
            dispose(new NullProgressMonitor());
        }
        super.finalize();
    }
    
    /**
     * Calls dispose on each member (when connected).
     * <p>
     * Subclasses
     * 
     */
    public void dispose( IProgressMonitor monitor ) {
        if( isDisposed ){
            throw new IllegalStateException("IService.dispose() called, for the second time"); // downgrade to warning?
        }
        monitor.beginTask(Messages.IService_dispose, 100);
        
        if (getStatus() == Status.CONNECTED) {
            try {
                // only ask for members if we are connected
                // (if not we just get an error trying to connect again)
                List< ? extends IResolve> members = members(new SubProgressMonitor(monitor, 1));

                int steps = (int) ((double) 99 / (double) members.size());
                for( IResolve resolve : members ) {
                    try {
                        SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, steps);
                        resolve.dispose(subProgressMonitor);
                        subProgressMonitor.done();
                    } catch (Throwable e) {
                        ErrorManager
                                .get()
                                .displayException(
                                        e,
                                        "Error during dispose: " + resolve.getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
                    }
                }
            } catch (Throwable e) {
                ErrorManager.get().displayException(e,
                        "Cleaning up memebers of service: " + getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
                return;
            }
        }
        isDisposed = true;
    }

}
