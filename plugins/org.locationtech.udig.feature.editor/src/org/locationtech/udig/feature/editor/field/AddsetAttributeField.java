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
package org.locationtech.udig.feature.editor.field;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;

/**
 * NOT to be fully implemented to the extent it will add stuff to EditManager just
 * showing the business users what it will look like
 */

public class AddsetAttributeField extends ListAttributeField {

    private String lastPath;

    /**
     * The special label text for directory chooser, or <code>null</code> if none.
     */
    private String prompt;

    /**
     * Creates a new add set attribute field
     */
    protected AddsetAttributeField() {
    }

    /**
     * Creates a add set attribute field.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param prompt the label text displayed for the directory chooser
     * @param parent the parent of the field editor's control
     */
    public AddsetAttributeField(String name, String labelText, String prompt, Composite parent) {
        init(name, labelText);
        this.prompt = prompt;
        createControl(parent);
    }

    static final String SEPARATOR = "\n"; //$NON-NLS-1$

    /**
     * (non-Javadoc) Method declared on ListAttributeField. Creates a single string from the given
     * array by separating each string with the appropriate OS-specific path separator.
     */
    @Override
    protected String createList(String[] items) {
        StringBuffer path = new StringBuffer("");//$NON-NLS-1$

        for (int i = 0; i < items.length; i++) {
            path.append(items[i]);
            path.append(SEPARATOR);
        }
        return path.toString();
    }

    /**
     * (non-Javadoc) Method declared on ListAttributeField. Creates a new path element by means of a
     * directory dialog.
     */
    @Override
    protected String getNewInputObject() {
        if (prompt == null) {
            prompt = "Please enter:";
        }
        InputDialog dialog = new InputDialog(getShell(), "New " + getLabelText(), prompt, "", //$NON-NLS-2$
                new IInputValidator() {
                    @Override
                    public String isValid(String newText) {
                        if (newText == null || newText.length() == 0) {
                            return "Action is required";
                        }
                        return null;
                    }
                });

        int sucess = dialog.open();
        if (sucess == InputDialog.CANCEL) {
            return null; // we may have to produce a default value here
        }
        return dialog.getValue();
    }

    @Override
    protected String[] parseString(String stringList) {
        if (stringList == null || stringList.length() == 0) {
            return new String[0];
        }
        String split[] = stringList.split(SEPARATOR);
        return split;
    }
}
