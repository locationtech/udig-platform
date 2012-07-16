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


public class EditFeatureListenerImpl implements EditFeatureListener {

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
