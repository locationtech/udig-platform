/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.properties;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.ui.AttributeValidator;
import org.locationtech.udig.ui.DateTimeCellEditor;
import org.locationtech.udig.ui.NumberCellEditor;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.util.CodeList;

/**
 * A PropertyDescriptor for attributes of a SimpleFeature.
 * 
 * @author jeichar
 * @since 0.3
 */
public class AttributePropertyDescriptor extends PropertyDescriptor {
    protected AttributeDescriptor type;
    AttributeValidator validator;
    private String[] comboBoxList;
    
    /** Is the property editable and should return a cell editor. */
    private boolean editable = false;

    /**
     * Creates a new instance of AttributePropertyDescriptor
     * 
     * @param id The object used to identify the value
     * @param displayName The Property display name
     * @param type The Attribute type that describes the attribute
     * @param featureType the featureType that contains the attributeType.
     */
    public AttributePropertyDescriptor( Object id, String displayName, AttributeDescriptor type, SimpleFeatureType schema ) {
        this(id, displayName, type, schema, false);
    }
    
    /**
     * 
     * @param id The object used to identify the value
     * @param displayName The Property display name
     * @param type  The Attribute type that describes the attribute
     * @param featureType the featureType that contains the attributeType.
     * @param editable
     */
    public AttributePropertyDescriptor( Object id, String displayName, AttributeDescriptor type , SimpleFeatureType schema, boolean editable) {
        super(id, displayName);
        this.type = type;
        validator = new AttributeValidator(type, schema );
        comboBoxList = createComboList();
        this.editable = editable;
    }

    /**
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    public CellEditor createPropertyEditor( Composite parent ) {
    	if(!editable)
    		return null;
    	
        try{
            if (Boolean.class.isAssignableFrom(type.getType().getBinding())
                    || boolean.class.isAssignableFrom(type.getType().getBinding()))
                return new ComboBoxCellEditor(
                        parent,
                        new String[]{
                                Messages.AttributePropertyDescriptor_true, Messages.AttributePropertyDescriptor_false});  
            if (String.class.isAssignableFrom(type.getType().getBinding()))
                return new TextCellEditor(parent);
            if (Integer.class.isAssignableFrom(type.getType().getBinding()))
                return new NumberCellEditor(parent, Integer.class);
            if (Double.class.isAssignableFrom(type.getType().getBinding()))
                return new NumberCellEditor(parent, Double.class);
            if (Float.class.isAssignableFrom(type.getType().getBinding()))
                return new NumberCellEditor(parent, Float.class);
            if (Long.class.isAssignableFrom(type.getType().getBinding()))
                return new NumberCellEditor(parent, Long.class);
            if (BigInteger.class.isAssignableFrom(type.getType().getBinding()))
                return new NumberCellEditor(parent, BigInteger.class);
            if (BigDecimal.class.isAssignableFrom(type.getType().getBinding()))
                return new NumberCellEditor(parent, BigDecimal.class);
            if (Long.class.isAssignableFrom(type.getType().getBinding()))
                return new NumberCellEditor(parent, Long.class);
            if (Date.class.isAssignableFrom(type.getType().getBinding()))
                return new DateTimeCellEditor(parent);
            if (CodeList.class.isAssignableFrom(type.getType().getBinding())) {
                return new ComboBoxCellEditor(parent, comboBoxList);
            }
            return super.createPropertyEditor(parent);
        }catch(Throwable t){
            ProjectUIPlugin.log("error converting attribute to string", t);
            return null;
        }
    }

    String[] createComboList() {
        if (!(CodeList.class.isAssignableFrom(type.getType().getBinding())))
            return null;
        CodeList list = (CodeList) type.getDefaultValue();
        CodeList[] family = list.family();
        String[] names = new String[family.length];
        for( int i = 0; i < names.length; i++ ) {
            names[i] = family[i].name();
        }

        return names;
    }

    /**
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#getValidator()
     */
    protected ICellEditorValidator getValidator() {
        return validator;
    }

    /**
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#getLabelProvider()
     */
    public ILabelProvider getLabelProvider() {
        return new LabelProvider(){
            /**
             * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
             */
            public String getText( Object element ) {
                if( element == null )
                    return "null"; //$NON-NLS-1$
                if (Boolean.class.isAssignableFrom(type.getType().getBinding()) && element instanceof Integer) {
                    int intValue = ((Integer) element).intValue();
                    return intValue == 1 ? "true" : "false"; //$NON-NLS-1$ //$NON-NLS-2$
                }
                return element.toString();
            }
        };
    }
    /**
     * @return Returns the comboBoxList.
     */
    public String[] getComboBoxList() {

        String[] c=new String[comboBoxList.length];
        System.arraycopy(comboBoxList, 0, c, 0, c.length);
        return c;
    }
}
