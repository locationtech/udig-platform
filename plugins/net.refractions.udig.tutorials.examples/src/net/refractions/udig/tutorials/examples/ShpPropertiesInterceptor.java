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
package net.refractions.udig.tutorials.examples;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.interceptor.ServiceInterceptor;
import net.refractions.udig.catalog.internal.shp.ShpServiceImpl;

public class ShpPropertiesInterceptor implements ServiceInterceptor {

    public void run(IService service){
        if( service instanceof ShpServiceImpl){
            ID id = service.getID();
            File directory = id.toFile().getParentFile();
            File infoFile = new File( directory, id.toBaseFile()+".properties" );
            if( infoFile.exists() ){
               try { 
                   FileReader infoReader = new FileReader( infoFile );
                 Properties info = new Properties();
                 info.load( infoReader );
                 String title = (String) info.get("title");
                 if( title != null ){
                   service.getPersistentProperties().put("title", title);
                 }
               } catch (IOException eek ){
               }
            }
        }
     }

}
