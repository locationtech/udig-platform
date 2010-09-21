/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.preferences;

import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditPlugin;

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
        addField( new BooleanFieldEditor(net.refractions.udig.project.preferences.PreferenceConstants.P_SHOW_ANIMATIONS, 
                net.refractions.udig.project.ui.internal.Messages.RenderPreferences_animations,
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
