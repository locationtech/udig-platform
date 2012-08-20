package net.refractions.udig.catalog.tests.internal.geotiff;

import java.io.File;
import java.net.URL;

import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.geotiff.GeoTiffServiceExtension;
import net.refractions.udig.catalog.tests.AbstractServiceTest;

import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.gce.geotiff.GeoTiffFormatFactorySpi;
import org.junit.Before;

public class GeoTiffServiceTest extends AbstractServiceTest {
    public static String TIFF_1 = "cir.tif"; //$NON-NLS-1$
    private IService service;

    @Before
    public void setUp() throws Exception {
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
