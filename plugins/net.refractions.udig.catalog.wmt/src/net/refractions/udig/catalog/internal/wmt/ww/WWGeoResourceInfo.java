/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2010, Refractions Research Inc.
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
package net.refractions.udig.catalog.internal.wmt.ww;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResourceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

class WWGeoResourceInfo extends IGeoResourceInfo {
    /** WMSResourceInfo resource field */
    private final WWGeoResource resource;
   
    WWGeoResourceInfo(WWGeoResource geoResourceImpl, IProgressMonitor monitor) throws IOException {
        this.resource = geoResourceImpl;
        
        this.title = this.resource.getTitle();        
        this.bounds = this.resource.getSource().getBounds();                  
    }
    
    @Override
    public CoordinateReferenceSystem getCRS() {
        return this.resource.getSource().getTileCrs();
    }
}