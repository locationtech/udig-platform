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
package net.refractions.udig.tutorials.catalog.csv;

import java.util.Date;

import net.refractions.udig.catalog.IServiceInfo;

public class CSVServiceInfo extends IServiceInfo {
    CSVService handle;
    public CSVServiceInfo( CSVService service ) {
        this.handle = service;
        this.title = handle.getIdentifier().toString();
        this.description = "Property File Service (" + this.title + ")";
        this.keywords = new String[]{ "CSV", "File" };
    }
    Date getTimestamp(){
        return new Date( handle.getFile().lastModified() );
    }
}
