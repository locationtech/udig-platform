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

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.coverage.grid.GridFormatFactorySpi;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.opengis.coverage.grid.GridCoverageReader;

/**
 * Provides a handle to a geotiff service allowing the service to be lazily
 * loaded.
 * @author mleslie
 * @since 0.6.0
 */
public class GeoTiffServiceImpl extends AbstractRasterService {
    private GeoTiffServiceInfo info;

    /**
     * Construct <code>GeoTiffServiceImpl</code>.
     *
     * @param id
     * @param factory
     */
    public GeoTiffServiceImpl(URL id, GridFormatFactorySpi factory) {
        super(id, factory);
    }

    @Override
    public List<AbstractRasterGeoResource> resources( IProgressMonitor monitor )
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
                this, getTitle());
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

    public void dispose( IProgressMonitor monitor ) {
        // do nothing
    }
    public GridCoverageReader getReader() {
        if(this.reader == null) {
            try {
                File file = new File(getIdentifier().toURI());
                this.reader = ((GeoTiffFormat)getFormat()).getReader(file);
            } catch(Exception ex) {
                this.message = ex;
            }
        }
        return this.reader;
    }

    public IServiceInfo getInfo(IProgressMonitor monitor) {
         if(monitor != null)
            monitor.beginTask(Messages.GeoTiffServiceImpl_loading_task_title, 2);
        if(this.info == null) {
            if(monitor != null)
                monitor.worked(1);
            this.info = new GeoTiffServiceInfo();
        }
        if(monitor != null)
            monitor.done();
        return this.info;
    }
    /**
     * Provides descriptive information about this service.
     * @author mleslie
     * @since 0.6.0
     */
    public class GeoTiffServiceInfo extends IServiceInfo {
        GeoTiffServiceInfo() {
            super();
            this.keywords = new String[] {
                    "WorldImage", "world image", ".gif", ".jpg", ".jpeg",   //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$
                    ".tif", ".tiff", ".png"};   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }

        public String getTitle() {
            return getIdentifier().getFile();
        }

        public String getDescription() {
            return getIdentifier().toString();
        }
    }
}
