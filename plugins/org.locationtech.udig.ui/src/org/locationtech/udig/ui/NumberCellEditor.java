/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2015, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.ui;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author Nikolaos Pringouris <nprigour@gmail.com>
 * @since 2.0.0
 */
public class NumberCellEditor extends TextCellEditor {

    protected Class<? extends Number> cls;

    public NumberCellEditor() {
        super();
    }

    public NumberCellEditor(Composite parent, Class<? extends Number> cls) {
        super(parent);
        this.cls = cls;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
     */
    @Override
    protected Object doGetValue() {
        return convertToNumber();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.CellEditor#doSetFocus()
     */
    @Override
    protected void doSetFocus() {
        super.doSetFocus();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
     */
    @Override
    protected void doSetValue(Object value) {
        if (value == null) {
            // data = null;
            super.doSetValue("");
        } else {
            super.doSetValue(value.toString());
        }
    }

    @Override
    protected boolean isCorrect(Object value) {
        if (value == null)
            return super.isCorrect(value);
        try {
            return super.isCorrect(convertToNumber());
        } catch (NumberFormatException e) {
            return super.isCorrect(value);
        }
    }

    @Override
    public boolean isValueValid() {
        try {
            Number number = convertToNumber();
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private Number convertToNumber() {
        if (cls == Integer.class) {
            return NumberUtils
                    .createInteger(StringUtils.isNotEmpty(text.getText()) ? text.getText() : null);
        } else if (cls == Long.class) {
            return NumberUtils
                    .createLong(StringUtils.isNotEmpty(text.getText()) ? text.getText() : null);
        } else if (cls == BigInteger.class) {
            return NumberUtils.createBigInteger(
                    StringUtils.isNotEmpty(text.getText()) ? text.getText() : null);
        } else if (cls == BigDecimal.class) {
            return NumberUtils.createBigDecimal(
                    StringUtils.isNotEmpty(text.getText()) ? text.getText() : null);
        } else if (cls == Double.class) {
            return NumberUtils
                    .createDouble(StringUtils.isNotEmpty(text.getText()) ? text.getText() : null);
        } else if (cls == Float.class) {
            return NumberUtils
                    .createFloat(StringUtils.isNotEmpty(text.getText()) ? text.getText() : null);
        } else if (cls == Short.class) {
            if (StringUtils.isNotEmpty(text.getText())) {
                return Short.valueOf(text.getText());
            } else {
                return null;
            }
        }
        return null;
    }
}