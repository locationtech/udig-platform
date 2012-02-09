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


public class LegendViewGridHandler implements ILayerListener, IMapCompositionListener {

    private Map map;
    private List<Layer> gridLayers;
    private Action gridAction;
    
    private boolean isLegendViewAddingGrid = false;
    
    public LegendViewGridHandler() {
        //Nothing yet
    }
    
    public void setMap(Map map) {
        cleanHandler();
        initMap(map);
        initGridLayers(map);
        setGridActionState();
    }
    
    public void disposeHandler() {
        cleanHandler();
        if (gridAction != null) {
            gridAction = null;
        }
    }
    
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
    
    private void initMap(Map map) {
        
        this.map = map;
        
        if (map != null) {
            this.map.addMapCompositionListener(this);
        }
        
    }
    
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
    
    private void addGridLayer(Layer layer) {
        if (layer != null) {
            layer.addListener(this);
            this.gridLayers.add(layer);    
        }
    }
    
    private void removeGridLayer(Layer layer) {
        if (layer != null) { 
            layer.removeListener(this);
            this.gridLayers.remove(layer);    
        }
    }

    public Action getGridAction() {
        
        gridAction = new Action(null, IAction.AS_CHECK_BOX){
            @Override
            public void run() {
                toggleGrid(gridAction.isChecked());
            }
        };
        gridAction.setImageDescriptor(ProjectUIPlugin.getDefault().getImageDescriptor(ISharedImages.ADD_CO));
        
        setGridActionState();
        
        return gridAction;
        
    }
    
    private void toggleGrid(boolean isChecked) {
        for( Layer gridLayer : gridLayers ) {
            gridLayer.setVisible(isChecked);
        }
    }
    
    @Override
    public void refresh( LayerEvent event ) {
        setGridActionState();
    }
    
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
