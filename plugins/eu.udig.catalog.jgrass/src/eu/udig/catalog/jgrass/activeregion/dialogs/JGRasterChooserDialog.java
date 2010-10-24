/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.catalog.jgrass.activeregion.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import eu.udig.catalog.jgrass.core.JGrassMapGeoResource;
import eu.udig.catalog.jgrass.messages.Messages;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGRasterChooserDialog extends AbstractChooserDialog {

    private IResourcesSelector active;
    private List<JGrassMapGeoResource> newLayers;
    private Dialog dialog;
    private String mapsetPath;

    public JGRasterChooserDialog( String mapsetPath ) {
        this.mapsetPath = mapsetPath;
    }

    public void open( Shell parentShell, final int selectionType ) {

        dialog = new Dialog(parentShell){

            @Override
            protected void configureShell( Shell shell ) {
                super.configureShell(shell);
                shell.setText(Messages.getString("JGRasterChooserDialog.chooseraster")); //$NON-NLS-1$
            }

            @Override
            protected Point getInitialSize() {
                return new Point(280, 380);
            }

            @Override
            protected Control createDialogArea( Composite parent ) {
                parentPanel = (Composite) super.createDialogArea(parent);
                GridLayout gLayout = (GridLayout) parentPanel.getLayout();

                gLayout.numColumns = 1;

                active = new JGRasterLayerTreeViewer(parentPanel, SWT.BORDER, selectionType, mapsetPath);

                makeCheckPanel();

                return parentPanel;
            }

            @Override
            protected void buttonPressed( int buttonId ) {
                String text = null;
                newLayers = new ArrayList<JGrassMapGeoResource>();
                if (buttonId == OK) {
                    List<IGeoResource> layers = (List<IGeoResource>) active.getSelectedLayers();
                    for( IGeoResource geoResource : layers ) {
                        newLayers.add((JGrassMapGeoResource) geoResource);
                        try {
                            text = geoResource.getInfo(null).getTitle();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (text == null) {
                        return;
                    }
                } else {
                    newLayers = null;
                }
                super.buttonPressed(buttonId);
            }
        };
        dialog.setBlockOnOpen(true);
        dialog.open();
    }

    public boolean isDisposed() {
        return dialog.getShell().isDisposed();
    }

    public void widgetSelected( SelectionEvent e ) {

        Button selectedButton = (Button) e.getSource();

        if (!selectedButton.getSelection()) {
            return;
        }

        isCatalogType = !selectedButton.getData("type").equals(VISIBLELAYERTYPE); //$NON-NLS-1$

        // remove the composite
        Widget[] childrens = parentPanel.getChildren();
        for( int i = 0; i < childrens.length; i++ ) {
            childrens[i].dispose();
        }

        if (isCatalogType) {
            active = new JGRasterCatalogTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE, mapsetPath);
        } else if (!isCatalogType) {
            active = new JGRasterLayerTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE, mapsetPath);
        }
        makeCheckPanel();

        parentPanel.layout();
    }

    public List<JGrassMapGeoResource> getSelectedResources() {
        return newLayers;
    }

    public String getNameOfResourceAtIndex( int index ) {
        try {
            if (newLayers != null && newLayers.size() > 0) {
                return newLayers.get(index).getInfo(null).getName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
