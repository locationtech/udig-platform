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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Query;
import org.geotools.feature.FeatureIterator;
import org.geotools.ows.wms.WebMapServer;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.internal.ProjectUIPlugin;
import org.locationtech.udig.project.ui.summary.SummaryData;
import org.locationtech.udig.project.ui.summary.SummaryDialog;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * An Operation that can operate on either GeoResources or on FeatureSources. If the object is a
 * georesource the operation prints out a summary about the resource. if the object is a
 * FeatureSource the number of features are printed.
 * 
 * @author jones
 */
public class MultiTargetOp implements IOp {

    /**
     * @see org.locationtech.udig.ui.operations.IOp#op(java.lang.Object)
     */
    public void op( final Display display, Object target, IProgressMonitor monitor )
            throws Exception {
        if (target instanceof IGeoResource) {
            IGeoResource resource = (IGeoResource) target;
            op(display, monitor, resource);
        } else if (target instanceof FeatureSource) {
            FeatureSource<SimpleFeatureType, SimpleFeature> source = (FeatureSource<SimpleFeatureType, SimpleFeature>) target;
            op(display, monitor, source);
        }
    }

    /**
     * @param display
     * @param monitor
     * @param resource
     * @throws IOException
     */
    private void op( final Display display, IProgressMonitor monitor, IGeoResource resource )
            throws IOException {
        IGeoResourceInfo info = resource.getInfo(monitor);
        Envelope bounds = info.getBounds();
        final List<SummaryData> data=new ArrayList<SummaryData>(); 
        String crs;
        if (info.getCRS() != null)
            crs = info.getCRS().getName().toString();
        else
            crs = Messages.MultiTargetOp_unknown; 
        crs=crs.replace('\n', ' ');
        
        try {
            data.add(new SummaryData(Messages.MultiTargetOp_name, info.getName()));
            data.add(new SummaryData( Messages.MultiTargetOp_title, info.getTitle()));
            data.add(new SummaryData( Messages.MultiTargetOp_bounds, LayerSummary.parseBounds(bounds) ));
            data.add(new SummaryData( Messages.MultiTargetOp_crs, crs));
            data.add(new SummaryData( Messages.MultiTargetOp_featuresource, resource.canResolve(FeatureSource.class)));
            data.add(new SummaryData( Messages.MultiTargetOp_featurestore, resource.canResolve(FeatureStore.class)));
            data.add(new SummaryData( Messages.MultiTargetOp_wms, resource.canResolve(WebMapServer.class)));
            boolean first=false;
            for( String word : info.getKeywords() ) {
                if( first )
                    data.add(new SummaryData( Messages.MultiTargetOp_keywords, word));
                else
                    data.add(new SummaryData( null , word ));
            }
        } catch (Exception e) {
            display.asyncExec(new Runnable(){
                public void run() {
                    MessageDialog.openError(display.getActiveShell(), Messages.MultiTargetOp_resource_summary, 
                            Messages.MultiTargetOp_error); 
                }
            });
            ProjectUIPlugin.log(null, e);

        }
        display.asyncExec(new Runnable(){
            public void run() {
                Dialog d=new SummaryDialog(display.getActiveShell(), Messages.MultiTargetOp_resource_summary,
                        data);
                d.setBlockOnOpen(true);
                d.open();
            }
        });
    }

    private void op( final Display display, IProgressMonitor monitor,  FeatureSource<SimpleFeatureType, SimpleFeature> source )
            throws IOException {
        int tmp = 0;
        tmp = source.getCount(Query.ALL);
        if (tmp == -1) {
            FeatureIterator<SimpleFeature> iter = source.getFeatures().features();

            try {
                while( iter.hasNext() ) {
                    try {
                        iter.next();
                    } catch (Exception e) {
                        // do nothing
                    }
                    tmp++;
                }
            } finally {
                iter.close();
            }
        }
        final int features = tmp;
        display.asyncExec(new Runnable(){
            public void run() {
                MessageDialog
                        .openInformation(
                                display.getActiveShell(),
                                "Number of features", //$NON-NLS-1$
                                Messages.MultiTargetOp_number + (features == -1 ? Messages.MultiTargetOp_expensive : String.valueOf(features)));  
            }
        });
    }

}
