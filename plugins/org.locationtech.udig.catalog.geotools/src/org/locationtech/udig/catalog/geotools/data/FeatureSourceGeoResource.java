/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.geotools.data;

import java.io.IOException;
import java.net.URL;

import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.data.DataStore;
import org.geotools.data.ResourceInfo;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.feature.type.Name;

public class FeatureSourceGeoResource extends IGeoResource {

    protected Name name;
    private ID id;

    public FeatureSourceGeoResource( DataStoreService service, Name name ) {
        this.service = service;
        this.name = name;
        this.id = new ID(service.getID(), name.getLocalPart());
    }
    public DataStoreService service( IProgressMonitor monitor ) throws IOException {
        return (DataStoreService) this.service;
    };
    public SimpleFeatureSource toFeatureSource() throws IOException {
        DataStore dataStore = service(null).toDataAccess();
        return dataStore.getFeatureSource(name);
    }
    @Override
    protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        SimpleFeatureSource featureSource = toFeatureSource();
        ResourceInfo gtInfo = featureSource.getInfo();
        return new FeatureSourceGeoResourceInfo(gtInfo);
    }

    @Override
    public ID getID() {
        return id;
    }

    @Override
    public URL getIdentifier() {
        return id.toURL();
    }

    public Throwable getMessage() {
        return null;
    }

    public Status getStatus() {
        return null;
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null) {
            return false;
        }
        return adaptee.isInstance(this)
                || adaptee.isAssignableFrom(FeatureSourceGeoResourceInfo.class)
                || adaptee.isAssignableFrom(SimpleFeatureSource.class)
                || adaptee.isAssignableFrom(SimpleFeatureStore.class)
                || super.canResolve(adaptee);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isInstance(this)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(FeatureSourceGeoResourceInfo.class)) {
            return adaptee.cast(createInfo(monitor));
        }
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            SimpleFeatureSource fs = toFeatureSource();
            if (fs instanceof SimpleFeatureStore) {
                return adaptee.cast(fs);
            }
        }
        if (adaptee.isAssignableFrom(SimpleFeatureSource.class)) {
            return adaptee.cast(toFeatureSource());
        }
        return super.resolve(adaptee, monitor);
    }

}
