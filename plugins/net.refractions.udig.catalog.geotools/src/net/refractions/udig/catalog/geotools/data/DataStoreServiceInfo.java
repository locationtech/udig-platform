package net.refractions.udig.catalog.geotools.data;

import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;

import org.geotools.data.ServiceInfo;

/**
 * Simple Info object delegating to the provided gtInfo.
 * <p>
 * This implementation is directly dependent on the quality of the
 * GeoTools DataStore you are using. We will need to make it more
 * defensive over time to avoid any troubles.
 * </p>
 * 
 * @author Jody Garnett
 * @since 1.2.0
 */
public class DataStoreServiceInfo extends IServiceInfo {

    //private DataStoreService service;
    private ServiceInfo info;

    public DataStoreServiceInfo( ServiceInfo gtInfo ) {
        //this.service = dataStoreService;
        this.info = gtInfo;
        this._abstract = info.getDescription();
        this.description = info.getDescription();
        this.keywords = (info.getKeywords() != null ? info.getKeywords().toArray(new String[0]) : new String[0]);
        this.publisher = info.getPublisher();
        this.schema = info.getSchema();
        this.source = info.getSource();
        this.title = info.getTitle();

        ISharedImages images = CatalogUIPlugin.getDefault().getImages();
        this.icon = images.getImageDescriptor( ISharedImages.DATASTORE_OBJ ); // generic!
    }
    
    public ServiceInfo toServiceInfo(){
        return info;
    }
    
}
