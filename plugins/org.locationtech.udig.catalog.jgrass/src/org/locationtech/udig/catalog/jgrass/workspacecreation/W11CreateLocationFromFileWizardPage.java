/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.workspacecreation;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class W11CreateLocationFromFileWizardPage extends WizardPage {

    public static final String ID = "W11CreateLocationFromFileWizardPage"; //$NON-NLS-1$
    private final WorkspaceProperties properties;

    public W11CreateLocationFromFileWizardPage( WorkspaceProperties properties ) {
        super(ID);
        this.properties = properties;
        setTitle("Setting the file and location info");
        setDescription("In this page the user is asked to supply the file to import and info for the creation of the location.");
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl( Composite maxparent ) {
        Composite parent = new Composite(maxparent, SWT.None);
        parent.setLayout(new GridLayout());

        // the import file group
        Group fileimportGroup = new Group(parent, SWT.None);
        fileimportGroup.setLayout(new GridLayout(2, false));
        fileimportGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        fileimportGroup.setText("choose a file for which to create a location and mapset");

        final Text fileText = new Text(fileimportGroup, SWT.BORDER);
        fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
                | GridData.VERTICAL_ALIGN_CENTER));
        fileText.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                String path = fileText.getText();
                if (isImportFileValid(path)) {
                    properties.importFilePath = path;
                }
            }
        });
        final Button fileChooseButton = new Button(fileimportGroup, SWT.BORDER);
        fileChooseButton.setText("...");
        fileChooseButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(fileChooseButton.getShell(), SWT.OPEN);
                fileDialog.setFilterExtensions(new String[]{"*.asc", "*.tif", "*.tiff"});
                String path = fileDialog.open();
                if (path != null)
                    path = path.replaceAll("\\s+", "_");
                if (isImportFileValid(path)) {
                    properties.importFilePath = path;
                    fileText.setText(path);
                }
            }
        });

        // the base folder group
        Group baseFolderGroup = new Group(parent, SWT.None);
        baseFolderGroup.setLayout(new GridLayout(2, false));
        baseFolderGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        baseFolderGroup.setText("choose a folder in which to create the new location");

        final Text baseFolderText = new Text(baseFolderGroup, SWT.BORDER);
        baseFolderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
                | GridData.VERTICAL_ALIGN_CENTER));
        baseFolderText.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                String path = baseFolderText.getText();
                if (isBaseFolderValid(path)) {
                    properties.basePath = path;
                }
            }
        });
        final Button locationPathChooseButton = new Button(baseFolderGroup, SWT.BORDER);
        locationPathChooseButton.setText("...");
        locationPathChooseButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                DirectoryDialog fileDialog = new DirectoryDialog(locationPathChooseButton.getShell(), SWT.OPEN);
                String path = fileDialog.open();
                if (path != null)
                    path = path.replaceAll("\\s+", "_");
                if (isBaseFolderValid(path)) {
                    properties.basePath = path;
                    baseFolderText.setText(path);
                }
            }
        });

        // the location name group
        Group locationGroup = new Group(parent, SWT.None);
        locationGroup.setLayout(new GridLayout(2, false));
        locationGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        locationGroup.setText("enter a name for the new location");

        final Text locationText = new Text(locationGroup, SWT.BORDER);
        locationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
                | GridData.VERTICAL_ALIGN_CENTER));
        locationText.setText("newLocation");
        locationText.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                String locName = locationText.getText();
                if (isLocationNameValid(locName)) {
                    properties.locationName = locName;
                }
            }
        });

        // the mapset name group
        Group mapsetGroup = new Group(parent, SWT.None);
        mapsetGroup.setLayout(new GridLayout(2, false));
        mapsetGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
        mapsetGroup.setText("enter a name for the new mapset");

        final Text mapsetText = new Text(mapsetGroup, SWT.BORDER);
        mapsetText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
                | GridData.VERTICAL_ALIGN_CENTER));
        mapsetText.setText("newMapset");
        mapsetText.addModifyListener(new ModifyListener(){
            public void modifyText( ModifyEvent e ) {
                String mapsetName = mapsetText.getText();
                if (isMpasetNameValid(mapsetName)) {
                    properties.mapsetName = mapsetName;
                }
            }
        });

        setControl(parent);
    }

    private boolean isImportFileValid( String path ) {
        if (path != null && new File(path).exists() && (path.endsWith(".asc") || path.endsWith(".tif") || path.endsWith(".tiff"))) {
            setErrorMessage(null);
            canFinish(true);
            return true;
        } else {
            setErrorMessage("Only esrii ascii and geotiffs are supported.");
            canFinish(false);
            return false;
        }
    }

    private boolean isBaseFolderValid( String path ) {
        if (path != null && new File(path).exists()) {
            setErrorMessage(null);
            canFinish(true);
            return true;
        } else {
            setErrorMessage("The base folder is a needed parameter.");
            canFinish(false);
            return false;
        }
    }

    private boolean isLocationNameValid( String locationName ) {
        if (locationName != null && locationName.length() > 0) {
            setErrorMessage(null);
            canFinish(true);
            return true;
        } else {
            setErrorMessage("The location name is a needed parameter.");
            canFinish(false);
            return false;
        }
    }

    private boolean isMpasetNameValid( String mapsetName ) {
        if (mapsetName != null && mapsetName.length() > 0) {
            setErrorMessage(null);
            canFinish(true);
            return true;
        } else {
            setErrorMessage("The mapset name is a needed parameter.");
            canFinish(false);
            return false;
        }
    }

    private void canFinish( boolean doFinish ) {
        ((NewJGrassLocationFromFileWizard) getWizard()).canFinish = doFinish;
        getWizard().getContainer().updateButtons();
    }

    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            ((NewJGrassLocationFromFileWizard) getWizard()).canFinish = false;
            getWizard().getContainer().updateButtons();
        }
        super.setVisible(visible);
    }

}
