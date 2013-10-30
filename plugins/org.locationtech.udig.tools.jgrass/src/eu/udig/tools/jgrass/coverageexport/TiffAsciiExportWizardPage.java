/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.tools.jgrass.coverageexport;

import java.io.IOException;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.ui.CRSChooserDialog;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class TiffAsciiExportWizardPage extends WizardPage {
    public static final String ID = "org.locationtech.udig.tools.jgrass.rasterexport.TiffAsciiExportWizardPage";

    private Text outFolderText;
    private Text crsText;
    private CoordinateReferenceSystem fileCrs;
    private IGeoResource geoResource;

    private CoordinateReferenceSystem newCrs;
    private String folderPath;
    private String fileName;
    private boolean isTiff = true;
    private boolean isAscii = false;
    
    private ControlDecoration cdFolder;
    private ControlDecoration cdFile;
    private ControlDecoration cdCrs;
    

    public TiffAsciiExportWizardPage() {
        super(ID);
        setTitle("Export coverage map");
        setDescription("Export coverage map to geotiff or ascii grid");
    }

    public void createControl( Composite parent ) {

        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.verticalSpacing = 10;
        mainComposite.setLayout(gridLayout);

        ILayer selectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
        geoResource = selectedLayer.getGeoResource();
        try {
            fileCrs = geoResource.getInfo(new NullProgressMonitor()).getCRS();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        /*
         * layer selected
         */
        Label selectedLayerLabel = new Label(mainComposite, SWT.NONE);
        GridData selectedLayerLabelGd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        selectedLayerLabelGd.horizontalSpan = 1;
        selectedLayerLabel.setLayoutData(selectedLayerLabelGd);
        selectedLayerLabel.setText("Selected Layer to Export: ");
        
        Label lbl = new Label(mainComposite, SWT.NONE);
        GridData gd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        gd.horizontalSpan = 2;
        lbl.setLayoutData(gd);
        lbl.setText(geoResource.getTitle());

        /*
         * output folder
         */
        Label outFolderLabel = new Label(mainComposite, SWT.NONE);
        outFolderLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        outFolderLabel.setText("Output Folder:");
        outFolderLabel.setToolTipText("Folder to which to save the file to");

        outFolderText = new Text(mainComposite, SWT.BORDER);
        outFolderText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        outFolderText.setEditable(false);
        cdFolder = createControlDecoration(outFolderText);
        
        final Button outFolderButton = new Button(mainComposite, SWT.PUSH);
        outFolderButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        outFolderButton.setText("..."); //$NON-NLS-1$
        outFolderButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                DirectoryDialog folderDialog = new DirectoryDialog(outFolderButton.getShell(), SWT.OPEN);
                String path = folderDialog.open();
                if (path == null || path.length() < 1) {
                    outFolderText.setText(""); //$NON-NLS-1$
                    folderPath = null;
                } else {
                    outFolderText.setText(path);
                    folderPath = path;
                }
                validate();
            }
        });
        
        /*
         * output file name
         */
        Label outputNameLabel = new Label(mainComposite, SWT.NONE);
        outputNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        outputNameLabel.setText("Output File Name (without extension):");

        final Text outputNameText = new Text(mainComposite, SWT.BORDER);
        outputNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        outputNameText.addKeyListener(new KeyAdapter(){
            public void keyReleased( KeyEvent e ) {
                fileName = outputNameText.getText();
                validate();
            }
        });
        cdFile = createControlDecoration(outputNameText);
        
        Label dummyLabel = new Label(mainComposite, SWT.NONE);
        dummyLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        dummyLabel.setText("");

        /*
         * crs to which to reproject
         */
        Label crsLabel = new Label(mainComposite, SWT.NONE);
        crsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        crsLabel.setText("Crs (optional):");
        crsLabel.setToolTipText("Crs to which to reproject (optional):");

        crsText = new Text(mainComposite, SWT.BORDER);
        crsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        crsText.setEditable(false);
        cdCrs = createControlDecoration(crsText);
        
        final Button crsButton = new Button(mainComposite, SWT.PUSH);
        crsButton.setText("...");
        crsButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                CoordinateReferenceSystem crs = ApplicationGIS.getActiveMap().getViewportModel().getCRS();
                CRSChooserDialog dialog = new CRSChooserDialog(crsButton.getShell(), crs);
                int code = dialog.open();
                if (Window.OK == code) {
                    CoordinateReferenceSystem result = dialog.getResult();
                    crsText.setText(result.getName().toString());
                    crsText.setData(result);
                    newCrs = result;
                }
                validate();
            }
        });

        lbl = new Label(mainComposite, SWT.NONE);
        lbl.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));
        lbl.setText("Output Format:");

        
        /*
         * output type
         */
        Composite opFormatComp = new Composite(mainComposite, SWT.NONE);
        opFormatComp.setLayout(new GridLayout());
        opFormatComp.setLayoutData(new GridData(SWT.FILL,SWT.FILL, true, false));
        ((GridData)opFormatComp.getLayoutData()).horizontalSpan = 2;
        
        final Button tiffRadioButton = new Button(opFormatComp, SWT.RADIO);
        GridData tiffRadioGd = new GridData(SWT.BEGINNING, SWT.CENTER, true, false);
        tiffRadioButton.setLayoutData(tiffRadioGd);
        tiffRadioButton.setText("export as geotiff");
        tiffRadioButton.setSelection(true);
        tiffRadioButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                isTiff = tiffRadioButton.getSelection();
                validate();
            }
        });

        final Button asciiRadioButton = new Button(opFormatComp, SWT.RADIO);
        asciiRadioButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));
        asciiRadioButton.setText("export as esri ascii");
        asciiRadioButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                isAscii = asciiRadioButton.getSelection();
                validate();
            }
        });

        setControl(mainComposite);
        validate();
    }

    private ControlDecoration createControlDecoration(Control parent){
    	ControlDecoration dec = new ControlDecoration(parent, SWT.TOP | SWT.LEFT);
    	dec.setImage(PlatformUI.getWorkbench().
    	getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
    	dec.hide();
    	return dec;
    }
    
	private void validate() {
		String errorMessage = null;
		
		boolean show = false;
		if (isAscii) {
			// check that it is not lat long
			if (fileCrs instanceof DefaultGeographicCRS) {
				Object crsData = crsText.getData();
				if (crsData == null || crsData instanceof DefaultGeographicCRS) {
					errorMessage = "In the case of export to ascii grids, it is necessary to choose a non angular coordinate system.";
					cdCrs.setDescriptionText(errorMessage);
					show = true;
				}
			}
		}
		if (show){
			cdCrs.show();
		}else{
			cdCrs.hide();
		}

		if (fileName == null || fileName.length() <= 0) {
			errorMessage = "Invalid file name.";
			cdFile.setDescriptionText(errorMessage);
			cdFile.show();
		}else{
			cdFile.hide();
		}

		if (folderPath == null) {
			errorMessage = "Invalid output folder.";
			cdFolder.setDescriptionText(errorMessage);
			cdFolder.show();
		}else{
			cdFolder.hide();
		}

		TiffAsciiExportWizard.canFinish = (errorMessage == null);
		getWizard().getContainer().updateButtons();
	}
    
    public CoordinateReferenceSystem getNewCrs() {
        return newCrs;
    }

    public CoordinateReferenceSystem getFileCrs() {
        return fileCrs;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public String getFileName() {
        return fileName;
    }
    public boolean isAscii() {
        return isAscii;
    }

    public boolean isTiff() {
        return isTiff;
    }

    public IGeoResource getGeoResource() {
        return geoResource;
    }

    public void dispose() {
        super.dispose();
    }


}
