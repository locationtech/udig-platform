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
package net.refractions.udig.project;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;

public class AdaptingFeatureType implements SimpleFeatureType, IAdaptable, UDIGAdaptableDecorator {

    private Object target;
    private SimpleFeatureType delegate;

    public AdaptingFeatureType( SimpleFeatureType featureType, Object adaptor ){
        this.delegate = featureType;
        this.target = adaptor;
        
    }
    
    public SimpleFeatureType getObject() {
        return delegate;
    }
    
    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
        if( target != null  && adapter.isInstance(target)){
            return target;
        }
        return Platform.getAdapterManager().getAdapter(delegate, adapter);
    }
    
    public int getAttributeCount() {
        return delegate.getAttributeCount();
    }

    public List<AttributeDescriptor> getAttributeDescriptors() {
        return delegate.getAttributeDescriptors();
    }

    public AttributeDescriptor getDescriptor( String name) {
        return delegate.getDescriptor( name );
    }

    public AttributeDescriptor getDescriptor( Name arg0 ) {
        return delegate.getDescriptor(arg0);
    }

    public AttributeDescriptor getDescriptor( int arg0 ) throws IndexOutOfBoundsException {
        return delegate.getDescriptor(arg0);
    }

    public AttributeType getType( String arg0 ) {
        return delegate.getType(arg0);
    }

    public AttributeType getType( Name arg0 ) {
        return delegate.getType(arg0);
    }

    public AttributeType getType( int arg0 ) throws IndexOutOfBoundsException {
        return delegate.getType(arg0);
    }

    public String getTypeName() {
        return delegate.getTypeName();
    }

    public List<AttributeType> getTypes() {
        return delegate.getTypes();
    }

    public int indexOf( String arg0 ) {
        return delegate.indexOf(arg0);
    }

    public int indexOf( Name arg0 ) {
        return delegate.indexOf(arg0);
    }

    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return delegate.getCoordinateReferenceSystem();
    }

    public GeometryDescriptor getGeometryDescriptor() {
        return delegate.getGeometryDescriptor();
    }

    public boolean isIdentified() {
        return delegate.isIdentified();
    }

    public Class<Collection<Property>> getBinding() {
        return delegate.getBinding();
    }

    public Collection<PropertyDescriptor> getDescriptors() {
        return delegate.getDescriptors();
    }

    public boolean isInline() {
        return delegate.isInline();
    }

    public AttributeType getSuper() {
        return delegate.getSuper();
    }

    public InternationalString getDescription() {
        return delegate.getDescription();
    }

    public Name getName() {
       return delegate.getName();
    }

    public List<Filter> getRestrictions() {
        return delegate.getRestrictions();
    }

    public Map<Object, Object> getUserData() {
        return delegate.getUserData();
    }

    public boolean isAbstract() {
        return delegate.isAbstract();
    }

}
