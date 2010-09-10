
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