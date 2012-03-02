/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.project.internal.commands;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Messages;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Adds a Layer item to the map. More specifically, adds a Layer item into the LegendItem list of
 * the map.
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class AddLayerItemCommand extends AbstractCommand implements UndoableMapCommand {

    /**
     * Layer to be added
     */
    private Layer layer;
    
    /**
     * GeoResrouce to be added
     */
    private IGeoResource geoResource;
    
    /**
     * Insert position
     */
    private int index = -1;
    
    //private ILayer selectedLayer;
    
    /**
     * Creates an add layer command from a layer
     * @param layer
     */
    public AddLayerItemCommand( Layer layer ) {
        this(layer, -1);
    }
    
    /**
     * Creates an add layer command from a layer and index
     * @param layer
     * @param index
     */
    public AddLayerItemCommand( Layer layer, int index ) {
        this.layer = layer;
        this.index = index;
    }
    
    /**
     * Creates an add layer command from a GeoResource
     * @param geoResource
     */
    public AddLayerItemCommand( IGeoResource geoResource ) {
        this(geoResource, -1);
    }

    /**
     * Creates an add layer command from a GeoResource and index
     * @param geoResource
     * @param index
     */
    public AddLayerItemCommand( IGeoResource geoResource, int index ) {
        this.geoResource = geoResource;
        this.index = index;
    }
    
    @Override
    public String getName() {
        if (layer == null) {
            return Messages.AddLayerItemCommand_Name;
        } else {
            return Messages.AddLayerItemCommand_Name + layer.getName();    
        }
    }
    
    @Override
    public void run( IProgressMonitor monitor ) throws Exception {
        
        final Map map = getMap();
        
        if (layer == null) {
            if (geoResource instanceof IGeoResource) {
                layer = map.getLayerFactory().createLayer((IGeoResource) geoResource);
            }
            if (geoResource instanceof Layer) {
                layer = (Layer) geoResource;
            }
        }
        
        if (layer != null) {
            if (index < 0 || index > getMap().getLegend().size()) {
                map.getLegend().add(layer);    
            } else {
                map.getLegend().add(index, layer);
            }
            //selectedLayer = getMap().getEditManager().getSelectedLayer();
            //map.getEditManagerInternal().setSelectedLayer(layer);
        }
        
    }

    @Override
    public void rollback( IProgressMonitor monitor ) throws Exception {
        if (layer != null) {
            getMap().getLegend().remove(layer);
            //getMap().getEditManagerInternal().setSelectedLayer((Layer) selectedLayer);
        }
    }

}
