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
package net.refractions.udig.project.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.ILayerListener;
import net.refractions.udig.project.IMapCompositionListener;
import net.refractions.udig.project.LayerEvent;
import net.refractions.udig.project.MapCompositionEvent;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.commands.AddLayersCommand;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * 
 * The Grid Handler of the Legend View. This class is designed to handle the maintenance of the
 * grid layer visibility toggle functionality. 
 * 
 * @author Naz Chan (LISAsoft)
 * @since 1.3.1
 */
public class LegendViewGridHandler implements ILayerListener, IMapCompositionListener {

    private Map map;
    private List<Layer> gridLayers;
    private Action gridAction;
    
    private boolean isLegendViewAddingGrid = false;
    
    /**
     * Creates a LegendViewGridHandler
     */
    public LegendViewGridHandler() {
        //Nothing yet
    }
    
    /**
     * Sets the current map
     * @param map
     */
    public void setMap(Map map) {
        cleanHandler();
        initMap(map);
        initGridLayers(map);
        setGridActionState();
    }
    
    /**
     * Cleans up the handler of listeners and objects.
     */
    public void disposeHandler() {
        cleanHandler();
        if (gridAction != null) {
            gridAction = null;
        }
    }
    
    /**
     * Cleans the handler of listeners attached to the grid layers, current map and the current map itself.
     */
    private void cleanHandler() {
        
        if (gridLayers != null) {
            for( Layer gridLayer : gridLayers ) {
                gridLayer.removeListener(this);
            }
            for( int i = 0; i < gridLayers.size(); i++ ) {
                gridLayers.remove(i);
            }
            gridLayers = null;            
        }
        
        if (map != null) {
            map.removeMapCompositionListener(this);
            map = null;
        }
        
    }
    
    /**
     * Initialises the current map and adds listeners to it.
     * @param map
     */
    private void initMap(Map map) {
        
        this.map = map;
        
        if (map != null) {
            this.map.addMapCompositionListener(this);
        }
        
    }
    
    /**
     * Initialises the grid layers from the passed map. The method traverses the map and looks for
     * grid layers and add these to the grid layers list.
     * 
     * @param map
     */
    private void initGridLayers(Map map) {
        
        if (map != null) {

            this.gridLayers = new ArrayList<Layer>();
            
            for( ILayer layer : map.getMapLayers() ) {
                if (LegendViewUtils.isGridLayer(layer)) {
                    addGridLayer((Layer) layer);
                }
            }
            
            if (gridLayers.size() == 0) {
                this.isLegendViewAddingGrid = true;
                this.map.sendCommandASync(new AddLayersCommand(Collections.singletonList(LegendViewUtils.getGridMapGraphic())));
            }
            
        }
                
    }
    
    /**
     * Adds a grid layer to the grid layer list
     * @param layer
     */
    private void addGridLayer(Layer layer) {
        if (layer != null) {
            layer.addListener(this);
            this.gridLayers.add(layer);    
        }
    }
    
    /**
     * Removes a grid layer from the grid layer list
     * @param layer
     */
    private void removeGridLayer(Layer layer) {
        if (layer != null) { 
            layer.removeListener(this);
            this.gridLayers.remove(layer);    
        }
    }

    /**
     * Initialises and returns the gridAction action.
     * @return
     */
    public Action getGridAction() {
        
        gridAction = new Action(null, IAction.AS_CHECK_BOX){
            @Override
            public void run() {
                toggleGrid(gridAction.isChecked());
            }
        };
        gridAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.GRID_OBJ));
        
        setGridActionState();
        
        return gridAction;
        
    }
    
    /**
     * Toggles the visibility of the grid layers in the grid list
     * @param isChecked
     */
    private void toggleGrid(boolean isChecked) {
        for( Layer gridLayer : gridLayers ) {
            gridLayer.setVisible(isChecked);
        }
    }
    
    /**
     * For testing only
     * @param isChecked
     */
    public void testToggleGrid(boolean isChecked) { 
        toggleGrid(isChecked);
    }
    
    /**
     * ILayerListener method
     */
    @Override
    public void refresh( LayerEvent event ) {
        setGridActionState();
    }
    
    /**
     * IMapCompositionListener method
     */
    @Override
    public void changed( MapCompositionEvent event ) {

        if (MapCompositionEvent.EventType.ADDED == event.getType()) {
            final Layer layer = (Layer) event.getNewValue();
            if (LegendViewUtils.isGridLayer(layer)) {
                if (this.isLegendViewAddingGrid) {
                    layer.setVisible(false);
                    this.isLegendViewAddingGrid = false;
                }
                addGridLayer(layer);
            }
        } else if (MapCompositionEvent.EventType.REMOVED == event.getType()) {
            final Layer layer = (Layer) event.getOldValue();
            if (LegendViewUtils.isGridLayer(layer)) {
                removeGridLayer(layer);
            }
        }
        
        setGridActionState();
        
    }
    
    /**
     * Sets the grid action (button) state. This manages the enabled/disabled and checked/unchecked
     * states of the action.
     */
    private void setGridActionState() {
        if (gridAction != null) {
            if (gridLayers == null) {
                gridAction.setEnabled(false);
            } else {
                if (gridLayers.size() > 0) {
                    gridAction.setEnabled(true);
                    setGridActionCheckedState();
                } else {
                    gridAction.setEnabled(false);
                }                
            }
        }
    }

    /**
     * This manages the checked/unchecked states of the action as well as toggling the tooltip.
     */
    private void setGridActionCheckedState() {
        boolean isAtLeastOneVisible = false;
        for( Layer gridLayer : gridLayers ) {
            if (gridLayer.isVisible()) {
                isAtLeastOneVisible = true;
                break;
            }
        }
        gridAction.setChecked(isAtLeastOneVisible);
        if (isAtLeastOneVisible) {
            gridAction.setToolTipText(Messages.LegendView_hide_grid_tooltip);    
        } else {
            gridAction.setToolTipText(Messages.LegendView_show_grid_tooltip);
        }
        
    }
    
}
