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
 *
 */
package net.refractions.udig.project.ui.internal.properties;

import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;

/**
 * A Descriptor for a SimpleFeatureType aka Schema for the properties view.
 * 
 * @author jeichar
 * @since 1.0.0
 */
public class SchemaDescriptor extends PropertyDescriptor implements IPropertySource {

    private IPropertyDescriptor[] descriptors;
    private SimpleFeatureType type;

    /**
     * Creates a new instance of FeatureSourceDescriptor
     * 
     * @param id
     * @param name
     * @param source
     */
    public SchemaDescriptor( Object id, String name,  FeatureSource<SimpleFeatureType, SimpleFeature> source ) {
        super(id, name);
        type = source.getSchema();
    }

    /**
     * @see org.eclipse.ui.views.properties.PropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
     */
    public CellEditor createPropertyEditor( Composite parent ) {
        return new SchemaEditor(parent, type);
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (descriptors == null) {
            List<IPropertyDescriptor> desc = new ArrayList<IPropertyDescriptor>();
            AttributeDescriptor[] attrs = type.getAttributeDescriptors().toArray(new AttributeDescriptor[0]);
            PropertyDescriptor d;
            for( int i = 0; i < attrs.length; i++ ) {
                String name = attrs[i].getLocalName().toLowerCase();
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                d = new PropertyDescriptor(Integer.valueOf(i), name);
                if ( attrs[i] instanceof GeometryDescriptor ) 
                    d.setCategory(Messages.ScemaDescriptor_geometry); 
                else
                    d.setCategory(Messages.ScemaDescriptor_attributeTypes); 
                desc.add(d);
            }
            descriptors = new IPropertyDescriptor[desc.size()];
            desc.toArray(descriptors);
        }
        IPropertyDescriptor[] c=new IPropertyDescriptor[descriptors.length];
        System.arraycopy(descriptors, 0, c, 0, c.length);
        return c;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue( Object id ) {
        int i = ((Integer) id).intValue();
        String name = type.getDescriptor(i).getType().getBinding().getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet( Object id ) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue( Object id ) {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object,
     *      java.lang.Object)
     */
    public void setPropertyValue( Object id, Object value ) {
        // TODO Auto-generated method stub

    }
}
