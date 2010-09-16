/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.catalog.internal.geotiff;

import java.io.IOException;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.geotiff.internal.Messages;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResourceInfo;
import net.refractions.udig.catalog.rasterings.AbstractRasterService;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;


/**
 * Provides a handle to a geotiff resource allowing the service to be lazily 
 * loaded.
 * 
 * @author mleslie
 * @since 0.6.0
 */
public class GeoTiffGeoResourceImpl extends AbstractRasterGeoResource {
    /**
     * Construct <code>GeoTiffGeoResourceImpl</code>.
     */
    public GeoTiffGeoResourceImpl(AbstractRasterService service, String name) {
        super(service, name);
    }
    
    protected AbstractRasterGeoResourceInfo createInfo(IProgressMonitor monitor) throws IOException {
        if( monitor == null ) monitor = new NullProgressMonitor();
        
        monitor.beginTask(Messages.GeoTiffGeoResource_connect, 2); 
        try {
            monitor.worked(1);  
            return new AbstractRasterGeoResourceInfo(this, "GeoTiff", ".tif", ".tiff");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                    }
        finally {
            monitor.done();
        }
    }
    
    @Override
    public GeoTiffServiceImpl service(IProgressMonitor monitor) throws IOException {
    	IService serv = super.service(monitor);
    	return (serv != null && serv instanceof GeoTiffServiceImpl) 
    			? (GeoTiffServiceImpl) serv : null;
    }
}
