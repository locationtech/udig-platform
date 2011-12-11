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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import net.refractions.udig.project.IMap;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.util.ScaleConfigUtils;
import net.refractions.udig.project.preferences.PreferenceConstants;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
        ScaleListFieldEditor scaleListFieldEditor = new ScaleListFieldEditor(
                PreferenceConstants.P_DEFAULT_PREFERRED_SCALES,
                Messages.MapPreferencePage_defaultPreferredScales,
                getFieldEditorParent());
        addField(scaleListFieldEditor);
    }

    public void init( IWorkbench workbench ) {
    }

    protected class ScaleListFieldEditor extends ListEditor {

        protected ScaleListFieldEditor(String name, String labelText,
                Composite parent) {
            super(name, labelText, parent);
        }

        @Override
        protected String createList(String[] items) {
            SortedSet<Double> scales = new TreeSet<Double>();
            for (String item : items) {
                Double scale = ScaleConfigUtils.fromLabel(item);
                if (scale != null) {
                    scales.add(scale);
                }
            }

            return ScaleConfigUtils.toScaleDenominatorsPrefString(scales);
        }

        @Override
        protected String getNewInputObject() {
            String str = new String("");
            // TODO (fgdrf) use the same logic here like in ScaleRatioLabel
            InputDialog dialog = new InputDialog(Display.getCurrent()
                    .getActiveShell(), "New Scale",
                    "Enter the scale denominator", str, new IInputValidator() {

                        @Override
                        public String isValid(String newText) {
                            if (newText != null && newText.length() > 0) {
                                try {
                                    Double.parseDouble(newText);
                                } catch (NumberFormatException e) {
                                    return "'"
                                            + newText
                                            + "' is not a valid scale denominator";
                                }

                            } else {
                                return "please enter a valid scale denominator";
                            }

                            return null;
                        }
                    });
            int result = dialog.open();
            if (result == Window.OK) {
                str = dialog.getValue();
            }

            return ScaleConfigUtils.toLabel(Double.parseDouble(str));
        }

        @Override
        protected String[] parseString(String stringList) {
            List<String> scales = new ArrayList<String>();
            SortedSet<Double> scaleConfiguration = ScaleConfigUtils
                    .getScaleConfiguration(stringList);
            for (Double value : scaleConfiguration) {
                scales.add(ScaleConfigUtils.toLabel(value));
            }

            return scales.toArray(new String[] {});
        }
    }

    @Override
    public boolean performOk() {
        // call before applying
        boolean result = super.performOk();

        Collection<? extends IMap> openMaps = ApplicationGIS.getOpenMaps();
        if (openMaps != null && !openMaps.isEmpty()) {
            SortedSet<Double> scaleConfiguration = ScaleConfigUtils
                    .getScaleDenominatorsFromPreferences();
            for (IMap map : openMaps) {
                if (map instanceof Map) {
                    ScaleConfigUtils.setPreferredScales((Map) map,
                            scaleConfiguration);
                }
            }
        }

        return result;

    }
}
