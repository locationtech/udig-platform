package eu.udig.catalog.jgrass.operations;

import java.io.File;

import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import eu.hydrologis.jgrass.libs.utils.JGrassConstants;
import eu.hydrologis.jgrass.libs.utils.dialogs.ProblemDialogs;
import eu.hydrologis.jgrass.libs.utils.monitor.EclipseProgressMonitorAdapter;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * @author Andrea Antonello - www.hydrologis.com
 *
 */
public class FtImportOperation implements IOp {

    private final double[] novalue = {-9999.0};
    private final String[] selpath = {""}; //$NON-NLS-1$

    public void op( Display display, Object target, IProgressMonitor pm ) throws Exception {

        JGrassMapsetGeoResource mapsetResource = (JGrassMapsetGeoResource) target;
        String type = JGrassConstants.FTRASTERMAP;
        EclipseProgressMonitorAdapter monitor = new EclipseProgressMonitorAdapter(pm);
        String mapName = JGrassCatalogUtilities.importMapForType(monitor, mapsetResource, type,
                novalue, selpath);
        if (mapName == null)
            return;
        File mapsetFile = mapsetResource.getFile();
        JGrassCatalogUtilities.addMapToCatalog(mapsetFile.getParent(), mapsetFile.getName(), mapName,
                JGrassConstants.GRASSBINARYRASTERMAP);

        ProblemDialogs.infoDialog(null, "Map successfully written to " + mapName, true);
    }

}
