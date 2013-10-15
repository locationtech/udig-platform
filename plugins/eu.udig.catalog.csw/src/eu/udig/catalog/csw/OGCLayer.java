/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package eu.udig.catalog.csw;

import java.net.URL;

/**
 * A description of an open web service layer.
 * @author Jessie Eichar
 */
public class OGCLayer {
    protected java.lang.String name;
    protected java.lang.String title;
    protected String description;
    protected URL id;   
    protected URL onlineresource;
    protected java.lang.String servertype;
    
    public OGCLayer() {
        // no op
    }
    
    public OGCLayer(java.lang.String name, 
            java.lang.String title, 
            java.lang.String description, 
            URL onlineresource, 
            java.lang.String servertype, 
            URL id) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.onlineresource = onlineresource;
        this.servertype = servertype;
        this.id = id;
    }
    
    public java.lang.String getName() {
        return name;
    }
    
    public void setName(java.lang.String name) {
        this.name = name;
    }
    
    public java.lang.String getTitle() {
        return title;
    }
    
    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    
    public java.lang.String getDescription() {
        return description;
    }
    
    public void setDescription(java.lang.String description) {
        this.description = description;
    }
    
    public URL getOnlineresource() {
        return onlineresource;
    }
    
    public void setOnlineresource(URL onlineresource) {
        this.onlineresource = onlineresource;
    }
    
    public java.lang.String getServertype() {
        return servertype;
    }
    
    public void setServertype(java.lang.String servertype) {
        this.servertype = servertype;
    }
    
    public URL getId() {
        return id;
    }

    public void setId( URL id ) {
        this.id = id;
    }
    
}
