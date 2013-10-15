/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.catalog.kml.wizard;

import java.io.File;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ui.ApplicationGIS;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eu.udig.catalog.kml.core.KmlUtils;
import eu.udig.catalog.kml.internal.Messages;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 * @author Frank Gasdorf
 */
public class KmlExportWizardPage extends WizardPage {
    public static final String ID = "eu.udig.catalog.kml.wizard.KmlExportWizardPage"; //$NON-NLS-1$

    private IGeoResource geoResource;

    private String filePath;

    public KmlExportWizardPage() {
        super(ID);
        setTitle(Messages.getString("KmlExportWizardPage.windowTitle")); //$NON-NLS-1$
        setDescription(Messages.getString("KmlExportWizardPage.description")); //$NON-NLS-1$
    }

    public void createControl( Composite parent ) {

        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout());

        ILayer selectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
        geoResource = selectedLayer.getGeoResource();

        /*
         * layer selected
         */
        Label selectedLayerLabel = new Label(mainComposite, SWT.NONE);
        GridData selectedLayerLabelGd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        selectedLayerLabelGd.horizontalSpan = 3;
        selectedLayerLabel.setLayoutData(selectedLayerLabelGd);
        selectedLayerLabel.setText(Messages.getString("KmlExportWizardPage.selectedLayerToExportLabel") + selectedLayer.getName()); //$NON-NLS-1$

        Group inputGroup = new Group(mainComposite, SWT.None);
        inputGroup.setText(Messages.getString("KmlExportWizardPage.outFileLabel")); //$NON-NLS-1$
        inputGroup.setLayout(new GridLayout(2, false));
        inputGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData1.horizontalSpan = 2;

        final Text kmlText = new Text(inputGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        kmlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        kmlText.setText(""); //$NON-NLS-1$
        kmlText.setEditable(false);

        final Button outFolderButton = new Button(inputGroup, SWT.PUSH);
        outFolderButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        outFolderButton.setText(Messages.getString("KmlWizardPages.chooseFileButtonLabel")); //$NON-NLS-1$
        outFolderButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){

            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog saveKmlDialog = new FileDialog(outFolderButton.getShell(), SWT.SAVE);
                saveKmlDialog.setFilterExtensions(KmlUtils.SUPPORTED_FILE_EXTENSIONS);
                saveKmlDialog.setOverwrite(true);
                String path = saveKmlDialog.open();
                if (path == null || path.length() < 1) {
                    kmlText.setText(""); //$NON-NLS-1$
                } else {
                    kmlText.setText(path);
                    filePath = path;
                }
                checkFinish();
            }
        });

        setControl(mainComposite);

    }

    public String getFilePath() {
        return filePath;
    }

    public IGeoResource getGeoResource() {
        return geoResource;
    }

    public void dispose() {
        super.dispose();
    }

    private void checkFinish() {
        if (filePath == null) {
            KmlExportWizard.canFinish = false;
        } else {
            File file = new File(filePath);
            File parentFolder = file.getParentFile();
            if (parentFolder.isDirectory()) {
                KmlExportWizard.canFinish = true;
            } else {
                KmlExportWizard.canFinish = false;
            }
        }
        getWizard().getContainer().updateButtons();
    }

}
