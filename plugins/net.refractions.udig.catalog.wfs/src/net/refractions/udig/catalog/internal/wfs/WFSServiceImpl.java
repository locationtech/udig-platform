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
package net.refractions.udig.catalog.internal.wfs;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.IResolveChangeEvent;
import net.refractions.udig.catalog.IResolveDelta;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.CatalogImpl;
import net.refractions.udig.catalog.internal.ResolveChangeEvent;
import net.refractions.udig.catalog.internal.ResolveDelta;
import net.refractions.udig.catalog.internal.wfs.UDIGWFSDataStoreFactory.UDIGWFSDataStore;
import net.refractions.udig.catalog.wfs.internal.Messages;
import net.refractions.udig.ui.ErrorManager;
import net.refractions.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.geotools.data.ows.WFSCapabilities;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.xml.wfs.WFSSchema;

/**
 * Handle for a WFS service.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class WFSServiceImpl extends IService {

    private URL url = null;
    private Map<String,Serializable> params = null;
    protected Lock rLock=new UDIGDisplaySafeLock();
    public WFSServiceImpl(URL arg1, Map<String,Serializable> arg2){
        url = arg1;params = arg2;
    }

    /*
     * Required adaptions:
     * <ul>
     * <li>IServiceInfo.class
     * <li>List.class <IGeoResource>
     * </ul>
     *
     * @see net.refractions.udig.catalog.IService#resolve(java.lang.Class, org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if(adaptee == null)
            return null;
        if(adaptee.isAssignableFrom(WFSDataStore.class)){
            return adaptee.cast( getDS(monitor));
        }
        return super.resolve(adaptee, monitor);
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        if(adaptee == null)
            return false;
        return adaptee.isAssignableFrom(UDIGWFSDataStore.class)||
                super.canResolve(adaptee);
    }
    public void dispose( IProgressMonitor monitor ) {
        if( members==null)
            return;

        int steps = (int) ((double) 99 / (double) members.size());
        for( IResolve resolve : members ) {
            try {
                SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, steps);
                resolve.dispose(subProgressMonitor);
                subProgressMonitor.done();
            } catch (Throwable e) {
                ErrorManager.get().displayException(e,
                        "Error disposing members of service: " + getIdentifier(), CatalogPlugin.ID); //$NON-NLS-1$
            }
        }
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    public List<WFSGeoResourceImpl> resources( IProgressMonitor monitor ) throws IOException {

    	if(members == null){
            rLock.lock();
            try{
            	if(members == null){
                    getDS(monitor); // load ds
                    members = new LinkedList<WFSGeoResourceImpl>();
                    String[] typenames = ds.getTypeNames();
                    if(typenames!=null)
                    for(int i=0;i<typenames.length;i++){
                        try{
                            members.add(new WFSGeoResourceImpl(this,typenames[i]));
                        }catch (Exception e) {
                            WfsPlugin.log("", e); //$NON-NLS-1$
                        }
                    }
            	}
            }finally{
                rLock.unlock();
            }
    	}
        return members;
    }
    private volatile List<WFSGeoResourceImpl> members = null;

    /*
     * @see net.refractions.udig.catalog.IService#getInfo(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IServiceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        getDS(monitor); // load ds
        if(info == null && ds!=null){
            rLock.lock();
            try{
                if(info == null){
                    info = new IServiceWFSInfo(ds);
                    IResolveDelta delta = new ResolveDelta( this, IResolveDelta.Kind.CHANGED );
                    ((CatalogImpl)CatalogPlugin.getDefault().getLocalCatalog()).fire( new ResolveChangeEvent( this, IResolveChangeEvent.Type.POST_CHANGE, delta )  );
                }
            }finally{
                rLock.unlock();
            }
        }
        return info;
    }
    private volatile IServiceInfo info = null;
    /*
     * @see net.refractions.udig.catalog.IService#getConnectionParams()
     */
    public Map<String,Serializable> getConnectionParams() {
        return params;
    }
    private Throwable msg = null;
    private volatile UDIGWFSDataStore ds = null;
    private static final Lock dsLock = new UDIGDisplaySafeLock();

    UDIGWFSDataStore getDS(IProgressMonitor monitor) throws IOException{
        if(ds == null){
            if (monitor == null) monitor = new NullProgressMonitor();
            monitor.beginTask(Messages.WFSServiceImpl_task_name, 3);
            dsLock.lock();
            monitor.worked(1);
            try{
                if(ds == null){
                    UDIGWFSDataStoreFactory dsf = new UDIGWFSDataStoreFactory();
                    monitor.worked(1);
                    if(dsf.canProcess(params)){
                        monitor.worked(1);
                        try {
                            ds = (UDIGWFSDataStore) dsf.createDataStore(params);
                            monitor.worked(1);
                        } catch (IOException e) {
                            msg = e;
                            throw e;
                        }
                    }
                }
            }finally{
                dsLock.unlock();
                monitor.done();
            }
            IResolveDelta delta = new ResolveDelta( this, IResolveDelta.Kind.CHANGED );
            ((CatalogImpl)CatalogPlugin.getDefault().getLocalCatalog()).fire( new ResolveChangeEvent( this, IResolveChangeEvent.Type.POST_CHANGE, delta )  );
        }
        return ds;
    }
    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        return msg != null? Status.BROKEN : ds == null? Status.NOTCONNECTED : Status.CONNECTED;
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
        return url;
    }

    private class IServiceWFSInfo extends IServiceInfo {

        private WFSCapabilities caps = null;
        IServiceWFSInfo( UDIGWFSDataStore resource ){
            super();

            try{
                caps = resource.getCapabilities();
            }catch(Throwable t){
                t.printStackTrace();
                caps = null;
            }
        }

        /*
         * @see net.refractions.udig.catalog.IServiceInfo#getAbstract()
         */
        public String getAbstract() {
            return caps==null?null:caps.getService()==null?null:caps.getService().get_abstract();
        }
        /*
         * @see net.refractions.udig.catalog.IServiceInfo#getIcon()
         */
        public ImageDescriptor getIcon() {
            //return CatalogUIPlugin.getDefault().getImages().getImageDescriptor( ISharedImages.WFS_OBJ );
            return AbstractUIPlugin.imageDescriptorFromPlugin( WfsPlugin.ID, "icons/obj16/wfs_obj.16"); //$NON-NLS-1$
        }
        /*
         * @see net.refractions.udig.catalog.IServiceInfo#getKeywords()
         */
        public String[] getKeywords() {
            return caps==null?null:caps.getService()==null?null:caps.getService().getKeywordList();
        }
        /*
         * @see net.refractions.udig.catalog.IServiceInfo#getSchema()
         */
        public URI getSchema() {
            return WFSSchema.NAMESPACE;
        }
        public String getDescription() {
            return getIdentifier().toString();
        }

        public URL getSource() {
            return getIdentifier();
        }

        public String getTitle() {
            return (caps==null || caps.getService()==null)?
                    (getIdentifier()==null?Messages.WFSServiceImpl_broken:getIdentifier().toString())
                    :caps.getService().getTitle();
        }
    }
}
