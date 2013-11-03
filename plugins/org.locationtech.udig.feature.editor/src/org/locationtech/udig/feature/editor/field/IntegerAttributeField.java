/**
 * 
 */
package org.locationtech.udig.feature.editor.field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.geotools.util.Converters;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

/**
 * Purpose of IntegerAttributeField is to act as a wrapper for a numeric ( Integer ) Form Field in
 * which is an attribute of this type is added to a feature
 * <p>
 * <ul>
 * <li></li>
 * </ul>
 * </p>
 * 
 * @author myleskenihan
 * @since 1.2.0
 */
public class IntegerAttributeField extends StringAttributeField {

    private int minValidValue = 0;
    private int maxValidValue = Integer.MAX_VALUE;
    private static final int DEFAULT_TEXT_LIMIT = 10;

    /**
     * Creates a new integer attribute field
     */
    protected IntegerAttributeField() {
    }

    /**
     * Creates an integer attribute field.
     * 
     * @param name the name of the preference this attribute field works on
     * @param labelText the label text of the attribute field
     * @param parent the parent of the attribute field's control
     */
    public IntegerAttributeField( String name, String labelText, Composite parent ) {
        this(name, labelText, parent, DEFAULT_TEXT_LIMIT);
    }

    /**
     * Creates an integer attribute field.
     * 
     * @param name the name of the preference this attribute field works on
     * @param labelText the label text of the attribute field
     * @param parent the parent of the attribute field's control
     * @param textLimit the maximum number of characters in the text.
     */
    public IntegerAttributeField( String name, String labelText, Composite parent, int textLimit ) {
        init(name, labelText);
        setTextLimit(textLimit);
        setEmptyStringAllowed(false);
        setErrorMessage(JFaceResources.getString("IntegerAttributeField.errorMessage"));//$NON-NLS-1$
        createControl(parent);
    }

    /**
     * Sets the range of valid values for this field.
     * 
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     */
    public void setValidRange( int min, int max ) {
        minValidValue = min;
        maxValidValue = max;
        setErrorMessage(JFaceResources.format("IntegerAttributeField.errorMessageRange", //$NON-NLS-1$
                new Object[]{new Integer(min), new Integer(max)}));
    }

    /*
     * (non-Javadoc) Method declared on StringAttributeField. Checks whether the entered String is a
     * valid integer or not.
     */
    public boolean checkState() {

        Text text = getTextControl();

        if (text == null) {
            return false;
        }

        String numberString = text.getText();
        try {
            int number = Integer.valueOf(numberString).intValue();
            if (number >= minValidValue && number <= maxValidValue) {
                clearErrorMessage();
                return true;
            }

            showErrorMessage();
            return false;

        } catch (NumberFormatException e1) {
            showErrorMessage();
        }

        return false;
    }

    /*
     * (non-Javadoc) Method declared on AttributeField.
     */
    public void doLoad() {
        Object value = getFeature().getAttribute(getAttributeName());
        Integer thenumber = Converters.convert(value, Integer.class);
        textField.setText("" + thenumber); //$NON-NLS-1$
        oldValue = "" + thenumber; //$NON-NLS-1$

    }

    /*
     * (non-Javadoc) Method declared on AttributeField.
     */
    protected void doLoadDefault() {
        if (textField != null) {
            SimpleFeatureType schema = getFeature().getFeatureType();
            AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());
            Object value = descriptor.getDefaultValue();
            Integer thenumber = Converters.convert(value, Integer.class);
            textField.setText("" + thenumber); //$NON-NLS-1$
        }
        valueChanged();
    }

    /*
     * (non-Javadoc) Method declared on AttributeField.
     */
    protected void doStore() {

        SimpleFeatureType schema = getFeature().getFeatureType();
        AttributeDescriptor descriptor = schema.getDescriptor(getAttributeName());
        Object value = Converters.convert(textField, descriptor.getType().getBinding());
        getFeature().setAttribute(getAttributeName(), value);

    }

    /**
     * Returns this attribute field's current value as an integer.
     * 
     * @return the value
     * @exception NumberFormatException if the <code>String</code> does not contain a parsable
     *            integer
     */
    public int getIntValue() throws NumberFormatException {
        return new Integer(getStringValue()).intValue();
    }
  
    @Override
    public Text getTextControl( Composite parent ) {
        if (textField == null) {
            
            textField = new Text(parent, SWT.MULTI | SWT.BORDER); // this is a multi-line text
                        
            textField.setFont(parent.getFont());

            textField.addKeyListener(new KeyAdapter(){
                /*
                 * (non-Javadoc)
                 * @see
                 * org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent
                 * )
                 */
                public void keyReleased( KeyEvent e ) {
                    //backspace pressed
                    if(e.keyCode == SWT.BS){
                    valueChanged();                       
                    }
                    
                    //numeric pressed
                    if (e.keyCode >=48 && e.keyCode <=57){
                    valueChanged();
                    }
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
}
