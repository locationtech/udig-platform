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
package net.refractions.udig.catalog.wmsc.server;

import org.geotools.data.ows.Capabilities;
import org.geotools.data.ows.Service;

/**
 * A class to represent the WMSC Capabilities document.
 * <p>
 * http://wiki.osgeo.org/wiki/WMS_Tiling_Client_Recommendation#GetCapabilities_Responses
 * </p>
 * 
 * @author Emily Gouge (Refractions Research, Inc)
 * @since 1.1.0
 */
public class WMSCCapabilities extends Capabilities {

    private Capability vs = null;
    private Service service = null;

    public void setCapabilitiy( Capability vs ) {
        this.vs = vs;
    }

    public Capability getCapability() {
        return this.vs;
    }
    
    public Service getService(){
        return this.service;
    }
    public void setService(Service s){
        this.service = s;
    }
}
