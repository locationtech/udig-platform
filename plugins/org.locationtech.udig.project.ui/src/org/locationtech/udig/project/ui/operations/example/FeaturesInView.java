/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.project.ui.operations.example;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 * Counts all the features that are within the current view.
 * 
 * @author jeichar
 * @since 1.0
 */
public class FeaturesInView implements IOp {

    /**
     * @see org.locationtech.udig.ui.operations.IOp#op(org.eclipse.swt.widgets.Display,
     *      java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void op( final Display display, Object target, IProgressMonitor monitor )
            throws Exception {
        IMap map = (IMap) target;

        Exception e = null;

        int featureCount = 0;
        for( ILayer layer : map.getMapLayers() ) {
            if (layer.isType(FeatureSource.class) && layer.isVisible()) {
                try {
                    FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.getResource(FeatureSource.class, monitor);
                    Filter filter = layer.createBBoxFilter(map.getViewportModel().getBounds(),
                            monitor);
                    FeatureCollection<SimpleFeatureType, SimpleFeature>  results = source.getFeatures(new Query(layer.getSchema()
                            .getName().getLocalPart(), filter));
                    int count = results.size();
                    // FeatureReader<SimpleFeatureType, SimpleFeature> reader=results.reader()
                    if (count > 0) {
                        featureCount += count;
                    }
                } catch (Exception e1) {
                    e = e1;
                    // continue
                }
            }
        }

        final Exception exception = e;
        final int finalCount = featureCount;

        display.asyncExec(new Runnable(){
            public void run() {
                if (exception == null)
                    MessageDialog.openInformation(display.getActiveShell(), 
                    		Messages.FeaturesInView_0,
                            Messages.FeaturesInView_1 + finalCount); 
                else
                    MessageDialog.openInformation(display.getActiveShell(), 
                    		Messages.FeaturesInView_0,
                    		MessageFormat.format(Messages.FeaturesInView_3, new Object[] {finalCount})
                    );
            }
        });
    }

}
