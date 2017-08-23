/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;

/**
 * Preference page for all tools.
 * @author Jesse
 * @since 1.1.0
 */
public class ToolPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    
    //attributes are usually utilized during modal tools actions
    private StringFieldEditor featureAttributeName;

    public ToolPreferences(){
        super(GRID);
        setPreferenceStore(ProjectUIPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        featureAttributeName = new StringFieldEditor(PreferenceConstants.FEATURE_ATTRIBUTE_NAME, Messages.Feature_Attribute_Name, getFieldEditorParent());
        addField(featureAttributeName);
        featureAttributeName.getLabelControl(getFieldEditorParent()).setToolTipText(Messages.Feature_Attribute_Name_tooltip);
    }

    public void init( IWorkbench workbench ) {
    }

}
