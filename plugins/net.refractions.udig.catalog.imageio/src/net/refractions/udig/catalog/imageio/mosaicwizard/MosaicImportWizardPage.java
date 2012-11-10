/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package net.refractions.udig.catalog.imageio.mosaicwizard;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class MosaicImportWizardPage extends WizardPage {

    public static final String ID = "MosaicImportWizardPage"; //$NON-NLS-1$
    private File folderFile = null;

    public MosaicImportWizardPage( String pageName ) {
        super(ID);
        setTitle(pageName);
        setDescription("Import a folder of imagery files as mosaic");
    }

    public void createControl( Composite parent ) {
        Composite fileSelectionArea = new Composite(parent, SWT.NONE);
        fileSelectionArea.setLayout(new GridLayout());

        Group inputGroup = new Group(fileSelectionArea, SWT.None);
        inputGroup.setText("Choose the folder containing the imagery to mosaic");
        inputGroup.setLayout(new GridLayout(2, false));
        inputGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData1.horizontalSpan = 2;

        final Text imageFolderText = new Text(inputGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        imageFolderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        imageFolderText.setText("");
        final Button imageFolderButton = new Button(inputGroup, SWT.PUSH);
        imageFolderButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        imageFolderButton.setText("...");
        imageFolderButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                DirectoryDialog directoryDialog = new DirectoryDialog(imageFolderButton.getShell(), SWT.OPEN);
                String path = directoryDialog.open();
                if (path != null) {
                    File f = new File(path);
                    if (f.exists()) {
                        imageFolderText.setText(path);
                        folderFile = f;
                    } 
                }
                checkFinish();
            }
        });

        setControl(fileSelectionArea);
    }

    public void dispose() {
    }

    public File getImageryFolder() {
        return folderFile;
    }

    private void checkFinish() {
        if (folderFile != null) {
            if (folderFile.exists() && folderFile.isDirectory()) {
                MosaicImportWizard.canFinish = true;
            }else{
                MosaicImportWizard.canFinish = false;
            }
        } else {
            MosaicImportWizard.canFinish = false;
        }
        getWizard().getContainer().updateButtons();
    }

}
