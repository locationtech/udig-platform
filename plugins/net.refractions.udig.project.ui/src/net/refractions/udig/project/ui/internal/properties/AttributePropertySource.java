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


import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.geotools.feature.AttributeType;

/**
 * Allows complex attributes to be a property source. Only nested attributes are property
 *
 * @author jeichar
 * @since 0.3
 */
public class AttributePropertySource implements IPropertySource2 {
    private Object attr;
    IPropertyDescriptor[] descriptors;
    private static final String OTHER = Messages.AttributePropertySource_other;

    /**
     * Creates a new instance of AttributePropertySource
     *
     * @param type
     * @param attr
     */
    public AttributePropertySource( AttributeType type, Object attr ) {
        this.attr = attr;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
     */
    public boolean isPropertyResettable( Object id ) {
        return false;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return null;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (descriptors == null) {
            descriptors = new IPropertyDescriptor[]{new PropertyDescriptor(OTHER,
            		Messages.AttributePropertySource_value)};
        }

        IPropertyDescriptor[] c=new IPropertyDescriptor[descriptors.length];
        System.arraycopy(descriptors, 0, c, 0, c.length);
        return c;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue( Object id ) {
        if (id.equals(OTHER))
            return attr.toString();
        return null;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
     */
    public boolean isPropertySet( Object id ) {
        return false;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
     */
    public void resetPropertyValue( Object id ) {
        // do nothing
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object,
     *      java.lang.Object)
     */
    public void setPropertyValue( Object id, Object value ) {
        // do nothing
    }
}
