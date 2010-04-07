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
package net.refractions.udig.catalog.google;

import java.net.URL;

/**
 * A description of an open web service layer.
 */
public class OGCLayer {
    protected java.lang.String name;
    protected java.lang.String title;
    protected String description;
    protected URL id;   
    protected URL onlineresource;
    protected java.lang.String servertype;
    protected java.lang.String serverversion;
    
    public OGCLayer() {
        // no op
    }
    
    public OGCLayer(java.lang.String name, 
            java.lang.String title, 
            java.lang.String description, 
            URL onlineresource, 
            java.lang.String servertype, 
            java.lang.String serverversion,
            URL id) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.onlineresource = onlineresource;
        this.servertype = servertype;
        this.serverversion = serverversion;
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
    
    public java.lang.String getServerversion() {
        return serverversion;
    }
    
    public void setServerversion(java.lang.String serverversion) {
        this.serverversion = serverversion;
    }

    public URL getId() {
        return id;
    }

    public void setId( URL id ) {
        this.id = id;
    }
    
}
