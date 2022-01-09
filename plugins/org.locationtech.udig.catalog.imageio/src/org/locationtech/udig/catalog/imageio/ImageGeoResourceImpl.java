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
package org.locationtech.udig.catalog.imageio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.parameter.ParameterGroup;
import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResource;
import org.locationtech.udig.catalog.rasterings.AbstractRasterGeoResourceInfo;

/**
 * Provides a handle to a MrSID resource allowing the service to be lazily loaded.
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
     * @param service Service creating this resource.
     * @param name Human readable name of this resource.
     */
    public ImageGeoResourceImpl(ImageServiceImpl service, String name) {
        super(service, name);
        // System.out.println(service.getDescription());
    }

    /**
     * Get metadata about a geoResource, represented by instance of {@link ImageGeoResourceInfo}.
     */
    @Override
    protected AbstractRasterGeoResourceInfo createInfo(IProgressMonitor monitor)
            throws IOException {
        this.lock.lock();
        try {
            if (getStatus() == Status.BROKEN) {
                return null; // not available
            }
            return new AbstractRasterGeoResourceInfo(this, "ECW", "SID"); //$NON-NLS-1$ //$NON-NLS-2$
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retrieves the parameters used to create the <code>GridCoverageReader</code> for this
     * resource.
     */
    @Override
    public ParameterGroup getReadParameters() {
        final Map<String, Object> info1 = new HashMap<>();
        info1.put("name", "Imageio-ext"); //$NON-NLS-1$//$NON-NLS-2$
        info1.put("description", //$NON-NLS-1$
                "A MrSID raster file"); //$NON-NLS-1$
        info1.put("vendor", "Geotools"); //$NON-NLS-1$ //$NON-NLS-2$
        info1.put("docURL", "http://www.geotools.org/"); //$NON-NLS-1$ //$NON-NLS-2$
        info1.put("version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$

        return (ParameterGroup) ImageServiceExtension.getFactory("HFA") //$NON-NLS-1$
                .createFormat().getReadParameters();
    }

}
