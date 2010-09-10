
package net.refractions.udig.catalog.internal.wmt.ww;

import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.internal.wmt.WMTPlugin;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WWSource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.ww.QuadTileSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Based on WMSGeoResourceImpl this class represents a WWSource (QuadTileSet) 
 * in the catalog.
 * 
 * @see net.refractions.udig.catalog.internal.wmt.wmtsource.ww.QuadTileSet
 * @see net.refractions.udig.catalog.internal.wmt.wmtsource.WWSource
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WWGeoResource extends IGeoResource {

    private WWSource wwSource;
    
    private URL identifier;
    private IResolve parent;
    
    private Throwable msg = null;

    public WWGeoResource(WWService service, IResolve parent, WWSource wwSource) {
        this.service = service;
        this.wwSource = wwSource;
        
        // if parent is empty, use the service as parent
        if (parent == null) {
            this.parent = service;
        } else {
            this.parent = parent;
        }
                
        try {
            identifier = new URL(service.getIdentifier().toString() + "#" + wwSource.getId()); //$NON-NLS-1$

        } catch (Throwable e) {
            WMTPlugin.log(null, e);
            identifier = service.getIdentifier();
        }
    }
    
    public WWGeoResource(WWService service, IResolve parent, QuadTileSet quadTileSet) {
        this(service, parent, new WWSource(quadTileSet));
    }

    @Override
    public IResolve parent( IProgressMonitor monitor ) throws IOException {
        return parent;
    }
    
    public WMTSource getSource(){
        return wwSource;
    }
    
    public String getTitle() {
        return getSource().getName();   
    }
    
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null
                && (adaptee.isAssignableFrom(WMTSource.class)
                        || super.canResolve(adaptee));
    }

    /*
     * @see net.refractions.udig.catalog.IGeoResource#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified" ); //$NON-NLS-1$
        }        
        if (adaptee.isAssignableFrom(WMTSource.class)) {
            return adaptee.cast(getSource());
        }

        return super.resolve(adaptee, monitor);
    }


    /*
     * @see net.refractions.udig.catalog.IGeoResourceInfo#createInfo(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor
     */
    protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null){
            synchronized (this) {
                if (info == null){
                    info = new WWGeoResourceInfo(this, monitor);
                }
            }
        }
        return info;
    }


    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        if (msg != null) {
            return Status.BROKEN;
        } else if (wwSource == null){
            return Status.NOTCONNECTED;
        } else {
            return Status.CONNECTED;
        }
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return msg;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return identifier;
    }
}