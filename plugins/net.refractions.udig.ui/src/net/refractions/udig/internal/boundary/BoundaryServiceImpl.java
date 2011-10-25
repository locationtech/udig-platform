/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
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
package net.refractions.udig.internal.boundary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.boundary.BoundaryListener;
import net.refractions.udig.boundary.BoundaryProxy;
import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This is the default implementation of BoundaryService; it delegates to the internal strategy
 * object.
 * 
 * @author pfeiffp
 */
public class BoundaryServiceImpl implements IBoundaryService {
    
    /** This is the boundary extension point processed to get BoundaryStrategy entries */
    private static final String EXT_ID = "net.refractions.udig.ui.boundary";

    /**
     * the id of the all strategy (ie the default)
     */
    public static final String STRATEGY_ALL_ID = "net.refractions.udig.ui.boundaryAll";
    
    /*
     * A list of all the strategies
     */
    protected List<BoundaryProxy> proxyList = new ArrayList<BoundaryProxy>();

    protected BoundaryListener watcher = new BoundaryListener(){
        public void handleEvent( BoundaryListener.Event event ) {
            notifyListeners(event);
        }
    };
    /*
     * A list of listeners to be notified when the Strategy changes
     */
    protected Set<BoundaryListener> listeners = new CopyOnWriteArraySet<BoundaryListener>();
    
    @Override
    public void addListener( BoundaryListener listener ) {
        if( listener == null ){
            throw new NullPointerException("BoundaryService listener required to be non null");
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener( BoundaryListener listener ) {
        listeners.add(listener);
    }
    
    /**
     * Notifies listener that the value of the filter has changed.
     */
    private void notifyListeners(BoundaryListener.Event event) {
        for( BoundaryListener listener : listeners ) {
            if( event == null ){
                event = new BoundaryListener.Event(getProxy());
            }
            try {
                if( listener != null ){
                    listener.handleEvent( event );
                }
            } catch (Exception e) {
                UiPlugin.trace(UiPlugin.ID, listener.getClass(), e.getMessage(), e );
            }
        }
    }
    
    private BoundaryProxy currentProxy;

    public BoundaryServiceImpl() {
        
        // process the extension point here to get the list of Strategies
        ExtensionPointProcessor processBoundaryItems = new ExtensionPointProcessor(){
            @Override
            public void process( IExtension extension, IConfigurationElement element ) throws Exception {
               BoundaryProxy proxy = new BoundaryProxy(element);
               proxyList.add(proxy);
               /*String className = element.getAttribute("class");
               if( currentClassName != null && currentClassName.equals( className )){
                   initialStrategy = strategy;
               }*/
            }
        };
        ExtensionPointUtil.process( UiPlugin.getDefault(), EXT_ID,  processBoundaryItems );

        this.setProxy(this.getDefault());
    }

    @Override
    public ReferencedEnvelope getExtent() {
        return this.currentProxy.getExtent();
    }

    @Override
    public void setProxy( BoundaryProxy proxy ) {
        if( this.currentProxy == proxy ){
            return; // no change
        }
        if( this.currentProxy != null ){
            this.currentProxy.removeListener(watcher);
        }
        this.currentProxy = proxy;
        if( this.currentProxy != null ){
            this.currentProxy.addListener(watcher);
        }
        BoundaryListener.Event event = new BoundaryListener.Event(proxy);
        // we are not filling in event.geometry here as we only changed strategy
        notifyListeners(event);
    }

    @Override
    public Geometry getGeometry() {
        return this.currentProxy.getGeometry();
    }

    @Override
    public CoordinateReferenceSystem getCrs() {
        return this.currentProxy.getCrs();
    }

    @Override
    public BoundaryProxy getProxy() {
        return this.currentProxy;
    }
    
    @Override
    public BoundaryProxy getDefault() {
        return this.findProxy(STRATEGY_ALL_ID);
    }

    @Override
    public List<BoundaryProxy> getProxyList() {
        return Collections.unmodifiableList( proxyList );
    }

    @Override
    public BoundaryProxy findProxy( String id ) {
        for (BoundaryProxy boundaryProxy: proxyList) {
            if (boundaryProxy.getId().equals(id)) {
                return boundaryProxy;
            }
        }
        return null;
    }
}
