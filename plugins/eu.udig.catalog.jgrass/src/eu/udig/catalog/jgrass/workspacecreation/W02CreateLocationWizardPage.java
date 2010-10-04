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
package eu.udig.catalog.jgrass.workspacecreation;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class W02CreateLocationWizardPage extends WizardPage {

    public static final String ID = "W02CreateLocationWizardPage"; //$NON-NLS-1$
    private final WorkspaceProperties properties;
    private Text nText;
    private Text sText;
    private Text wText;
    private Text eText;
    private Text xresText;
    private Text yresText;

    public W02CreateLocationWizardPage( WorkspaceProperties properties ) {
        super(ID);
        this.properties = properties;
        setTitle("Define the location's region");
        setDescription("In this page the user is asked to supply bounds and resolution for the new location.");
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl( Composite maxparent ) {
        Composite parent = new Composite(maxparent, SWT.None);
        parent.setLayout(new GridLayout(1, true));

        // the bounds group
        Group boundsGroup = new Group(parent, SWT.None);
        boundsGroup.setLayout(new GridLayout(2, true));
        boundsGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL));
        boundsGroup.setText("location bounds");

        Label nLabel = new Label(boundsGroup, SWT.None);
        nLabel.setText("north");
        nLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        nText = new Text(boundsGroup, SWT.BORDER);
        nText.setText("1000");
        nText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label sLabel = new Label(boundsGroup, SWT.None);
        sLabel.setText("south");
        sLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        sText = new Text(boundsGroup, SWT.BORDER);
        sText.setText("0");
        sText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label eLabel = new Label(boundsGroup, SWT.None);
        eLabel.setText("east");
        eLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        eText = new Text(boundsGroup, SWT.BORDER);
        eText.setText("1000");
        eText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label wLabel = new Label(boundsGroup, SWT.None);
        wLabel.setText("west");
        wLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        wText = new Text(boundsGroup, SWT.BORDER);
        wText.setText("0");
        wText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        // the resolution group
        Group resGroup = new Group(parent, SWT.None);
        resGroup.setLayout(new GridLayout(2, true));
        resGroup.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL));
        resGroup.setText("location resolution");

        Label xresLabel = new Label(resGroup, SWT.None);
        xresLabel.setText("x resolution");
        xresLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        xresText = new Text(resGroup, SWT.BORDER);
        xresText.setText("100");
        xresText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Label yresLabel = new Label(resGroup, SWT.None);
        yresLabel.setText("y resolution");
        yresLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        yresText = new Text(resGroup, SWT.BORDER);
        yresText.setText("100");
        yresText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        setControl(parent);
    }

    @Override
    public void setVisible( boolean visible ) {

        if (!visible) {
            properties.north = Double.parseDouble(nText.getText());
            properties.south = Double.parseDouble(sText.getText());
            properties.west = Double.parseDouble(wText.getText());
            properties.east = Double.parseDouble(eText.getText());
            properties.xres = Double.parseDouble(xresText.getText());
            properties.yres = Double.parseDouble(yresText.getText());
        } else {
            ((NewJGrassLocationWizard) getWizard()).canFinish = false;
            getWizard().getContainer().updateButtons();
        }

        super.setVisible(visible);
    }

}