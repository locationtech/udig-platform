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
