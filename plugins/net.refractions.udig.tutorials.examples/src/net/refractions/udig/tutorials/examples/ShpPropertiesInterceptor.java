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
