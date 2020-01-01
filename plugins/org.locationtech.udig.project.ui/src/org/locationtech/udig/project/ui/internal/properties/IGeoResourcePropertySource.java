/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.internal.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource2;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.geotools.data.FeatureSource;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ui.internal.Messages;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * A Property source for services.
 * 
 * @author jeichar
 * @since 0.3
 */
public class IGeoResourcePropertySource implements IPropertySource2 {
    private IGeoResource geoResource;
    private IPropertyDescriptor[] descriptors;
    private static final String RESOURCE = Messages.IGeoResourcePropertySource_0; 
    IPropertyDescriptor resourceDescriptor;
    private static final String FEATURE_SOURCE = Messages.IGeoResourcePropertySource_1; 
    /**
     * Creates a new instance of DataPropertySource
     * 
     * @param entry
     */
    public IGeoResourcePropertySource( IGeoResource entry ) {
        this.geoResource = entry;
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
            try {
                if (geoResource.canResolve(FeatureSource.class)) {
                    resourceDescriptor = new SchemaDescriptor(
                            FEATURE_SOURCE,
                            Messages.IGeoResourcePropertySource_schema, (FeatureSource<SimpleFeatureType, SimpleFeature>) geoResource.resolve(FeatureSource.class, null)); 
                } else {
                    resourceDescriptor = new PropertyDescriptor(RESOURCE, 
                    		Messages.IGeoResourcePropertySource_data);
                }
            } catch (IOException e) {
                // TODO Catch e
                e.printStackTrace();
            }
            desc.add(resourceDescriptor);
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
        if (id.equals(FEATURE_SOURCE)) {
            return resourceDescriptor;
        }
        if (id.equals(RESOURCE))
            try {
                return geoResource.resolve(FeatureSource.class, null);
            } catch (IOException e) {
                // TODO Catch e
                e.printStackTrace();
            }
        return null;
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
    /**
     * @see org.eclipse.ui.views.properties.IPropertySource2#isPropertyResettable(java.lang.Object)
     */
    public boolean isPropertyResettable( Object id ) {
        // TODO implement method body
        return true;
    }
}
