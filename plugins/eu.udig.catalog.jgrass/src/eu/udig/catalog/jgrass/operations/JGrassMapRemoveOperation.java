package eu.udig.catalog.jgrass.operations;

import java.io.File;
import java.io.IOException;

import net.refractions.udig.ui.ProgressManager;
import net.refractions.udig.ui.operations.IOp;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.gce.grassraster.JGrassConstants;

import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGrassMapRemoveOperation implements IOp {

    public void op( final Display display, final Object target, IProgressMonitor monitor ) throws Exception {

        display.syncExec(new Runnable(){
            public void run() {
                JGrassMapGeoResource mr = (JGrassMapGeoResource) target;
                String type = mr.getType();
                if (type.equals(JGrassConstants.GRASSBINARYRASTERMAP)) {
                    String[] mapsetpathAndMapname = JGrassCatalogUtilities.getMapsetpathAndMapnameFromJGrassMapGeoResource(mr);
                    try {
                        removeGrassRasterMap(mapsetpathAndMapname[0], mapsetpathAndMapname[1]);
                        MessageDialog.openInformation(display.getActiveShell(), "Information", mapsetpathAndMapname[1]
                                + " was succesfully removed");
                        ((JGrassMapsetGeoResource) mr.parent(new NullProgressMonitor())).removeMap(mapsetpathAndMapname[1],
                                JGrassConstants.GRASSBINARYRASTERMAP);
                    } catch (Exception e) {
                        MessageDialog.openInformation(display.getActiveShell(), "Information",
                                "Problems occurred while removing the map.");
                    }
                }
            }
        });

    }

    /**
     * Given the mapsetpath and the mapname, the map is removed with all its accessor files
     * 
     * @param mapsetPath
     * @param mapName
     * @throws IOException 
     */
    public void removeGrassRasterMap( String mapsetPath, String mapName ) throws IOException {
        // list of files to remove
        String mappaths[] = filesOfRasterMap(mapsetPath, mapName);

        // first delete the list above, which are just files
        for( int j = 0; j < mappaths.length; j++ ) {
            File filetoremove = new File(mappaths[j]);
            if (filetoremove.exists()) {
                FileUtils.forceDelete(filetoremove);
            }
        }
    }

    /**
     * Returns the list of files involved in the raster map issues. If for example a map has to be
     * deleted, then all these files have to.
     * 
     * @param mapsetPath - the path of the mapset
     * @param mapname -the name of the map
     * @return the array of strings containing the full path to the involved files
     */
    public String[] filesOfRasterMap( String mapsetPath, String mapname ) {
        String filesOfRaster[] = new String[]{
                mapsetPath + File.separator + JGrassConstants.FCELL + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELL + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CATS + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.HIST + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELLHD + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.COLR + File.separator + mapname,
                // it is very important that the folder cell_misc/mapname comes
                // before the files in it
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_FORMAT,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_QUANT,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_RANGE,
                mapsetPath + File.separator + JGrassConstants.CELL_MISC + File.separator + mapname + File.separator
                        + JGrassConstants.CELLMISC_NULL};
        return filesOfRaster;
    }
}
