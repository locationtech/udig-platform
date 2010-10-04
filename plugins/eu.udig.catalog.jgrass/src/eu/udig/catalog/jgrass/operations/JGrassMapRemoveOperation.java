package eu.udig.catalog.jgrass.operations;

import java.io.File;

import net.refractions.udig.ui.ProgressManager;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import eu.hydrologis.jgrass.libs.utils.FileUtilities;
import eu.hydrologis.jgrass.libs.utils.JGrassConstants;
import eu.hydrologis.jgrass.libs.utils.JGrassUtilities;
import eu.hydrologis.jgrass.libs.utils.dialogs.ProblemDialogs;
import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGrassMapRemoveOperation implements IOp {

    public void op( Display display, Object target, IProgressMonitor monitor ) throws Exception {
        JGrassMapGeoResource mr = (JGrassMapGeoResource) target;

        String type = mr.getType();
        if (type.equals(JGrassConstants.ESRIRASTERMAP) || type.equals(JGrassConstants.FTRASTERMAP)
                || type.equals(JGrassConstants.GRASSASCIIRASTERMAP)) {
            String path = JGrassCatalogUtilities.getDirectPathFromJGrassMapGeoResource(mr);
            File filetoremove = new File(path);

            if (filetoremove.exists()) {
                if (!FileUtilities.deleteFileOrDir(filetoremove)) {
                    ProblemDialogs.errorDialog(null, "Problems occurred while removing the map.", true);
                } else {
                    ProblemDialogs.infoDialog(null,
                            filetoremove.getName() + " was succesfully removed", true);
                    JGrassCatalogUtilities.refreshJGrassService(filetoremove.getParentFile()
                            .getParentFile().getParent(), monitor);
                }
            }

        } else if (type.equals(JGrassConstants.GRASSBINARYRASTERMAP)) {
            String[] mapsetpathAndMapname = JGrassCatalogUtilities
                    .getMapsetpathAndMapnameFromJGrassMapGeoResource(mr);
            if (!JGrassUtilities.removeGrassRasterMap(mapsetpathAndMapname[0],
                    mapsetpathAndMapname[1])) {
                ProblemDialogs.errorDialog(null, "Problems occurred while removing the map.", true);
            } else {
                ProblemDialogs.infoDialog(null,
                        mapsetpathAndMapname[1] + " was succesfully removed", true);
                ((JGrassMapsetGeoResource) mr.parent(ProgressManager.instance().get(null)))
                        .removeMap(mapsetpathAndMapname[1], JGrassConstants.GRASSBINARYRASTERMAP);
            }
        }

    }
}
