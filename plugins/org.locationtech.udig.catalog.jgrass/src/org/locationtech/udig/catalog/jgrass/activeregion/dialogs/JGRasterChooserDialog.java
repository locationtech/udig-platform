/**
 * JGrass - Free Open Source Java GIS http://www.jgrass.org
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.activeregion.dialogs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapGeoResource;
import org.locationtech.udig.catalog.jgrass.messages.Messages;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class JGRasterChooserDialog extends AbstractChooserDialog {

    private IResourcesSelector active;

    private List<JGrassMapGeoResource> newLayers;

    private Dialog dialog;

    private String mapsetPath;

    public JGRasterChooserDialog(String mapsetPath) {
        this.mapsetPath = mapsetPath;
    }

    @Override
    public void open(Shell parentShell, final int selectionType) {

        dialog = new Dialog(parentShell) {

            @Override
            protected void configureShell(Shell shell) {
                super.configureShell(shell);
                shell.setText(Messages.getString("JGRasterChooserDialog.chooseraster")); //$NON-NLS-1$
            }

            @Override
            protected Point getInitialSize() {
                return new Point(280, 380);
            }

            @Override
            protected Control createDialogArea(Composite parent) {
                parentPanel = (Composite) super.createDialogArea(parent);
                GridLayout gLayout = (GridLayout) parentPanel.getLayout();

                gLayout.numColumns = 1;

                active = new JGRasterLayerTreeViewer(parentPanel, SWT.BORDER, selectionType,
                        mapsetPath);

                makeCheckPanel();

                return parentPanel;
            }

            @Override
            protected void buttonPressed(int buttonId) {
                String text = null;
                newLayers = new ArrayList<>();
                if (buttonId == OK) {
                    List<IGeoResource> layers = (List<IGeoResource>) active.getSelectedLayers();
                    for (IGeoResource geoResource : layers) {
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

    @Override
    public void widgetSelected(SelectionEvent e) {

        Button selectedButton = (Button) e.getSource();

        if (!selectedButton.getSelection()) {
            return;
        }

        isCatalogType = !selectedButton.getData("type").equals(VISIBLELAYERTYPE); //$NON-NLS-1$

        // remove the composite
        Widget[] childrens = parentPanel.getChildren();
        for (int i = 0; i < childrens.length; i++) {
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

    @Override
    public List<JGrassMapGeoResource> getSelectedResources() {
        return newLayers;
    }

    @Override
    public String getNameOfResourceAtIndex(int index) {
        try {
            if (newLayers != null && !newLayers.isEmpty()) {
                return newLayers.get(index).getInfo(null).getName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
