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

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.geotiff.internal.Messages;
import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResource;
import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResourceInfo;
import org.locationtech.udig.catalog.rasterings.AbstractRasterService;

/**
 * Provides a handle to a GeoTiff resource allowing the service to be lazily loaded.
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

    @Override
    protected AbstractRasterGeoResourceInfo createInfo(IProgressMonitor monitor)
            throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();

        monitor.beginTask(Messages.GeoTiffGeoResource_connect, 2);
        try {
            monitor.worked(1);
            return new AbstractRasterGeoResourceInfo(this, "GeoTiff", ".tif", ".tiff"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        } finally {
            monitor.done();
        }
    }

    @Override
    public GeoTiffServiceImpl service(IProgressMonitor monitor) throws IOException {
        IService serv = super.service(monitor);
        return (serv != null && serv instanceof GeoTiffServiceImpl) ? (GeoTiffServiceImpl) serv
                : null;
    }
}
