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
