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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.FilterAttributeExtractor;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

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
        @SuppressWarnings("unchecked")
		FeatureSource<SimpleFeatureType, SimpleFeature> source = layer
                .getResource(FeatureSource.class, new NullProgressMonitor());
        
        FilterAttributeExtractor extractor = new FilterAttributeExtractor(source.getSchema());
        layer.getFilter().accept(extractor, null);
        String[] atts = extractor.getAttributeNames();
        Query query = new Query(source.getSchema().getTypeName(),Filter.INCLUDE,atts);
        FeatureCollection<SimpleFeatureType, SimpleFeature> allCollection = source.getFeatures(query);

        FeatureIterator<SimpleFeature> allFeatures = allCollection.features();

        try {
            int selectedCount = 0;
            int allCount = 0;
            long lastUpdate = System.currentTimeMillis();
            while( allFeatures.hasNext() ) {
                SimpleFeature next = allFeatures.next();
                allCount++;
                if(layer.getFilter().evaluate(next)) {
                	selectedCount++;
                }
            	if(System.currentTimeMillis() - lastUpdate > 1000) {
                	monitor.setTaskName("Count: Processed "+allCount+" features");
                	lastUpdate = System.currentTimeMillis();
            	}
            }
            
            final int finalAllCount = allCount;
            final int finalSelectedCount = selectedCount;
            display.asyncExec(new Runnable(){
                public void run() {
                    String pattern = "There are {0} features in the current layer, of which {1} are selected";
                    String message = MessageFormat.format(pattern, finalAllCount, finalSelectedCount);
                    MessageDialog.openInformation(display.getActiveShell(), "Selected Features", message);
                }
            });
        } finally {
            allFeatures.close();
            monitor.done();
        }
    }

}
