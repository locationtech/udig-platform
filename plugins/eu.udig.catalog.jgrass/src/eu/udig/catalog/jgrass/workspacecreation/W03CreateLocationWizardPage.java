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

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import eu.hydrologis.jgrass.libs.utils.JGrassConstants;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class W03CreateLocationWizardPage extends WizardPage {

    public static final String ID = "W03CreateLocationWizardPage"; //$NON-NLS-1$
    private final WorkspaceProperties properties;
    private TableViewer lv;
    private List<String> mapsetNames = null;

    public W03CreateLocationWizardPage( WorkspaceProperties properties ) {
        super(ID);
        this.properties = properties;
        setTitle("Add mapsets to the location");
        setDescription("In this page the user is asked to supply additional mapsets to be used in the location.");
        mapsetNames = properties.mapsets;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl( Composite maxparent ) {
        Composite parent = new Composite(maxparent, SWT.None);
        parent.setLayout(new GridLayout(2, false));

        // insert names
        final Text newMapseText = new Text(parent, SWT.BORDER);
        newMapseText
                .setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Button addButton = new Button(parent, SWT.PUSH);
        addButton.setText("  add  ");
        addButton.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                String mapset = newMapseText.getText();
                if (mapset.length() > 0) {
                    if (mapset.indexOf(' ') != -1) {
                        MessageBox msgBox = new MessageBox(newMapseText.getShell(), SWT.ICON_ERROR);
                        msgBox
                                .setMessage("Mapset names can't contain spaces. Please choose a name without spaces.");
                        msgBox.open();
                        return;
                    }

                    mapsetNames.add(mapset);
                    lv.setInput(mapsetNames);
                    newMapseText.setText("");
                    if (mapsetNames.size() > 0) {
                        canDoFinish(true);
                    } else {
                        canDoFinish(false);
                    }
                }
            }
        });

        lv = new TableViewer(parent);
        GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL
                | GridData.GRAB_VERTICAL);
        gd.horizontalSpan = 2;
        lv.getControl().setLayoutData(gd);
        lv.setContentProvider(new ArrayContentProvider());
        lv.setLabelProvider(new LabelProvider());
        lv.setInput(mapsetNames);

        // add a popup
        MenuManager popManager = new MenuManager();
        Menu menu = popManager.createContextMenu(lv.getTable());
        lv.getTable().setMenu(menu);
        // clear all entries
        IAction clearAction = new Action(){
            public void run() {
                mapsetNames.removeAll(mapsetNames);
                mapsetNames.add(JGrassConstants.PERMANENT_MAPSET);
                lv.setInput(mapsetNames);
                canDoFinish(false);
            }
        };
        clearAction.setText("Clear all entries");
        popManager.add(clearAction);
        // clear all entries
        IAction deleteSelectedAction = new Action(){
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) lv.getSelection();
                String sel = (String) selection.getFirstElement();
                // remove from table
                if (sel.equals(JGrassConstants.PERMANENT_MAPSET))
                    return;
                mapsetNames.remove(sel);
                lv.setInput(mapsetNames);
                if (mapsetNames.size() > 0) {
                    canDoFinish(true);
                } else {
                    canDoFinish(false);
                }
            }
        };
        deleteSelectedAction.setText("Clear selected entries");
        popManager.add(deleteSelectedAction);

        setControl(parent);
    }

    @Override
    public void setVisible( boolean visible ) {

        if (visible) {
            canDoFinish(false);
        }

        super.setVisible(visible);
    }

    private void canDoFinish( boolean canDoFinish ) {
        ((NewJGrassLocationWizard) getWizard()).canFinish = canDoFinish;
        getWizard().getContainer().updateButtons();
    }

}