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
import java.util.ArrayList;
import java.util.List;

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.Messages;
import net.refractions.udig.project.ui.summary.SummaryData;
import net.refractions.udig.project.ui.summary.SummaryDialog;
import net.refractions.udig.ui.operations.IOp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Displays a summary of the layer in a dialog.
 * 
 * @author jeichar
 * @since 0.6.0
 */
public class LayerSummary implements IOp {
    /**
     * @see net.refractions.udig.ui.operations.IOp#op(org.eclipse.swt.widgets.Display,
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
