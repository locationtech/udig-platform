/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wmsc;

import java.io.IOException;

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
//        this.title = service.getIdentifier().toString();
        Service capservice;
        try {
            capservice = service.getWMSC().getCapabilities().getService();
        } catch (IOException e) {
            capservice = null; // no connection no info?
        }
        if (capservice != null){
            this.title = capservice.getName();
            this._abstract = capservice.get_abstract();
            this.description = capservice.getTitle();
            this.keywords = capservice.getKeywordList();
        }
    }

}
