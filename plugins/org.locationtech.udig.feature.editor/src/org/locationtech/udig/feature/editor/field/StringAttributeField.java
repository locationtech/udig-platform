/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2010, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.feature.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.geotools.feature.type.Types;
import org.geotools.util.Converters;
import org.locationtech.udig.project.ui.feature.EditFeature;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 * Attribute field for a string type attribute.
 *
 * @since 1.2.0
 */
public class StringAttributeField extends AttributeField {

    /**
     * Validation strategy constant (value <code>0</code>) indicating that the editor should perform
     * validation after every key stroke.
     *
     * @see #setValidateStrategy
     */
    public static final int VALIDATE_ON_KEY_STROKE = 0;

    /**
     * Validation strategy constant (value <code>1</code>) indicating that the editor should perform
     * validation only when the text widget loses focus.
     *
     * @see #setValidateStrategy
     */
    public static final int VALIDATE_ON_FOCUS_LOST = 1;

    /**
     * Text limit constant (value <code>-1</code>) indicating unlimited text limit and width.
     */
    public static int UNLIMITED = -1;

    /**
     * Cached valid state.
     */
    protected boolean isValid;

    /**
     * Old text value.
     *
     * @since 3.4 this field is protected.
     */
    protected String oldValue;

    /**
     * The text field, or <code>null</code> if none.
     */
    protected Text textField;

    /**
     * Width of text field in characters; initially unlimited.
     */
    private int widthInChars = UNLIMITED;

    /**
     * Text limit of text field in characters; initially unlimited.
     */
    protected int textLimit = UNLIMITED;

    /**
     * Text field is single line or multi, we need to know if it's single line <code>false</code> by
     * default
     */
    protected boolean multi = false;

    /**
     * The error message, or <code>null</code> if none.
     */
    private String errorMessage;

    /**
     * Indicates whether the empty string is legal; <code>true</code> by default.
     */
    private boolean emptyStringAllowed = true;

    /**
     * The validation strategy; <code>VALIDATE_ON_KEY_STROKE</code> by default.
     */
    private int validateStrategy = VALIDATE_ON_KEY_STROKE;

    private boolean required;

    /**
     * Creates a new string field editor
     */
    protected StringAttributeField() {
    }

    /**
     * Creates a string field editor. Use the method <code>setTextLimit</code> to limit the text.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param width the width of the text input field in characters, or <code>UNLIMITED</code> for
     *        no limit
     * @param strategy either <code>VALIDATE_ON_KEY_STROKE</code> to perform on the fly checking
     *        (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to perform validation only after
     *        the text has been typed in
     * @param parent the parent of the field editor's control
     * @since 2.0
     */
    public StringAttributeField(String name, String labelText, int width, int strategy,
            Composite parent, int control_style) {
        if (control_style == 1) {
            multi = true;
        }
        init(name, labelText);
        widthInChars = width;
        setValidateStrategy(strategy);
        isValid = false;
        errorMessage = "Text not valid";
        createControl(parent);
    }

    /**
     * Creates a string field editor. Use the method <code>setTextLimit</code> to limit the text.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param width the width of the text input field in characters, or <code>UNLIMITED</code> for
     *        no limit
     * @param parent the parent of the field editor's control
     */
    public StringAttributeField(String name, String labelText, int width, Composite parent,
            int control_style) {
        this(name, labelText, width, VALIDATE_ON_KEY_STROKE, parent, control_style);
    }

    /**
     * Creates a string field editor of unlimited width. Use the method <code>setTextLimit</code> to
     * limit the text.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */

    public StringAttributeField(String name, String labelText, Composite parent,
            int control_style) {
        this(name, labelText, UNLIMITED, control_style, parent, control_style); // UNLIMITED
    }

    public StringAttributeField(String name, String labelText, Composite parent) {
        this(name, labelText, UNLIMITED, parent, 0); // UNLIMITED
    }

    /**
     * Creates a string field editor of unlimited width. Use the method <code>setTextLimit</code> to
     * limit the text.
     *
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     * @param line text field type. True if multiline, false if single line
     */

    @Override
    public void adjustForNumColumns(int numColumns) {
        GridData gd = (GridData) textField.getLayoutData();
        gd.horizontalSpan = numColumns - 1;
        // We only grab excess space if we have to
        // If another field editor has more columns then
        // we assume it is setting the width.
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
    }

    /**
     * Checks whether the text input field contains a valid value or not.
     *
     * @return <code>true</code> if the field value is valid, and <code>false</code> if invalid
     */
    public boolean checkState() {
        boolean result = false;
        if (emptyStringAllowed) {
            result = true;
        }

        if (textField == null) {
            result = false;
        }

        String txt = textField.getText();

        result = (txt.trim().length() > 0) || emptyStringAllowed;

        // call hook for subclasses
        result = result && doCheckState();

        if (result) {
            clearErrorMessage();
        } else {
            showErrorMessage(errorMessage);
        }

        return result;
    }

    /**
     * Hook for subclasses to do specific state checks.
     * <p>
     * The default implementation of this framework method does nothing and returns
     * <code>true</code>. Subclasses should override this method to specific state checks.
     * </p>
     *
     * @return <code>true</code> if the field value is valid, and <code>false</code> if invalid
     */
    protected boolean doCheckState() {
        EditFeature feature = getFeature();
        if (feature == null)
            return true; // cannot check right now
        SimpleFeatureType schema = feature.getFeatureType();
        AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());
        if (descriptor == null) {
            // the schema changed on us! help ...
            return false;
        }
        String text = textField.getText();

        if (text == null || text.length() == 0) {
            return !descriptor.isNillable();
        }
        Object value = Converters.convert(text, descriptor.getType().getBinding());
        try {
            Types.validate(descriptor, value);
            if (isRequired() && value == null) {
                errorMessage = getAttributeName() + " is required";
                showErrorMessage(errorMessage);
                return false;
            }
            return true;
        } catch (IllegalAttributeException bad) {
            errorMessage = bad.getLocalizedMessage();
            return false;
        }
    }

    boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Fills this field editor's basic controls into the given parent.
     * <p>
     * The string field implementation of this <code>AttributeField</code> framework method
     * contributes the text field. Subclasses may override but must call
     * <code>super.doFillIntoGrid</code>.
     * </p>
     */
    @Override
    protected void doFillIntoGrid(final Composite parent, int numColumns) {
        Label label = getLabelControl(parent);
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        label.setLayoutData(gd);

        textField = getTextControl(parent);
        gd = new GridData();
        gd.horizontalIndent = 5;
        gd.horizontalSpan = numColumns - 1;
        if (widthInChars != UNLIMITED) {
            GC gc = new GC(textField);
            try {
                Point extent = gc.textExtent("X");//$NON-NLS-1$
                gd.widthHint = widthInChars * extent.x;
            } finally {
                gc.dispose();
            }
        } else {
            gd.horizontalAlignment = GridData.FILL;
            gd.grabExcessHorizontalSpace = true;
        }
        if (multi) {
            GC gc = new GC(textField);
            try {
                Point extent = gc.textExtent("X");//$NON-NLS-1$
                gd.heightHint = extent.y * 3;
            } finally {
                gc.dispose();
            }
        }
        textField.setLayoutData(gd);
        if (multi) {
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    int numLines = textField.getLineCount();
                    GridData gd = (GridData) textField.getLayoutData();
                    GC gc = new GC(textField);
                    Point extent = gc.textExtent("X");//$NON-NLS-1$
                    if (numLines >= 3) {
                        if ((gd.heightHint / extent.y) > (numLines + 1)) {
                            try {
                                gd.heightHint = extent.y * (numLines--);
                            } finally {
                                gc.dispose();
                            }

                        } else {
                            try {
                                gd.heightHint = extent.y * (numLines++);
                            } finally {
                                gc.dispose();
                            }

                        }

                    } else {
                        try {
                            gd.heightHint = extent.y * 3;
                        } finally {
                            gc.dispose();
                        }
                    }

                    parent.layout();
                }
            });
        }

    }

    @Override
    public void doLoad() {
        if (textField != null && getFeature() != null) {
            Object value = getFeature().getAttribute(getAttributeName());
            String text = Converters.convert(value, String.class);
            if (text == null) {
                text = ""; //$NON-NLS-1$
            }
            textField.setText(text);
            oldValue = text;
        }
    }

    @Override
    protected void doLoadDefault() {
        if (textField != null) {
            SimpleFeatureType schema = getFeature().getFeatureType();
            AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());
            Object value = descriptor.getDefaultValue();

            String text = Converters.convert(value, String.class);
            textField.setText(text);
        }
        valueChanged();
    }

    @Override
    protected void doStore() {
        SimpleFeatureType schema = getFeature().getFeatureType();
        AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());

        String text = textField.getText();
        Object value = Converters.convert(text, descriptor.getType().getBinding());
        getFeature().setAttribute(getAttributeName(), value);
    }

    /**
     * Returns the error message that will be displayed when and if an error occurs.
     *
     * @return the error message, or <code>null</code> if none
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public int getNumberOfControls() {
        return 2;
    }

    /**
     * Returns the field editor's value.
     *
     * @return the current value
     */
    public String getStringValue() {
        if (textField != null) {
            return textField.getText();
        }

        Object value = getFeature().getAttribute(getAttributeName());
        String text = Converters.convert(value, String.class);
        return text;
    }

    /**
     * Returns this field editor's text control.
     *
     * @return the text control, or <code>null</code> if no text field is created yet
     */
    protected Text getTextControl() {
        return textField;
    }

    @Override
    public Text getControl() {
        return textField;
    }

    /**
     * Returns this field editor's text control.
     * <p>
     * The control is created if it does not yet exist
     * </p>
     *
     * @param parent the parent
     * @return the text control
     */
    public Text getTextControl(Composite parent) {
        if (textField == null) {
            if (multi) {
                textField = new Text(parent, SWT.MULTI | SWT.BORDER); // this is a multi-line text
                // field
            } else {
                textField = new Text(parent, SWT.SINGLE | SWT.BORDER); // this is a single-line text
                // field
            }

            textField.setFont(parent.getFont());
            switch (validateStrategy) {
            case VALIDATE_ON_KEY_STROKE:
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        valueChanged();
                    }
                });
                textField.addFocusListener(new FocusAdapter() {
                    // Ensure that the value is checked on focus loss in case we
                    // missed a keyRelease or user hasn't released key.
                    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=214716
                    @Override
                    public void focusLost(FocusEvent e) {
                        valueChanged();
                    }
                });

                break;
            case VALIDATE_ON_FOCUS_LOST:
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        clearErrorMessage();
                    }
                });
                textField.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        refreshValidState();
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        valueChanged();
                        clearErrorMessage();
                    }
                });
                break;
            default:
                Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
            }
            textField.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent event) {
                    textField = null;
                }
            });
            if (textLimit > 0) {// Only set limits above 0 - see SWT spec
                textField.setTextLimit(textLimit);
            }
        } else {
            checkParent(textField, parent);
        }
        return textField;
    }

    /**
     * Returns whether an empty string is a valid value.
     *
     * @return <code>true</code> if an empty string is a valid value, and <code>false</code> if an
     *         empty string is invalid
     * @see #setEmptyStringAllowed
     */
    public boolean isEmptyStringAllowed() {
        return emptyStringAllowed;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    protected void refreshValidState() {
        isValid = checkState();
    }

    /**
     * Sets whether the empty string is a valid value or not.
     *
     * @param b <code>true</code> if the empty string is allowed, and <code>false</code> if it is
     *        considered invalid
     */
    public void setEmptyStringAllowed(boolean b) {
        emptyStringAllowed = b;
    }

    /**
     * Sets the error message that will be displayed when and if an error occurs.
     *
     * @param message the error message
     */
    public void setErrorMessage(String message) {
        errorMessage = message;
    }

    @Override
    public void setFocus() {
        if (textField != null) {
            textField.setFocus();
        }
    }

    /**
     * Sets this field editor's value.
     *
     * @param value the new value, or <code>null</code> meaning the empty string
     */
    public void setStringValue(String value) {
        if (textField != null) {
            if (value == null) {
                value = "";//$NON-NLS-1$
            }
            oldValue = textField.getText();
            if (!oldValue.equals(value)) {
                textField.setText(value);
                valueChanged();
            }
        }
    }

    /**
     * Sets this text field's text limit.
     *
     * @param limit the limit on the number of character in the text input field, or
     *        <code>UNLIMITED</code> for no limit
     */
    public void setTextLimit(int limit) {
        textLimit = limit;
        if (textField != null) {
            textField.setTextLimit(limit);
        }
    }

    /**
     * Sets the strategy for validating the text.
     * <p>
     * Calling this method has no effect after <code>createPartControl</code> is called. Thus this
     * method is really only useful for subclasses to call in their constructor. However, it has
     * public visibility for backward compatibility.
     * </p>
     *
     * @param value either <code>VALIDATE_ON_KEY_STROKE</code> to perform on the fly checking (the
     *        default), or <code>VALIDATE_ON_FOCUS_LOST</code> to perform validation only after the
     *        text has been typed in
     */
    public void setValidateStrategy(int value) {
        Assert.isTrue(value == VALIDATE_ON_FOCUS_LOST || value == VALIDATE_ON_KEY_STROKE);
        validateStrategy = value;
    }

    /**
     * Shows the error message set via <code>setErrorMessage</code>.
     */
    public void showErrorMessage() {
        showErrorMessage(errorMessage);
    }

    /**
     * Informs this field editor's listener, if it has one, about a change to the value (
     * <code>VALUE</code> property) provided that the old and new values are different.
     * <p>
     * This hook is <em>not</em> called when the text is initialized (or reset to the default value)
     * from the preference store.
     * </p>
     */
    protected void valueChanged() {

        setPresentsDefaultValue(false);
        boolean oldState = isValid;
        refreshValidState();
        if (isValid != oldState) {
            fireStateChanged(IS_VALID, oldState, isValid);
        }

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                refreshValidState();
            }

            @Override
            public void focusLost(FocusEvent e) {
                String newValue = textField.getText();
                if (!newValue.equals(oldValue)) {
                    fireValueChanged(VALUE, oldValue, newValue);
                    oldValue = newValue;
                    System.out.println("oldValue"); //$NON-NLS-1$
                }
                clearErrorMessage();
            }
        });

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (getControl() != null && !getControl().isDisposed()) {
            getControl().setVisible(visible);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (getControl() != null && !getControl().isDisposed()) {
            getControl().setEnabled(enabled);
        }
    }

}
