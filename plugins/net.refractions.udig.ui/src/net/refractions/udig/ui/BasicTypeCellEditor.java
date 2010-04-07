/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.ui;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * A Cell editor 
 * @author Jesse
 * @since 1.1.0
 */
public class BasicTypeCellEditor extends TextCellEditor {

    private Class<? extends Object> type;
    private Object lastLegalValue;


    public BasicTypeCellEditor( Composite composite, Class<? extends Object> type ) {
        super( composite );
        if ( !isLegalType(type) )
            throw new IllegalArgumentException(type+" is not a supported type by this editor"); //$NON-NLS-1$
        this.type=type;
    }
 
    @Override
    protected boolean isCorrect( Object value ) {
        if( value == null )
            return super.isCorrect(value);
        try{
            return super.isCorrect(convertToType(value.toString()));
        }catch(NumberFormatException e){
            return super.isCorrect(value);
        }
    }
    
    @Override
    protected Object doGetValue() {
        return safeConvertToType(super.doGetValue());
    }

    private Object safeConvertToType(Object value2) {
        String value=(String) value2;
        value=value.trim();
        try{
            Object convertToType = convertToType(value);
            lastLegalValue=convertToType;
            return convertToType;
        }catch( NumberFormatException e ){
            if( lastLegalValue!=null )
                return lastLegalValue;
            else
                return 0;
        }
    }
    @Override
    protected void doSetValue( Object value ) {
        if( value!=null && !isLegalType(value.getClass()) ){
            throw new IllegalArgumentException(type+" is not a supported type by this editor"); //$NON-NLS-1$
        }
        String stringValue;
        if( value==null )
            stringValue = ""; //$NON-NLS-1$
        else{
            stringValue = value.toString();
            lastLegalValue=value;
        }
        super.doSetValue(stringValue);
    }

    private boolean isLegalType( Class< ? extends Object> type ) {
        if( type == Short.class ){
            return true;
        }else if( type == String.class ){
            return true;
        }else if( type == Integer.class ){
            return true;
        }else if( type == Byte.class ){
            return true;
        }else if( type == Character.class ){
            return true;
        }else if( type == Long.class ){
            return true;
        }else if( type == Double.class ){
            return true;
        }else if( type == Float.class ){
            return true;
        }else if( type == BigDecimal.class ){
            return true;
        }else if( type == BigInteger.class ){
            return true;
        }
        return false;
    }
  
    private Object convertToType( String value ) {
        if( type == Short.class ){
            return Short.valueOf(value);
        }else if( type == String.class ){
            return value;
        }else if( type == Integer.class ){
            return Integer.valueOf(value);
        }else if( type == Byte.class ){
            return Byte.valueOf(value);
        }else if( type == Character.class ){
            return value.charAt(0);
        }else if( type == Long.class ){
            return Long.valueOf(value);
        }else if( type == Double.class ){
            return Double.valueOf(value);
        }else if( type == Float.class ){
            return Float.valueOf(value);
        }else if(type == BigDecimal.class){
            return new BigDecimal(value);
        }else if( type == BigInteger.class ){
            return new BigInteger(value);
        }
        return null;
    }

}
