/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
package net.refractions.udig.project.ui.internal.properties;


import java.math.BigDecimal;
import java.math.BigInteger;

import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.internal.ProjectUIPlugin;
import net.refractions.udig.ui.AttributeValidator;
import net.refractions.udig.ui.BasicTypeCellEditor;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.geotools.feature.AttributeType;
import org.geotools.feature.FeatureType;
import org.opengis.util.CodeList;

/**
 * A PropertyDescriptor for attributes of a Feature.
 *
 * @author jeichar
 * @since 0.3
 */
public class AttributePropertyDescriptor extends PropertyDescriptor {
    protected AttributeType type;
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
    public AttributePropertyDescriptor( Object id, String displayName, AttributeType type, FeatureType schema ) {
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
    public AttributePropertyDescriptor( Object id, String displayName, AttributeType type , FeatureType schema, boolean editable) {
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
            if (Boolean.class.isAssignableFrom(type.getType())
                    || boolean.class.isAssignableFrom(type.getType()))
                return new ComboBoxCellEditor(
                        parent,
                        new String[]{
                                Messages.AttributePropertyDescriptor_true, Messages.AttributePropertyDescriptor_false});
            if (String.class.isAssignableFrom(type.getType()))
                return new TextCellEditor(parent);
            if (Integer.class.isAssignableFrom(type.getType()))
                return new BasicTypeCellEditor(parent, Integer.class);
            if (Double.class.isAssignableFrom(type.getType()))
                return new BasicTypeCellEditor(parent, Double.class);
            if (Float.class.isAssignableFrom(type.getType()))
                return new BasicTypeCellEditor(parent, Float.class);
            if (Long.class.isAssignableFrom(type.getType()))
                return new BasicTypeCellEditor(parent, Long.class);
            if (BigInteger.class.isAssignableFrom(type.getType()))
                return new BasicTypeCellEditor(parent, BigInteger.class);
            if (BigDecimal.class.isAssignableFrom(type.getType()))
                return new BasicTypeCellEditor(parent, BigDecimal.class);
            if (Long.class.isAssignableFrom(type.getType()))
                return new BasicTypeCellEditor(parent, Long.class);
            if (CodeList.class.isAssignableFrom(type.getType())) {
                return new ComboBoxCellEditor(parent, comboBoxList);
            }
            return super.createPropertyEditor(parent);
        }catch(Throwable t){
            ProjectUIPlugin.log("error converting attribute to string", t);
            return null;
        }
    }

    String[] createComboList() {
        if (!(CodeList.class.isAssignableFrom(type.getType())))
            return null;
        CodeList list = (CodeList) type.createDefaultValue();
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
                if (Boolean.class.isAssignableFrom(type.getType()) && element instanceof Integer) {
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
