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

import java.io.File;
import java.net.URL;

import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffFormatFactorySpi;
import org.junit.Assume;
import org.junit.Before;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.internal.geotiff.GeoTiffServiceExtension;
import org.locationtech.udig.catalog.tests.AbstractServiceTest;

public class GeoTiffServiceTest extends AbstractServiceTest {
    public static String TIFF_1 = "cir.tif"; //$NON-NLS-1$
    private IService service;

    @Before
    public void setUp() throws Exception {
        Assume.assumeTrue(GDALUtilities.isGDALAvailable());

        GeoTiffServiceExtension fac = new GeoTiffServiceExtension();
        URL url = Data.getResource(GeoTiffServiceTest.class, TIFF_1);

        GeoTiffFormatFactorySpi factory = GeoTiffServiceExtension.getFactory();
        GeoTiffFormat format = (GeoTiffFormat) factory.createFormat();

        try {
            ID id = new ID(url);
            File file = id.toFile();

            format.accepts(file);

        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }

        service = fac.createService(url, fac.createParams(url));
    }

    @Override
    protected IService getResolve() {
        return service;
    }

    @Override
    protected boolean hasParent() {
        return false;
    }

    @Override
    protected boolean isLeaf() {
        return false;
    }

}
