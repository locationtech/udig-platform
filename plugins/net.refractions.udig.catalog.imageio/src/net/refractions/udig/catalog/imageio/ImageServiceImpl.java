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
package net.refractions.udig.catalog.imageio;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.refractions.udig.catalog.IResolve;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterService;
import net.refractions.udig.catalog.rasterings.AbstractRasterServiceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.io.GridFormatFactorySpi;

/**
 * Provides a handle to an imageio-ext based image service allowing the service to be lazily loaded.
 * 
 * @author mleslie
 * @author Daniele Romagnoli, GeoSolutions
 * @author Jody Garnett
 * @author Simone Giannecchini, GeoSolutions
 * @author Frank Gasdorf
 * @since 0.6.0
 */
public class ImageServiceImpl extends AbstractRasterService {
    private AbstractRasterGeoResource resource;

    /**
     * Construct <code>ImageServiceImpl</code>.
     * 
     * @param id
     * @param factory
     */
    public ImageServiceImpl( URL id, GridFormatFactorySpi factory ) {
        super(id, ImageServiceExtension.TYPE, factory);
    }

    /**
     * Added to prevent creation of new GeoResource on each call to members
     */
    public synchronized AbstractRasterGeoResource getGeoResource( IProgressMonitor monitor ) {
        if (resource == null) {
            resource = new ImageGeoResourceImpl(this, getHandle());
        }
        return resource;
    }

    public List<IResolve> members( IProgressMonitor monitor ) throws IOException {
        List<IResolve> list = new ArrayList<IResolve>();
        list.add(getGeoResource(monitor));
        return list;
    }
    public List<AbstractRasterGeoResource> resources( IProgressMonitor monitor ) throws IOException {
        List<AbstractRasterGeoResource> list = new ArrayList<AbstractRasterGeoResource>();
        list.add(getGeoResource(monitor));
        return list;
    }

    /**
     * Get metadata about an Image Service, represented by instance of {@link ImageServiceInfo}.
     */
    protected AbstractRasterServiceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        if (monitor == null)
            monitor = new NullProgressMonitor();
        try {
            monitor.beginTask("GDAL ImageIO-Ext", 2);
            monitor.worked(1);
            return new AbstractRasterServiceInfo(this, ImageServiceExtension.TYPE, "gdal"); //$NON-NLS-1$ //$NON-NLS-2$
        } finally {
            monitor.done();
        }
    }

    public Map<String, Serializable> getConnectionParams() {
        return new ImageServiceExtension().createParams(getIdentifier());
    }

    public void dispose( IProgressMonitor monitor ) {
        // do nothing
    }
}
