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
package net.refractions.udig.tools.internal;

import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.preferences.PreferenceConstants;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page to set mouse preferences.
 * 
 * @author Jody Garnett (LISAsoft)
 */
public class NavigationToolPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    public static final String SCALE = "scale";
    public static final String TILED = "titled";

    private BooleanFieldEditor scale;

    private BooleanFieldEditor tiled;
    
    public NavigationToolPreferencePage() {
        super(GRID);
        IPreferenceStore store = ToolsPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setDescription("Navigation");
    }

    protected void createFieldEditors() {
        scale = new BooleanFieldEditor(SCALE, "Fixed Scale", getFieldEditorParent());
        addField(scale);
        tiled = new BooleanFieldEditor(TILED, "Tiled", getFieldEditorParent());
        addField(tiled);
    }

    public void init( IWorkbench workbench ) {
    }

}
