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
