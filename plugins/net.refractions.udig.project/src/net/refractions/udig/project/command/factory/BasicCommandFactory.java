/*
 * uDig - User Friendly Desktop Internet GIS client http://udig.refractions.net (C) 2004,
 * Refractions Research Inc. This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation; version 2.1 of the License. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 */
package net.refractions.udig.project.command.factory;

import java.util.Collection;
import java.util.List;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.Map;
import net.refractions.udig.project.internal.Project;
import net.refractions.udig.project.internal.commands.AddLayerCommand;
import net.refractions.udig.project.internal.commands.AddLayersCommand;
import net.refractions.udig.project.internal.commands.ChangeCRSCommand;
import net.refractions.udig.project.internal.commands.CreateMapCommand;
import net.refractions.udig.project.internal.commands.DeleteLayerCommand;
import net.refractions.udig.project.internal.commands.SetApplicabilityCommand;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Creates Edit commands which must be used to modify editable feature data. API internal classes
 * are in the returned API
 * 
 * @author jeichar
 * @since 0.3
 */
@SuppressWarnings("deprecation")
public class BasicCommandFactory extends net.refractions.udig.project.command.BasicCommandFactory {
    /**
     * Creates a new EditCommandFactory object
     * 
     * @return a new EditCommandFactory object
     */
    public static BasicCommandFactory getInstance() {
        return instance;
    }

    private static final BasicCommandFactory instance = new BasicCommandFactory();
    protected BasicCommandFactory() {
        // no op
    }

    /**
     * Create a delete layer command
     * 
     * @param map the map containing the layer
     * @param layer the layer to delete
     * @return a new {@linkplain DeleteLayerCommand}object that deletes the layer.
     * @see DeleteLayerCommand
     */
    public UndoableMapCommand createDeleteLayer( ILayer layer ) {
        return new DeleteLayerCommand((Layer) layer);
    }

    /**
     * Create an Add Layer command
     * 
     * @param layer the layer to add to the map.
     * @return a new {@linkplain AddLayerCommand}object that deletes the feature.
     * @see AddLayerCommand
     */
    public UndoableMapCommand createAddLayer( ILayer layer ) {
        return new AddLayerCommand((Layer) layer);
    }

    /**
     * Create an Add Layer command
     * 
     * @param layer the layer to add to the map.
     * @param index the zorder where the layer will be added.
     * @return a new {@linkplain AddLayerCommand}object that deletes the feature.
     * @see AddLayerCommand
     */
    public UndoableMapCommand createAddLayer( ILayer layer, int index ) {
        return new AddLayerCommand((Layer) layer, index);
    }

    /**
     * Create an AddLayers command that adds all the layers in the collection
     * 
     * @param evaluationObject the layer to add to the map.
     * @param index the zorder where the layer will be added.
     * @return a new {@linkplain AddLayersCommand}object that deletes the feature.
     * @see AddLayersCommand
     */
    public UndoableMapCommand createAddManyLayers( Collection layers, int index ) {
        return new AddLayersCommand(layers, index);
    }

    /**
     * Create an AddLayers command that adds all the layers in the collection
     * 
     * @param evaluationObject the layer to add to the map.
     * @return a new {@linkplain AddLayersCommand}object that deletes the feature.
     * @see AddLayersCommand
     */
    public UndoableMapCommand createAddManyLayers( Collection layers ) {
        return new AddLayersCommand(layers);
    }

    /**
     * Create a Change CRS command
     * 
     * @param map the map for which the CRS is going to change.
     * @return a new {@linkplain ChangeCRSCommand}object that changes the CRS.
     * @see ChangeCRSCommand
     */
    public UndoableMapCommand createChangeCRS( IMap map, CoordinateReferenceSystem crs ) {
        return new ChangeCRSCommand((Map) map, crs);
    }

    /**
     * Create a CreateMapCommand
     * 
     * @param name the name of the map
     * @param layerResources the IGeoResources that will make up the layers of the map.
     * @param owner The project that will contain the map.
     * @return
     */
    public UndoableMapCommand createCreateMapCommand( String name, List<IGeoResource> layerResources,
            Project owner ) {
        return new CreateMapCommand(name, layerResources, owner);
    }

    /**
     * Create a CreateMapCommand
     * 
     * @param layerResources the IGeoResources that will make up the layers of the map.
     * @param owner The project that will contain the map.
     * @return
     */
    public UndoableMapCommand createCreateMapCommand( List<IGeoResource> layerResources, Project owner ) {
        return new CreateMapCommand(null, layerResources, owner);
    }

    /**
     * Create a CreateMapCommand
     * 
     * @param name the name of the map
     * @param layerResources the IGeoResources that will make up the layers of the map.
     * @return
     */
    public UndoableMapCommand createCreateMapCommand( String name, List<IGeoResource> layerResources ) {
        return new CreateMapCommand(name, layerResources, null);
    }

    /**
     * Create a CreateMapCommand
     * 
     * @param layerResources the objects, (Layers or IGeoResources) that will make up the map.
     * @return
     */
    public UndoableMapCommand createCreateMapCommand( List<IGeoResource> layerResources ) {
        return new CreateMapCommand(null, layerResources, null);
    }

    public UndoableMapCommand createSetApplicabilityCommand( ILayer layer, String applicabilityId, boolean newValue ) {
        return new SetApplicabilityCommand(layer, applicabilityId, newValue);
    }

}
