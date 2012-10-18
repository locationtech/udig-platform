package net.refractions.udig.mapgraphic;

import static org.junit.Assert.*;

import java.io.IOException;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.ID;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IRepository;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

public class CatalogIntegrationTest {

    @Test
    public void testAquire() throws Exception {
        IRepository local = CatalogPlugin.getDefault().getLocal();
        local.acquire(MapGraphicService.SERVICE_URL, null);
        final ID SCALE_BAR = new ID("mapgraphic:/localhost/mapgraphic#scalebar", null);
        IGeoResource scalebarRessource = local.getById(IGeoResource.class, SCALE_BAR,
                new NullProgressMonitor());

        assertNotNull(scalebarRessource);
    }

}
