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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.project.ui.summary.SummaryData;
import org.locationtech.udig.project.ui.summary.SummaryDialog;
import org.locationtech.udig.ui.operations.IOp;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Displays a summary of the layer in a dialog.
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class LayerSummary implements IOp {
    /**
     * @see org.locationtech.udig.ui.operations.IOp#op(org.eclipse.swt.widgets.Display,
     *      java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void op( final Display display, Object target, IProgressMonitor monitor )
            throws Exception {
        
        final Layer layer = (Layer) target;
        final CoordinateReferenceSystem layerCRS = layer.getCRS();
        final Envelope bounds = layer.getBounds(monitor, layerCRS);
        
        final List<SummaryData> data=new ArrayList<SummaryData>();
        
        data.add(new SummaryData(Messages.LayerSummary_name, layer.getName()));
        data.add(new SummaryData(Messages.LayerSummary_id,layer.getID()));
        data.add(new SummaryData(Messages.LayerSummary_zorder,layer.getZorder()));
        data.add(new SummaryData(Messages.LayerSummary_crs,layerCRS.getName()));
        data.add(new SummaryData(Messages.LayerSummary_bounds, parseBounds(bounds)));
        data.add(new SummaryData(Messages.LayerSummary_selection,layer.getFilter()));

        display.asyncExec(new Runnable(){
            public void run() {
                SummaryDialog d=new SummaryDialog( display.getActiveShell(), Messages.LayerSummary_title 
                        + layer.getName(), data );
                
                d.setBlockOnOpen(true);
                d.open();
            }
        });
    }
    
    public static String parseBounds( Envelope env ){
        String minx = chopDouble( env.getMinX() );
        String maxx = chopDouble( env.getMaxX() );
        String miny = chopDouble( env.getMinY() );
        String maxy = chopDouble( env.getMaxY() );
        return MessageFormat.format(Messages.LayerSummary_boundsFormat,
        		new Object[] { 
        			minx, miny,
        			maxx, maxy
        		});
    }
    
    private static String chopDouble( double d ){
        String s=String.valueOf(d);
        int end=s.indexOf('.')+2;
        while( end>s.length() )
            end--;
        return s.substring(0, end);
    }
}
