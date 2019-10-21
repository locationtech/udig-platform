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
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.project.preferences.PreferenceConstants;
import org.locationtech.udig.project.ui.internal.Messages;

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
