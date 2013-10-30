/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.ui;

import net.refractions.udig.ui.internal.Messages;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Cell editor for Boolean choices
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class BooleanCellEditor extends ComboBoxCellEditor {

    private static final String FALSE = Messages.BooleanCellEditor_FALSE;
    private static final String TRUE = Messages.BooleanCellEditor_TRUE;

    public BooleanCellEditor( Composite control ) {
        super(control, new String[]{TRUE, FALSE}, SWT.READ_ONLY);
    }

    @Override
    protected boolean isCorrect( Object value ) {
        if (value == null)
            return super.isCorrect(null);
        return super.isCorrect(Boolean.valueOf(value.equals(Integer.valueOf(0))));
    }

    @Override
    protected Object doGetValue() {

        Object value = super.doGetValue();

        if (value == null)
            return null;

        return Boolean.valueOf(value.equals(Integer.valueOf(0)));
    }

    @Override
    protected void doSetValue( Object value ) {
        if (value == null) {
            super.doSetValue(0);
        } else {
            Boolean bool = (Boolean) value;
            if (bool)
                super.doSetValue(0);
            else
                super.doSetValue(1);
        }
    }
}
