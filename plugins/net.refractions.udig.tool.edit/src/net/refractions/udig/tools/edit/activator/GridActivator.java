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
package net.refractions.udig.tools.edit.activator;

import java.io.IOException;
import java.util.List;

import net.refractions.udig.catalog.CatalogPlugin;
import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.mapgraphic.grid.GridMapGraphic;
import net.refractions.udig.mapgraphic.internal.MapGraphicService;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.SetLayerVisibilityCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.LayerFactory;
import net.refractions.udig.project.internal.commands.AddLayerCommand;
import net.refractions.udig.tools.edit.Activator;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.preferences.PreferenceUtil;
import net.refractions.udig.tools.edit.support.SnapBehaviour;
import net.refractions.udig.ui.ProgressManager;

/**
 * If the snap behaviour is GRID then enables the grid map graphic on activation and 
 * disables the layer if this activator originally added the layer (or sets it to visible). 
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class GridActivator implements Activator {

    private static final String KEY = "GRID_ACTIVATOR_INDICATOR";

    /**
     * Hides the grid layer (visibility=false) if the layer was added or set visible by this class.
     */
    public void hideGrid( IMap map ) {
        ILayer gridLayer = findGridLayer(map);
        if( gridLayer!=null && gridLayer.getBlackboard().get(KEY)!=null ){
            setGridLayerVisibility(gridLayer, false);
        }
    }

    /**
     * Shows the grid.
     * 
     * @param map map to add the grid to.
     */
    public void showGrid( IMap map ) {
        ILayer gridLayer = findGridLayer(map);
        
        if( gridLayer!=null ){
            setGridLayerVisibility(gridLayer, true);
        }else{
            addLayer(map);
        }
    }

    public void activate( EditToolHandler handler ) {
        if( PreferenceUtil.instance().getSnapBehaviour() == SnapBehaviour.GRID ){
            IMap map = handler.getContext().getMap();
            showGrid(map);
        }
    }

    public void handleDeactivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error);
    }

    public void handleActivateError( EditToolHandler handler, Throwable error ) {
        EditPlugin.log("", error);
    }

    public void deactivate( EditToolHandler handler ) {
        if( PreferenceUtil.instance().getSnapBehaviour() == SnapBehaviour.GRID ){
            IMap map = handler.getContext().getMap();
            hideGrid(map);
        }
    }

    private void addLayer( IMap map ) {
        MapGraphicService service=CatalogPlugin.getDefault().getLocalCatalog().getById(MapGraphicService.class, MapGraphicService.SERVICE_ID, ProgressManager.instance().get());
        try {
            List< ? extends IGeoResource> members = service.resources(ProgressManager.instance().get());
            for( IGeoResource resource : members ) {
                if( resource.canResolve(GridMapGraphic.class) ){
                    LayerFactory factory = map.getLayerFactory();
                    Layer newLayer = factory.createLayer(resource);
                    newLayer.getBlackboard().put(KEY, KEY);
                    
                    AddLayerCommand command = new AddLayerCommand(newLayer);
                    map.sendCommandASync(command);
                }
            }
            
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException( ).initCause( e );
        }
    }

    private void setGridLayerVisibility( ILayer gridLayer, boolean enabled ) {
        if( enabled!=gridLayer.isVisible() ) {
            if( enabled ) {
                gridLayer.getBlackboard().putString(KEY, KEY);
            }
            gridLayer.getMap().sendCommandASync(new SetLayerVisibilityCommand(gridLayer, enabled) );
        }
    }

    private ILayer findGridLayer( IMap map ) {
        List<ILayer> layers = map.getMapLayers();
        ILayer graphicLayer=null;
        for( ILayer layer : layers ) {
            if( layer.hasResource(GridMapGraphic.class) ){
                graphicLayer=layer;
                break;
            }
        }
        return graphicLayer;
    }

}
