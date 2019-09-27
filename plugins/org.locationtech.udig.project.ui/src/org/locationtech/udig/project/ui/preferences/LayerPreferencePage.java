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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.internal.Messages;

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
