package org.locationtech.udig.mapgraphic;

import static org.junit.Assert.assertNotNull;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IRepository;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;

public class CatalogIntegrationTest {

    @Test
    public void testAquire() throws Exception {
        
        IRepository local = CatalogPlugin.getDefault().getLocal();
        local.acquire(MapGraphicService.SERVICE_URL, null);
        System.out.println("1");
        // Test ScaleBar
        final ID SCALE_BAR = new ID(MapGraphicService.SERVICE_URL+"#scalebar", null); //$NON-NLS-1$
        IGeoResource scalebarResource = local.getById(IGeoResource.class, SCALE_BAR,
                new NullProgressMonitor());
        System.out.println("2");
        assertNotNull(scalebarResource);
        
        // Test Graticule
        final ID GRATICULE = new ID(MapGraphicService.SERVICE_URL+"#graticule", null); //$NON-NLS-1$
        System.out.println("3");
        IGeoResource graticuleResource = local.getById(IGeoResource.class, GRATICULE,
                new NullProgressMonitor());
        System.out.println("4");
        assertNotNull(graticuleResource);
        
    }

}
