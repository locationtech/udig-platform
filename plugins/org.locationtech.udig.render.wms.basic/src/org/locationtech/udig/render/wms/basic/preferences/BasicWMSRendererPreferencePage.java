/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.render.wms.basic.preferences;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.locationtech.udig.render.wms.basic.WMSPlugin;
import org.locationtech.udig.render.wms.basic.internal.Messages;

/**
 * Preference page controlling the basic WMS renderer.
 * <p>
 * By subclassing <samp>FieldEditorPreferencePage</samp>, we can use the field support built into
 * JFace that allows us to create a page that is small and knows how to save, restore and apply
 * itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the preference store that
 * belongs to the main plug-in class. That way, preferences can be accessed directly via the
 * preference store.
 */
public class BasicWMSRendererPreferencePage extends FieldEditorPreferencePage
        implements IWorkbenchPreferencePage {

    private ImageTypeListEditor editor;

    private BooleanFieldEditor checkbox;

    public BasicWMSRendererPreferencePage() {
        super(GRID);
        setPreferenceStore(WMSPlugin.getDefault().getPreferenceStore());
        setDescription(Messages.BasicWMSRendererPreferencePage_warning);
    }

    /**
     * Creates the field editors. Field editors are abstractions of the common GUI blocks needed to
     * manipulate various types of preferences. Each field editor knows how to save and restore
     * itself.
     */
    @Override
    public void createFieldEditors() {
        checkbox = new BooleanFieldEditor(PreferenceConstants.P_USE_DEFAULT_ORDER,
                Messages.BasicWMSRendererPreferencePage_useDefaults, getFieldEditorParent());
        // checkbox.setPropertyChangeListener(this);
        addField(checkbox);
        editor = new ImageTypeListEditor(PreferenceConstants.P_IMAGE_TYPE_ORDER,
                Messages.BasicWMSRendererPreferencePage_setOrder, getFieldEditorParent());
        editor.setEnabled(!getPreferenceStore().getBoolean(PreferenceConstants.P_USE_DEFAULT_ORDER),
                getFieldEditorParent());
        addField(editor);
    }

    @Override
    public void init(IWorkbench workbench) {
        // nothing to do here
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        super.propertyChange(event);
        if (event.getSource().equals(checkbox)) {
            boolean useDefault = ((Boolean) event.getNewValue()).booleanValue();
            editor.setEnabled(!useDefault, getFieldEditorParent());
        }
    }

    protected class ImageTypeListEditor extends ListEditor {
        protected ImageTypeListEditor(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

        @Override
        protected String createList(String[] items) {
            StringBuilder stringList = new StringBuilder();
            for (String str : items) {
                if (stringList.length() > 0) {
                    stringList.append(',');
                }
                stringList.append(str);

            }
            return stringList.toString();
        }

        @Override
        protected String getNewInputObject() {
            String str = new String("image/"); //$NON-NLS-1$
            InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(),
                    "New Image Type", "Enter the image type", str, null); //$NON-NLS-1$ //$NON-NLS-2$
            int result = dialog.open();
            if (result == Window.OK) {
                str = dialog.getValue();
            }
            if ("image/".equals(str)) { //$NON-NLS-1$
                return null; // nothing to add
            }
            return str;
        }

        @Override
        protected String[] parseString(String stringList) {
            String[] items = stringList.split(","); //$NON-NLS-1$
            return items;
        }

    }

}
