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

import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences for setting optional preferences that impact the performance of editing.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class PerformancelPreferences extends FieldEditorPreferencePage
        implements
            IWorkbenchPreferencePage {

    public PerformancelPreferences(  ) {
        super(GRID);
        IPreferenceStore store = EditPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription(Messages.PerformancelPreferences_description);
    }

    @Override
    protected void createFieldEditors() {
        addField( new BooleanFieldEditor(PreferenceConstants.P_FILL_POLYGONS, 
                Messages.PerformancelPreferences_fill_polygons,
                getFieldEditorParent()));
        addField( new BooleanFieldEditor(PreferenceConstants.P_HIDE_SELECTED_FEATURES, 
                Messages.PerformancelPreferences_hide_features,
                getFieldEditorParent()));
        addField( new BooleanFieldEditor(org.locationtech.udig.project.preferences.PreferenceConstants.P_SHOW_ANIMATIONS, 
                org.locationtech.udig.project.ui.internal.Messages.RenderPreferences_animations,
                getFieldEditorParent()){
            
            @Override
            public IPreferenceStore getPreferenceStore() {
                return ProjectPlugin.getPlugin().getPreferenceStore();
            }
        });
    }

    public void init( IWorkbench workbench ) {
    }

}
