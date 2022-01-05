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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.geotools.util.Converters;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

public class BooleanAttributeField extends AttributeField {

    /**
     * Style constant (value <code>0</code>) indicating the default layout where the field editor's
     * check box appears to the left of the label.
     */
    public static final int DEFAULT = 0;

    /**
     * Style constant (value <code>1</code>) indicating a layout where the field editor's label
     * appears on the left with a check box on the right.
     */
    public static final int SEPARATE_LABEL = 1;

    /**
     * Style bits. Either <code>DEFAULT</code> or <code>SEPARATE_LABEL</code>.
     */
    private int style;

    /**
     * The previously selected, or "before", value.
     */
    private boolean wasSelected;

    /**
     * The checkbox control, or <code>null</code> if none.
     */
    private Button checkBox = null;

    final protected Object YES;

    final protected Object NO;

    @Override
    public Button getControl() {
        return checkBox;
    }

    /**
     * Creates a new boolean attribute field
     */
    protected BooleanAttributeField() {
        YES = Boolean.TRUE;
        NO = Boolean.FALSE;
    }

    /**
     * Creates a boolean attribute field in the given style.
     *
     * @param name the name of the preference this attribute field works on
     * @param labelText the label text of the attribute field
     * @param style the style, either <code>DEFAULT</code> or <code>SEPARATE_LABEL</code>
     * @param parent the parent of the attribute field's control
     * @see #DEFAULT
     * @see #SEPARATE_LABEL
     */
    public BooleanAttributeField(String name, String labelText, int style, Composite parent) {
        init(name, labelText);
        this.style = style;
        YES = Boolean.TRUE;
        NO = Boolean.FALSE;

        createControl(parent);
    }

    public BooleanAttributeField(String name, String labelText, int style, Composite parent,
            Object yes, Object no) {
        init(name, labelText);
        this.style = style;
        YES = yes;
        NO = no;
        createControl(parent);
    }

    /**
     * Creates a boolean attribute field in the default style.
     *
     * @param name the name of the preference this attribute field works on
     * @param label the label text of the attribute field
     * @param parent the parent of the attribute field's control
     */
    public BooleanAttributeField(String name, String label, Composite parent) {
        this(name, label, DEFAULT, parent);
    }

    @Override
    public void adjustForNumColumns(int numColumns) {
        if (style == SEPARATE_LABEL) {
            numColumns--;
        }
        ((GridData) checkBox.getLayoutData()).horizontalSpan = numColumns;
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        String text = getLabelText();
        switch (style) {
        case SEPARATE_LABEL:
            getLabelControl(parent);
            numColumns--;
            text = null;
            //$FALL-THROUGH$
        default:
            checkBox = getChangeControl(parent);
            GridData gd = new GridData();
            gd.horizontalSpan = numColumns;
            checkBox.setLayoutData(gd);
            if (text != null) {
                checkBox.setText(text);
            }
        }
    }

    /**
     * Returns the control responsible for displaying this attribute field's label. This method can
     * be used to set a tooltip for a <code>BooleanAttributeField</code>. Note that the normal
     * pattern of <code>getLabelControl(parent).setToolTipText(tooltipText)</code> does not work for
     * boolean attribute fields, as it can lead to duplicate text (see bug 259952).
     *
     * @param parent the parent composite
     * @return the control responsible for displaying the label
     * @since 3.5
     */
    public Control getDescriptionControl(Composite parent) {
        if (style == SEPARATE_LABEL) {
            return getLabelControl(parent);
        }
        return getChangeControl(parent);
    }

    /**
     * (non-Javadoc) Method declared on AttributeField. Loads the value from the feature type schema
     * and sets it to the check box.
     */
    @Override
    public void doLoad() {
        if (checkBox != null) {
            Object value = getFeature().getAttribute(getAttributeName());
            Boolean check = toBoolean(value);
            checkBox.setSelection(check);
            wasSelected = check;
        }
    }

    private Boolean toBoolean(Object value) {
        if (YES.equals(value)) {
            return true;
        }
        if (NO.equals(value)) {
            return false;
        }
        Class<?> target = YES.getClass(); // usually Boolean

        if (Boolean.class.isAssignableFrom(target)) {
            Boolean check = (Boolean) Converters.convert(value, target);
            if (check != null) {
                return check;
            }
            if (value instanceof Number) {
                Number number = (Number) value;
                check = number.longValue() != 0;
            }
        }

        Object test = Converters.convert(value, target);
        if (test != null && YES.equals(test)) {
            return true;
        } else if (test != null && NO.equals(test)) {
            return false;
        }

        Object sure = Converters.convert(YES, target);
        Object huh = Converters.convert(NO, target);

        if (sure != null && sure.equals(value)) {
            return true;
        } else if (huh != null && huh.equals(value)) {
            return false;
        }

        return false; // (sigh)
    }

    /**
     * (non-Javadoc) Method declared on AttributeField. Loads the default value from the feature
     * type schema and sets it to the check box.
     */
    @Override
    protected void doLoadDefault() {
        if (checkBox != null) {
            SimpleFeatureType schema = getFeature().getFeatureType();
            AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());
            Object value = descriptor.getDefaultValue();

            Boolean check = toBoolean(value);

            checkBox.setSelection(check);
            wasSelected = check;
        }
    }

    @Override
    protected void doStore() {
        SimpleFeatureType schema = getFeature().getFeatureType();
        AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());
        Class<?> binding = descriptor.getType().getBinding();

        boolean check = checkBox.getSelection();

        Object checkValue = check ? YES : NO;
        Object value = Converters.convert(checkValue, binding);

        getFeature().setAttribute(getAttributeName(), value);
    }

    /**
     * Returns this attribute field's current value.
     *
     * @return the value
     */
    public boolean getBooleanValue() {
        return checkBox.getSelection();
    }

    /**
     * Returns the change button for this attribute field.
     *
     * @param parent The Composite to create the receiver in.
     * @return the change button
     */
    protected Button getChangeControl(Composite parent) {
        if (checkBox == null) {
            checkBox = new Button(parent, SWT.CHECK | SWT.LEFT);
            checkBox.setFont(parent.getFont());
            checkBox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    boolean isSelected = checkBox.getSelection();
                    valueChanged(wasSelected, isSelected);
                    wasSelected = isSelected;
                }
            });
            checkBox.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent event) {
                    checkBox = null;
                }
            });
        } else {
            checkParent(checkBox, parent);
        }
        return checkBox;
    }

    @Override
    public int getNumberOfControls() {
        switch (style) {
        case SEPARATE_LABEL:
            return 2;
        default:
            return 1;
        }
    }

    @Override
    public void setFocus() {
        if (checkBox != null) {
            checkBox.setFocus();
        }
    }

    @Override
    public void setLabelText(String text) {
        super.setLabelText(text);
        Label label = getLabelControl();
        if (label == null && checkBox != null) {
            checkBox.setText(text);
        }
    }

    /**
     * Informs this attribute field's listener, if it has one, about a change to the value (
     * <code>VALUE</code> property) provided that the old and new values are different.
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    protected void valueChanged(boolean oldValue, boolean newValue) {
        setPresentsDefaultValue(false);
        if (oldValue != newValue) {
            fireStateChanged(VALUE, oldValue, newValue);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        // Only call super if there is a label already
        if (style == SEPARATE_LABEL) {
            super.setVisible(visible);
        }
        if (checkBox != null && !checkBox.isDisposed()) {
            checkBox.setVisible(visible);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        // Only call super if there is a label already
        if (style == SEPARATE_LABEL) {
            super.setEnabled(enabled);
        }
        if (checkBox != null && !checkBox.isDisposed()) {
            checkBox.setEnabled(enabled);
        }
    }

    public String getStringValue() {
        // FIXME : return a correct String value here
        if (YES == (Object) 1) {
            System.out.println("Retired"); //$NON-NLS-1$
            return "1"; //$NON-NLS-1$

        } else {
            System.out.println("un Retired"); //$NON-NLS-1$
            return "0"; //$NON-NLS-1$
        }
    }

}
