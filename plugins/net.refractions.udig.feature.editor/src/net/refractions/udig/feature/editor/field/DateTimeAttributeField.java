package net.refractions.udig.feature.editor.field;

import java.util.Calendar;
import java.util.Date;

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
            dateTime = new DateTime( parent, SWT.DEFAULT );
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

    protected void valueChanged() {
        setPresentsDefaultValue(false);
        boolean oldState = isValid;
        refreshValidState();

        if (isValid != oldState) {
            fireStateChanged(IS_VALID, oldState, isValid);
        }

        int day = dateTime.getDay(); // 1-..
        int month = dateTime.getMonth(); // 0-11
        int year = dateTime.getYear(); // 1752-9999
        
        Calendar calendar = Calendar.getInstance();
        calendar.set( year, month, day );        
        Date newValue = calendar.getTime();

        if (!newValue.equals(oldValue)) {
            fireValueChanged(VALUE, oldValue, newValue);
            oldValue = newValue;
        }
    }
    

    @Override
    public void doLoad() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void doLoadDefault() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void doStore() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Control getControl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getNumberOfControls() {
        // TODO Auto-generated method stub
        return 0;
    }

}
