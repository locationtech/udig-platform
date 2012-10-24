/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
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
 */
package net.refractions.udig.catalog.tests.internal.geotiff;

import it.geosolutions.imageio.gdalframework.GDALUtilities;

import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.geotiff.GeoTiffServiceExtension;
import net.refractions.udig.catalog.tests.AbstractGeoResourceTest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Assume;
import org.junit.Before;

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
