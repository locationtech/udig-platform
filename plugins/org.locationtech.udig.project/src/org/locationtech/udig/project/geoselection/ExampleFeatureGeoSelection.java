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
package org.locationtech.udig.project.geoselection;

import java.util.ArrayList;
import java.util.Iterator;

import org.locationtech.udig.project.ILayer;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Example what is a real IGeoSelection.
 * 
 * @author Vitalus
 */
public class ExampleFeatureGeoSelection extends AbstractGeoSelection {

    final private SimpleFeature feature;

    final private ILayer layer;

    private ArrayList list;

    public ExampleFeatureGeoSelection( SimpleFeature feature, ILayer layer ) {
        super();
        this.feature = feature;
        this.layer = layer;

    }

    public Iterator iterator() {
        if (list == null) {
            list = new ArrayList(2);
            list.add(feature);
            list.add(layer);
        }
        return list.iterator();
    }

    public Object getAdapter( Class adapter ) {
        if (SimpleFeature.class.isAssignableFrom(adapter))
            return feature;
        if (ILayer.class.isAssignableFrom(adapter))
            return layer;

        return null;
    }

}
