/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.catalog.tests;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import com.vividsolutions.jts.geom.Envelope;
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