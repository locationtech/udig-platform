/* uDig - User Friendly Desktop Internet GIS client
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

import org.locationtech.udig.project.internal.Layer;

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
                switch( featureEvent.getType() ) {
                case ADDED:
                    // do something
                    break;
                case REMOVED:
                    // do something
                    break;
                case CHANGED:
                    // do something
                    break;

                default:
                    break;
                }
            }
            
        });
    }
}
