/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.catalog.internal.arcsde;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.geotools.arcsde.data.ArcSDERasterFormatFactory;
import org.geotools.arcsde.raster.gce.ArcSDERasterFormat;
import org.geotools.arcsde.session.ArcSDEConnectionConfig;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.data.ServiceInfo;
import org.geotools.geometry.GeneralEnvelope;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.locationtech.jts.geom.Envelope;

public class ArcSDERasterGeoResource extends IGeoResource {

    private String name;

    private Status status;

    private Throwable msg;

    public ArcSDERasterGeoResource( ArcServiceImpl service, String name ) {
        super();
        super.service = service;
        this.name = name;
        this.status = Status.NOTCONNECTED;
    }

    @Override
    protected IGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        try {
            AbstractGridCoverage2DReader reader = getReader(monitor);
            ServiceInfo serviceInfo = reader.getInfo();

            String name = serviceInfo.getTitle();
            String title = serviceInfo.getTitle();
            String description = serviceInfo.getDescription();
            URI schema = serviceInfo.getSchema();
            String[] keywords = serviceInfo.getKeywords().toArray(
                    new String[serviceInfo.getKeywords().size()]);

            GeneralEnvelope env = reader.getOriginalEnvelope();
            Envelope bounds = new Envelope(env.getMinimum(0), env.getMaximum(0), env.getMinimum(1),
                    env.getMaximum(1));
            CoordinateReferenceSystem crs = reader.getCoordinateReferenceSystem();
            ImageDescriptor icon = null;
            IGeoResourceInfo rasterInfo = new IGeoResourceInfo(title, name, description, schema,
                    bounds, crs, keywords, icon);
            return rasterInfo;
        } catch (Exception e) {
            // e.printStackTrace();
            msg = e;
            status = Status.BROKEN;
            throw (IOException) (new IOException(e.getLocalizedMessage()).initCause(e));
        }

    }

    @Override
    public ArcServiceImpl service( IProgressMonitor monitor ) throws IOException {
        return (ArcServiceImpl) service;
    }

    @Override
    public URL getIdentifier() {
        try {
            return new URL(service.getIdentifier().toString() + "#" + name); //$NON-NLS-1$
        } catch (MalformedURLException e) {
            return service.getIdentifier();
        }
    }

    public Throwable getMessage() {
        return msg;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        return adaptee != null
                && (adaptee.isAssignableFrom(GridCoverage.class)
                        || adaptee.isAssignableFrom(AbstractGridCoverage2DReader.class) || super
                        .canResolve(adaptee));
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {

        if (adaptee == null) {
            throw new NullPointerException("No adaptor specified"); //$NON-NLS-1$
        }

        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        // if (adaptee.isAssignableFrom(GridCoverage.class)) {
        // return adaptee.cast(createInfo(monitor));
        // }
        if (adaptee.isAssignableFrom(AbstractGridCoverage2DReader.class)) {
            AbstractGridCoverage2DReader reader = getReader(monitor);
            status = Status.CONNECTED;
            return adaptee.cast(reader);
        }

        return super.resolve(adaptee, monitor);
    }

    private AbstractGridCoverage2DReader getReader( IProgressMonitor monitor ) {
        String source = getCoverageURL();
        ArcSDERasterFormatFactory formatFactory = new ArcSDERasterFormatFactory();
        ArcSDERasterFormat format = formatFactory.createFormat();
        AbstractGridCoverage2DReader reader;
        reader = format.getReader(source);
        return reader;
    }

    private String getCoverageURL() {
        ArcSDEConnectionConfig config = ((ArcServiceImpl) service).getConnectionConfig();
        return ArcSDERasterFormat.createRasterURL(config, name);
    }

}
