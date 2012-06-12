/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.catalog.geotools.data;

import java.io.Serializable;
import java.util.Map;

import net.refractions.udig.catalog.IServiceInfo;

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
        /* 
         * This is a horrible hack to handle null namespaces in Name
         * If the namespace NPE's, we can just leave schema as it is.
         */
        try {
            this.schema = info.getSchema();
        } catch(NullPointerException ex) {
            ;
        }
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
