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
package net.refractions.udig.project.ui.feature;

import java.util.Collection;
import java.util.List;

import net.refractions.udig.project.IEditManager;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.CompositeCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.command.factory.EditCommandFactory;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.internal.commands.edit.SetAttributeCommand;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.geotools.feature.DecoratingFeature;
import org.geotools.feature.IllegalAttributeException;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A SimpleFeature can handle setAttribute in a threadsafe way.
 * 
 * @since 1.2.0
 */
public class EditFeature extends DecoratingFeature implements IAdaptable, SimpleFeature {
    private IEditManager manager;
    
    /**
     * True if the feature has been edited.
     */
    boolean isDirty=true;

    /**
     * Construct <code>AdaptableFeature</code>.
     * 
     * @param feature the wrapped feature
     * @param evaluationObject the layer that contains the feature.
     */
    public EditFeature( IEditManager manager ) {
        super( manager.getEditFeature() );
        this.manager = manager;
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter ) {
        if (IEditManager.class.isAssignableFrom(adapter)) {
            if (manager != null){
                return manager;
            }
        }
        if (ILayer.class.isAssignableFrom(adapter)) {
            if (manager != null){
                return manager.getEditLayer();
            }
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    @Override
    public boolean equals(Object obj) {
    	return delegate.equals( obj );
    }
    @Override
    public int hashCode() {
    	return delegate.hashCode();
    }
    
    @Override
    public void setAttribute( int index, Object value ) {
        isDirty = true;
        SimpleFeatureType schema = getFeatureType();
        AttributeDescriptor attribute = schema.getAttributeDescriptors().get( index );
        
        SetAttributeCommand sync = new SetAttributeCommand( attribute.getLocalName(), value);
        
        manager.getMap().sendCommandASync( sync );
    }
    @Override
    public void setAttribute( Name name, Object value ) {
        isDirty = true;
        EditCommandFactory factory = EditCommandFactory.getInstance();

        UndoableMapCommand sync = factory.createSetAttributeCommand( name.getLocalPart(), value);
        manager.getMap().sendCommandASync( sync );
    }
    @Override
    public void setAttribute( String path, Object attribute ) {
        // TODO: call edit manager with a setAttribute command!
    }
    @Override
    public void setAttributes( List<Object> arg0 ) {
        // TODO: call edit manager with a setAttribute command!
    }
    @Override
    public void setAttributes( Object[] arg0 ) {
        // TODO: call edit manager with a setAttribute command!
    }
    @Override
    public void setDefaultGeometry( Object geometry ) {
        // TODO: call edit manager with a setAttribute command!
    }
    @Override
    public void setDefaultGeometryProperty( GeometryAttribute arg0 ) {
        // TODO: call edit manager with a setAttribute command!
    }
    @Override
    public void setValue( Collection<Property> arg0 ) {
        // TODO: call edit manager with a setAttribute command!
    }
    @Override
    public void setValue( Object arg0 ) {
        // TODO: call edit manager with a setAttribute command!
    }
    @Override
    public void setDefaultGeometry( Geometry geometry ) throws IllegalAttributeException {
        // TODO: call edit manager with a setAttribute command!
    }
}
