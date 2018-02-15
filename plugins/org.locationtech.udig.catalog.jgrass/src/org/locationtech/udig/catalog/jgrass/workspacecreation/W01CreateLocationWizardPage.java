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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.locationtech.udig.ui.ChooseCoordinateReferenceSystemDialog;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class W01CreateLocationWizardPage extends WizardPage {

    public static final String ID = "W01CreateLocationWizardPage"; //$NON-NLS-1$
    private final WorkspaceProperties properties;

    public W01CreateLocationWizardPage( WorkspaceProperties properties ) {
        super(ID);
        this.properties = properties;
        setTitle("Setting the location path and projection");
        setDescription("In this page the user is asked to supply the new location path and its coordinate reference system.");
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl( Composite maxparent ) {
        Composite parent = new Composite(maxparent, SWT.None);
        parent.setLayout(new GridLayout());

        // the location path group
        Group locPathGroup = new Group(parent, SWT.None);
        locPathGroup.setLayout(new GridLayout(2, false));
        locPathGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL));
        locPathGroup.setText("choose the path for the new location");

        final Text locPathText = new Text(locPathGroup, SWT.BORDER);
        locPathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_CENTER));
        locPathText.addKeyListener(new KeyAdapter(){
            public void keyPressed( KeyEvent e ) {
                String path = locPathText.getText();
                properties.locationPath = path;
            }
        });
        final Button locationPathChooseButton = new Button(locPathGroup, SWT.BORDER);
        locationPathChooseButton.setText("...");
        locationPathChooseButton
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
                    public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                        FileDialog fileDialog = new FileDialog(locationPathChooseButton.getShell(),
                                SWT.SAVE);
                        String path = fileDialog.open();

                        if (path == null || path.length() < 1) {
                            locPathText.setText("");
                        } else {
                            File locationFile = new File(path);
                            String locationName = locationFile.getName();
                            if (locationName.indexOf(' ') != -1) {
                                MessageBox msgBox = new MessageBox(locationPathChooseButton
                                        .getShell(), SWT.ICON_ERROR);
                                msgBox.setMessage("Location names can't contain spaces. Please choose a name without spaces.");
                                msgBox.open();
                                locPathText.setText("");
                                return;
                            }

                            locPathText.setText(path);
                            properties.locationPath = path;
                        }
                    }
                });

        // the crs choice group
        Group crsGroup = new Group(parent, SWT.None);
        crsGroup.setLayout(new GridLayout(2, false));
        crsGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL));
        crsGroup.setText("choose the coordinate reference system for the new location");

        final Text crsText = new Text(crsGroup, SWT.BORDER);
        crsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_CENTER));
        crsText.setEditable(false);

        final Button crsButton = new Button(crsGroup, SWT.BORDER);
        crsButton.setText(" Choose CRS ");
        crsButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                final ChooseCoordinateReferenceSystemDialog crsChooser = new ChooseCoordinateReferenceSystemDialog();
                crsChooser.open(new Shell(Display.getDefault()));
                CoordinateReferenceSystem readCrs = crsChooser.getCrs();
                if (readCrs == null)
                    return;
                properties.crs = readCrs;
                crsText.setText(readCrs.getName().toString());
            }
        });

        setControl(parent);
    }

    @Override
    public void setVisible( boolean visible ) {
        if (visible) {
            ((NewJGrassLocationWizard) getWizard()).canFinish = false;
            getWizard().getContainer().updateButtons();
        }
        super.setVisible(visible);
    }

}
