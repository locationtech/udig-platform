/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.internal.wmsc;

import net.refractions.udig.catalog.IServiceInfo;

import org.geotools.data.ows.Service;

/**
 * 
 * Service information for a WMS-C Service
 * @author Emily Gouge
 * @since 1.1.0
 */
public class WMSCServieInfo extends IServiceInfo {
    
    public WMSCServieInfo(WMSCServiceImpl service ){
        Service capservice;
        capservice = service.getWMSC().getCapabilities().getService();
        
        if (capservice != null){
            this.title = capservice.getName();
            this._abstract = capservice.get_abstract();
            this.description = capservice.getTitle();
            this.keywords = capservice.getKeywordList();
        }
    }

}
