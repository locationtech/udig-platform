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
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences for Style elements
 * @author chorner
 * @since 1.1.0
 */
public class StylePreferencePage extends FieldEditorPreferencePage
        implements
            IWorkbenchPreferencePage {

    public StylePreferencePage(){
        super(GRID);
        setPreferenceStore(ProjectPlugin.getPlugin().getPreferenceStore());
    }
    
    @Override
    protected void createFieldEditors() {
        IntegerFieldEditor defaultPerpOffset = new IntegerFieldEditor(PreferenceConstants.P_STYLE_DEFAULT_PERPENDICULAR_OFFSET, Messages.StylePreferencePage_perpendicularOffset, getFieldEditorParent(), 3); 
        addField(defaultPerpOffset);
    }

    public void init( IWorkbench workbench ) {
    }

}
