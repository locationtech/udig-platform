/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.core;

import java.util.Collection;
import java.util.Iterator;

import org.geotools.feature.collection.AdaptorFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Converts a java.util.Collection to a FeatureCollection
 * @author Jesse
 * @since 1.1.0
 */
public class StaticFeatureCollection extends AdaptorFeatureCollection {

    private Collection<SimpleFeature> features;

    public StaticFeatureCollection( Collection<SimpleFeature> features, SimpleFeatureType memberType) {
        super("static",memberType);
        this.features=features;
    }

    @Override
    protected void closeIterator( Iterator close ) {
        //nothing to do
    }

    @Override
    protected Iterator openIterator() {
        return features.iterator();
    }

    @Override
    public int size() {
        return features.size();
    }

}
