package net.refractions.udig.ui.preferences;

import net.refractions.udig.internal.ui.UiPlugin;
import net.refractions.udig.ui.internal.Messages;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the Preferences dialog. By
 * subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into
 * JFace that allows us to create a page that is small and knows how to save, restore and apply
 * itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 */

public class UiPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public UiPreferences() {
        super(GRID);
        setPreferenceStore(UiPlugin.getDefault().getPreferenceStore());
        setDescription(Messages.UiPreferences_description);
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
     * manipulate various types of preferences. Each field editor knows how to save and restore
     * itself.
     */
    public void createFieldEditors() {
        addField(new CharSetFieldEditor(
                net.refractions.udig.ui.preferences.PreferenceConstants.P_DEFAULT_CHARSET,
                Messages.UiPreferences_charset, getFieldEditorParent()));

        //if (Platform.getOS().equals(Platform.OS_LINUX)) {
            addField(new BooleanFieldEditor(
                    net.refractions.udig.ui.preferences.PreferenceConstants.P_ADVANCED_GRAPHICS,
                    Messages.UiPreferences_advancedGraphics_label, getFieldEditorParent()));
        //}

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init( IWorkbench workbench ) {
    }

}
