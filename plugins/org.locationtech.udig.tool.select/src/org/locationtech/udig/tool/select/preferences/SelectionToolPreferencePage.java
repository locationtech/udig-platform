/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2011, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tool.select.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;
import org.locationtech.udig.tool.select.SelectPlugin;
import org.locationtech.udig.tool.select.internal.Messages;

/**
 * This  preference page provides access to all preference relating to selection tools.
 * 
 * @author leviputna
 * @since 1.3.0
 */
public class SelectionToolPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    
    public static final String ZOOM_TO_SELECTION = "aoiZoomToSelection"; //$NON-NLS-1$
    public static final String NAVIGATE_SELECTION = "aoiNavigateSelection"; //$NON-NLS-1$

//    private BooleanFieldEditor zoomToSelection;
    private BooleanFieldEditor navigateSelection;
    private IntegerFieldEditor featureSelectionRadius;

    public SelectionToolPreferencePage() {
        super(GRID);
        IPreferenceStore store = SelectPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setTitle(Messages.Select_Title);
        setDescription(Messages.Select_Description);
    }

    @Override
    protected Control createContents(Composite parent) {
        // create a link to general tooling preferences page
        PreferenceLinkArea preferenceLinkArea = new PreferenceLinkArea(parent, SWT.WRAP | SWT.MULTI,
                "org.locationtech.udig.project.ui.preferences.tool",
                org.locationtech.udig.project.ui.internal.Messages.PREFERENCES_LINK_TO_GENERAL_PAGE,
                (IWorkbenchPreferenceContainer) getContainer(), null);
        preferenceLinkArea.getControl()
                .setLayoutData(GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).create());
        return super.createContents(parent);
    }

    @Override
    protected void createFieldEditors() {
        // general Selection preferences
        featureSelectionRadius = new IntegerFieldEditor(PreferenceConstants.FEATURE_SELECTION_SCALEFACTOR, Messages.Feature_Selection_Radius, getFieldEditorParent());
        addField(featureSelectionRadius);
        featureSelectionRadius.getLabelControl(getFieldEditorParent()).setToolTipText(Messages.Feature_Selection_Radius_tooltip);

//      zoomToSelection = new BooleanFieldEditor(ZOOM_TO_SELECTION, Messages.Zoom_To_Selection, getFieldEditorParent());
//      addField(zoomToSelection);

        // Area of interest (AOI) preferences
        Label aoiSelection = new Label (getFieldEditorParent(), SWT.HORIZONTAL | SWT.BOLD | SWT.TITLE);
        aoiSelection.setText(Messages.Group_AOI);
        aoiSelection.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));

        navigateSelection = new BooleanFieldEditor(NAVIGATE_SELECTION, Messages.Navigate_Selection, getFieldEditorParent());
        addField(navigateSelection);

    }

    @Override
    public void init( IWorkbench workbench ) {
    }
}