/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
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
 *
 */
package net.refractions.udig.project.ui.operations.example;

import java.text.MessageFormat;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
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
     * @see net.refractions.udig.ui.operations.IOp#op(org.eclipse.swt.widgets.Display,
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
                    FeatureCollection<SimpleFeatureType, SimpleFeature>  results = source.getFeatures(new DefaultQuery(layer.getSchema()
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
