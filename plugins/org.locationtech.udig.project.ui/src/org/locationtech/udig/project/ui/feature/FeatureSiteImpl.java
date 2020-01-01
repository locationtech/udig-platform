/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.feature;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.internal.Map;
import org.locationtech.udig.project.ui.IFeatureSite;
import org.locationtech.udig.project.ui.internal.tool.impl.ToolContextImpl;
import org.opengis.feature.simple.SimpleFeature;

/**
 * Facilitate editing of feature content.
 */
public class FeatureSiteImpl extends ToolContextImpl implements IFeatureSite {

    EditFeature editFeature;

    public FeatureSiteImpl() {

    }

    public FeatureSiteImpl( ILayer layer ) {
        this(layer.getMap());
    }

    public FeatureSiteImpl( IMap map ) {
        setMapInternal((Map) map);
    }

    public void setFeature( SimpleFeature feature ) {
        if (feature == null) {
            editFeature = null;
            return;
        }
        if (editFeature != null && feature != null && editFeature.getID().equals(feature.getID())) {
            // they are the same
            return;
        }
        editFeature = new EditFeature(getEditManager(), feature);
    }

    public EditFeature getEditFeature() {
        if( editFeature == null && getEditManager() != null ){
            setFeature( getEditManager().getEditFeature() );
        }
        return editFeature;
    }

    public void setMapInternal( Map map ) {
        if( map == getMap() ){
            return;
        }
        super.setMapInternal(map);
    }

    /**
     * Copy the provided FeatureSite.
     * 
     * @param copy
     */
    public FeatureSiteImpl( FeatureSiteImpl copy ) {
        super(copy);
    }

    public FeatureSiteImpl copy() {
        return new FeatureSiteImpl(this);
    }
}
