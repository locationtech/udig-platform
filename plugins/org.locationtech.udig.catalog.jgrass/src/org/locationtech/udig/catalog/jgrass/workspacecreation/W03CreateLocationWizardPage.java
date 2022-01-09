/**
 * JGrass - Free Open Source Java GIS http://www.jgrass.org
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.workspacecreation;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
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
import org.geotools.gce.grassraster.JGrassConstants;

/**
 * @author Andrea Antonello - www.hydrologis.com
 */
public class W03CreateLocationWizardPage extends WizardPage {

    public static final String ID = "W03CreateLocationWizardPage"; //$NON-NLS-1$

    private final WorkspaceProperties properties;

    private TableViewer lv;

    private List<String> mapsetNames = null;

    public W03CreateLocationWizardPage(WorkspaceProperties properties) {
        super(ID);
        this.properties = properties;
        setTitle("Add mapsets to the location");
        setDescription(
                "In this page the user is asked to supply additional mapsets to be used in the location.");
        mapsetNames = properties.mapsets;
    }

    @Override
    public void createControl(Composite maxparent) {
        Composite parent = new Composite(maxparent, SWT.None);
        parent.setLayout(new GridLayout(2, false));

        // insert names
        final Text newMapseText = new Text(parent, SWT.BORDER);
        newMapseText
                .setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
        Button addButton = new Button(parent, SWT.PUSH);
        addButton.setText("  add  ");
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String mapset = newMapseText.getText();
                if (mapset.length() > 0) {
                    if (mapset.indexOf(' ') != -1) {
                        MessageBox msgBox = new MessageBox(newMapseText.getShell(), SWT.ICON_ERROR);
                        msgBox.setMessage(
                                "Mapset names can't contain spaces. Please choose a name without spaces.");
                        msgBox.open();
                        return;
                    }

                    mapsetNames.add(mapset);
                    lv.setInput(mapsetNames);
                    newMapseText.setText("");
                    if (!mapsetNames.isEmpty()) {
                        canDoFinish(true);
                    } else {
                        canDoFinish(false);
                    }
                }
            }
        });

        lv = new TableViewer(parent);
        GridData gd = new GridData(
                GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
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
        IAction clearAction = new Action() {
            @Override
            public void run() {
                mapsetNames.clear();
                mapsetNames.add(JGrassConstants.PERMANENT_MAPSET);
                lv.setInput(mapsetNames);
                canDoFinish(false);
            }
        };
        clearAction.setText("Clear all entries");
        popManager.add(clearAction);
        // clear all entries
        IAction deleteSelectedAction = new Action() {
            @Override
            public void run() {
                IStructuredSelection selection = (IStructuredSelection) lv.getSelection();
                String sel = (String) selection.getFirstElement();
                // remove from table
                if (sel.equals(JGrassConstants.PERMANENT_MAPSET))
                    return;
                mapsetNames.remove(sel);
                lv.setInput(mapsetNames);
                if (!mapsetNames.isEmpty()) {
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
    public void setVisible(boolean visible) {

        if (visible) {
            canDoFinish(false);
        }

        super.setVisible(visible);
    }

    private void canDoFinish(boolean canDoFinish) {
        ((NewJGrassLocationWizard) getWizard()).canFinish = canDoFinish;
        getWizard().getContainer().updateButtons();
    }

}
