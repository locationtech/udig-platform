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
package net.refractions.udig.tutorials.examples;

import java.io.IOException;

import net.refractions.udig.project.ILayer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;

/**
 * Obtains the selected features from a layer.
 * @author Jesse
 * @since 1.1.0
 */
public class GetSelectedFeaturesFromLayer {

    public void getSelectedFeaturesFromLayerRestrictedToFIDs(ILayer layer, IProgressMonitor monitor) throws IOException{

        monitor.beginTask("Loading Features", 100);
        monitor.worked(1);
        // first create a query that restricts the attributes returned.  Since we just want the FID then we don't need
        // any attributes because that is not an attribute it part of the Feature itself.
        // also use the Layer.getFilter() method to return the selected features.
        // the new String[0] parameter indicates that we don't want any attributes.  We could have put an attribute/column
        // name to indicate the attributes that we want.
        Query query = new DefaultQuery(layer.getSchema().getTypeName(), layer.getFilter(), new String[0]);

        FeatureSource featureSource = layer.getResource(FeatureSource.class, new SubProgressMonitor(monitor, 1));
        FeatureCollection features = featureSource.getFeatures(query);
        FeatureIterator featureIterator = features.features();
        try{
            SubProgressMonitor monitor2 = new SubProgressMonitor(monitor, 98);
            monitor2.beginTask("this is ignored", features.size());
            while(featureIterator.hasNext() ){
                monitor2.worked(1);
                Feature feature=featureIterator.next();

                // do something.
                // the features here will have no attributes or geometry but you can call getID().
                // warning you can't call getBounds() because no geometry was requested in the query.
                System.out.println( feature.getID() );
            }

        }finally{
            // don't forget to close the iterator...
            featureIterator.close();
            monitor.done();
        }

    }

}
