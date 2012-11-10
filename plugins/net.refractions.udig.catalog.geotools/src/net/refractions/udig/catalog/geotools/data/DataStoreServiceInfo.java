/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
