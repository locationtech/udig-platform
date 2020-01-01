/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.internal.commands;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.core.StaticProvider;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.render.ViewportModel;
import org.locationtech.udig.project.ui.internal.Messages;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Sets the CRS of the layer
 * @author Jesse
 * @since 1.1.0
 */
public class SetLayerCRSCommand extends AbstractCommand implements UndoableMapCommand {
    public static final String NAME=Messages.SetLayerCRSCommand_name; 

    private StaticProvider<ILayer> provider;
    private Layer layer;
    private CoordinateReferenceSystem crs,old;

    private ReferencedEnvelope oldBounds=null;

    public SetLayerCRSCommand(ILayer layer, CoordinateReferenceSystem crs){
        this.provider=new StaticProvider<ILayer>(layer);
        this.crs=crs;
    }
    
    public String getName() {
        return NAME;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(NAME, 1);
        if( layer==null )
            layer=(Layer) provider.get();
        old=layer.getCRS(monitor);
        monitor.setTaskName(NAME);
        layer.setCRS(crs);
        ViewportModel viewportModel = layer.getMapInternal().getViewportModelInternal();
            ReferencedEnvelope bounds = (ReferencedEnvelope) layer.getMapInternal().getViewportModel().getBounds();
            if( layer.getMapInternal().getMapLayers().size()==1 
                    && !bounds.intersects( (Envelope) layer.getBounds(monitor, viewportModel.getCRS()))){
                oldBounds=bounds;
                
                viewportModel.zoomToExtent();
            }
        monitor.done();
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {        
        String name=Messages.SetLayerCRSCommand_undoTask; 
        monitor.beginTask(name, 1);
        Layer layer=(Layer) provider.get();
        layer.setCRS(old);
        if( oldBounds!=null ){
            ViewportModel viewportModel = layer.getMapInternal().getViewportModelInternal();
            viewportModel.setBounds(oldBounds);
        }
        monitor.done();
    }

}
