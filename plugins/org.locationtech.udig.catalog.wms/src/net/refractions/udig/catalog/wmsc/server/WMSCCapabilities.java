/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.wmsc.server;

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
