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
package net.refractions.udig.project.ui;

import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.project.ui.preferences.PreferenceConstants;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page to set mouse preferences.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class MousePreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private IntegerFieldEditor clickSpeedEditor;
    private IntegerFieldEditor longClickSpeedEditor;

    public MousePreferences() {
        super(GRID);
        IPreferenceStore store = ProjectUIPlugin.getDefault().getPreferenceStore();
        setPreferenceStore(store);

        int longClickMillis = store.getInt(PreferenceConstants.MOUSE_LONGCLICK_SPEED);
        if (longClickMillis == 0) {
            store.setValue(PreferenceConstants.MOUSE_LONGCLICK_SPEED, PreferenceConstants.DEFAULT_LONGCLICK_SPEED_MILLIS);
        }

        setDescription(Messages.mousePreferences_title);
    }

    protected void createFieldEditors() {
        clickSpeedEditor = new IntegerFieldEditor(PreferenceConstants.MOUSE_SPEED, Messages.mousePreferences_setvalue,
                getFieldEditorParent());
        addField(clickSpeedEditor);
        longClickSpeedEditor = new IntegerFieldEditor(PreferenceConstants.MOUSE_LONGCLICK_SPEED,
                Messages.mousePreferences_setlongclickvalue, getFieldEditorParent());
        addField(longClickSpeedEditor);
    }
    
    @Override
    protected void performApply() {
        savePrefs();
        super.performApply();
    }

    @Override
    public boolean performOk() {
        boolean performOk = super.performOk();
        if (performOk) {
            savePrefs();
        }
        return performOk;
    }

    private void savePrefs() {
        int milliseconds = clickSpeedEditor.getIntValue();
        getPreferenceStore().setValue(PreferenceConstants.MOUSE_SPEED, milliseconds);
        milliseconds = longClickSpeedEditor.getIntValue();
        getPreferenceStore().setValue(PreferenceConstants.MOUSE_LONGCLICK_SPEED, milliseconds);
    }

    public void init( IWorkbench workbench ) {
    }

}
