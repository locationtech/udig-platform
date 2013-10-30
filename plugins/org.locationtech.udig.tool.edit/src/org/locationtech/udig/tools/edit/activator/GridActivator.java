/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.activator;

import java.io.IOException;
import java.util.List;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.mapgraphic.grid.GridMapGraphic;
import org.locationtech.udig.mapgraphic.internal.MapGraphicService;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.SetLayerVisibilityCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.LayerFactory;
import org.locationtech.udig.project.internal.commands.AddLayerCommand;
import org.locationtech.udig.tools.edit.Activator;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.preferences.PreferenceUtil;
import org.locationtech.udig.tools.edit.support.SnapBehaviour;
import org.locationtech.udig.ui.ProgressManager;

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
