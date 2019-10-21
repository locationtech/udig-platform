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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.preferences.PreferenceConstants;

/**
 * Preference page to set mouse preferences.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class MousePreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private IntegerFieldEditor clickSpeedEditor;

    public MousePreferences() {
        super(GRID);
        IPreferenceStore store = ProjectUIPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);

        setDescription(Messages.mousePreferences_title);
    }

    protected void createFieldEditors() {
        clickSpeedEditor = new IntegerFieldEditor(PreferenceConstants.MOUSE_SPEED,
                Messages.mousePreferences_setvalue, getFieldEditorParent());
        addField(clickSpeedEditor);
    }

    @Override
    public boolean performOk() {
        boolean performOk = super.performOk();
        if (performOk) {
            String stringValue = clickSpeedEditor.getStringValue();
            try {
                int milliseconds = Integer.parseInt(stringValue);
                getPreferenceStore().setValue(ProjectUIPlugin.MOUSE_SPEED_KEY, milliseconds);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return performOk;
    }

    public void init( IWorkbench workbench ) {
    }

}
