/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui;

import org.locationtech.udig.project.internal.ProjectPlugin;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page for all tools.
 * @author Jesse
 * @since 1.1.0
 */
public class ToolPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    
    public ToolPreferences(){
        super(GRID);
        setPreferenceStore(ProjectPlugin.getPlugin().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
    }

    public void init( IWorkbench workbench ) {
    }

}
