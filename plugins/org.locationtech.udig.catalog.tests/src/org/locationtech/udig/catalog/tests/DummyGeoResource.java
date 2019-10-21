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
package org.locationtech.udig.catalog.tests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import org.locationtech.jts.geom.Envelope;
/**
 *  Simple IGeoResource for testing purposes.
 * <p>
 * {@link #addResolveTos(Object)} allows the resource to be configured by declaring what objects
 * the resource can "resolve to".  It can always resolve to an IService and a IGeoResourceInfo.
 * </p>
 * @author Jesse
 * @since 1.1.0
 */
public class DummyGeoResource extends IGeoResource {

	IService parent;
	String name;
    
	
	public DummyGeoResource(IService parent, String name) {
	    this.service = parent;
		this.parent = parent;
		this.name = name;
	}
    public List<Object> resolveTos=new ArrayList<Object>(); 
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
        for( Object resolveObject : resolveTos ) {
            if( adaptee.isAssignableFrom(resolveObject.getClass()) )
                return adaptee.cast(resolveObject);
        }
//           if( IService.class.isAssignableFrom(adaptee) )
//                return adaptee.cast(parent);
           if( IGeoResourceInfo.class.isAssignableFrom(adaptee) )
               return adaptee.cast(createInfo(monitor));
           if( List.class.isAssignableFrom(adaptee) )
               return adaptee.cast(resolveTos);
               
		return super.resolve(adaptee, monitor);
	}
    public <T> boolean canResolve(Class<T> adaptee) {
        for( Object resolveObject : resolveTos ) {
            if( adaptee.isAssignableFrom(resolveObject.getClass()))
                return true;
        }

		return adaptee.isAssignableFrom(IService.class) || 
			adaptee.isAssignableFrom(IGeoResourceInfo.class)||
            adaptee.isAssignableFrom(List.class) ||
			super.canResolve(adaptee);
	}

	public Status getStatus() {
		return Status.CONNECTED;
	}

	public Throwable getMessage() {
		return null;
	}

	public URL getIdentifier() {
		try {
			return new URL(parent.getIdentifier().toString() + "#" + name); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected IGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
		return new DummyGeoResourceInfo();
	}
	
	public class DummyGeoResourceInfo extends IGeoResourceInfo {
		@Override
		public String getName() {
			return DummyGeoResource.this.getIdentifier().toExternalForm();
		}
		
		@Override
		public String getTitle() {
			return DummyGeoResource.this.getIdentifier().toExternalForm();
		}
        @Override
        public ReferencedEnvelope getBounds() {
            return new ReferencedEnvelope(new Envelope(-180,180,-90,90), DefaultGeographicCRS.WGS84);
        }
	}
}
