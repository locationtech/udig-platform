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
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences for Project elements
 * @author Jesse
 * @since 1.1.0
 */
public class ProjectPreferencePage extends FieldEditorPreferencePage
        implements
            IWorkbenchPreferencePage {

    public ProjectPreferencePage(){
        super(GRID);
        setPreferenceStore(ProjectPlugin.getPlugin().getPreferenceStore());
    }
    
    @Override
    protected void createFieldEditors() {
        BooleanFieldEditor deleteProjectFiles = new BooleanFieldEditor(
                PreferenceConstants.P_PROJECT_DELETE_FILES, 
                Messages.ProjectPreferencePage_deleteFiles,
                getFieldEditorParent());
        addField(deleteProjectFiles);
        IntegerFieldEditor maxUndo = new IntegerFieldEditor(
                PreferenceConstants.P_MAX_UNDO, 
                Messages.ProjectPreferencePage_maxundo,
                getFieldEditorParent());
        addField(maxUndo);
    }

    public void init( IWorkbench workbench ) {
    }

}
