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
import org.geotools.data.DataStore;
import org.locationtech.udig.project.internal.impl.UDIGSimpleFeatureStore;

public class FeatureChooserDialog extends AbstractChooserDialog {

    private IResourcesSelector active;

    private List<DataStore> selectedLayers;

    @Override
    public void open(Shell parentShell, final int selectionType) {

        Dialog dialog = new Dialog(parentShell) {

            @Override
            protected void configureShell(Shell shell) {
                super.configureShell(shell);
                shell.setText("Select vector map"); //$NON-NLS-1$
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

                active = new FeatureLayerTreeViewer(parentPanel, SWT.BORDER, selectionType);

                makeCheckPanel();

                return parentPanel;
            }

            @Override
            protected void buttonPressed(int buttonId) {
                if (buttonId == OK) {
                    Object tmp = null;
                    try {
                        tmp = ((List) active.getSelectedLayers()).get(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                        selectedLayers = null;
                    }
                    if (tmp instanceof DataStore) {
                        selectedLayers = (List<DataStore>) active.getSelectedLayers();
                    } else if (tmp instanceof UDIGSimpleFeatureStore) {
                        List l = (List) active.getSelectedLayers();
                        List<DataStore> ll = new ArrayList<>();
                        for (Object object : l) {
                            UDIGSimpleFeatureStore internal = (UDIGSimpleFeatureStore) object;
                            ll.add(internal.getDataStore());
                        }
                        selectedLayers = ll;
                    }
                } else {
                    selectedLayers = null;
                }
                super.buttonPressed(buttonId);
            }

        };
        dialog.setBlockOnOpen(true);
        dialog.open();
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
            active = new FeatureCatalogTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE);
        } else if (!isCatalogType) {
            active = new FeatureLayerTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE);
        }
        makeCheckPanel();

        parentPanel.layout();
    }

    @Override
    public List<DataStore> getSelectedResources() {
        return selectedLayers;
    }

    @Override
    public String getNameOfResourceAtIndex(int index) {
        try {
            return selectedLayers.get(index).getTypeNames()[0];
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
