/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.testsupport;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Simple IService for testing purposes.
 * <p>
 * {@link #addResolveTos(Object)} allows the service to be configured by declaring what objects
 * the service can "resolve to".  It can always resolve to a List and a IServiceInfo.
 * </p>
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class DummyService extends IService {

	public static URL url;
	static {
		try {
			url = new URL("file://dummy.refractions.net/dummy");	 //$NON-NLS-1$
		}
		catch(MalformedURLException e) {}
	}
	
	List<DummyGeoResource> members;
	Map<String, Serializable> params;
	
	public DummyService(Map<String, Serializable> params) {
		this.params = params;
		
		members = new ArrayList<DummyGeoResource>();
		members.add(new DummyGeoResource(this, "dummy")); //$NON-NLS-1$
	}
	
	public DummyService() {
        params=new HashMap<String, Serializable>();
        params.put("dummy",url); //$NON-NLS-1$
        members = new ArrayList<DummyGeoResource>();
        members.add(new DummyGeoResource(this, "dummy")); //$NON-NLS-1$
    }

    List<Object> resolveTos=new ArrayList<Object>(); 
    /**
     * Add an obect that this resource will resolve to.
     *
     * @param obj new object
     */
    void addResolveTos(Object obj){
        this.resolveTos.add(obj);
    }
    void clearResolveTos(){
        resolveTos.clear();
    }
    void removeResolveTos(Object obj){
        resolveTos.remove(obj);
    }

    @Override
	public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified" );
        }        
        for( Object resolveObject : resolveTos ) {
            if( adaptee.isAssignableFrom(resolveObject.getClass()) )
                return adaptee.cast(resolveObject);
        }
		return super.resolve(adaptee, monitor);
	}

	@Override
	public List<? extends IGeoResource> resources(IProgressMonitor monitor) throws IOException {
		return members;
	}

	@Override
	public Map<String, Serializable> getConnectionParams() {
		return params;
	}

	public <T> boolean canResolve(Class<T> adaptee) {
        if( adaptee == null ) return false;
        for( Object resolveObject : resolveTos ) {
            if( adaptee.isAssignableFrom(resolveObject.getClass()) )
                return true;
        }

		return adaptee.isAssignableFrom(DummyService.class) ||
            super.canResolve(adaptee);
	}

	public Status getStatus() {
		return Status.CONNECTED;
	}

	public Throwable getMessage() {
		return null;
	}

	public URL getIdentifier() {
			return (URL) params.get("dummy"); //$NON-NLS-1$
	}
	
	@Override
	public DummyServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
	    return (DummyServiceInfo) super.getInfo(monitor);
	}
	@Override
	protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
		return new DummyServiceInfo();
	}
	
	class DummyServiceInfo extends IServiceInfo {
		
		
	}

    /**
     * Creates a service.
     * @param id id of the service to create.  if null it will use {@link #url}
     *
     * @param serviceResolveTos the objects (besides georesources and info objects) that the service can resolve to. may be null
     * @param resourceResolveTos the objects (besides georesources and info objects) that the geoResources can resolve to.   may be null
     * The outer lists are for each resource and the inner lists are the objects the resource resolves to.
     */
    public static IService createService(  URL id, List<Object> serviceResolveTos, List<List<Object>> resourceResolveTos ) {
        URL id2;
        if( id==null )
            id2=url;
        else 
            id2=id;
        
        Map<String, Serializable> params=new HashMap<String, Serializable>();
        params.put("dummy",id2); //$NON-NLS-1$
        DummyService service=new DummyService(params);
        if( serviceResolveTos!=null )
        for( Object object : serviceResolveTos ) {
            service.addResolveTos(object);
        }
        
        int i=0;
        List<DummyGeoResource> resources=new ArrayList<DummyGeoResource>();
        if( resourceResolveTos!=null )
        for( List<Object> list : resourceResolveTos ) {
            i++;
            DummyGeoResource resource = new DummyGeoResource(service, "DummyResource."+i); //$NON-NLS-1$
            for( Object object : list ) {
                resource.addResolveTos(object);
            }
            resources.add(resource);
        }
        
        service.members=resources;
        
        return service;
    }
	
}
