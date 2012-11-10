/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package net.refractions.udig.style.cache;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.project.StyleContent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.geotools.styling.Style;

/**
 * Used to indicate if we are caching content (in memory or otherwise).
 * <p>
 * To start out with we will simply have a boolean flag; we may
 * wish to provide a threshold of memory use.
 */
public final class CacheContent extends StyleContent {

    /** style id, used to identify cache style on a blackboard */
    public static final String ID = "net.refractions.udig.style.cache"; //$NON-NLS-1$

    /**
     * SLDContent constructor.
     */
    public CacheContent() {
        super(ID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.project.StyleContent#getStyleClass()
     */
    public Class<?> getStyleClass() {
        return Boolean.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.project.StyleContent#save(org.eclipse.ui.IMemento,
     *      java.lang.Object)
     */
    public void save( IMemento memento, Object value ) {
        Boolean style = (Boolean) value;
        
        memento.putBoolean("cache", style );
        memento.putString("type", "CacheStyle"); //$NON-NLS-1$ //$NON-NLS-2$
        memento.putString("version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.project.StyleContent#load(org.eclipse.ui.IMemento)
     */
    public Object load( IMemento momento ) {
        Boolean style = momento.getBoolean("cache");
        
        return style;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.project.StyleContent#load(java.net.URL)
     */
    public Object load( URL url, IProgressMonitor m ) throws IOException {
        return null;
    }
    
    /**
     * This will need to know the "scheme."
     */
    public Object createDefaultStyle( IGeoResource resource, Color colour, 
            IProgressMonitor m ) throws IOException {
        
        if( resource.canResolve(Boolean.class)){
            Boolean isCaching = resource.resolve( Boolean.class, m);
            if( isCaching != null ){
                return isCaching;
            }
        }
        IService service = resource.service(null);
        if( service == null ){
            return null; // cannot determine sevice at this time!
        }
        ID serviceID = service.getID();        
//        if( serviceID.isWFS() ){
//            return true; // we want to cache for WFS
//        }
        if( serviceID.isFile() ){
            String ext = serviceID.toExtension();
            if( ext.toLowerCase().endsWith("jpg") ||
                    ext.toLowerCase().endsWith("jpeg")){
                return true; // we want to cache JPEG
            }
        }
        return null; // there is no good Style default for this resource type
    }    

    
}