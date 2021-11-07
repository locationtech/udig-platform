/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.wmt.ww;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IResolve;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.IServiceInfo;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.ww.LayerSet;
import org.locationtech.udig.catalog.internal.wmt.wmtsource.ww.QuadTileSet;
import org.locationtech.udig.catalog.wmt.internal.Messages;
import org.locationtech.udig.ui.ErrorManager;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;


/**
 * Based on WMSServiceImpl this class represents a 
 * NASA WorldWind configuration file in the catalog.
 * 
 * @see org.locationtech.udig.catalog.internal.wmt.wmtsource.ww.LayerSet
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WWService extends IService {

    public static final String WW_URL_KEY = "org.locationtech.udig.catalog.internal.wmt.WWService.WW_URL_KEY"; //$NON-NLS-1$
    public static final String WW_LAYERSET_KEY = "org.locationtech.udig.catalog.internal.wmt.WWService.WW_LAYERSET_KEY"; //$NON-NLS-1$

    private Map<String, Serializable> params;

    private Throwable error;
    private URL url;

    private volatile LayerSet layerSet = null;
    private volatile List<IResolve> members;
    
    protected final Lock rLock=new UDIGDisplaySafeLock();
    private static final Lock dsLock = new UDIGDisplaySafeLock();

    public WWService(URL url, Map<String, Serializable> params) {
        this.params = params;
        this.url = url;

        if (params.containsKey(WW_LAYERSET_KEY)) {
            Object obj = params.get(WW_LAYERSET_KEY);

            if (obj instanceof LayerSet) {
                this.layerSet = (LayerSet) obj;
            }
        }
    }
    
    public Status getStatus() {
        if( layerSet == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    /**
     * Aquire the actual LayerSet instance (load the file).
     * <p>
     * Note this method is blocking and throws an IOException to indicate such.
     * </p>
     * @param theUserIsWatching 
     * @return LayerSet instance
     * @throws IOException 
     */
    protected LayerSet getLayerSet(IProgressMonitor theUserIsWatching) throws IOException{
        if (layerSet == null) {
            dsLock.lock();
            try{
                if (layerSet == null) {
                    try {
                        if( theUserIsWatching != null ) {
                        	String message = MessageFormat.format(Messages.WWService_Connecting_to, new Object[] { url }); 
                            theUserIsWatching.beginTask(message, 100 );
                        }
                        URL url1 = (URL) getConnectionParams().get(WW_URL_KEY);
                        if( theUserIsWatching != null )
                            theUserIsWatching.worked( 5 );  
                        
                        layerSet = LayerSet.getFromUrl(url1); 
                        
                        if( theUserIsWatching != null )
                            theUserIsWatching.done();
                    }
                    catch(Exception exc){                      
                        IOException broken = new IOException( 
                                MessageFormat.format(Messages.WWService_Could_not_connect, 
                                new Object[] { exc.getLocalizedMessage() }));
                        broken.initCause( exc );
                        error = broken;                
                        throw broken;                
                    }
                }
            }finally{
                dsLock.unlock();
            }
        }
        return layerSet;
    }

    protected IServiceInfo createInfo(IProgressMonitor monitor) throws IOException {
        if (info == null){
            getLayerSet( monitor );
            rLock.lock();
            try{
                if(info == null){
                	info = new WWServiceInfo(this, monitor);
                }
            }finally{
                rLock.unlock();
            }
        }
        return info;
    }

    /*
     * @see org.locationtech.udig.catalog.IService#resolve(java.lang.Class, org.eclipse.core.runtime.IProgressMonitor)
     */
    public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor) throws IOException {
        if (adaptee == null) {
            return null;
        }
        
        if (adaptee.isAssignableFrom(IServiceInfo.class)) {
            return adaptee.cast(createInfo(monitor));
        }
        
        if (adaptee.isAssignableFrom(List.class)) {
            return adaptee.cast(members(monitor));
        }
        
        if (adaptee.isAssignableFrom(LayerSet.class)) {
            return adaptee.cast(getLayerSet(monitor));
        }
        
        return super.resolve(adaptee, monitor);
    }

    /**
     * @see org.locationtech.udig.catalog.IService#getConnectionParams()
     */
    public Map<String,Serializable> getConnectionParams() {
        return params;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve(Class<T> adaptee) {
        if (adaptee == null)
            return false;

        return adaptee.isAssignableFrom(LayerSet.class) || super.canResolve(adaptee);
    }
    public void dispose( IProgressMonitor monitor ) {
        super.dispose(monitor);
        if( members != null ){
            members = null;
        }
        if( layerSet != null ){
            layerSet.dispose(); // clean up
            layerSet = null;
        }
    }
    
    public List<WWGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        // seed the potentially null field
        members(monitor);
        List<WWGeoResource> children = new ArrayList<WWGeoResource>();
        collectChildren(this, children);
        
        return children;
    }
    
    private void collectChildren( IResolve resolve, List<WWGeoResource> children )
            throws IOException {
        List<IResolve> resolves = resolve.members(new NullProgressMonitor());

        if (resolves.isEmpty() && resolve instanceof WWGeoResource) {
            children.add((WWGeoResource) resolve);
        } else {
            for( IResolve resolve2 : resolves ) {
                collectChildren(resolve2, children);
            }
        }
    }

    public List<IResolve> members(IProgressMonitor monitor) throws IOException {

        if(members == null){
            getLayerSet(monitor);
            rLock.lock();
            try{
                if(members == null){
                    getLayerSet(monitor); // load ds
                    members = new LinkedList<IResolve>();
                    
                    // add QuadTileSets
                    List<QuadTileSet> quadTileSets = getLayerSet(monitor).getQuadTileSets();
                    for (QuadTileSet quadTileSet : quadTileSets) {
                        members.add(new WWGeoResource(this, this, quadTileSet));
                    }
                    
                    // add LayerSets
                    List<LayerSet> layerSets = getLayerSet(monitor).getChildLayerSets();
                    for (LayerSet layerSet : layerSets) {
                        members.add(new WWFolder(this, this, layerSet));
                    }
                }
            }finally{
                rLock.unlock();
            }
        }
        return members;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return error;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return url;
    }
    
}
