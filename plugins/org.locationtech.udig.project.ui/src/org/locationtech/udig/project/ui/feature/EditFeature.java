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
package org.locationtech.udig.project.ui.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.geotools.feature.DecoratingFeature;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.udig.project.IEditManager;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.commands.edit.SetAttributeCommand;
import org.locationtech.udig.project.internal.commands.edit.SetAttributesCommand;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * A SimpleFeature can handle setAttribute in a threadsafe way.
 * 
 * @since 1.2.0
 */
public class EditFeature extends DecoratingFeature implements IAdaptable, SimpleFeature {
    private IEditManager manager;
    
    // not used yet; could be used to "batch up" changes to send in one command?
    private Set<String> dirty = new LinkedHashSet<String>(); // we no longer need this

    private boolean batch;

    /**
     * Construct <code>AdaptableFeature</code>.
     * 
     * @param feature the wrapped feature
     * @param evaluationObject the layer that contains the feature.
     */
    public EditFeature( IEditManager manager ) {
        super(manager.getEditFeature());
        this.manager = manager;
    }
    
    public EditFeature( IEditManager manager, SimpleFeature feature ) {
        super( feature );
        this.manager = manager;
    }
    
    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    public Object getAdapter( Class adapter ) {
        if (IEditManager.class.isAssignableFrom(adapter)) {
            if (manager != null) {
                return manager;
            }
        }
        if (ILayer.class.isAssignableFrom(adapter)) {
            if (manager != null) {
                return manager.getEditLayer();
            }
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    @Override
    public boolean equals( Object obj ) {
        return delegate.equals(obj);
    }
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public void setAttribute( int index, Object value ) {
        SimpleFeatureType schema = getFeatureType();
        AttributeDescriptor attribute = schema.getAttributeDescriptors().get(index);
        String name = attribute.getLocalName();
        SetAttributeCommand sync = new SetAttributeCommand(name, value);
        dirty.add(name);
        manager.getMap().sendCommandASync(sync);
    }
    @Override
    public void setAttribute( Name name, Object value ) {
        SetAttributeCommand sync = new SetAttributeCommand(name.getLocalPart(), value);
        dirty.add(name.getLocalPart());
        manager.getMap().sendCommandASync(sync);
    }
    @Override
    public void setAttribute( String path, Object value ) {
        //System.out.println("made it to set attribute");
        SetAttributeCommand sync = new SetAttributeCommand(path, value);
        //System.out.println("made it to before dirty");
        dirty.add(path);
        //System.out.println("made it to after dirty");
        manager.getMap().sendCommandASync(sync);
    }
    @Override
    public void setAttributes( List<Object> values ) {
        String[] xpath;
        Object[] value;
        ArrayList<String> xpathlist = new ArrayList<String>();
        SimpleFeatureType schema = getFeatureType();
        for( PropertyDescriptor x : schema.getDescriptors() ) {
            xpathlist.add(x.getName().getLocalPart());
            dirty.add(x.getName().getLocalPart());
        }
        xpath = xpathlist.toArray(new String[xpathlist.size()]);
        value = values.toArray();
        SetAttributesCommand sync = new SetAttributesCommand(xpath, value);
        manager.getMap().sendCommandASync(sync);       
    }
    @Override
    public void setAttributes( Object[] values ) {
        String[] xpath;
        ArrayList<String> xpathlist = new ArrayList<String>();
        SimpleFeatureType schema = getFeatureType();
        for( PropertyDescriptor x : schema.getDescriptors() ) {
            xpathlist.add(x.getName().getLocalPart());
        }
        dirty.addAll(xpathlist);
        xpath = xpathlist.toArray( new String[xpathlist.size()]);
        SetAttributesCommand sync = new SetAttributesCommand(xpath, values);
        manager.getMap().sendCommandASync(sync);
    }
    @Override
    // This is simply the same as in DecoratingFeature.class
    public void setDefaultGeometry( Object geometry ) {
        GeometryDescriptor geometryDescriptor = getFeatureType().getGeometryDescriptor();
        setAttribute(geometryDescriptor.getName(), geometry);
    }
    @Override
    public void setDefaultGeometryProperty( GeometryAttribute geometryAttribute ) {
        if (geometryAttribute != null)
            setDefaultGeometry(geometryAttribute.getValue());
        else
            setDefaultGeometry(null);
    }

    @Override
    public void setValue( Collection<Property> values ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setValue( Object value ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultGeometry( Geometry geometry ) throws IllegalAttributeException {
        GeometryDescriptor geometryDescriptor = getFeatureType().getGeometryDescriptor();
        setAttribute(geometryDescriptor.getName(), geometry);
    }

    public void setBatch( boolean batch ){
        this.batch = batch;
    }
}
