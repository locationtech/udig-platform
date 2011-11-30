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
package net.refractions.udig.internal.aoi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.refractions.udig.aoi.AOIListener;
import net.refractions.udig.aoi.AOIProxy;
import net.refractions.udig.aoi.IAOIService;
import net.refractions.udig.core.internal.ExtensionPointProcessor;
import net.refractions.udig.core.internal.ExtensionPointUtil;
import net.refractions.udig.internal.ui.UiPlugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This is the default implementation of AOIService; it delegates to the internal strategy
 * object.
 * 
 * @author pfeiffp
 */
public class AOIServiceImpl implements IAOIService {

    /** This is the AOI extension point processed to get AOIStrategy entries */
    private static final String EXT_ID = "net.refractions.udig.ui.aoi";

    /**
     * the id of the all strategy (ie the default)
     */
    public static final String STRATEGY_ALL_ID = "net.refractions.udig.ui.aoiAll";

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
