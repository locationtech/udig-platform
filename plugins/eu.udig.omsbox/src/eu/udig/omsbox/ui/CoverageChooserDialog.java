/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.omsbox.ui;

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

/**
 * Coverage chooser dialog.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CoverageChooserDialog extends AbstractChooserDialog {

    private IGeoResourcesSelector active;
    private Dialog dialog;

    private List<IGeoResource> resourcesList;

    public void open( Shell parentShell, final int selectionType ) {

        dialog = new Dialog(parentShell){

            @Override
            protected void configureShell( Shell shell ) {
                super.configureShell(shell);
                shell.setText("");
            }

            @Override
            protected Point getInitialSize() {
                return new Point(320, 450);
            }

            @Override
            protected Control createDialogArea( Composite parent ) {
                parentPanel = (Composite) super.createDialogArea(parent);
                GridLayout gLayout = (GridLayout) parentPanel.getLayout();
                gLayout.numColumns = 1;
                active = new CoverageLayersTreeViewer(parentPanel, SWT.BORDER, selectionType);
                makeCheckPanel();
                return parentPanel;
            }

            @Override
            protected void buttonPressed( int buttonId ) {
                if (buttonId == OK) {
                    resourcesList = active.getSelectedResources();
                } else {
                    resourcesList = null;
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

        isCatalogType = !selectedButton.getData("type").equals(VISIBLELAYERTYPE);

        // remove the composite
        Widget[] childrens = parentPanel.getChildren();
        for( int i = 0; i < childrens.length; i++ ) {
            childrens[i].dispose();
        }

        if (isCatalogType) {
            active = new CoverageCatalogTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE);
        } else if (!isCatalogType) {
            active = new CoverageLayersTreeViewer(parentPanel, SWT.BORDER, SWT.SINGLE);
        }
        makeCheckPanel();

        parentPanel.layout();
    }

    public List<IGeoResource> getSelectedResources() {
        return resourcesList;
    }

    public String getNameOfResourceAtIndex( int index ) {
        IGeoResource iGeoResource = resourcesList.get(index);
        return iGeoResource.getTitle();
    }

}
