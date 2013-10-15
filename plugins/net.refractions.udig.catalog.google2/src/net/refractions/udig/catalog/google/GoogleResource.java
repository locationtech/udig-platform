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
package net.refractions.udig.catalog.google;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.v1_0_0.xml.WFSSchema;
import org.geotools.data.wms.WebMapServer;
import org.geotools.data.wms.xml.WMSSchema;

import com.vividsolutions.jts.geom.Envelope;

/**
 * A Georesource that lazily loads the "real" WFS or WMS resource.  GetInfo will return a placeholder until
 * the "real" resource is loaded.
 * 
 * @author David Zwiers, Refractions Research
 */
abstract class GoogleResource extends IGeoResource {

    public static GoogleResource getResource(OGCLayer layer) {
        if (layer.getServertype().equalsIgnoreCase("WMS")) { //$NON-NLS-1$
            return getWMSResource(layer);
        } 
        if (layer.getServertype().equalsIgnoreCase("WFS")) { //$NON-NLS-1$
            return getWFSResource(layer);
        }
        return null;
    }
    
    public static GoogleResource getWMSResource(OGCLayer layer) {
        return new GoogleWMSResource(layer);
    }
    public static GoogleResource getWFSResource(OGCLayer layer) {
        return new GoogleWFSResource(layer);
    }
    
    protected Throwable msg = null;
    private URL id = null;
    
    public GoogleResource(OGCLayer layer) {
        service = null; // we do not have a service (you will need to connect first!)
        id = layer.getId();
        info = new IGeoResourceInfo(layer.getTitle(), layer.getName(), layer.getDescription(), getSchema(), 
                new Envelope(), null, new String[] {layer.getServertype(), layer.getName(), layer.getTitle()}, getIcon() );
    }
    
    protected abstract URI getSchema();
    protected abstract ImageDescriptor getIcon();
    
    /*
     * Required adaptions:
     * <ul>
     * <li>IGeoResourceInfo.class
     * <li>IService.class
     * </ul>
     * 
     * @see net.refractions.udig.catalog.IResolve#resolve(java.lang.Class, org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException{
        if(adaptee == null)
            return null;

        if(real != null)
            return real.resolve(adaptee,monitor);
        
        if(adaptee.isAssignableFrom(IGeoResourceInfo.class))
            return adaptee.cast( this.createInfo(monitor));
        
        loadReal(monitor);
        if(real != null)
            return real.resolve(adaptee,monitor);
        return super.resolve(adaptee, monitor);
    }
    protected IGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException{
        if(real==null){
            return null; // could not connect
        }
        return real.getInfo(monitor);
    }
    protected IGeoResource real = null;
    protected abstract void loadReal(IProgressMonitor monitor) throws IOException;
    
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if(adaptee == null)
            return false;
        return (adaptee.isAssignableFrom(IGeoResourceInfo.class) || 
                adaptee.isAssignableFrom(IService.class))||
                super.canResolve(adaptee);
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return msg==null?Status.CONNECTED:Status.BROKEN;
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
        if( real!=null )
            return real.getIdentifier();
        return id;
    }

static class GoogleWMSResource extends GoogleResource{

    /**
     * Construct <code>GoogleWMSResource</code>.
     *
     * @param layer
     * @throws MalformedURLException
     */
    public GoogleWMSResource( OGCLayer layer ) {
        super(layer);
        url = layer.getOnlineresource();
    }
    private URL url = null;

    /*
     * @see net.refractions.udig.catalog.google.GoogleResource#getSchema()
     */
    protected URI getSchema() {
        return WMSSchema.NAMESPACE;
    }

    /*
     * @see net.refractions.udig.catalog.google.GoogleResource#getIcon()
     */
    protected ImageDescriptor getIcon() {
        return null;
    }
    
    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        if(adaptee == null)
            return false;
        return super.canResolve(adaptee) ||
            adaptee.isAssignableFrom(WebMapServer.class) ||
            adaptee.isAssignableFrom(org.geotools.data.ows.Layer.class)||
            super.canResolve(adaptee);
        
    }

    /*
     * @see net.refractions.udig.catalog.google.GoogleResource#loadReal(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void loadReal( IProgressMonitor monitor ) throws IOException {
        createInfo(monitor); // load me
        if(info == null || url == null|| info.getName() == null)
            return;
        
        URL id = getIdentifier();
        if(id==null)
            return;
        
        if(service == null){
        	List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
        	Iterator<IService> srv = services.iterator();
        	while(srv.hasNext()){
        		IService s = srv.next();
        		if(s.canResolve(WebMapServer.class)){
        			service = s;
        		}
        	}

            if(service == null){
                return;
            }
        }
        if(real == null){
	        CatalogPlugin.getDefault().getLocalCatalog().add(service);
	        // search for the child
	        List<? extends IGeoResource> resL = service.resources(monitor);
	        if(resL==null){
	            return;
	        }
	        Iterator<? extends IGeoResource> res = resL.iterator();
	        while(res.hasNext() && real == null){
	            IGeoResource restmp = res.next();
	            if(restmp!=null && restmp.getIdentifier()!=null && info.getName().equals(restmp.getIdentifier().getRef()))
	                real = restmp;
	        }
        }
        IResolveDelta delta = new ResolveDelta( this, real, null);
        ((CatalogImpl)CatalogPlugin.getDefault().getLocalCatalog()).fire( new ResolveChangeEvent( this, IResolveChangeEvent.Type.POST_CHANGE, delta )  );
    }
}


static class GoogleWFSResource extends GoogleResource{

    /**
     * Construct <code>GoogleWMSResource</code>.
     *
     * @param layer
     * @throws MalformedURLException
     */
    public GoogleWFSResource( OGCLayer layer ) {
        super(layer);
        url = layer.getOnlineresource();
    }
    private URL url = null;

    /*
     * @see net.refractions.udig.catalog.google.GoogleResource#getSchema()
     */
    protected URI getSchema() {
        return WFSSchema.NAMESPACE;
    }

    /*
     * @see net.refractions.udig.catalog.google.GoogleResource#getIcon()
     */
    protected ImageDescriptor getIcon() {
        return null;
    }

    /*
     * @see net.refractions.udig.catalog.google.GoogleResource#loadReal(org.eclipse.core.runtime.IProgressMonitor)
     */
    protected void loadReal( IProgressMonitor monitor ) throws IOException {
        createInfo(monitor); // load me
        if(info == null || url == null|| info.getName() == null)
            return;
        
        URL id = getIdentifier();
        if(id==null)
            return;
        
        if(service == null){

        	List<IService> services = CatalogPlugin.getDefault().getServiceFactory().createService(url);
        	Iterator<IService> srv = services.iterator();
        	while(srv.hasNext()){
        		IService s = srv.next();
        		if(s.canResolve(WFSDataStore.class)){
        			service = s;
        		}
        	}

            if(service == null){
                return;
            }
        }

        if(real == null){
	        // search for the child
	        List<? extends IGeoResource> resL = service.resources(monitor);
	        if(resL==null)
	            return;
	        Iterator<? extends IGeoResource> res = resL.iterator();
	        while(res.hasNext() && real == null){
	            IGeoResource restmp = res.next();
	            if(restmp!=null && restmp.getIdentifier()!=null && info.getName().equals(restmp.getIdentifier().getRef()))
	                real = restmp;
	        }
        }
        IResolveDelta delta = new ResolveDelta( this, real, null );
        ((CatalogImpl)CatalogPlugin.getDefault().getLocalCatalog()).fire( new ResolveChangeEvent( this, IResolveChangeEvent.Type.POST_CHANGE, delta )  );
    }
}

}