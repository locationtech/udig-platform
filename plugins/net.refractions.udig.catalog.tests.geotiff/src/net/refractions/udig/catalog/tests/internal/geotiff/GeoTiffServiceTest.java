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

import java.io.File;
import java.net.URL;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.geotiff.GeoTiffServiceExtension;
import net.refractions.udig.catalog.tests.AbstractServiceTest;

import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffFormatFactorySpi;
import org.junit.Assume;
import org.junit.Before;

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
