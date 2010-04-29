/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2010, Refractions Research Inc.
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
package net.refractions.udig.feature.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.swt.widgets.Text;
import org.geotools.util.Converters;
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
    private boolean isValid;

    /**
     * Old text value.
     * 
     * @since 3.4 this field is protected.
     */
    protected String oldValue;

    /**
     * The text field, or <code>null</code> if none.
     */
    Text textField;

    /**
     * Width of text field in characters; initially unlimited.
     */
    private int widthInChars = UNLIMITED;

    /**
     * Text limit of text field in characters; initially unlimited.
     */
    private int textLimit = UNLIMITED;

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
    public StringAttributeField( String name, String labelText, int width, int strategy,
            Composite parent ) {
        init(name, labelText);
        widthInChars = width;
        setValidateStrategy(strategy);
        isValid = false;
        errorMessage = JFaceResources.getString("StringFieldEditor.errorMessage");//$NON-NLS-1$
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
    public StringAttributeField( String name, String labelText, int width, Composite parent ) {
        this(name, labelText, width, VALIDATE_ON_KEY_STROKE, parent);
    }

    /**
     * Creates a string field editor of unlimited width. Use the method <code>setTextLimit</code> to
     * limit the text.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public StringAttributeField( String name, String labelText, Composite parent ) {
        this(name, labelText, UNLIMITED, parent);
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected void adjustForNumColumns( int numColumns ) {
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
    protected boolean checkState() {
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
        return true;
    }

    /**
     * Fills this field editor's basic controls into the given parent.
     * <p>
     * The string field implementation of this <code>FieldEditor</code> framework method contributes
     * the text field. Subclasses may override but must call <code>super.doFillIntoGrid</code>.
     * </p>
     */
    protected void doFillIntoGrid( Composite parent, int numColumns ) {
        getLabelControl(parent);

        textField = getTextControl(parent);
        GridData gd = new GridData();
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
        textField.setLayoutData(gd);
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected void doLoad() {
        if (textField != null) {
            Object value = getFeature().getAttribute( getAttributeName() );            
            String text = Converters.convert(value, String.class );
            
            textField.setText( text );
            oldValue = text;
        }
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected void doLoadDefault() {
        if (textField != null) {
            SimpleFeatureType schema = getFeature().getFeatureType();
            AttributeDescriptor descriptor = schema.getDescriptor( getAttributeName());            
            Object value = descriptor.getDefaultValue();
            
            String text = Converters.convert(value, String.class );
            textField.setText(text);
        }
        valueChanged();
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected void doStore() {
        SimpleFeatureType schema = getFeature().getFeatureType();
        AttributeDescriptor descriptor = schema.getDescriptor( getAttributeName());  
        
        String text = textField.getText();
        Object value = Converters.convert( text, descriptor.getType().getBinding() );        
        getFeature().setAttribute( getAttributeName(), textField.getText() );
    }

    /**
     * Returns the error message that will be displayed when and if an error occurs.
     * 
     * @return the error message, or <code>null</code> if none
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
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

        Object value = getFeature().getAttribute( getAttributeName());
        String text = Converters.convert( value, String.class );
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

    /**
     * Returns this field editor's text control.
     * <p>
     * The control is created if it does not yet exist
     * </p>
     * 
     * @param parent the parent
     * @return the text control
     */
    public Text getTextControl( Composite parent ) {
        if (textField == null) {
            textField = new Text(parent, SWT.SINGLE | SWT.BORDER);
            textField.setFont(parent.getFont());
            switch( validateStrategy ) {
            case VALIDATE_ON_KEY_STROKE:
                textField.addKeyListener(new KeyAdapter(){

                    /*
                     * (non-Javadoc)
                     * @see
                     * org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent
                     * )
                     */
                    public void keyReleased( KeyEvent e ) {
                        valueChanged();
                    }
                });
                textField.addFocusListener(new FocusAdapter(){
                    // Ensure that the value is checked on focus loss in case we
                    // missed a keyRelease or user hasn't released key.
                    // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=214716
                    public void focusLost( FocusEvent e ) {
                        valueChanged();
                    }
                });

                break;
            case VALIDATE_ON_FOCUS_LOST:
                textField.addKeyListener(new KeyAdapter(){
                    public void keyPressed( KeyEvent e ) {
                        clearErrorMessage();
                    }
                });
                textField.addFocusListener(new FocusAdapter(){
                    public void focusGained( FocusEvent e ) {
                        refreshValidState();
                    }

                    public void focusLost( FocusEvent e ) {
                        valueChanged();
                        clearErrorMessage();
                    }
                });
                break;
            default:
                Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
            }
            textField.addDisposeListener(new DisposeListener(){
                public void widgetDisposed( DisposeEvent event ) {
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

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    public boolean isValid() {
        return isValid;
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
    protected void refreshValidState() {
        isValid = checkState();
    }

    /**
     * Sets whether the empty string is a valid value or not.
     * 
     * @param b <code>true</code> if the empty string is allowed, and <code>false</code> if it is
     *        considered invalid
     */
    public void setEmptyStringAllowed( boolean b ) {
        emptyStringAllowed = b;
    }

    /**
     * Sets the error message that will be displayed when and if an error occurs.
     * 
     * @param message the error message
     */
    public void setErrorMessage( String message ) {
        errorMessage = message;
    }

    /*
     * (non-Javadoc) Method declared on FieldEditor.
     */
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
    public void setStringValue( String value ) {
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
    public void setTextLimit( int limit ) {
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
    public void setValidateStrategy( int value ) {
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

        String newValue = textField.getText();
        if (!newValue.equals(oldValue)) {
            fireValueChanged(VALUE, oldValue, newValue);
            oldValue = newValue;
        }
    }

    /*
     * @see FieldEditor.setEnabled(boolean,Composite).
     */
    public void setEnabled( boolean enabled, Composite parent ) {
        super.setEnabled(enabled, parent);
        getTextControl(parent).setEnabled(enabled);
    }

}
