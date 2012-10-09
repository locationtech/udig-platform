/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.feature.editor.field;

import java.util.Calendar;
import java.util.Date;

import net.refractions.udig.project.ui.feature.EditFeature;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.geotools.feature.type.Types;
import org.geotools.util.Converters;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

public class DateTimeAttributeField extends AttributeField {

    /**
     * Cached valid state.
     */
    private boolean isValid;

    /**
     * Old text value.
     * 
     * @since 3.4 this field is protected.
     */
    protected Date oldValue;
    
    /**
     * The date/time field, or null if none
     */
    protected DateTime dateTime;
    
    /**
     * The error message, or null if none
     */
    protected String errorMessage;

    
    protected DateTimeAttributeField( String name, String label, Composite parent ){
        init( name, label );
        isValid = false;
        errorMessage = "Date not valid";
        createControl( parent );
    }
    
    
    @Override
    public void adjustForNumColumns( int numColumns ) {
        GridData gd = (GridData) dateTime.getLayoutData();
        gd.horizontalSpan = numColumns-1;
        gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
    }

    @Override
    protected void doFillIntoGrid( Composite parent, int numColumns ) {
        Label label = getLabelControl( parent );
        GridData gd = new GridData();
        gd.horizontalSpan = 1;
        label.setLayoutData(gd);
        
        dateTime = getDateTimeControl(parent);
        gd = new GridData();
        gd.horizontalIndent=5;
        gd.horizontalSpan = numColumns - 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
    }

    private DateTime getDateTimeControl( Composite parent ) {
        if( dateTime == null ){
            dateTime = new DateTime( parent, SWT.DATE | SWT.DROP_DOWN | SWT.LONG );
            dateTime.setFont( parent.getFont() );
            dateTime.addKeyListener( new KeyAdapter(){
                @Override
                public void keyReleased( KeyEvent e ) {
                    valueChanged();
                }
            });
            dateTime.addFocusListener( new FocusAdapter(){
                @Override
                public void focusLost( FocusEvent e ) {
                    valueChanged();
                }
            });        
            dateTime.addDisposeListener( new DisposeListener(){                
                public void widgetDisposed( DisposeEvent e ) {
                    dateTime = null;
                }
            });
        }
        else {
            checkParent( dateTime, parent );
        }
        return dateTime;
    }

    /** Step 2: called to update isValid */
    protected void refreshValidState() {
        isValid = checkState();
    }
    
    private Date toDate(){
        if( dateTime == null || dateTime.isDisposed() ){
            return null;
        }
        int day = dateTime.getDay(); // 1-..
        int month = dateTime.getMonth(); // 0-11
        int year = dateTime.getYear(); // 1752-9999
        
        Calendar calendar = Calendar.getInstance();
        calendar.set( year, month, day );        
        Date newValue = calendar.getTime();
        return newValue;
    } 
    
    /** Step 3: check that the field produces a valid value.
     * <p>
     * Will check against the attribute descriptor; if this method returns
     * false doStore cannot be called.
     * 
     * @return true if the field is displaying a valid value
     */
    protected boolean checkState() {
        boolean result = false;
        if (dateTime == null || dateTime.isDisposed()) {
            result = false;
        }
        else {
            Date date = toDate();
            result = checkState( date );
        }
        
        if (result) {
            clearErrorMessage();
        } else {
            showErrorMessage(errorMessage);
        }
        return result;
    }
    
    /**
     * Check the provided date against the attribute descriptor.
     * <p>
     * If false is returned you may check errorMessage for the reason why.
     * @param date
     * @return true if date is valid
     */
    protected boolean checkState( Date date ){
        EditFeature feature = getFeature();
        if( feature == null ){
            return false; // cannot check right now
        }
        
        SimpleFeatureType schema = feature.getFeatureType();
        AttributeDescriptor descriptor = schema.getDescriptor( getAttributeName());
        if( descriptor == null ){
            return false; // schema changed on us?
        }
        Class<?> binding = descriptor.getType().getBinding();
        
        Object value = Converters.convert( date, binding );
        try {
            Types.validate( descriptor, value );
            return true;
        }
        catch( IllegalAttributeException bad ){
            errorMessage = bad.getLocalizedMessage();
            return false;
        }
    }
    
    /**
     * Step 1: Called as the value is being changed!
     */
    protected void valueChanged() {
        setPresentsDefaultValue(false);
        boolean oldState = isValid;
        refreshValidState();

        if (isValid != oldState) {
            fireStateChanged(IS_VALID, oldState, isValid);
        }
        Date newValue = toDate();

        if (!newValue.equals(oldValue)) {
            fireValueChanged(VALUE, oldValue, newValue);
            oldValue = newValue;
        }
    }      

    @Override
    public void doLoad() {
        if (dateTime != null && getFeature() != null ) {
          Object value = getFeature().getAttribute( getAttributeName() );
          Date date = Converters.convert(value, Date.class );
          if( date == null ){
              date = new Date(); // today!
          }
          Calendar cal = Calendar.getInstance();
          cal.setTime( date );
          
          int year = cal.get(Calendar.YEAR); // actual year
          int day = cal.get( Calendar.DATE ); // from 1
          int month = cal.get( Calendar.MONTH ); // from 0=jan
          dateTime.setDate( year, month, day );
          oldValue = date;
      }
    }

    /** Today is used as a default; unless the attribute descriptor has a better idea */
    protected void doLoadDefault() {
        if (dateTime != null && getFeature() != null ) {
            SimpleFeatureType schema = getFeature().getFeatureType();
            AttributeDescriptor descriptor = schema.getDescriptor( getAttributeName());            
            Object value =  descriptor.getDefaultValue();       
            Date date = Converters.convert(value, Date.class );
            
            if( date == null ){
                date = new Date(); // today!
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime( date );
            
            int year = cal.get(Calendar.YEAR); // actual year
            int day = cal.get( Calendar.DATE ); // from 1
            int month = cal.get( Calendar.MONTH ); // from 0=jan
            dateTime.setDate( year, month, day );
            oldValue = date;
        }
        valueChanged();
    }

    @Override
    protected void doStore() {
        SimpleFeatureType schema = getFeature().getFeatureType();
        AttributeDescriptor descriptor = schema.getDescriptor( getAttributeName());  
 
        int year = dateTime.getYear();
        int day = dateTime.getDay();
        int month = dateTime.getMonth(); // from 0=Jan
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        
        Date date = cal.getTime();
        
        Object value = Converters.convert( date, descriptor.getType().getBinding() );        
        getFeature().setAttribute( getAttributeName(), value );
    }

    @Override
    public Control getControl() {
        return dateTime;
    }

    @Override
    public int getNumberOfControls() {
        return 2;
    }

}
