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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences for Layer elements
 * @author Jesse
 * @since 1.1.0
 */
public class LayerPreferencePage extends FieldEditorPreferencePage
        implements
            IWorkbenchPreferencePage {

    public LayerPreferencePage(){
        super(GRID);
        setPreferenceStore(ProjectPlugin.getPlugin().getPreferenceStore());
    }
    
    @Override
    protected void createFieldEditors() {
        RadioGroupFieldEditor layerSelectionColours = new RadioGroupFieldEditor(
                   PreferenceConstants.P_HIGHLIGHT, Messages.LayerPreferences_highlight, 1, 
                   new String[][] {
                           {Messages.LayerPreferences_none, PreferenceConstants.P_HIGHLIGHT_NONE}, 
                           {Messages.LayerPreferences_foreground, PreferenceConstants.P_HIGHLIGHT_FOREGROUND}, 
                           {Messages.LayerPreferences_background, PreferenceConstants.P_HIGHLIGHT_BACKGROUND} 
                   },
                   getFieldEditorParent(),
                   true);
        addField(layerSelectionColours);
    }

    public void init( IWorkbench workbench ) {
    }

}
