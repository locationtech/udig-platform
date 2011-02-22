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
package net.refractions.udig.project;

import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.ProjectPlugin;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureType;
import org.geotools.feature.IllegalAttributeException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * TODO Purpose of net.refractions.udig.project
 * <p>
 * </p>
 *
 * @author Jesse
 * @since 1.0.0
 */
public class AdaptableFeature implements IAdaptable, Feature, UDIGAdaptableDecorator {

    private Feature feature;
    private ILayer layer;

    /**
     * Construct <code>AdaptableFeature</code>.
     *
     * @param feature the wrapped feature
     * @param evaluationObject the layer that contains the feature.
     */
    public AdaptableFeature( Feature feature ) {
        this.feature = feature;
    }
    /**
     * Construct <code>AdaptableFeature</code>.
     *
     * @param feature the wrapped feature
     * @param layer the layer that contains the feature.
     */
    public AdaptableFeature( Feature feature, ILayer layer ) {
        this.feature = feature;
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

    /**
     * @see org.geotools.feature.Feature#getParent()
     */
    @SuppressWarnings("deprecation")
    public FeatureCollection getParent() {
        return feature.getParent();
    }

    /**
     * @see org.geotools.feature.Feature#setParent(org.geotools.feature.FeatureCollection)
     */
    public void setParent( FeatureCollection collection ) {
        feature.setParent(collection);
    }

    /**
     * @see org.geotools.feature.Feature#getFeatureType()
     */
    public FeatureType getFeatureType() {
        return feature.getFeatureType();
    }

    /**
     * @see org.geotools.feature.Feature#getID()
     */
    public String getID() {
        return feature.getID();
    }

    /**
     * @see org.geotools.feature.Feature#getAttributes(java.lang.Object[])
     */
    public Object[] getAttributes( Object[] attributes ) {
        return feature.getAttributes(attributes);
    }

    /**
     * @see org.geotools.feature.Feature#getAttribute(java.lang.String)
     */
    public Object getAttribute( String xPath ) {
        return feature.getAttribute(xPath);
    }

    /**
     * @see org.geotools.feature.Feature#getAttribute(int)
     */
    public Object getAttribute( int index ) {
        return feature.getAttribute(index);
    }

    /**
     * @see org.geotools.feature.Feature#setAttribute(int, java.lang.Object)
     */
    public void setAttribute( int position, Object val ) throws IllegalAttributeException,
            ArrayIndexOutOfBoundsException {
        feature.setAttribute(position, val);
    }

    /**
     * @see org.geotools.feature.Feature#getNumberOfAttributes()
     */
    public int getNumberOfAttributes() {
        return feature.getNumberOfAttributes();
    }

    /**
     * @see org.geotools.feature.Feature#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute( String xPath, Object attribute ) throws IllegalAttributeException {
        feature.setAttribute(xPath, attribute);
    }

    /**
     * @see org.geotools.feature.Feature#getDefaultGeometry()
     */
    public Geometry getDefaultGeometry() {
        return feature.getDefaultGeometry();
    }

    /**
     * @see org.geotools.feature.Feature#setDefaultGeometry(com.vividsolutions.jts.geom.Geometry)
     */
    public void setDefaultGeometry( Geometry geometry ) throws IllegalAttributeException {
        feature.setDefaultGeometry(geometry);
    }

    /**
     * @see org.geotools.feature.Feature#getBounds()
     */
    public Envelope getBounds() {
        return feature.getBounds();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj ) {
        return feature.equals(obj);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return feature.hashCode();
    }

    /**
     * @see net.refractions.udig.project.ui.internal.adapters.UDIGAdaptableDecorator#getObject()
     */
    public Object getObject() {
        return feature;
    }

    private boolean containsThis( IMap map ) {
        if (map.getEditManager() == null)
            return false;
        return map.getEditManager().getEditFeature() == this
                || map.getEditManager().getEditFeature() == feature;
    }

    @Override
    public String toString() {
        return feature.toString();
    }
}
