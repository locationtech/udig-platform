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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IServiceInfo;
import net.refractions.udig.catalog.geotiff.internal.Messages;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterService;
import net.refractions.udig.catalog.rasterings.AbstractRasterServiceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;
import org.geotools.gce.geotiff.GeoTiffFormat;

/**
 * Provides a handle to a geotiff service allowing the service to be lazily 
 * loaded.
 * @author mleslie
 * @since 0.6.0
 */
public class GeoTiffServiceImpl extends AbstractRasterService {
    /**
     * Construct <code>GeoTiffServiceImpl</code>.
     *
     * @param id
     * @param factory
     */
    public GeoTiffServiceImpl(URL id, GridFormatFactorySpi factory) {
        super(id, GeoTiffServiceExtension.TYPE, factory);
    }

    @Override
    public synchronized  List<AbstractRasterGeoResource> resources( IProgressMonitor monitor ) 
            throws IOException {
         if(monitor != null) {
            String msg = MessageFormat.format(
                    Messages.GeoTiffServiceImpl_connecting_to, 
                    new Object[] {});
            monitor.beginTask(msg, 5);
        }
        if(reader != null && monitor != null)
            monitor.worked(3);
        
        GeoTiffGeoResourceImpl res = new GeoTiffGeoResourceImpl(
                this, getHandle());
        List<AbstractRasterGeoResource> list = 
            new ArrayList<AbstractRasterGeoResource>();
        list.add(res);
        if(monitor != null)
            monitor.done();
        return list;
    } 
    
    @Override
    public Map<String, Serializable> getConnectionParams() {
        return new GeoTiffServiceExtension().createParams(getIdentifier());
    }

    public synchronized AbstractGridCoverage2DReader getReader() {
        if (this.reader == null) {
            try {
                File file = new File(getIdentifier().toURI());
                GeoTiffFormat geoTiffFormat = (GeoTiffFormat) getFormat();
                this.reader = (AbstractGridCoverage2DReader) geoTiffFormat.getReader(file);
            } catch (Exception ex) {
                this.message = ex;
            }
        }
        return this.reader;
    }

    protected synchronized AbstractRasterServiceInfo createInfo(IProgressMonitor monitor) {
         if(monitor == null) monitor = new NullProgressMonitor();
         try {
             monitor.beginTask(Messages.GeoTiffServiceImpl_loading_task_title, 2); 
             monitor.worked(1);
             return new AbstractRasterServiceInfo(this, "geotiff", "tiff", "tif");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
         }
         finally {
             monitor.done();
         }
    }
}
