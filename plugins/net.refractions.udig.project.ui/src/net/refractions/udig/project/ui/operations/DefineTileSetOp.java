package net.refractions.udig.project.ui.operations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.catalog.IGeoResourceInfo;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.operations.example.LayerSummary;
import net.refractions.udig.project.ui.summary.SummaryData;
import net.refractions.udig.project.ui.tileset.TileSetDialog;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.wms.WebMapServer;

import com.vividsolutions.jts.geom.Envelope;

/**
 * An operation to define a tileset on an GeoResource for an appropriately configured WMS Layer, so
 * if can resolve to both a WMSLayer and a TileSet data structure.
 * 
 * @author jhudson
 * @since 1.2.0
 */
public class DefineTileSetOp implements IOp {

    /**
     * @see net.refractions.udig.ui.operations.IOp#op(java.lang.Object)
     */
    @Override
    public void op( final Display display, Object target, IProgressMonitor monitor )
            throws Exception {
        if (target instanceof IGeoResource) {
            IGeoResource resource = (IGeoResource) target;
            op(display, monitor, resource);
        }
    }
    
    /**
     * @param display
     * @param monitor
     * @param resource
     * @throws IOException
     */
    private void op( final Display display, final IProgressMonitor monitor, final IGeoResource resource )
            throws IOException {        
        /**
         * display the dialog
         */
        display.asyncExec(new Runnable(){
            public void run() {
                Dialog d=new TileSetDialog(display.getActiveShell(), resource);
                d.setBlockOnOpen(true);
                d.open();
            }
        });
    }
}
