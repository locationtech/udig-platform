/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004, Refractions Research Inc. This
 * library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; version 2.1 of the License. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.ui.internal.properties;

import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * TODO provide type description
 * 
 * @author jeichar
 */
public class GeomPropertySource implements IPropertySource2 {
    private static final String TYPE = "TYPE"; //$NON-NLS-1$
    private static final String AREA = "AREA"; //$NON-NLS-1$
    private static final String GEOM = "GEOM"; //$NON-NLS-1$

    private Geometry geom;
    private final Geometry original;
    private final IPropertyDescriptor[] propertyDescriptors;
    
    /**
     * Creates a new instance of GeomPropertySource
     * 
     * @param geometry the geometry that is the source for this PropertySource
     */
    public GeomPropertySource( Geometry geometry ) {
        this.geom = geometry;
        this.original = geometry;
        propertyDescriptors = new IPropertyDescriptor[2];
        propertyDescriptors[0] = new PropertyDescriptor(new ID(AREA), 
                Messages.GeomPropertySource_area); 
        propertyDescriptors[1] = new TextPropertyDescriptor(new ID(GEOM), 
                Messages.GeomPropertySource_WKT);
    }


    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
     */
    public Object getEditableValue() {
        return geom;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {

        IPropertyDescriptor[] c=new IPropertyDescriptor[propertyDescriptors.length];
        System.arraycopy(propertyDescriptors, 0, c, 0, c.length);
        return c;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
     */
    public Object getPropertyValue( Object idObject ) {
        ID id = (ID) idObject;
        if (id.id == TYPE)
            return geom.getGeometryType();
        if (id.id == AREA)
            return String.valueOf(geom.getArea());
        if (id.id == GEOM) {
            return geomToText();
        }
        return null;
    }


    private Object geomToText() {
        WKTWriter writer = new WKTWriter();
        String text = writer.write(geom);
        text = text.replaceAll("[\\n\\r\\t]", " ");
        return text;
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
        geom = original;
    }
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object,
     *      java.lang.Object)
     */
    public void setPropertyValue( Object idObject, Object value ) {
        ID id = (ID) idObject;
        if (id.id == GEOM) {
            WKTReader reader = new WKTReader();
            try {
                geom = reader.read((String) value);
            } catch (ParseException e) {
                throw (RuntimeException) new RuntimeException( ).initCause( e );
            }
        }
        
    }

    static class ID {
        String id;
        ID( String id ) {
            this.id = id;
        }
    }

    /**
     * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
     */
    public boolean isPropertyResettable( Object id ) {
        return true;
    }
}
