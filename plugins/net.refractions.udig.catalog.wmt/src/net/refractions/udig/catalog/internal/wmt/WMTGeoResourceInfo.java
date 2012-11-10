/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2008, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.catalog.internal.wmt;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResourceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;


class WMTGeoResourceInfo extends IGeoResourceInfo {
    /** WMTResourceInfo resource field */
    private final WMTGeoResource resource;
    
    WMTGeoResourceInfo(WMTGeoResource resource, IProgressMonitor monitor) throws IOException {
        this.resource = resource;
        
        this.title = this.resource.getTitle();        
        this.bounds = this.resource.getSource().getBounds();
                  
    }
    @Override
    public CoordinateReferenceSystem getCRS() {
        return this.resource.getSource().getTileCrs();
    }
    
    
   
}