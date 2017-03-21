/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
import java.util.List;
import java.util.Map;

import org.locationtech.udig.catalog.IGeoResource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * For testing. A service with multiple resources like a WMS.
 * 
 * @author jones
 * @since 1.0.0
 */
public class DummyMultiResourceService extends DummyService {
    public static URL url;
    static {
        try {
            url = new URL("ftp://multidummy.refractions.net/dummy"); //$NON-NLS-1$
        } catch (MalformedURLException e) {
        }
    }
    List<IGeoResource> members = new ArrayList<IGeoResource>(2);
    public DummyMultiResourceService( Map<String, Serializable> params ) {
        super(params);
        members.add(new DummyGeoResource(this, "Resource1")); //$NON-NLS-1$
        members.add(new DummyGeoResource(this, "Resource2")); //$NON-NLS-1$
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified" );
        }        
        if (adaptee.isAssignableFrom(DummyMultiResourceService.class)){
            monitor.done();
            return adaptee.cast(this);
        }
        return super.resolve(adaptee, monitor);
    }

    @Override
    public List< ? extends IGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        return members;
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null
                && (adaptee.isAssignableFrom(DummyMultiResourceService.class) || super
                        .canResolve(adaptee));
    }

    @Override
    public URL getIdentifier() {
        return (URL) params.get(DummyMultiResourceServiceExtension.ID);
    }

}
