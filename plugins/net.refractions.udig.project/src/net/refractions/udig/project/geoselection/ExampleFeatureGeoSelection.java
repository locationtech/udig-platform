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
package net.refractions.udig.project.geoselection;

import java.util.ArrayList;
import java.util.Iterator;

import net.refractions.udig.project.ILayer;

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
