/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
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
