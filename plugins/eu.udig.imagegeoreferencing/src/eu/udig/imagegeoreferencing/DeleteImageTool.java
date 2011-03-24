/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
package eu.udig.imagegeoreferencing;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import eu.udig.imagegeoreferencing.i18n.Messages;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.ui.PlatformGIS;

/**
 * Deletes the selected geoImage from the map
 *
 * </p>
 * @author GDavis, Refractions Research
 * @since 1.1.0
 */
public class DeleteImageTool extends AbstractModalTool implements ModalTool {

    public static final String TOOLID = "net.refractions.udig.imagegeoreference.tools.deleteImageTool"; //$NON-NLS-1$

    public DeleteImageTool() {
        this(MOUSE);
    }

    public DeleteImageTool( int targets ) {
        super(targets);
    }

    @Override
    public void setActive( boolean active ) {
        super.setActive(active);
        if (active) {
            // delete the selected image
            IMap activeMap = ApplicationGIS.getActiveMap();
            deleteImage(activeMap);
        }
    }

    /**
     * Delete the currently selected image
     * 
     * @param map
     */
    private void deleteImage( IMap map ) {
        // get the selected Image to delete
        GeoReferenceImage selectedGeoImage = null;
        GeoReferenceMapGraphic mapGraphic = GeoReferenceUtils.getMapGraphic(map);
        if (mapGraphic != null && mapGraphic.getImages() != null) {
            selectedGeoImage = GeoReferenceUtils.getSelectedGeoImage(mapGraphic.getImages(), map);
        }
        if (selectedGeoImage == null) {
            showDialog(true);
            return;
        }

        // delete and remove the image
        int result = showDialog(false);
        if (result == Window.OK) {
            // delete the image within a progress dialog process
            DeleteImageProcess deleteProcess = new DeleteImageProcess(map, selectedGeoImage, mapGraphic);
            PlatformGIS.runInProgressDialog("Deleting Image", false, deleteProcess, false); //$NON-NLS-1$
            boolean success = deleteProcess.getSuccess();
            if (!success) {
                showDialog(true);
                return;
            }
        }
    }

    /**
     * Show a dialog with the error explaining why the delete would not process
     */
    private int showDialog( final boolean error ) {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        final Dialog dialog = new Dialog(shell){
            @Override
            protected Control createDialogArea( Composite parent ) {
                Composite container = (Composite) super.createDialogArea(parent);
                GridLayout layout = new GridLayout(1, true);
                container.setLayout(layout);
                GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
                layoutData.widthHint = 450;
                container.setLayoutData(layoutData);

                Label descLabel = new Label(container, SWT.WRAP);
                if (error) {
                    descLabel.setText(Messages.DeleteDialog_errordesc);
                } else {
                    descLabel.setText(Messages.DeleteDialog_desc);
                }
                layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
                layoutData.verticalSpan = 1;
                layoutData.horizontalSpan = 1;
                descLabel.setLayoutData(layoutData);

                return container;
            }

            @Override
            protected void configureShell( Shell newShell ) {
                super.configureShell(newShell);
                newShell.setText(Messages.DeleteDialog_title);
                GridLayout layout = new GridLayout();
                layout.numColumns = 1;
                newShell.setLayout(layout);
            }
        };
        dialog.setBlockOnOpen(true);
        int result = dialog.open();
        return result;
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void mousePressed( MapMouseEvent e ) {
        // validate the mouse click
        if (!validModifierButtonCombo(e)) {
            return;
        }
    }

    /**
     * Checks if the user clicked the right mouse button, and if they did then
     * rotate the selected image referencing tool.
     * 
     * Otherwise it returns true if the combination of buttons and modifiers 
     * are legal with a left-mouse-click.
     * 
     * @param e
     * @return
     */
    protected boolean validModifierButtonCombo( MapMouseEvent e ) {
        if (e.buttons == MapMouseEvent.BUTTON3) {
            // rotate to the next tool after the resize image tool
            GeoReferenceUtils.rotateToNextTool(ResizeImageTool.TOOLID);
            return false;
        }
        return e.buttons == MapMouseEvent.BUTTON1 && !(e.modifiersDown());
    }

}

/**
 * Deletes the given geoImage.
 * 
 * @author GDavis
 *
 */
final class DeleteImageProcess implements IRunnableWithProgress {

    private IMap map;
    private GeoReferenceMapGraphic mapGraphic;
    private GeoReferenceImage selectedGeoImage;
    boolean deleteSuccess = false;

    public DeleteImageProcess( IMap map, GeoReferenceImage selectedGeoImage, GeoReferenceMapGraphic mapGraphic ) {
        this.map = map;
        this.mapGraphic = mapGraphic;
        this.selectedGeoImage = selectedGeoImage;
    }

    public void run( IProgressMonitor monitor ) throws InvocationTargetException, InterruptedException {
        // remove the selected geoImage from the mapgraphic layer
        if (map == null || mapGraphic == null || selectedGeoImage == null) {
            deleteSuccess = false;
            return;
        }
        HashMap<IMap, HashMap<String, GeoReferenceImage>> mapimages = mapGraphic.getImages();
        selectedGeoImage.setSelected(false);
        HashMap<String, GeoReferenceImage> images = mapimages.get(map);
        if (images != null)
            images.remove(selectedGeoImage.getFilename());

        // refresh the map
        if (mapGraphic != null)
            mapGraphic.getLayer(map).refresh(null);

        deleteSuccess = true;
    }

    public boolean getSuccess() {
        return deleteSuccess;
    }

}
