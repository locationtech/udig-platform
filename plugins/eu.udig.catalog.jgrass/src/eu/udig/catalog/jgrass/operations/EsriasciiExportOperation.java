package eu.udig.catalog.jgrass.operations;

import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

import eu.hydrologis.jgrass.libs.utils.JGrassConstants;
import eu.hydrologis.jgrass.libs.utils.monitor.EclipseProgressMonitorAdapter;
import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.utils.JGrassCatalogUtilities;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class EsriasciiExportOperation implements IOp {
    private final String[] selpath = {""}; //$NON-NLS-1$

    public void op( Display display, Object target, IProgressMonitor pm ) throws Exception {
        JGrassMapGeoResource mr = (JGrassMapGeoResource) target;
        String type = JGrassConstants.ESRIRASTERMAP;
        EclipseProgressMonitorAdapter monitor = new EclipseProgressMonitorAdapter(pm);
        JGrassCatalogUtilities.exportMapToType(monitor, mr, type, selpath);

    }
}
