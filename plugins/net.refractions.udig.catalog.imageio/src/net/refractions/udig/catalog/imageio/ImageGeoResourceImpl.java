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
import java.util.HashMap;
import java.util.Map;

import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResource;
import net.refractions.udig.catalog.rasterings.AbstractRasterGeoResourceInfo;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.parameter.ParameterGroup;

/**
 * Provides a handle to a MrSID resource allowing the service to be lazily
 * loaded.
 * 
 * @author mleslie
 * @author Daniele Romagnoli, GeoSolutions
 * @author Jody Garnett
 * @author Simone Giannecchini, GeoSolutions
 * 
 * @since 0.6.0
 */
public class ImageGeoResourceImpl extends AbstractRasterGeoResource {

	String name;

	/**
	 * Construct <code>ImageGeoResourceImpl</code>.
	 * 
	 * @param service
	 *            Service creating this resource.
	 * @param name
	 *            Human readable name of this resource.
	 */
	public ImageGeoResourceImpl(ImageServiceImpl service, String name) {
		super(service, name);
		// System.out.println(service.getDescription());
	}
	
	/**
	 * Get metadata about a geoResource, represented by instance of
	 * {@link ImageGeoResourceInfo}.
	 */
	protected AbstractRasterGeoResourceInfo createInfo(IProgressMonitor monitor)
			throws IOException {
		this.lock.lock();
		try {
		    if( getStatus() == Status.BROKEN) {
		        return null; // not available
		    }
			return new AbstractRasterGeoResourceInfo(this, "ECW", "SID"); //$NON-NLS-1$ //$NON-NLS-2$
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Retrieves the parameters used to create the
	 * <code>GridCoverageReader</code> for this resource.
	 */
	public ParameterGroup getReadParameters() {
		final Map<String, Object> info1 = new HashMap<String, Object>();
		info1.put("name", "Imageio-ext"); //$NON-NLS-1$//$NON-NLS-2$
		info1.put("description", //$NON-NLS-1$
				"A MrSID raster file"); //$NON-NLS-1$
		info1.put("vendor", "Geotools"); //$NON-NLS-1$ //$NON-NLS-2$
		info1.put("docURL", "http://www.geotools.org/"); //$NON-NLS-1$ //$NON-NLS-2$
		info1.put("version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$

        return (ParameterGroup) ((AbstractGridFormat) ImageServiceExtension
                .getFactory("HFA").createFormat()).getReadParameters(); //$NON-NLS-1$
    }
	
}
