package net.refractions.udig.catalog.geotools.data;

import java.io.Serializable;
import java.util.Map;

import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.ui.CatalogUIPlugin;
import net.refractions.udig.catalog.ui.ISharedImages;

import org.geotools.data.DataAccessFactory;
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

    private ServiceInfo info;
    private DataAccessFactory factory;
    private GTFormat format;

    public DataStoreServiceInfo( DataAccessFactory factory, Map<String, Serializable> params, ServiceInfo gtInfo ) {
        this.factory = factory;
        this.info = gtInfo;
        this.format = GTFormat.format(factory);
        this._abstract = info.getDescription();
        this.description = info.getDescription();
        this.keywords = (info.getKeywords() != null ? info.getKeywords().toArray(new String[0]) : new String[0]);
        this.publisher = info.getPublisher();
        this.schema = info.getSchema();
        this.source = info.getSource();
        this.title = info.getTitle();
        if( title == null ){
            title = format.getTitle(factory, params);
        }
        this.icon = format.getIcon();
    }
    
    public ServiceInfo toServiceInfo(){
        return info;
    }
    
}
