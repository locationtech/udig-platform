/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package eu.udig.catalog.jgrass.activeregion.dialogs;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import eu.udig.catalog.jgrass.messages.Messages;

public abstract class AbstractChooserDialog extends SelectionAdapter {

    private Button visibleLayersRadioButton = null;
    private Label visibleLayersLabel = null;
    private Button catalogLayersRadioButton = null;
    private Label catalogLayersLabel = null;
    private static final String CATALOGTYPE = "catalogtype"; //$NON-NLS-1$
    protected final String VISIBLELAYERTYPE = "visiblelayertype"; //$NON-NLS-1$
    protected boolean isCatalogType = false;
    protected Composite checkArea;
    protected Composite parentPanel;

    public AbstractChooserDialog() {
        super();
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
    }

    /**
     * Open the dialog
     * 
     * @param parentShell
     * @param selectionType
     */
    public abstract void open( Shell parentShell, final int selectionType );

    protected void makeCheckPanel() {
        checkArea = new Composite(parentPanel, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        visibleLayersRadioButton = new Button(checkArea, SWT.RADIO);
        visibleLayersLabel = new Label(checkArea, SWT.NONE);
        visibleLayersLabel.setToolTipText(Messages.getString("AbstractChooserDialog.visualizevisiblelayers")); //$NON-NLS-1$
        visibleLayersLabel.setText(Messages.getString("AbstractChooserDialog.choosevisible")); //$NON-NLS-1$
        catalogLayersRadioButton = new Button(checkArea, SWT.RADIO);
        catalogLayersLabel = new Label(checkArea, SWT.NONE);
        catalogLayersLabel.setText(Messages.getString("AbstractChooserDialog.choosecatalog")); //$NON-NLS-1$
        catalogLayersLabel.setToolTipText(Messages.getString("AbstractChooserDialog.visualizecatalog")); //$NON-NLS-1$
        checkArea.setLayout(gridLayout);
        visibleLayersRadioButton.addSelectionListener(this);
        visibleLayersRadioButton.setData("type", VISIBLELAYERTYPE); //$NON-NLS-1$
        catalogLayersRadioButton.addSelectionListener(this);
        catalogLayersRadioButton.setData("type", CATALOGTYPE); //$NON-NLS-1$

        if (isCatalogType) {
            catalogLayersRadioButton.setSelection(true);
            visibleLayersRadioButton.setSelection(false);
        } else {
            catalogLayersRadioButton.setSelection(false);
            visibleLayersRadioButton.setSelection(true);
        }

    }

    /**
     * @return the list of selected resources in the dialog
     */
    public abstract List< ? > getSelectedResources();

    /**
     * Get the name of the resource at the index position of the resources list.
     * 
     * @param index the index of the resource to use
     * @return the nameof the selected resource
     */
    public abstract String getNameOfResourceAtIndex( int index );

}