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
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * Represents a handle to a spatial resource.
 * <p>
 * The resource is not guaranteed to exist, nor do we guarantee that we can connect with the
 * resource. Some/All portions of this handle may be loaded as required. This resource handle may
 * also be the result a metadata service query.
 * <h2>Implementing an IService</h2>
 * <p>
 * Implement the abstract methods and you are good to go:
 * <ul>
 * <li>getInfo - information describing the resource such as name, bounds and CRS
 * <li>service - the service providing the resource
 * <li>getIdentifier - unique identifier used to track the service in the catalog
 * </ul>
 * <p>
 * Please consider implementing support for resolve( ImageDescriptor.class, null ) as it will allow
 * your IGeoResource to show up with a unique representation in the Catalog view.
 * </p>
 * 
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public abstract class IGeoResource implements IResolve {

    protected static IGeoResourceInfo INFO_UNAVAILABLE = new IGeoResourceInfo();

    /** Service providing this resource */
    protected IService service = null;

    /** Description of this resource */
    protected volatile IGeoResourceInfo info = null;

    /**
     * Blocking operation to resolve into the adaptee, if available.
     * <p>
     * Required adaptions:
     * <ul>
     * <li>GeoResource.class - this
     * <li>IGeoResourceInfo.class - getInfo( monitor ) ie about this handles contents
     * <li>IService.class - service( monitor ) ie that is responsible for this GeoResource
     * </ul>
     * <p>
     * Example Use (no casting required!):
     * 
     * <pre>
     * <code>
     * IGeoResourceInfo info = resolve(IGeoResourceInfo.class);
     * </code>
     * </pre>
     * 
     * </p>
     * <p>
     * Recommended adaptions:
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
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
            return adaptee.cast(getInfo(monitor));
        }
        if (adaptee.isAssignableFrom(IService.class)) {
            return adaptee.cast(service(monitor));
        }
        if (adaptee.isAssignableFrom(IServiceInfo.class)) {
            try {
                monitor.beginTask("service info", 100); //$NON-NLS-1$
                IService service = service(new SubProgressMonitor(monitor, 40));
                if (service != null) {
                    IServiceInfo info = service.getInfo(new SubProgressMonitor(monitor, 60));
                    return adaptee.cast(info);
                }
            } finally {
                monitor.done();
            }
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
     * Harded coded to capture the IGeoResource contract.
     * <p>
     * That is we *must* resolve the following:
     * <p>
     * Required adaptions:
     * <ul>
     * <li>GeoResource.class - this
     * <li>IGeoResourceInfo.class - getInfo (ie about this handles contents)
     * <li>IService.class - service (ie that is responsible for this GeoResource)
     * </ul>
     * <p>
     * Recommendated adaptions:
     * <ul>
     * <li>ImageDescriptor.class (for icon provided by external service)
     * <li>List.class - members (ie children of this georesource as in the wms layer case)
     * </ul>
     * <p>
     * Here is an implementation example (for something that can adapt to ImageDescriptor and
     * FeatureSource):
     * 
     * <pre>
     * <code>
     * public &lt;T&gt; boolean canResolve( Class&lt;T&gt; adaptee ) {
     *     return adaptee != null
     *             &amp;&amp; (adaptee.isAssignableFrom(ImageDescriptor.class)
     *                     || adaptee.isAssignableFrom(FeatureSource.class) || super.canResolve(adaptee));
     * }
     * </code>
     * </pre>
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null && (adaptee.isAssignableFrom(IGeoResource.class) || // this
                adaptee.isAssignableFrom(IService.class) || // service( monitor )
                adaptee.isAssignableFrom(getClass()) || adaptee.isAssignableFrom(IResolve.class) || // parent
                CatalogPlugin.getDefault().getResolveManager().canResolve(this, adaptee));
    }

    /**
     * Delegate to implementing classes to create and return the appropriate IGeoResourceInfo
     * implementation.
     * 
     * @return IGeoResourceInfo resolve(IGeoResourceInfo.class,IProgressMonitor monitor);
     * @throws IOException
     */
    protected abstract IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException;

    /**
     * Blocking operation to describe this service.
     * <p>
     * Access to resource metadata describing the information. This method is used by
     * LabelDecorators to acquire title, and icon.
     * </p>
     * Example implementation:
     * <pre>
     * <code>    @Override
     *     public CSVGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
     *         return (CSVGeoResourceInfo) super.getInfo(monitor);
     *     }
     *     protected CSVGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
     *         return new CSVGeoResourceInfo( this, monitor );
     *     }
     * </code>
     * </pre>
     * <p>
     * Implementors are encouraged to override this method if providing a specific IGeoResourceInfo
     * implementation with "extra" information beyond the dublin core. However please call
     * *super.getInfo* as it is providing caching for you and will insure that createInfo is only
     * called once.
     * </p>
     * 
     * @return IGeoResourceInfo resolve(IGeoResourceInfo.class,IProgressMonitor monitor);
     * @see IGeoResource#resolve(Class, IProgressMonitor)
     * @see IGeoResource#createInfo(IProgressMonitor)
     * @see IGeoResource#getTitle()
     */
    public IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null) { // lazy creation
            synchronized (this) { // support concurrent access
                if (info == null) {
                    if (Display.getCurrent() != null) {
                        // This is bad and has a chance of hanging the UI
                        if( Platform.inDevelopmentMode()){
                            throw new IllegalStateException("Lookup of getInfo not available from the display thread"); //$NON-NLS-1$
                        }
                    }
                    info = createInfo(monitor);
                    if (info == null) {
                        // could not connect or INFO_UNAVAILABLE
                        info = INFO_UNAVAILABLE;
                    } else {
                        // could issue a catalog event indicating new information is available
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
            return null;
        }
        return info;
    }
    
    /**
     * Map of this resource's persistent properties, may be empty.
     * 
     * @return The map containing the persistent properties where the key is the
     *         {@link QualifiedName} of the property and the value is the {@link String} value of
     *         the property.
     */
    public Map<String, Serializable> getPersistentProperties() {
    	ID id = this.getID();
        Map<String, Serializable> properties = service.getPersistentProperties( id );
        return properties;
    }

    /**
     * Returns parent for this GeoResource.
     * <p>
     * Most implementations will use the following code example:
     * 
     * <pre>
     * <code>
     * public IService parent( IProgressMonitor monitor ) throws IOException {
     *     return service(monitor);
     * }
     * </code>
     * </pre>
     * 
     * This code example preserves backwords compatibility with uDig 1.0 via type narrowing IResolve
     * to IService.
     * <p>
     * You will need to provide a different implementation when working with nested content (like
     * database schema or wms layers).
     * 
     * @return parent IResolve for this GeoResource
     * @see IGeoResource#resolve(Class, IProgressMonitor)
     */
    // TODO public abstract IResolve parent( IProgressMonitor monitor ) throws IOException;
    public IResolve parent( IProgressMonitor monitor ) throws IOException {
        return service(monitor);
    }

    /**
     * List of children, or EMPTY_LIST for a leaf.
     * <p>
     * The provided implementation indicates that this IGeoResource is a leaf.
     * </p>
     * 
     * @return Collections.emptyList();
     * @see org.locationtech.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
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
    public boolean equals( Object other ) {
        if (other != null && other instanceof IGeoResource) {
            IGeoResource resource = (IGeoResource) other;
            if (getID() != null)
                return getID().equals(resource.getID());
        }
        return false;
    }

    /**
     * This should represent the identified
     * 
     * @see Object#hashCode()
     * @return
     */
    public int hashCode() {
        int value = 31;

        if (getID() != null)
            value += 31 + getID().hashCode();
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
     * getGenericIcon(IGeoResource resource){ if(resource !=null){ assert resource.getIdentifier()
     * != null; if(resource.canResolve(FeatureSource.class)){ // default feature return
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
        String classname = getClass().getSimpleName();
        buf.append(classname);
        buf.append("("); //$NON-NLS-1$
        ID ref = getID();
        buf.append( ref.labelResource() );
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
     * Disposes of any resources or listeners required. Default implementation does nothing.
     * 
     * @param monitor monitor to show progress
     */
    public void dispose( IProgressMonitor monitor ) {
        // default impl does nothing
    }
    /**
     * Retrieves the title from the IService cache or from the GeoResourceInfo object if they exist.
     * No objects are created and null is returned if there is no title readily available.
     * 
     * @return title or null if none is readily available
     */
    public String getTitle() {
        String title = null;
        if (info != null) {
            // We are connected and have a real title!
            title = info.getTitle();
            if (title != null && service != null) {
                // cache the title for when we are not connected
                Map<String, Serializable> persistentProperties = getPersistentProperties();
                persistentProperties.put("title", title); //$NON-NLS-1$
            }
        }
        if (title == null && service != null) {
            // let us grab the title from the cache
            Map<String, Serializable> persistentProperties = getPersistentProperties();
            Serializable s = persistentProperties.get("title"); //$NON-NLS-1$
            title = (s != null ? s.toString() : null);
        }
        return title;
    }
    
    public IService service( IProgressMonitor monitor ) throws IOException {
        return service;
    }
}
