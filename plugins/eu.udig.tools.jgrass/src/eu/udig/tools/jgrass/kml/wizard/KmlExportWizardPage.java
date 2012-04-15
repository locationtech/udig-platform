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
package eu.udig.tools.jgrass.kml.wizard;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class KmlExportWizardPage extends WizardPage {
    public static final String ID = "eu.udig.tools.jgrass.kml.wizard.KmlExportWizardPage"; //$NON-NLS-1$

    private Text outFileText;
    private IGeoResource geoResource;

    private String filePath;

    public KmlExportWizardPage() {
        super(ID);
        setTitle("Export kml");
        setDescription("Export feature layer to kml");
    }

    public void createControl( Composite parent ) {

        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.verticalSpacing = 10;
        mainComposite.setLayout(gridLayout);

        ILayer selectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
        geoResource = selectedLayer.getGeoResource();

        /*
         * layer selected
         */
        Label selectedLayerLabel = new Label(mainComposite, SWT.NONE);
        GridData selectedLayerLabelGd = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        selectedLayerLabelGd.horizontalSpan = 3;
        selectedLayerLabel.setLayoutData(selectedLayerLabelGd);
        selectedLayerLabel.setText("Selected layer to export: " + selectedLayer.getName());

        /*
         * output file
         */
        Label outFileLabel = new Label(mainComposite, SWT.NONE);
        outFileLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        outFileLabel.setText("Kml file to save");

        outFileText = new Text(mainComposite, SWT.BORDER);
        outFileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        outFileText.setEditable(false);

        final Button outFolderButton = new Button(mainComposite, SWT.PUSH);
        outFolderButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        outFolderButton.setText("..."); //$NON-NLS-1$
        outFolderButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){

            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog saveKmlDialog = new FileDialog(outFolderButton.getShell(), SWT.SAVE);
                saveKmlDialog.setFilterExtensions(new String[]{"*.kml"}); //$NON-NLS-1$
                String path = saveKmlDialog.open();
                if (path == null || path.length() < 1) {
                    outFileText.setText(""); //$NON-NLS-1$
                } else {
                    outFileText.setText(path);
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
