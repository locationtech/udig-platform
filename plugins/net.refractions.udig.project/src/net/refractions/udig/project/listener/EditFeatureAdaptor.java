/*
 *    Parkinfo
 *    http://qpws/parkinfo
 *
 *    (C) 2011, Department of Environment Resource Management
 *
 *    This code is provided for department use.
 */
package net.refractions.udig.project.listener;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;


/**
 * Abstract implementation to help with implementation of anonymous inner classes.
 * <p>
 * Example:<pre>
 * editFeature.addListener( new EditFeatureListenerAdaptor(){
 *     public void activate(EditFeature feature) {
 *         return true;
 *     }
 * });</pre>
 * @author leviputna
 *
 */
public abstract class EditFeatureAdaptor implements EditFeatureListener {

    
    @Override
    public Boolean beforeEdit(SimpleFeature feature, AttributeDescriptor attributeDescriptor) {
        return true;
    }

    @Override
    public void afterEdit(SimpleFeature feature, AttributeDescriptor attributeDescriptor) {
    }

    @Override
    public Boolean beforeCreate(SimpleFeature feature) {
        return true;
    }

    @Override
    public void afterCreate(SimpleFeature feature) {
    }

    @Override
    public Boolean beforeDelete(SimpleFeature feature) {
        return null;
    }

    @Override
    public void afterDelete(SimpleFeature feature) {
    }

    @Override
    public Boolean beforeDesplay(SimpleFeature feature) {
        return true;
    }

}
