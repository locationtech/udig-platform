/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.ui.preferences;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into
 * JFace that allows us to create a page that is small and knows how to save, restore and apply
 * itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 */

public class UiPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public UiPreferences() {
        super(GRID);
        setPreferenceStore(UiPlugin.getDefault().getPreferenceStore());
        setDescription(Messages.UiPreferences_description);
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
     * manipulate various types of preferences. Each field editor knows how to save and restore
     * itself.
     */
    public void createFieldEditors() {
        addField(new CharSetFieldEditor(
                net.refractions.udig.ui.preferences.PreferenceConstants.P_DEFAULT_CHARSET,
                Messages.UiPreferences_charset, getFieldEditorParent()));

        //if (Platform.getOS().equals(Platform.OS_LINUX)) {
            addField(new BooleanFieldEditor(
                    net.refractions.udig.ui.preferences.PreferenceConstants.P_ADVANCED_GRAPHICS,
                    Messages.UiPreferences_advancedGraphics_label, getFieldEditorParent()));
        //}
           
            String[][] values = {{Messages.UiPreferences_AutoUnits, PreferenceConstants.AUTO_UNITS}, {Messages.UiPreferences_MetricUnits, PreferenceConstants.METRIC_UNITS}, {Messages.UiPreferences_ImperialUnits,PreferenceConstants.IMPERIAL_UNITS}};
            addField(new ComboFieldEditor(net.refractions.udig.ui.preferences.PreferenceConstants.P_DEFAULT_UNITS, Messages.UiPreferences_UnitsLabel, values, getFieldEditorParent()));

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init( IWorkbench workbench ) {
    }

}