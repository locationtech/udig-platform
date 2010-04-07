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
package net.refractions.udig.project.ui.internal.commands;

import net.refractions.udig.core.StaticProvider;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.render.ViewportModel;
import net.refractions.udig.project.ui.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

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
