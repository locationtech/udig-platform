/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.internal.wmt;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.internal.wmt.wmtsource.NASASource;
import net.refractions.udig.catalog.internal.wmt.wmtsource.NASASourceManager;
import net.refractions.udig.catalog.internal.wmt.wmtsource.WMTSourceFactory;
import net.refractions.udig.core.internal.CorePlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Web Map Tile Server support.
 * 
 * @author to.srwn
 * @since 1.1.0
 */
public class WMTService extends IService {
    public static String ID = "wmt://localhost/wmt/"; //$NON-NLS-1$
    public static String KEY_PROPERTY_ZOOM_LEVEL_SELECTION_AUTOMATIC = "PROPERTY_ZOOM_LEVEL_SELECTION_AUTOMATIC"; //$NON-NLS-1$
    public static String KEY_PROPERTY_ZOOM_LEVEL_VALUE = "PROPERTY_ZOOM_LEVEL_VALUE"; //$NON-NLS-1$
        
    /** Related geo-resources * */
    private volatile List<IGeoResource> members;
    
    private Map<String, Serializable> params;
    private URL url;
    
    Exception message = null;

    public WMTService(Map<String, Serializable> params) {
        this.params = params;
        
        if (params != null && params.containsKey(WMTServiceExtension.KEY))
        {     
            if (params.get(WMTServiceExtension.KEY) instanceof URL) {
                this.url = (URL) params.get(WMTServiceExtension.KEY);
            } else {
                try {
                    this.url = new URL(null, (String) params.get(WMTServiceExtension.KEY), CorePlugin.RELAXED_HANDLER);
                }
                catch(MalformedURLException exc) {
                    WMTPlugin.log("[WMTService] Could not create url: " + params.get(WMTServiceExtension.KEY) , exc); //$NON-NLS-1$
                    this.url = null;
                } 
            }
        }
    }
    
    /**
     * Returns the WMTSouce name of the first WMTGeoResource
     *
     * @return
     */
    public String getName() {
        try{
            List<IGeoResource> resources = resources(null);
            
            if (resources.size() > 0) {
                WMTGeoResource wmtResource = (WMTGeoResource) resources.get(0);
                
                return wmtResource.getSource().getName();
            }
        } catch(Exception exc) {}
        
        return null;
    }
    
    /*
     * @see net.refractions.udig.catalog.IService#resolve(java.lang.Class,
     *      org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {        
        if (monitor == null)
            monitor = new NullProgressMonitor();

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified" ); //$NON-NLS-1$
        }        
        return super.resolve(adaptee, monitor);
    }
    
    @Override
	protected
    synchronized IServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        if (info == null){
            synchronized (this) {
                if (info == null){
                    info = new WMTServiceInfo(this, monitor);
                }
            }
        }
        return info;
    }
    /*
     * @see net.refractions.udig.catalog.IService#members(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public List<IGeoResource> resources(IProgressMonitor monitor) throws IOException {
        if (members == null) {
            synchronized (this) {
                if (members == null) {
                    try {
                        if (WMTSourceFactory.getClassFromUrl(
                                getIdentifier()).equals(NASASource.class.getCanonicalName())) {
                            members = new LinkedList<IGeoResource>();
                            
                            NASASourceManager sourceManager = NASASourceManager.getInstance();
                            sourceManager.buildGeoResources(this, members);
                        } else {                     
                            return Collections.singletonList(
                                    (IGeoResource) new WMTGeoResource(this, WMTGeoResource.DEFAULT_ID));
                        }
                    }
                    catch (Exception exc){
                        message = exc; // could not "connect"
                    }
                }
            }
        }
        
        return members;
    }
    
    public List<IGeoResource> emptyResourcesList(IProgressMonitor monitor) throws IOException {
        members = new LinkedList<IGeoResource>();
        
        return members;
    }
    
    /*
     * @see net.refractions.udig.catalog.IService#getConnectionParams()
     */
    @Override
    public Map<String, Serializable> getConnectionParams() {
        return params;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    public <T> boolean canResolve( Class<T> adaptee ) {
        return super.canResolve(adaptee);
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getStatus()
     */
    public Status getStatus() {
        if( members == null ){
            return super.getStatus();
        }
        return Status.CONNECTED;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getMessage()
     */
    public Throwable getMessage() {
        return message;
    }

    /*
     * @see net.refractions.udig.catalog.IResolve#getIdentifier()
     */
    public URL getIdentifier() {
        return url;
    }

    @Override
    public void dispose(IProgressMonitor monitor) {
        super.dispose(monitor);
        if( members != null ){
            members = null;
        }
    }
}