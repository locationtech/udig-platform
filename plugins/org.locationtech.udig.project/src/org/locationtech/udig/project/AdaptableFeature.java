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
package org.locationtech.udig.project;

import org.locationtech.udig.project.internal.Project;
import org.locationtech.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.geotools.feature.DecoratingFeature;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A SimpleFeature that can adapt to it's layer.
 * 
 * @author Jesse Eichar
 * @since 1.0.0
 */
public class AdaptableFeature extends DecoratingFeature implements IAdaptable, SimpleFeature, UDIGAdaptableDecorator {

    private ILayer layer;

    /**
     * Construct <code>AdaptableFeature</code>.
     * 
     * @param feature the wrapped feature
     * @param evaluationObject the layer that contains the feature.
     */
    public AdaptableFeature( SimpleFeature feature ) {
    	this( feature, null);
    }
    /**
     * Construct <code>AdaptableFeature</code>.
     * 
     * @param feature the wrapped feature
     * @param layer the layer that contains the feature.
     */
    public AdaptableFeature( SimpleFeature feature, ILayer layer ) {
    	super( feature );
        this.layer = layer;
    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter ) {
        if (ILayer.class.isAssignableFrom(adapter)) {
            if (layer != null)
                return layer;

            for( Project project : ProjectPlugin.getPlugin().getProjectRegistry().getProjects() ) {
                for( IMap current : project.getElements(IMap.class) ) {
                    if (containsThis(current))
                        return current.getEditManager().getEditLayer();
                }
            }
        }
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof AdaptableFeature) {
            return delegate.equals(((AdaptableFeature) obj).delegate);
        }
        return delegate.equals(obj);
    }
    
    @Override
    public int hashCode() {
    	return delegate.hashCode();
    }
    
    /**
     * @see org.locationtech.udig.project.ui.internal.adapters.UDIGAdaptableDecorator#getObject()
     */
    public Object getObject() {
        return delegate;
    }

    private boolean containsThis( IMap map ) {
        if (map.getEditManager() == null)
            return false;
        return map.getEditManager().getEditFeature() == this
                || map.getEditManager().getEditFeature() == delegate;
    }

}
