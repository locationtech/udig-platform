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
package org.locationtech.udig.catalog.memory.internal;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IResolveChangeEvent;
import org.locationtech.udig.catalog.IResolveDelta;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.ITransientResolve;
import org.locationtech.udig.catalog.IResolve.Status;
import org.locationtech.udig.catalog.internal.CatalogImpl;
import org.locationtech.udig.catalog.internal.Messages;
import org.locationtech.udig.catalog.internal.ResolveChangeEvent;
import org.locationtech.udig.catalog.internal.ResolveDelta;
import org.locationtech.udig.catalog.memory.ActiveMemoryDataStore;
import org.locationtech.udig.catalog.memory.MemoryDSFactory;
import org.locationtech.udig.catalog.memory.MemoryServiceExtensionImpl;
import org.locationtech.udig.ui.ErrorManager;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeatureType;

public class MemoryServiceImpl extends IService implements ITransientResolve {

    /** the data store * */
    private volatile ActiveMemoryDataStore ds;

    private volatile List<MemoryGeoResourceImpl> memberList;

    private URL id;

    public Lock rLock = new UDIGDisplaySafeLock();

    private Lock dsInstantiationLock = new UDIGDisplaySafeLock();

    private MemoryDSFactory factory;

    public MemoryServiceImpl( URL id ) {
        this(id, null);
    }

    public MemoryServiceImpl( URL id, MemoryDSFactory factory ) {
        this.id = id;
        this.factory = factory;
    }

    @Override
    public Map<String, Serializable> getConnectionParams() {
        Map<String, Serializable> params = new MemoryServiceExtensionImpl()
                .createParams(getIdentifier());

        List< ? extends IGeoResource> member = null;
        try {
            member = resources(null);
        } catch (IOException e) {
            CatalogPlugin.log("", e); //$NON-NLS-1$
        }

        if (member != null) {
            StringBuffer buffer = null;
            for( IGeoResource resource : member ) {
                SimpleFeatureType type;
                try {
                    type = (SimpleFeatureType) resource.resolve(FeatureSource.class, null)
                            .getSchema();
                    if (buffer == null) {
                        buffer = new StringBuffer();
                    } else {
                        buffer.append("_MEMBER_"); //$NON-NLS-1$
                    }

                    buffer.append(type.getName().getLocalPart());
                    buffer.append("_SPLIT_"); //$NON-NLS-1$
                    buffer.append(DataUtilities.encodeType(type));
                } catch (IOException e) {
                    CatalogPlugin.log("", e); //$NON-NLS-1$
                }
            }
            if (buffer != null)
                params.put(MemoryServiceExtensionImpl.MEMBER_PARAM, buffer.toString());
        }
        return params;
    }

    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null)
            return false;

        return adaptee.isAssignableFrom(ActiveMemoryDataStore.class)
                || adaptee.isAssignableFrom(ITransientResolve.class) || super.canResolve(adaptee);
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }
        if (adaptee.isAssignableFrom(ITransientResolve.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(ActiveMemoryDataStore.class)) {
            return adaptee.cast(getDS());
        }
        return super.resolve(adaptee, monitor);
    }
    @Override
    public ScratchServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (ScratchServiceInfo) super.getInfo(monitor);
    }
    @Override
    protected synchronized IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        return new ScratchServiceInfo();
    }

    @Override
    public List< ? extends IGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        ActiveMemoryDataStore ds = getDS();
        String[] types = ds.getTypeNames();
        if (memberList == null) {
            rLock.lock();
            try {
                if (memberList == null) {
                    this.memberList = new ArrayList<MemoryGeoResourceImpl>();
                    for( String type : types ) {
                        if (!found(type))
                            this.memberList.add(new MemoryGeoResourceImpl(type, this));
                    }
                }
            } finally {
                rLock.unlock();
            }
        }
        return this.memberList;
    }

    private boolean found( String type ) {
        for( IGeoResource resource : memberList ) {
            if (type.equals(resource.getIdentifier().getRef()))
                return true;
        }
        return false;
    }

    public Status getStatus() {
        if( isDisposed ){
            return Status.DISPOSED;
        }
        return Status.CONNECTED;
    }

    public Throwable getMessage() {
        return null;
    }

    public URL getIdentifier() {
        return id;
    }

    ActiveMemoryDataStore getDS() {
        if (ds == null) {
            boolean changed = false;
            dsInstantiationLock.lock();
            try {
                if (ds == null) {
                    ds = createNewDS();
                    changed = true;
                    ds.addListener(new MemoryServiceListener(){
                        public void schemaChanged() {
                            MemoryServiceImpl.this.memberList = null;
                            IResolveDelta delta = new ResolveDelta(MemoryServiceImpl.this,
                                    IResolveDelta.Kind.CHANGED);
                            ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                                    .fire(new ResolveChangeEvent(MemoryServiceImpl.this,
                                            IResolveChangeEvent.Type.POST_CHANGE, delta));
                        }
                    });
                }
            } finally {
                dsInstantiationLock.unlock();
            }
            if (changed) {
                IResolveDelta delta = new ResolveDelta(this, IResolveDelta.Kind.CHANGED);
                ((CatalogImpl) CatalogPlugin.getDefault().getLocalCatalog())
                        .fire(new ResolveChangeEvent(this, IResolveChangeEvent.Type.POST_CHANGE,
                                delta));
            }
        }

        return ds;
    }

    private ActiveMemoryDataStore createNewDS() {
        if (this.factory != null) {
            ActiveMemoryDataStore ds = this.factory.createNewDS();
            if (ds != null) {
                return ds;
            } else {
                CatalogPlugin
                        .log(
                                "MemoryDSFactory '" + this.factory.getClass() + "' returned invalid ActiveMemoryDataStore", null); //$NON-NLS-1$//$NON-NLS-2$
            }
        }
        return new ActiveMemoryDataStore();
    }

    public void dispose( IProgressMonitor monitor ) {
        super.dispose(monitor);
        if( memberList != null ){
            memberList = null;
        }
    }

    static class ScratchServiceInfo extends IServiceInfo {
        /*
         * @see org.locationtech.udig.catalog.IServiceInfo#getTitle()
         */
        public String getTitle() {
            return Messages.catalog_memory_service_title;
        }

        @Override
        public String getDescription() {
            return getTitle();
        }
    }
}
