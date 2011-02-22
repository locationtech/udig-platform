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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.internal.Messages;
import net.refractions.udig.ui.ErrorManager;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * Represents a geo spatial service handle. Follows the same design as IResource.
 * <p>
 * Represents a spatial service, which may be lazily loaded. The existence of this object does not
 * ensure that the advertised data is guaranteed to exist, nor does this interface guarantee that
 * the service exists based on this object's existence. We should also note the resource management
 * is left to the user, and that resolve() is not guaranteed to return the same instance object from
 * two subsequent calls, but may. This is merely a handle to some information about a service, and a
 * method of acquiring an instance of the service ...
 * </p>
 * <p>
 * NOTE: This may be the result of communications with a metadata service, and as such this service
 * described may not be running right now. Remember to check the service status.
 * </p>
 *
 * <h2>Implementing an IService</h2>
 *
 * Implement the abstract methods and you are good to go.
 *
 * <h2>Extending an IService</h2>
 *
 * You may want to implement your own IService in order to provide a handle for a new kind of <i>API</i>.
 * <ol>
 * <li>New method:<pre><code>
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
 *  </pre></code>
 *  (note the use of NullProgressMonitor)
 *  </li>
 *
 * <li>Optional: Customize resolve method to advertise your new API "dynamically"<pre><code>
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
 * </code></pre>
 * (note the call to super)
 * </li>
 * <li>Optional: cache your API as the "connection"<pre><code>
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
 * </code></pre>
 * (Note the use of getMessage and getStatus)
 * </li>
 * </ol>
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 * @see IServiceInfo
 * @see IServiceFactory
 */
public abstract class IService implements IResolve {
    /**
     * Will attempt to morph into the adaptee, and return that object. Hardened coded to capture the
     * IService contract.
     * <p>
     * That is we *must* resolve the following:
     * <ul>
     * <li>IService: this
     * <li>IServiceInfo.class: getInfo
     * <li>ICatalog.class: parent
     * </ul>
     * Recommended adaptations:
     * <ul>
     * <li>ImageDescriptor.class: for a custom icon
     * <li>List.class: members
     * </ul>
     * May Block.
     * <p>
     * Example implementation:<pre><code>
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
     * </code></pre>
     * @param adaptee
     * @param monitor
     * @return instance of adaptee, or null if unavailable (IServiceInfo and List<IGeoResource>
     *         must be supported)
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
     * Hardened coded to capture the IService contract.
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
     * <pre><code>
     * public &lt;T&gt; boolean canResolve( Class&lt;T&gt; adaptee ) {
     *     return adaptee != null
     *             &amp;&amp; (adaptee.isAssignableFrom(DataStore.class) || super.canResolve(adaptee));
     * }
     * </code></pre>
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null && (adaptee.isAssignableFrom(IService.class) || // this
                adaptee.isAssignableFrom(IServiceInfo.class) || // getInfo
                adaptee.isAssignableFrom(List.class) || // members
                adaptee.isAssignableFrom(ICatalog.class) || // parent
                CatalogPlugin.getDefault().getResolveManager().canResolve(this, adaptee));
    }

    /**
     * Returns LocalCatalog by default, subclass must override iff a custom catalog is used.
     */
    public ICatalog parent( IProgressMonitor monitor ) {
    	ICatalog localCatalog = CatalogPlugin.getDefault().getLocalCatalog();
    	if(localCatalog.getById(IService.class, getIdentifier(), monitor)!=null ) {
			return localCatalog;
		} else {
			return null;
		}
    }
    /**
     * List of concrete resources (IE with data) managed by this service.
     * <p>
     * Many file based services will just contain a single IGeoResource. Services
     * that arrange their data into folders will (like WMS) will return a list of
     * all the actual data made available by the service.
     * </p>
     * The IService.resources() method is the inverse of GeoResource.service().
     */
    public abstract List< ? extends IGeoResource> resources( IProgressMonitor monitor )
            throws IOException;

    /**
     * The contents of this IService presented as a list of members.
     * <p>
     * For many simple services this will be the same as the the resources, however
     * some services may choose to organize their resources into folders or schemas,
     * or even a tree (as is the case for WMS layers).
     */
    public List<IResolve> members( IProgressMonitor monitor )
            throws IOException{
        List<IResolve> resolves = new ArrayList<IResolve>(resources(monitor));
        return resolves;
    }

    /**
     * Information about this service.
     *
     * @return IServiceInfo resolve(IServiceInfo.class,IProgressMonitor monitor);
     * @see IService#resolve(Class, IProgressMonitor)
     */
    public abstract IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException;

    /**
     * Accessor to the set of params used to create this entry. There is no guarantee that these
     * params created a usable service (@see getStatus() ). These params may have been modified
     * within the factory during creation. This method is intended to be used for cloning (@see
     * IServiceFactory) or for persistence between sessions.
     *
     * <p>
     * <b>IMPORTANT:</b> Because of the serialization currently used only types that can be reconstructed from their toString() representation
     * can be used.  For example:
     * <pre><code>
     * valueThatIsSaved=url.toString().
     * URL restoredValue=new URL(valueThatIsSaved);
     * </code></pre>
     * Also only classes that this plugin can load can be loaded so custom classes from downstream plugins cannot be used.
     * It is recommended that only "normal" types be used like Integer, URL, Float, etc...
     *
     * </p>
     * This restriction will be lifted in the future.  (Except for the loading issue that is a design issue that we will live with.)
     * @see IServiceFactory
     * @return
     */
    public abstract Map<String, Serializable> getConnectionParams();

    /**
     * This should represent the identifier
     *
     * @see Object#equals(java.lang.Object)
     * @param arg0
     * @return
     */
    public final boolean equals( Object arg0 ) {
        if (arg0 != null && arg0 instanceof IService) {
            IService service = (IService) arg0;
            if (getIdentifier() != null && service.getIdentifier() != null)
                return URLUtils.urlToString(getIdentifier(), false).equals(
                        URLUtils.urlToString(service.getIdentifier(), false));
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

        if (getIdentifier() != null)
            value += 31 + URLUtils.urlToString(getIdentifier(), false).hashCode();
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
        String classname = getClass().getName();
        String name = classname.substring(classname.lastIndexOf('.') + 1);
        buf.append(name);
        buf.append("("); //$NON-NLS-1$
        buf.append(getIdentifier());
        buf.append(")"); //$NON-NLS-1$
        return buf.toString();
    }

    public void dispose( IProgressMonitor monitor ) {
        monitor.beginTask(Messages.IService_dispose, 100);
        List< ? extends IResolve> members;
        try {
            members = members(new SubProgressMonitor(monitor, 1));
        } catch (Throwable e) {
            ErrorManager.get().displayException(e,
                    "Error disposing members of service: " + getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
            return;
        }
        int steps = (int) ((double) 99 / (double) members.size());
        for( IResolve resolve : members ) {
            try {
                SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, steps);
                resolve.dispose(subProgressMonitor);
                subProgressMonitor.done();
            } catch (Throwable e) {
                ErrorManager.get().displayException(e,
                        "Error disposing members of service: " + getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
            }
        }
    }
}
