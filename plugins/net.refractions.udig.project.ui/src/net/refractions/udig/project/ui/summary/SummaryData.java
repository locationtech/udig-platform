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
package net.refractions.udig.project.ui.summary;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * Simple data object that has title and information fields.  If the modifier is set then the data can be edited. Th cell editor 
 * is always a {@link TextCellEditor} so the modifier should aways return a string.  The validator by default always accept 
 * the result.  For custom validation cell validator can be set.
 * @author Jesse
 * @since 1.1.0
 */
public class SummaryData {
    private static final ICellModifier DEFAULT_MODIFIER = new ICellModifier(){

        public boolean canModify( Object element, String property ) {
            return false;
        }

        public Object getValue( Object element, String property ) {
            return null;
        }

        public void modify( Object element, String property, Object value ) {
        }
        
    };
    private static final ICellEditorValidator TRUE_VALIDATOR = new ICellEditorValidator(){

        public String isValid( Object value ) {
            return null;
        }
        
    };
    private String title;
    private String info;
    private ICellModifier modifier=DEFAULT_MODIFIER;
    private ICellEditorValidator validator=TRUE_VALIDATOR;
    private SummaryData[] children;
    private SummaryData parent;
    
    /**
     * new instance. data has no children and no parent and is not editable
     * @param title title/property name of the data item
     * @param info the information to display.  toString is called on the item to display it.
     * 
     */
    public SummaryData( String title, Object info ) {
        this.title = title;
        if( info!=null )
            this.info = info.toString();
    }
    public String getInfo() {
        return info;
    }
    public void setInfo( String info ) {
        this.info = info;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle( String title ) {
        this.title = title;
    }
    /**
     * gets the items to display as children of this object
     *
     * @return the items to display as children of this object
     */
    public SummaryData[] getChildren() {
        if( children==null )
            return new SummaryData[0];
        
        SummaryData[] data=new SummaryData[children.length];
        System.arraycopy(children, 0, data, 0, data.length);
        return data;
    }
    public void setChildren( SummaryData[] children ) {
        SummaryData[] data;
        if( children==null ){
            data=new SummaryData[0];
        }else{
            data=new SummaryData[children.length];
            System.arraycopy(children, 0, data, 0, data.length);
        }
        
        this.children = data;
    }
    /**
     * @return the cell modifier that can edit this data.  
     */
    public ICellModifier getModifier() {
        return modifier;
    }
    /**
     * Sets the cell modifier used for this data item.  The property can be ignored and the element will always be 
     * the {@link SummaryData} object.  The newValue passed to the modify methods will always be a String
     *
     * @param modifier
     */
    public void setModifier( ICellModifier modifier ) {
        if (modifier==null) {
            throw new NullPointerException();
        }
        this.modifier = modifier;
    }
    public ICellEditorValidator getValidator() {
        return validator;
    }
    public void setValidator( ICellEditorValidator validator ) {
        if (validator==null) {
            throw new NullPointerException();
        }

        this.validator = validator;
    }
    public SummaryData getParent() {
        return parent;
    }
    public void setParent( SummaryData parent ) {
        this.parent = parent;
    }
}
