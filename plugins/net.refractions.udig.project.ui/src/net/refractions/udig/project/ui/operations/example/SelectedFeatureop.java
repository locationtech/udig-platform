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
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Counts the selected and non selected features on the layer
 * 
 * @author jesse
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SelectedFeatureop implements IOp {

    public void op( final Display display, Object target, IProgressMonitor monitor ) throws Exception {
        monitor.beginTask("Counting features", IProgressMonitor.UNKNOWN);
        monitor.worked(1);

        final ILayer layer = (ILayer) target;
        FeatureSource<SimpleFeatureType, SimpleFeature> source = layer
                .getResource(FeatureSource.class, new NullProgressMonitor());
        FeatureCollection<SimpleFeatureType, SimpleFeature> selectedCollection = source.getFeatures(layer.getFilter());
        FeatureCollection<SimpleFeatureType, SimpleFeature> allCollection = source.getFeatures();

        FeatureIterator<SimpleFeature> selectedFeatures = selectedCollection.features();
        FeatureIterator<SimpleFeature> allFeatures = allCollection.features();

        try {
            int selectedCount = 0;
            while( selectedFeatures.hasNext() ) {
                selectedFeatures.next();
                selectedCount++;
            }
            final int finalSelectedCount = selectedCount;
            int allCount = 0;
            while( allFeatures.hasNext() ) {
                allFeatures.next();
                allCount++;
            }
            final int finalAllCount = allCount;
            display.asyncExec(new Runnable(){
                public void run() {
                    String pattern = "There are {0} features in the current layer, of which {1} selected";
                    String message = MessageFormat.format(pattern, new Object[]{finalAllCount, finalSelectedCount});
                    MessageDialog.openInformation(display.getActiveShell(), "Selected Features", message);
                }
            });
        } finally {
            selectedFeatures.close();
            allFeatures.close();
            monitor.done();
        }
    }

}
