/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.internal.aoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.locationtech.udig.aoi.AOIListener;
import org.locationtech.udig.aoi.AOIProxy;
import org.locationtech.udig.aoi.IAOIService;
import org.locationtech.udig.core.internal.ExtensionPointProcessor;
import org.locationtech.udig.core.internal.ExtensionPointUtil;
import org.locationtech.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Geometry;

/**
 * This is the default implementation of AOIService; it delegates to the internal strategy
 * object.
 * 
 * @author pfeiffp
 */
public class AOIServiceImpl implements IAOIService {

    /** This is the AOI extension point processed to get AOIStrategy entries */
    private static final String EXT_ID = "org.locationtech.udig.ui.aoi";

    /**
     * the id of the all strategy (ie the default)
     */
    public static final String STRATEGY_ALL_ID = "org.locationtech.udig.ui.aoiAll";

    /*
     * A list of all the strategies
     */
    protected List<AOIProxy> proxyList = new ArrayList<AOIProxy>();

    protected AOIListener watcher = new AOIListener(){
        public void handleEvent( AOIListener.Event event ) {
            notifyListeners(event);
        }
    };
    /*
     * A list of listeners to be notified when the Strategy changes
     */
    protected Set<AOIListener> listeners = new CopyOnWriteArraySet<AOIListener>();

    @Override
    public void addListener( AOIListener listener ) {
        if (listener == null) {
            throw new NullPointerException("AOIService listener required to be non null");
        }
        listeners.add(listener);
    }

    @Override
    public void removeListener( AOIListener listener ) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    /*
     * Notifies listener that the value of the filter has changed.
     */
    private void notifyListeners( AOIListener.Event event ) {
        if (event == null) {
            event = new AOIListener.Event(getProxy());
        }
        for( AOIListener listener : listeners ) {
            try {
                if (listener != null) {
                    listener.handleEvent(event);
                }
            } catch (Exception e) {
                UiPlugin.log(getClass(), "notifyListeners", e);
            }
        }
    }

    private AOIProxy currentProxy;

    public AOIServiceImpl() {

        // process the extension point here to get the list of Strategies
        ExtensionPointProcessor processAOIItems = new ExtensionPointProcessor(){
            @Override
            public void process( IExtension extension, IConfigurationElement element )
                    throws Exception {
                AOIProxy proxy = new AOIProxy(element);
                proxyList.add(proxy);
                /*
                 * String className = element.getAttribute("class"); if( currentClassName != null &&
                 * currentClassName.equals( className )){ initialStrategy = strategy; }
                 */
            }
        };
        ExtensionPointUtil.process(UiPlugin.getDefault(), EXT_ID, processAOIItems);

        this.setProxy(this.getDefault());
    }

    @Override
    public ReferencedEnvelope getExtent() {
        return this.currentProxy.getExtent();
    }

    @Override
    public void setProxy( AOIProxy proxy ) {
        if (this.currentProxy == proxy) {
            return; // no change
        }
        if (this.currentProxy != null) {
            this.currentProxy.removeListener(watcher);
        }
        this.currentProxy = proxy;
        if (this.currentProxy != null) {
            this.currentProxy.addListener(watcher);
        }
        AOIListener.Event event = new AOIListener.Event(proxy);
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
    public AOIProxy getProxy() {
        return this.currentProxy;
    }

    @Override
    public AOIProxy getDefault() {
        return this.findProxy(STRATEGY_ALL_ID);
    }

    @Override
    public List<AOIProxy> getProxyList() {
        return Collections.unmodifiableList(proxyList);
    }

    @Override
    public AOIProxy findProxy( String id ) {
        for( AOIProxy aOIProxy : proxyList ) {
            if (aOIProxy.getId().equals(id)) {
                return aOIProxy;
            }
        }
        return null;
    }
}
