/*
 * (C) HydroloGIS - www.hydrologis.com 
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
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
package eu.udig.catalog.kml.wizard;

import java.io.File;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import eu.udig.catalog.kml.core.KmlUtils;
import eu.udig.catalog.kml.internal.Messages;

/**
 * @author Andrea Antonello - www.hydrologis.com
 * @author Frank Gasdorf
 */
public class KmlImportWizardPage extends WizardPage {

    public static final String ID = "eu.udig.catalog.kml.wizard.KmlImportWizardPage"; //$NON-NLS-1$
    private File inFile = null;

    private boolean inIsOk = false;

    public KmlImportWizardPage( String pageName, Map<String, String> params ) {
        super(ID);
        setTitle(pageName);
        setDescription(Messages.getString("KmlImportWizardPage.description")); // NON-NLS-1 //$NON-NLS-1$
    }

    public void createControl( Composite parent ) {
        Composite fileSelectionArea = new Composite(parent, SWT.NONE);
        fileSelectionArea.setLayout(new GridLayout());

        Group inputGroup = new Group(fileSelectionArea, SWT.None);
        inputGroup.setText(Messages.getString("KmlImportWizardPage.chooseFileTextLabel")); //$NON-NLS-1$
        inputGroup.setLayout(new GridLayout(2, false));
        inputGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
        gridData1.horizontalSpan = 2;

        final Text kmlText = new Text(inputGroup, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        kmlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        kmlText.setText(""); //$NON-NLS-1$
        final Button kmlButton = new Button(inputGroup, SWT.PUSH);
        kmlButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        kmlButton.setText(Messages.getString("KmlWizardPages.chooseFileButtonLabel")); //$NON-NLS-1$
        kmlButton.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter(){
            public void widgetSelected( org.eclipse.swt.events.SelectionEvent e ) {
                FileDialog fileDialog = new FileDialog(kmlButton.getShell(), SWT.OPEN);
                fileDialog.setFilterExtensions(KmlUtils.SUPPORTED_FILE_EXTENSIONS);
                String path = fileDialog.open();
                if (path != null) {
                    File f = new File(path);
                    if (f.exists()) {
                        inIsOk = true;
                        kmlText.setText(path);
                        inFile = f;
                    } else {
                        inIsOk = false;
                    }
                }
                checkFinish();
            }
        });

        setControl(fileSelectionArea);
    }

    public void dispose() {
    }

    public File getKmlFile() {
        return inFile;
    }

    private void checkFinish() {
        if (inIsOk) {
            KmlImportWizard.canFinish = true;
        } else {
            KmlImportWizard.canFinish = false;
        }
        getWizard().getContainer().updateButtons();
    }

}
