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
package net.refractions.udig.catalog.internal.worldimage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.URLUtils;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResourceInfo;
import net.refractions.udig.catalog.rasterings.GridCoverageLoader;
import net.refractions.udig.catalog.worldimage.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.geotools.data.PrjFileReader;
import org.geotools.parameter.DefaultParameterDescriptor;
import org.geotools.parameter.DefaultParameterDescriptorGroup;
import org.geotools.parameter.ParameterGroup;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Provides a handle to a world image resource allowing the service to be lazily loaded.
 * 
 * @author mleslie
 * @since 0.6.0
 */
public class WorldImageGeoResourceImpl extends AbstractRasterGeoResource {
    private URL prjURL;
    private InMemoryCoverageLoader loader;

    /**
     * Construct <code>WorldImageGeoResourceImpl</code>.
     * 
     * @param service Service creating this resource.
     * @param name Human readable name of this resource.
     * @param prjURL Name a projection file associated with this resource can be expected to have.
     */
    public WorldImageGeoResourceImpl( WorldImageServiceImpl service, String name, URL prjURL ) {
        super(service, name);
        this.prjURL = prjURL;
        try {
            this.loader = new InMemoryCoverageLoader(this, fileName);
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException().initCause(e);
        }
    }
    @Override
    public WorldImageInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return (WorldImageInfo) super.getInfo(monitor);
    }
    protected WorldImageInfo createInfo( IProgressMonitor monitor ) throws IOException {
        this.lock.lock();
        try {
            if (getStatus() == Status.BROKEN) {
                return null; // unavailable
            }
            CoordinateReferenceSystem crs = readCrs();
            return new WorldImageInfo(this, crs);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Convenience method for opening a PrjFileReader. Stolen from Geotools ShapefileSomethingROther
     * 
     * @return A new PrjFileReader
     * @throws IOException If an error occurs during creation.
     */
    private PrjFileReader openPrjReader( URL prjURL ) throws IOException, FactoryException {
        ReadableByteChannel rbc = null;
        try {
            if (prjURL != null) {
                rbc = getReadChannel(prjURL);
            }
        } catch (IOException e) {
            CatalogPlugin.getDefault().getLog().log(
                    new org.eclipse.core.runtime.Status(IStatus.WARNING,
                            "net.refractions.udig.catalog", 0, //$NON-NLS-1$
                            Messages.WorldImageGeoResourceImpl_PrjUnavailable, e));
        }
        if (rbc == null) {
            return null;
        }

        return new PrjFileReader(rbc);
    }

    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (GridCoverageLoader.class.isAssignableFrom(adaptee) && !isTiff())
            return true;

        return super.canResolve(adaptee);
    }

    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (GridCoverageLoader.class.isAssignableFrom(adaptee) && !isTiff()) {
            return adaptee.cast(loader);
        }

        return super.resolve(adaptee, monitor);
    }

    private boolean isTiff() {
        boolean isTiff = fileName.toLowerCase().endsWith(".tiff") || fileName.toLowerCase().endsWith(".tif"); //$NON-NLS-1$ //$NON-NLS-2$
        return isTiff;
    }

    /**
     * Convienience method to create a ReadableByteChannel from a URL.
     * 
     * @param prjURL
     * @return A Channel for the given file
     * @throws IOException
     */
    private ReadableByteChannel getReadChannel( URL prjURL ) throws IOException {
        ReadableByteChannel channel = null;

        if (prjURL.getProtocol().equalsIgnoreCase("file")) { //$NON-NLS-1$
            File file = URLUtils.urlToFile(prjURL);

            if (!file.exists() || !file.canRead()) {
                throw new FileNotFoundException(file.getAbsolutePath());
            }
            FileInputStream in = new FileInputStream(file);
            channel = in.getChannel();

        } else {
            InputStream in = prjURL.openConnection().getInputStream();
            channel = Channels.newChannel(in);
        }

        return channel;
    }

    public ParameterGroup getReadParameters() {
        try {
            CoordinateReferenceSystem crsSys = readCrs();

            DefaultParameterDescriptor<CoordinateReferenceSystem> crs = new DefaultParameterDescriptor<CoordinateReferenceSystem>(
                    "crs", //$NON-NLS-1$
                    CoordinateReferenceSystem.class, null, crsSys);

            DefaultParameterDescriptor<GridGeometry> gridGeometryDescriptor = getWorldGridGeomDescriptor();

            // Stolen from WorldImageFormat, as mInfo is not externally
            // accesible
            HashMap<String, Object> info1 = new HashMap<String, Object>();
            info1.put("name", "WorldImage"); //$NON-NLS-1$//$NON-NLS-2$
            info1.put("description", //$NON-NLS-1$
                    "A raster file accompanied by a spatial data file"); //$NON-NLS-1$
            info1.put("vendor", "Geotools"); //$NON-NLS-1$ //$NON-NLS-2$
            info1.put("docURL", "http://www.geotools.org/WorldImageReader+formats"); //$NON-NLS-1$ //$NON-NLS-2$
            info1.put("version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
            return new ParameterGroup(new DefaultParameterDescriptorGroup(info1,
                    new GeneralParameterDescriptor[]{crs, gridGeometryDescriptor}));

        } catch (MalformedURLException e) {
            CatalogPlugin.getDefault().getLog().log(
                    new org.eclipse.core.runtime.Status(IStatus.WARNING,
                            "net.refractions.udig.catalog", 0, //$NON-NLS-1$
                            "", e)); //$NON-NLS-1$
            return super.getReadParameters();
        } catch (IOException e) {
            CatalogPlugin.getDefault().getLog().log(
                    new org.eclipse.core.runtime.Status(IStatus.WARNING,
                            "net.refractions.udig.catalog", 0, //$NON-NLS-1$
                            "", e)); //$NON-NLS-1$
            return super.getReadParameters();
        }
    }

    private CoordinateReferenceSystem readCrs() throws IOException {
        PrjFileReader prjRead = null;
        try {
            prjRead = openPrjReader(this.prjURL);
        } catch (FileNotFoundException e) {
            CatalogPlugin.getDefault().getLog().log(
                    new org.eclipse.core.runtime.Status(IStatus.WARNING,
                            "net.refractions.udig.catalog", 0, //$NON-NLS-1$
                            "", e)); //$NON-NLS-1$
        } catch (FactoryException e) {
            CatalogPlugin.getDefault().getLog().log(
                    new org.eclipse.core.runtime.Status(IStatus.WARNING,
                            "net.refractions.udig.catalog", 0, //$NON-NLS-1$
                            "", e)); //$NON-NLS-1$
        }
        CoordinateReferenceSystem crsSys = null;
        if (prjRead != null) {
            crsSys = prjRead.getCoordinateReferenceSystem();
        } else {
            // prj file not read, default to lat long
            crsSys = DefaultEngineeringCRS.GENERIC_2D;
        }
        return crsSys;
    }

}
