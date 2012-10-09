/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
