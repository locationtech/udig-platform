/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditPlugin;

/**
 * Preference page for all tools.
 * @author Jesse
 * @since 1.1.0
 */
public class DeleteToolPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    
    private IntegerFieldEditor featureDeleteRadius;
    
    public DeleteToolPreferences(){
        super(GRID);
        IPreferenceStore store = EditPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription(Messages.DeleteToolPreferences_description);
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
    
        featureDeleteRadius = new IntegerFieldEditor(PreferenceConstants.P_DELETE_TOOL_SEARCH_SCALEFACTOR, Messages.DeleteToolPreferences_Delete_Radius, getFieldEditorParent());
        addField(featureDeleteRadius);
        featureDeleteRadius.getLabelControl(getFieldEditorParent()).setToolTipText(Messages.DeleteToolPreferences_Delete_Radius_tooltip);
        addField(new BooleanFieldEditor(PreferenceConstants.P_DELETE_TOOL_CONFIRM, Messages.DeleteTool_confirmation_title,
                getFieldEditorParent()));
    }

    public void init( IWorkbench workbench ) {
    }

}
