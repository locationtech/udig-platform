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

public class Capability {

    private VendorSpecificCapabilities vs = null;

    public void setVendorCapabilities( VendorSpecificCapabilities vs ) {
        this.vs = vs;
    }

    public VendorSpecificCapabilities getVSCapabilities() {
        return this.vs;
    }
    
}
