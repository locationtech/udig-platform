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
package net.refractions.udig.project.ui.preferences;

import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.preferences.PreferenceConstants;
import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for setting the preferences of maps.
 * @author Jesse
 * @since 1.1.0
 */
public class MapPreferencePage extends FieldEditorPreferencePage
        implements
            IWorkbenchPreferencePage {

    /**
     * @param style
     */
    public MapPreferencePage() {
        super(GRID);
        setPreferenceStore(ProjectPlugin.getPlugin().getPreferenceStore());
        setDescription(Messages.MapPreferencePage_map_preferences_description);  
    }

    @Override
    protected void createFieldEditors() {
        BooleanFieldEditor removeTempLayers = new BooleanFieldEditor(
                PreferenceConstants.P_REMOVE_LAYERS,
                Messages.MapPreferencePage_pref_remove_layer_label, 
                getFieldEditorParent());
        addField(removeTempLayers);
        addField(
                new BooleanFieldEditor(
                    PreferenceConstants.P_WARN_IRREVERSIBLE_COMMAND,
                    Messages.MapPreferencePage_warnIrreversible, 
                    getFieldEditorParent()));

        IntegerFieldEditor integerFieldEditor = new IntegerFieldEditor(
                PreferenceConstants.P_DEFAULT_CRS,
                Messages.MapPreferences_defaultCRS, 
                getFieldEditorParent(), 7);
        integerFieldEditor.setValidRange(-1, 9999999);
        integerFieldEditor.setErrorMessage(Messages.MapPreferences_errorMessage); 
        addField(integerFieldEditor);
        addField(new ColorFieldEditor(PreferenceConstants.P_BACKGROUND,
                Messages.MapPreferences_backgroundColor, 
                getFieldEditorParent()));
        addField(new ColorFieldEditor(PreferenceConstants.P_SELECTION_COLOR,
                Messages.MapPreferences_selectionColor, 
                getFieldEditorParent()));
        addField(new ColorFieldEditor(PreferenceConstants.P_SELECTION2_COLOR,
                Messages.MapPreferencePage_selectionColor2, 
                getFieldEditorParent()));
        PaletteSelectionFieldEditor defaultPalette = new PaletteSelectionFieldEditor(
                PreferenceConstants.P_DEFAULT_PALETTE,
                Messages.MapPreferences_defaultPalette, 
                getFieldEditorParent());
        addField(defaultPalette);
    }

    public void init( IWorkbench workbench ) {
    }

}
