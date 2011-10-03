/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page to set mouse preferences.
 * 
 * @author Jody Garnett (LISAsoft)
 */
public class NavigationToolPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    public static final String SCALE = "scale"; //$NON-NLS-1$
    public static final String TILED = "titled"; //$NON-NLS-1$

    private BooleanFieldEditor scale;

    private BooleanFieldEditor tiled;
    
    public NavigationToolPreferencePage() {
        super(GRID);
        IPreferenceStore store = ToolsPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);
        setTitle(Messages.Navigation_Title);
        setDescription(Messages.Navigation_Description);
    }

    protected void createFieldEditors() {
        scale = new BooleanFieldEditor(SCALE, Messages.Navigation_Scale, getFieldEditorParent());
        addField(scale);
        tiled = new BooleanFieldEditor(TILED, Messages.Navigation_Tiled, getFieldEditorParent());
        addField(tiled);
    }

    public void init( IWorkbench workbench ) {
    }

}
