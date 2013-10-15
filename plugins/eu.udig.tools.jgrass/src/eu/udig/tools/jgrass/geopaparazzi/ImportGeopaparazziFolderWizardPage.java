/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.tools.jgrass.geopaparazzi;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import eu.udig.tools.jgrass.JGrassToolsPlugin;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ImportGeopaparazziFolderWizardPage extends WizardPage {

    protected DirectoryFieldEditor editor;
    private Text geopaparazziFolderText;
    private Text outputFolderText;

    private String geopaparazziFolderPath = null;
    private String outputFolderPath = null;

    public ImportGeopaparazziFolderWizardPage( String pageName, IStructuredSelection selection ) {
        super(pageName);
        setTitle(pageName); // NON-NLS-1
        setDescription("Import GeoPaparazzi folder into the workspace. The folder will import everything found in the folder: gpslogs, notes and pictures.");
        ImageDescriptor imageDescriptorFromPlugin = AbstractUIPlugin.imageDescriptorFromPlugin(JGrassToolsPlugin.PLUGIN_ID,
                "icons/geopaparazzi_small.png");
        setImageDescriptor(imageDescriptorFromPlugin);
    }

    public void createControl( Composite parent ) {
        final Composite fileSelectionArea = new Composite(parent, SWT.NONE);
        GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
        fileSelectionArea.setLayoutData(fileSelectionData);

        GridLayout fileSelectionLayout = new GridLayout();
        fileSelectionLayout.numColumns = 2;
        fileSelectionLayout.makeColumnsEqualWidth = false;
        fileSelectionLayout.marginWidth = 0;
        fileSelectionLayout.marginHeight = 0;
        fileSelectionArea.setLayout(fileSelectionLayout);

        // folder chooser
        geopaparazziFolderText = new Text(fileSelectionArea, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        geopaparazziFolderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        geopaparazziFolderText.setText("Select the geopaparazzi root folder");
        geopaparazziFolderText.setEditable(false);
        Button folderButton = new Button(fileSelectionArea, SWT.PUSH);
        folderButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        folderButton.setText("...");
        folderButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                DirectoryDialog directoryDialog = new DirectoryDialog(fileSelectionArea.getShell(), SWT.OPEN);
                directoryDialog.setText("Select geopaparazzi folder");
                geopaparazziFolderPath = directoryDialog.open();
                if (geopaparazziFolderPath == null || geopaparazziFolderPath.length() < 1) {
                    geopaparazziFolderText.setText("");
                } else {
                    geopaparazziFolderText.setText(geopaparazziFolderPath);
                }
            }
        });

        outputFolderText = new Text(fileSelectionArea, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        outputFolderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        outputFolderText.setText("Select the output folder");
        outputFolderText.setEditable(false);
        Button outputFolderButton = new Button(fileSelectionArea, SWT.PUSH);
        outputFolderButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        outputFolderButton.setText("...");
        outputFolderButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                DirectoryDialog directoryDialog = new DirectoryDialog(fileSelectionArea.getShell(), SWT.SAVE);
                directoryDialog.setText("Select output folder");
                outputFolderPath = directoryDialog.open();
                if (outputFolderPath == null || outputFolderPath.length() < 1) {
                    outputFolderText.setText("");
                } else {
                    outputFolderText.setText(outputFolderPath);
                }
            }
        });

        setControl(fileSelectionArea);
    }

    public String getGeopaparazziFolderPath() {
        return geopaparazziFolderPath;
    }

    public String getOutputFolderPath() {
        return outputFolderPath;
    }

}
