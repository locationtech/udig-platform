package net.refractions.udig.catalog.rasterings;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IServiceInfo;

public class AbstractRasterServiceInfo extends IServiceInfo {

    private final AbstractRasterService service;

    public AbstractRasterServiceInfo( AbstractRasterService service, String... keywords ) {
        this.service = service;
        super.keywords = keywords; 
    }
    
    @Override
    public String getTitle() {
        ID id = service.getID();
        
        String title;
        if( id.isFile() ){
            title = id.toFile().getAbsolutePath();            
        }
        else {
            title = id.toString();
        }
        return title;
    }

    @Override
    public String getShortTitle() {
        return service.getID().toFile().getName();
    }
    
    @Override
    public String getDescription() {
        return service.getIdentifier().toString();
    }
    
}
