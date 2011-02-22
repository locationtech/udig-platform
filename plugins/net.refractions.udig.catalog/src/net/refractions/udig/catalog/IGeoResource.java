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
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Represents a handle to a spatial resource.
 * <p>
 * The resource is not guaranteed to exist, nor do we guarantee that we can connect with the
 * resource. Some/All portions of this handle may be loaded as required. This resource handle may
 * also be the result a metadata service query.
 *
 * <h2>Implementing an IService</h2>
 * <p>
 * Implement the abstract methods and you are good to go:
 * <ul>
 * <li>getInfo - information describing the resource such as name, bounds and CRS
 * <li>service - the service providing the resource
 * <li>getIdentifier - unique identifier used to track the service in the catalog
 * </ul>
 * <p>
 * Please consider implementing support for resolve( ImageDescriptor.class, null ) as it
 * will allow your IGeoResource to show up with a unique representation in the Catalog view.
 * </p>
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public abstract class IGeoResource implements IResolve {

    private volatile String stringURL;

    /**
     * Blocking operation to resolve into the adaptee, if available.
     * <p>
     * Required adaptations:
     * <ul>
     * <li>GeoResource.class - this
     * <li>IGeoResourceInfo.class - getInfo( monitor ) ie about this handles contents
     * <li>IService.class - service( monitor ) ie that is responsible for this GeoResource
     * </ul>
     * <p>
     * Example Use (no casting required!):
     *
     * <pre><code>
     * IGeoResourceInfo info = resolve(IGeoResourceInfo.class);
     * </code></pre>
     *
     * </p>
     * <p>
     * Recommended adaptations:
     * <ul>
     * <li>ImageDescriptor.class (for icon provided by external service)
     * <li>List.class - members( monitor ) ie children of this georesource as in the wms layer case
     * </ul>
     * </p>
     *
     * @param adaptee
     * @param monitor
     * @return instance of adaptee, or null if unavailable (IGeoResourceInfo and IService must be
     *         supported)
     * @see IGeoResourceInfo
     * @see IService
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
            return adaptee.cast(service(monitor));
        }
        if (adaptee.isAssignableFrom(IGeoResource.class)) {
            monitor.done();
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(getClass())) {
            return adaptee.cast(this);
        }
        IResolveManager rm = CatalogPlugin.getDefault().getResolveManager();
        if (rm.canResolve(this, adaptee)) {
            return rm.resolve(this, adaptee, monitor);
        }
        return null; // no adapter found (check to see if ResolveAdapter is registered?)
    }
    /**
     * Hardened coded to capture the IGeoResource contract.
     * <p>
     * That is we *must* resolve the following:
     * <p>
     * Required adaptations:
     * <ul>
     * <li>GeoResource.class - this
     * <li>IGeoResourceInfo.class - getInfo (ie about this handles contents)
     * <li>IService.class - service (ie that is responsible for this GeoResource)
     * </ul>
     * <p>
     * Recommended adaptations:
     * <ul>
     * <li>ImageDescriptor.class (for icon provided by external service)
     * <li>List.class - members (ie children of this georesource as in the wms layer case)
     * </ul>
     * <p>
     * Here is an implementation example (for something that can adapt to ImageDescriptor and
     * FeatureSource):
     *
     * <pre><code>
     * public &lt;T&gt; boolean canResolve( Class&lt;T&gt; adaptee ) {
     *     return adaptee != null
     *             &amp;&amp; (adaptee.isAssignableFrom(ImageDescriptor.class)
     *                     || adaptee.isAssignableFrom(FeatureSource.class) || super.canResolve(adaptee));
     * }
     * </code></pre>
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null && (adaptee.isAssignableFrom(IGeoResource.class) || // this
                adaptee.isAssignableFrom(IService.class) || // service( monitor )
                adaptee.isAssignableFrom(getClass()) ||
                adaptee.isAssignableFrom(IResolve.class) || // parent
                CatalogPlugin.getDefault().getResolveManager().canResolve(this, adaptee));
    }

    /**
     * Blocking operation to describe this service.
     * <p>
     * As an example this method is used by LabelDecorators to acquire title, and icon.
     * </p>
     *
     * @return IGeoResourceInfo resolve(IGeoResourceInfo.class,IProgressMonitor monitor);
     * @see IGeoResource#resolve(Class, IProgressMonitor)
     */
    public abstract IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException;

    /**
     * Returns the IService for this GeoResource.
     * <p>
     * Method is useful in dealing with deeply nested GeoResource children (where parent may not
     * always be an IService).
     *
     * @return IService for this GeoResource
     * @see IGeoResource#resolve(Class, IProgressMonitor)
     */
    public abstract IService service( IProgressMonitor monitor ) throws IOException;

    /**
     * Returns parent for this GeoResource.
     * <p>
     * Most implementations will use the following code example:
     *
     * <pre><code>
     * public IService parent( IProgressMonitor monitor ) throws IOException {
     *     return service(monitor);
     * }
     * </code></pre>
     *
     * This code example preserves backwards compatibility with uDig 1.0 via type narrowing IResolve
     * to IService.
     * <p>
     * You will need to provide a different implementation when working with nested content (like
     * database schema or wms layers).
     *
     * @return parent IResolve for this GeoResource
     * @see IGeoResource#resolve(Class, IProgressMonitor)
     */
//  TODO public abstract IResolve parent( IProgressMonitor monitor ) throws IOException;
    public IResolve parent( IProgressMonitor monitor ) throws IOException {
        return service(monitor);
    }

    /**
     * List of children, or EMPTY_LIST for a leaf.
     * <p>
     * The provided implementation indicates that this IGeoResource is a leaf.
     * </p>
     * @return Collections.emptyList();
     * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<IResolve> members( IProgressMonitor monitor ) {
        return Collections.emptyList(); // type safe EMPTY_LIST
    }

    /**
     * This should represent the identifier
     *
     * @see Object#equals(java.lang.Object)
     * @param arg0
     * @return
     */
    public boolean equals( Object arg0 ) {
        if (arg0 != null && arg0 instanceof IGeoResource) {
            IGeoResource resource = (IGeoResource) arg0;
            if (getIdentifier() != null && resource.getIdentifier() != null)
                return getStringURL().equals(resource.getStringURL());
        }
        return false;
    }
    private String getStringURL() {
        if( stringURL==null ){
            synchronized (this) {
                if( stringURL==null ){
                    stringURL=URLUtils.urlToString(getIdentifier(), false);
                }
            }
        }
        return stringURL;
    }
    /**
     * This should represent the identified
     *
     * @see Object#hashCode()
     * @return
     */
    public int hashCode() {
        int value = 31;

        if (getIdentifier() != null)
            value += 31 + URLUtils.urlToString(getIdentifier(), false).hashCode();
        value += 31 + getClass().getName().hashCode();
        return value;
    }

    /**
     * Non blocking label used by LabelProvider. public static final String
     * getGenericLabel(IGeoResource resource){ assert resource.getIdentifier() != null; return
     * resource==null ||
     * resource.getIdentifier()==null?"Resource":resource.getIdentifier().toString(); }
     */
    /**
     * Non blocking icon used by LabelProvider. public static final ImageDescriptor
     * getGenericIcon(IGeoResource resource){ if(resource !=null){ assert resource.getIdentifier() !=
     * null; if(resource.canResolve(FeatureSource.class)){ // default feature return
     * Images.getDescriptor(ISharedImages.FEATURE_OBJ); }
     * if(resource.canResolve(GridCoverage.class)){ // default raster return
     * Images.getDescriptor(ISharedImages.GRID_OBJ); } } return
     * Images.getDescriptor(ISharedImages.RESOURCE_OBJ); }
     */
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
     * The identifier of a IGeoResource is identified by parent().getIdentifer()#ResourceID
     * <p>
     * For example: A WMS (IService) with an id of http://www.something.com/wms?Service=WMS would
     * have georesources with ids similar to: http://www.something.com/wms?Service=WMS#layer1
     *
     * @see IResolve#getIdentifier()
     */
    public abstract URL getIdentifier();

    /**
     * Disposes of any resources or listeners required. Default implementation does nothing.
     *
     * @param monitor monitor to show progress
     */
    public void dispose( IProgressMonitor monitor ) {
        // default impl does nothing
    }
}
