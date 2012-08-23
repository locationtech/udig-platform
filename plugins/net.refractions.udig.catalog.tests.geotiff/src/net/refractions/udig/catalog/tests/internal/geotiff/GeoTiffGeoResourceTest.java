package net.refractions.udig.catalog.tests.internal.geotiff;

import java.net.URL;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.internal.geotiff.GeoTiffServiceExtension;
import net.refractions.udig.catalog.tests.AbstractGeoResourceTest;

import org.eclipse.core.runtime.IProgressMonitor;
import org.junit.Before;

public class GeoTiffGeoResourceTest extends AbstractGeoResourceTest {
    
    public static String TIFF_1 = "cir.tif"; //$NON-NLS-1$
    private IGeoResource resource;
    private IService service;

    @Before
    public void setUp() throws Exception {
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
