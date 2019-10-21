/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.catalog.rasterings;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridEnvelope2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.parameter.DefaultParameterDescriptor;
import org.geotools.parameter.DefaultParameterDescriptorGroup;
import org.geotools.parameter.ParameterGroup;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.util.factory.GeoTools;
import org.geotools.xml.styling.SLDParser;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.catalog.rasterings.internal.Messages;
import org.locationtech.udig.ui.ProgressManager;
import org.locationtech.udig.ui.UDIGDisplaySafeLock;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridCoverageReader;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;

/**
 * Provides a handle to a raster resource allowing the service to be lazily
 * loaded.
 * <p>
 * This class provides functionality common to GridCoverage based resources.
 * 
 * @author mleslie
 * @since 0.6.0
 */
@SuppressWarnings("deprecation")
public abstract class AbstractRasterGeoResource extends IGeoResource {
	private volatile SoftReference<GridCoverage> coverage;

	private ParameterGroup readParams;

	protected String fileName;

	private Throwable msg;

	protected Lock lock = new UDIGDisplaySafeLock();

	final protected ID id;

	/**
	 * Construct <code>AbstractRasterGeoResource</code>.
	 * 
	 * @param service
	 *            The service creating this resource.
	 * @param name
	 *            Human readable name of this resource.
	 */
	public AbstractRasterGeoResource(AbstractRasterService service, String name) {
	    this.service = service;
		if (name == null) {
			URL url = service.getIdentifier();
			File file = URLUtils.urlToFile(url);
            name = file.getAbsolutePath();
			int slash = name.lastIndexOf('/');
			name = name.substring((slash == -1 && slash < name.length() - 1 ? 0
					: name.lastIndexOf('/')) + 1,
					(name.lastIndexOf('.') == -1 ? name.length() : name
							.lastIndexOf('.')));

		}
		this.fileName = name;
		this.id = new ID(service.getID(), fileName);
	}

	public Status getStatus() {
		return service.getStatus();
	}

	public Throwable getMessage() {
		if (msg != null) {
			return msg;
		} else {
			return service.getMessage();
		}
	}

    /**
     * Retrieves the parameters used to create the GridCoverageReader for this resource. This simply
     * delegates the creation of these parameters to a GridFormat.
     * 
     * @return ParameterGroup describing the GeoResource
     */
    public synchronized ParameterGroup getReadParameters() {
        if (this.readParams == null) {

            DefaultParameterDescriptor<GridGeometry> gridGeometryDescriptor = getWorldGridGeomDescriptor();
            // HashMap duplicate of that in GeoTools WorldImageFormat mInfo.
            // due to visibility restrictions
            HashMap<String, Object> info1 = new HashMap<String, Object>();
            info1.put("name", "Raster"); //$NON-NLS-1$//$NON-NLS-2$
            info1.put("description", //$NON-NLS-1$
                    "A raster file accompanied by a spatial data file"); //$NON-NLS-1$
            info1.put("vendor", "Geotools"); //$NON-NLS-1$ //$NON-NLS-2$
            info1.put("docURL", "http://www.geotools.org/WorldImageReader+formats"); //$NON-NLS-1$ //$NON-NLS-2$
            info1.put("version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
            this.readParams = new ParameterGroup(new DefaultParameterDescriptorGroup(info1,
                    new GeneralParameterDescriptor[] { gridGeometryDescriptor }));
        }
        return this.readParams;
    }

	/**
	 * Finds or creates the GridCoverage for this resource.
	 * 
	 * @return GridCoverage for this GeoResource
	 * @throws IOException
	 */
	public final synchronized Object findResource() throws IOException {
		lock.lock();
		try {
			if (this.coverage == null  || this.coverage.get()==null ) {
				try {
					GridCoverage gridCoverage = loadCoverage();
                    this.coverage = new SoftReference<GridCoverage>(gridCoverage);
				} catch (Throwable t) {
					msg = t;
					RasteringsPlugin.log("error reading coverage", t); //$NON-NLS-1$
					return null;
				}
			}
			return this.coverage.get();
		} finally {
			lock.unlock();
		}
	}

    /**
     * Template method called by findResource that is responsible for loading the coverage.  By default
     * it loads a very small coverage for getting info from but not using as a real datasource
     * @return
     * @throws IOException
     */
    protected GridCoverage loadCoverage() throws IOException {
        AbstractGridCoverage2DReader reader = this.service(new NullProgressMonitor()).getReader(null);
        ParameterGroup pvg = getReadParameters();
        List<GeneralParameterValue> list = pvg.values();
        GeneralParameterValue[] values = list.toArray(new GeneralParameterValue[0]);
        GridCoverage gridCoverage = reader.read(values);
        return gridCoverage;
    }

    @Override
    public ID getID() {
        return id;
    }
    
	public URL getIdentifier() {
		return getID().toURL();
	}

	public <T> T resolve(Class<T> adaptee, IProgressMonitor monitor)
 throws IOException {
        if (monitor == null)
            monitor = ProgressManager.instance().get();
        try {
            if (monitor != null)
                monitor.beginTask(Messages.AbstractRasterGeoResource_resolve, 3);
            if (adaptee == null) {
                return null;
            }
            if (adaptee.isAssignableFrom(GridCoverageLoader.class)) {
                return adaptee.cast(new GridCoverageLoader(this));
            }
            if (adaptee.isAssignableFrom(AbstractGridCoverage2DReader.class)) {
                AbstractGridCoverage2DReader reader = service(monitor).getReader(monitor);
                return adaptee.cast(reader);
            }
            if (adaptee.isAssignableFrom(GridCoverage.class)) {
                return adaptee.cast(findResource());
            }
            if (adaptee.isAssignableFrom(GridCoverage2D.class)) {
                return adaptee.cast(findResource());
            }
            if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
                if (monitor != null)
                    monitor.done();
                return adaptee.cast(createInfo(monitor));
            }
            if (adaptee.isAssignableFrom(ParameterGroup.class)) {
                if (monitor != null)
                    monitor.done();
                return adaptee.cast(getReadParameters());
            }
            if(adaptee.isAssignableFrom(Style.class)){
                Style style = style(monitor);
                if( style != null ){
                    return adaptee.cast( style(monitor));
                }
            }
            return super.resolve(adaptee, monitor);
        } finally {
            monitor.done();
        }
    }
	
	public Style style( IProgressMonitor monitor ) {
        URL url = service.getIdentifier();
        File file = URLUtils.urlToFile(url);
        String mapFile = file.getAbsolutePath();

        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());

        // strip off the extension and check for sld
        int lastdot = mapFile.lastIndexOf('.');
        String sld = mapFile.substring(0, lastdot) + ".sld"; //$NON-NLS-1$
        File f = new File(sld);
        if (!f.exists()) {
            // try upper case
            sld = mapFile.substring(0, lastdot) + ".SLD"; //$NON-NLS-1$
            f = new File(sld);
        }

        if (f.exists()) {
            // parse it up
            SLDParser parser = new SLDParser(styleFactory);
            try {
                parser.setInput(f);
            } catch (FileNotFoundException e) {
                return null; // well that is unexpected since f.exists()
            }
            Style[] styles = parser.readXML();

            if (styles.length > 0 && styles[0] != null) {
                return styles[0];
            }
        }
        return null; // well nothing worked out; make your own style
    }

	public <T> boolean canResolve(Class<T> adaptee) {
		if (adaptee == null)
			return false;
		return adaptee.isAssignableFrom(IGeoResourceInfo.class)
				|| adaptee.isAssignableFrom(IService.class)
				|| adaptee.isAssignableFrom(GridCoverage.class)
                || adaptee.isAssignableFrom(AbstractGridCoverage2DReader.class)
                || adaptee.isAssignableFrom(GridCoverageLoader.class)
                || adaptee.isAssignableFrom(Style.class)
				|| super.canResolve(adaptee);
	}

	@Override
	public AbstractRasterGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
	    return (AbstractRasterGeoResourceInfo) super.getInfo(monitor);
	}
	protected abstract AbstractRasterGeoResourceInfo createInfo(IProgressMonitor monitor)
			throws IOException;

	/**
	 * Returns A recommended {@link ParameterDescriptor} for all {@link GridCoverageReader}s. 
	 * This parameter requests an overview that is 100,100 of the image.
	 * <p>
	 * This is not intended to be overridden rather it is a useful method for getReadParamaters to call. 
	 * </p>
	 * 
	 * @return parameter requesting an overview that is 100,100 of the image.
	 */
	protected DefaultParameterDescriptor<GridGeometry> getWorldGridGeomDescriptor() {
		// this is a little dumb
	    GridEnvelope2D gridRange = new GridEnvelope2D(new Rectangle(0,0,100,100));
		ReferencedEnvelope env = new ReferencedEnvelope(-180.0, 180.0,-90.0, 90.0, DefaultGeographicCRS.WGS84);
		GridGeometry2D world = new GridGeometry2D(gridRange, env);
	
		DefaultParameterDescriptor<GridGeometry> gridGeometryDescriptor = new DefaultParameterDescriptor<GridGeometry>(
				AbstractGridFormat.READ_GRIDGEOMETRY2D.getName().toString(),
				GridGeometry.class, null, world); 
		return gridGeometryDescriptor;
	}

	@Override
	public AbstractRasterService service(IProgressMonitor monitor) throws IOException {
		return (AbstractRasterService) super.service(monitor);
	}
}
