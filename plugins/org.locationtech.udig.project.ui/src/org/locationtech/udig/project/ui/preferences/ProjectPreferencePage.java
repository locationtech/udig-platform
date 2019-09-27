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
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.internal.Messages;

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
