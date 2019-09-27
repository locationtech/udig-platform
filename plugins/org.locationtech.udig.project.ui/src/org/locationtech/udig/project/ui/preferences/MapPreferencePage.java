/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.internal.Messages;

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
