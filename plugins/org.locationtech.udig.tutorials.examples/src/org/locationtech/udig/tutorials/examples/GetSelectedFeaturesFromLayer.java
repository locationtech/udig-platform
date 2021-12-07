/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.locationtech.udig.project.ILayer;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Obtains the selected features from a layer.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class GetSelectedFeaturesFromLayer {

    @SuppressWarnings({ "unchecked", "unused" })
    public void getSelectedFeaturesFromLayerRestrictedToFIDs(ILayer layer, IProgressMonitor monitor)
            throws IOException {

        monitor.beginTask("Loading Features", 100); //$NON-NLS-1$
        monitor.worked(1);
        /**
         * First create a query that restricts the attributes returned. Since we just want the FID
         * then we don't need any attributes because that is not an attribute it part of the Feature
         * itself. Also use the Layer.getFilter() method to return the selected features. The new
         * String[0] parameter indicates that we don't want any attributes. We could have put an
         * attribute/column name to indicate the attributes that we want.
         */
        SimpleFeatureType schema = layer.getSchema();
        String typeName = schema.getTypeName();
        Filter selected = layer.getFilter();
        Query query = new Query(typeName, selected, Query.ALL_NAMES);

        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = layer
                .getResource(FeatureSource.class, SubMonitor.convert(monitor, 1));
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource
                .getFeatures(query);
        FeatureIterator<SimpleFeature> featureIterator = features.features();
        try {
            SubMonitor monitor2 = SubMonitor.convert(monitor, 98);
            monitor2.beginTask("this is ignored", features.size()); //$NON-NLS-1$
            while (featureIterator.hasNext()) {
                monitor2.worked(1);
                SimpleFeature feature = featureIterator.next();

                /**
                 * Do something. The features here will have no attributes or geometry but you can
                 * call getID(). Warning: you can't call getBounds() because no geometry was
                 * requested in the query. Replace the parameter Query.ALL_NAMES with an array of
                 * attribute names you want to retrieve.
                 */
            }
        } finally {
            // don't forget to close the iterator...
            featureIterator.close();
            monitor.done();
        }
    }

}
