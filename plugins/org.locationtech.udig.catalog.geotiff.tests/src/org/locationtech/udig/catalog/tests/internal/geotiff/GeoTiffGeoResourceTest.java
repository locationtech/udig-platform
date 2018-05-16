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
package org.locationtech.udig.catalog.tests.internal.geotiff;

import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Assume;
import org.junit.Before;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.geotiff.GeoTiffServiceExtension;
import org.locationtech.udig.catalog.tests.AbstractGeoResourceTest;

public class GeoTiffGeoResourceTest extends AbstractGeoResourceTest {

    public static String TIFF_1 = "cir.tif"; //$NON-NLS-1$
    private IGeoResource resource;
    private IService service;

    @Before
    public void setUp() throws Exception {
        Assume.assumeTrue(GDALUtilities.isGDALAvailable());

        GeoTiffServiceExtension fac = new GeoTiffServiceExtension();
        URL url = Data.getResource(GeoTiffGeoResourceTest.class, TIFF_1);
        service = fac.createService(url, fac.createParams(url));
        resource = service.resources((IProgressMonitor) null).get(0);
    }

    @Override
    protected IGeoResource getResolve() {
        return resource;
    }

    @Override
    protected boolean hasParent() {
        return true;
    }

}
