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

import net.refractions.udig.project.internal.Layer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureSource;

/**
 * In this example an FeatureEvent listener is created and added to a FeatureSource in order to receive
 * notification when the data is changed, for example when an edit occurs.
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class EditEventListeners {

    public void createAndAddListener(Layer layer, IProgressMonitor monitor) throws IOException {
        FeatureSource source=layer.getResource(FeatureSource.class, monitor);
        source.addFeatureListener(new FeatureListener(){

            public void changed( FeatureEvent featureEvent ) {
                switch( featureEvent.getEventType() ) {
                case FeatureEvent.FEATURES_ADDED:
                    // do something
                    break;
                case FeatureEvent.FEATURES_REMOVED:
                    // do something
                    break;
                case FeatureEvent.FEATURES_CHANGED:
                    // do something
                    break;

                default:
                    break;
                }
            }
            
        });
    }
}
