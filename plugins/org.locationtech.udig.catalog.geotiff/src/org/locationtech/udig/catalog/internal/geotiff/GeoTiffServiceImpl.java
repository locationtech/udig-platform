/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.internal.geotiff;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.locationtech.udig.catalog.geotiff.internal.Messages;
import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResource;
import org.locationtech.udig.catalog.rasterings.AbstractRasterService;
import org.locationtech.udig.catalog.rasterings.AbstractRasterServiceInfo;

/**
 * Provides a handle to a GeoTiff service allowing the service to be lazily loaded.
 *
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
    public synchronized List<AbstractRasterGeoResource> resources(IProgressMonitor monitor)
            throws IOException {
        if (monitor != null) {
            String msg = MessageFormat.format(Messages.GeoTiffServiceImpl_connecting_to,
                    new Object[] {});
            monitor.beginTask(msg, 5);
        }
        if (reader != null && monitor != null)
            monitor.worked(3);

        GeoTiffGeoResourceImpl res = new GeoTiffGeoResourceImpl(this, getHandle());
        List<AbstractRasterGeoResource> list = new ArrayList<>();
        list.add(res);
        if (monitor != null)
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
                this.reader = geoTiffFormat.getReader(file);
            } catch (Exception ex) {
                this.message = ex;
            }
        }
        return this.reader;
    }

    @Override
    protected synchronized AbstractRasterServiceInfo createInfo(IProgressMonitor monitor) {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        try {
            monitor.beginTask(Messages.GeoTiffServiceImpl_loading_task_title, 2);
            monitor.worked(1);
            return new AbstractRasterServiceInfo(this, "geotiff", "tiff", "tif"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        } finally {
            monitor.done();
        }
    }
}
