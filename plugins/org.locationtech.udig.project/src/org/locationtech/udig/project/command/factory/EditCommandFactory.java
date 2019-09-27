/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.command.factory;

import java.net.URL;

import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.StaticBlockingProvider;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.EditCommand;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.commands.DeleteLayerCommand;
import org.locationtech.udig.project.internal.commands.DeleteLayersCommand;
import org.locationtech.udig.project.internal.commands.edit.AddFeatureCommand;
import org.locationtech.udig.project.internal.commands.edit.CopyFeaturesCommand;
import org.locationtech.udig.project.internal.commands.edit.CreateFeatureCommand;
import org.locationtech.udig.project.internal.commands.edit.CreateLayerCommand;
import org.locationtech.udig.project.internal.commands.edit.DeleteFeatureCommand;
import org.locationtech.udig.project.internal.commands.edit.DeleteManyFeaturesCommand;
import org.locationtech.udig.project.internal.commands.edit.ResetEditFeatureCommand;
import org.locationtech.udig.project.internal.commands.edit.RollbackCommand;
import org.locationtech.udig.project.internal.commands.edit.SetAttributeCommand;
import org.locationtech.udig.project.internal.commands.edit.SetEditFeatureCommand;
import org.locationtech.udig.project.internal.commands.edit.SetGeometryCommand;
import org.locationtech.udig.project.internal.commands.edit.WriteEditFeatureCommand;
import org.locationtech.udig.project.internal.commands.selection.CommitCommand;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

/**
 * Creates Edit commands which must be used to modify editable feature data. API internal classes
 * are in the returned API
 * 
 * @author jeichar
 * @since 0.3
 */
@SuppressWarnings("deprecation")
public class EditCommandFactory extends org.locationtech.udig.project.command.EditCommandFactory {
    /**
     * Creates a new EditCommandFactory object
     * 
     * @return a new EditCommandFactory object
     */
    public static EditCommandFactory getInstance() {
        return instance;
    }

    private static final EditCommandFactory instance = new EditCommandFactory();
    protected EditCommandFactory() {
        // no op
    }

    /**
     * Creates a {@linkplain SetAttributeCommand}object.
     * 
     * @param xpath xpath that identifies an attribute in the current editable SimpleFeature
     * @param value the value that the attribute will be set to.
     * @return a new {@linkplain SetAttributeCommand}object
     * @see EditCommand
     */
    public UndoableMapCommand createSetAttributeCommand( String xpath, Object value ) {
        return new SetAttributeCommand(xpath, value);
    }

    /**
     * Creates a {@linkplain SetGeometryCommand}object.
     * 
     * @param xpath xpath that identifies an attribute in the current editable SimpleFeature
     * @param geom the geom (in <b>layer </b> CRS) that the geometry will be set to.
     * @return a new {@linkplain SetGeometryCommand}object
     * @see EditCommand
     * @see Geometry
     */
    public UndoableMapCommand createSetGeometryCommand( String xpath, Geometry geom ) {
        return new SetGeometryCommand(xpath, geom);
    }

    /**
     * Creates a {@linkplain SetGeometryCommand}object that sets the default geometry.
     * 
     * @param geom the geom (in <b>layer </b> CRS) that the geometry will be set to.
     * @return a new {@linkplain SetGeometryCommand}object.
     * @see EditCommand
     * @see Geometry
     */
    public UndoableMapCommand createSetGeometryCommand( Geometry geom ) {
        return new SetGeometryCommand(SetGeometryCommand.DEFAULT, geom);
    }
    /**
     * Creates a {@linkplain SetAttributeCommand}object.
     * 
     * @param xpath xpath that identifies an attribute in the current editable SimpleFeature
     * @param value the value that the attribute will be set to.
     * @return a new {@linkplain SetAttributeCommand}object
     * @see EditCommand
     */
    public UndoableMapCommand createSetAttributeCommand( SimpleFeature feature, ILayer layer, String xpath, Object value ) {
        return new SetAttributeCommand(new StaticBlockingProvider<SimpleFeature>(feature), 
                new StaticBlockingProvider<ILayer>(layer), xpath, value);
    }

    /**
     * Creates a {@linkplain SetGeometryCommand}object.
     * 
     * @param xpath xpath that identifies an attribute in the current editable SimpleFeature
     * @param geom the geom (in <b>layer </b> CRS) that the geometry will be set to.
     * @return a new {@linkplain SetGeometryCommand}object
     * @see EditCommand
     * @see Geometry
     */
    public UndoableMapCommand createSetGeomteryCommand( SimpleFeature feature, ILayer layer, String xpath, Geometry geom ) {
        return new SetGeometryCommand(new StaticBlockingProvider<SimpleFeature>(feature), 
                new StaticBlockingProvider<ILayer>(layer), xpath, geom);
    }

    /**
     * Creates a {@linkplain SetGeometryCommand}object that sets the default geometry.
     * 
     * @param feature a feature that has a default feature type that will accept the geometry type.
     * @param layer the layer containing the feature.
     * @param geom the geom (in <b>layer </b> CRS) that the geometry will be set to.
     * @return a new {@linkplain SetGeometryCommand}object.
     * @see EditCommand
     * @see Geometry
     */
    public UndoableMapCommand createSetGeomteryCommand(SimpleFeature feature,  ILayer layer, Geometry geom ) {
        return new SetGeometryCommand( new StaticBlockingProvider<SimpleFeature>(feature), 
                new StaticBlockingProvider<ILayer>(layer), SetGeometryCommand.DEFAULT, geom);
    }

    /**
     * Creates a {@linkplain SetGeometryCommand}object that sets the default geometry.
     * 
     * @param featureID the id of a feature in layer.
     * @param layer the layer containing the feature.
     * @param geom the geom (in <b>layer </b> CRS) that the geometry will be set to.
     * @return a new {@linkplain SetGeometryCommand}object.
     * @see EditCommand
     * @see Geometry
     */
    public UndoableMapCommand createSetGeomteryCommand( String featureID, ILayer layer, Geometry geom ) {
        return new SetGeometryCommand(featureID, new StaticBlockingProvider<ILayer>(layer), SetGeometryCommand.DEFAULT, geom);
    }


    /**
     * Creates a {@linkplain SetEditFeatureCommand}object that sets the current editVictim victim.
     * 
     * @param feature the feature that will be the new editable SimpleFeature.
     * @param layer A victim Store that contains the editable SimpleFeature.
     * @return a new {@linkplain SetEditFeatureCommand}object.
     * @see SimpleFeature
     * @see Layer
     * @see UndoableMapCommand
     */
    public UndoableMapCommand createSetEditFeatureCommand( SimpleFeature feature, ILayer layer ) {
        return new SetEditFeatureCommand(feature, layer);
    }
    /**
     * Create a Commit command
     * 
     * @return a new {@linkplain CommitCommand} object that deletes the feature.
     * @see CommitCommand
     */
    public MapCommand createCommitCommand() {
        return new CommitCommand();
    }
    /**
     * Create a Commit command
     * 
     * @return a new {@linkplain RollbackCommand} object that deletes the feature.
     * @see RollbackCommand
     */
    public MapCommand createRollbackCommand() {
        return new RollbackCommand();
    }

    /**
     * Creates a {@linkplain SetEditFeatureCommand}object that sets the current editable SimpleFeature.
     * 
     * @param feature the feature that will be the new editable SimpleFeature.
     * @return a new {@linkplain SetEditFeatureCommand}object.
     * @see UndoableMapCommand
     * @see SimpleFeature
     */
    public UndoableMapCommand createSetEditFeatureCommand( SimpleFeature feature ) {
        return new SetEditFeatureCommand(feature);
    }

    /**
     * Creates a {@linkplain SetEditFeatureCommand}object that sets the current editable SimpleFeature
     * to null.
     * 
     * @return a new {@linkplain SetEditFeatureCommand}object that sets the current editable
     *         SimpleFeature to null..
     * @see UndoableMapCommand
     */
    public UndoableMapCommand createNullEditFeatureCommand() {
        return new SetEditFeatureCommand((SimpleFeature) null, (ILayer) null);
    }

    /**
     * @param coordinates the coordinates of the new feature in <b>Map </b> CRS.
     * @return a new {@linkplain CreateFeatureCommand}object creates the feature.
     * @see CreateFeatureCommand
     */
    public UndoableMapCommand createFeature( Coordinate[] coordinates ) {
        return new CreateFeatureCommand(coordinates);
    }
    
    /**
     * Create a delete multiple layers command
     * 
     * @param map the map containing the layer
     * @param evaluationObject the layer to delete
     * @return a new {@linkplain DeleteLayerCommand}object that deletes the layer.
     * @see DeleteLayerCommand
     */
    public UndoableMapCommand createDeleteLayers( ILayer[] layers ) {
        return new DeleteLayersCommand(layers);
    }

    /**
     * Create a delete layer command
     * 
     * @deprecated use optimized createDeleteLayers(ILayer[]) method.
     * 
     * @param map the map containing the layer
     * @param layer the layer to delete
     * @return a new {@linkplain DeleteLayerCommand}object that deletes the layer.
     * @see DeleteLayerCommand
     */
    public UndoableMapCommand createDeleteLayer( ILayer layer ) {
    	return new DeleteLayersCommand(new ILayer[]{layer});
    }

    /**
     * Create a Delete SimpleFeature command
     * 
     * @param layer the layer containing the feature
     * @param feature the feature to delete
     * @return a new {@linkplain DeleteFeatureCommand}object that deletes the feature.
     * @see DeleteFeatureCommand
     */
    @Override
    public UndoableMapCommand createDeleteFeature( SimpleFeature feature, ILayer layer ) {
        return new DeleteFeatureCommand(new StaticBlockingProvider<SimpleFeature>(feature), new StaticBlockingProvider<ILayer>(layer));
    }

    /**
     * Create a Delete SimpleFeature command
     * 
     * @param evaluationObject the layer containing the feature
     * @param feature the feature to delete
     * @return a new {@linkplain DeleteFeatureCommand}object that deletes the feature.
     * @see DeleteFeatureCommand
     */
    public UndoableMapCommand createDeleteFeature(IBlockingProvider<SimpleFeature> provider, IBlockingProvider<ILayer> layerProvider) {
    	return new DeleteFeatureCommand(provider, layerProvider);
    }

    /**
     * Create a {@linkplain WriteEditFeatureCommand} command
     * 
     * @return a new {@linkplain WriteEditFeatureCommand} object that deletes the feature.
     * @see WriteEditFeatureCommand
     */
    public UndoableMapCommand createWriteEditFeatureCommand() {
        return new WriteEditFeatureCommand();
    }
    
    /**
     * Create a {@linkplain CreateLayerCommand}
     * 
     * @see CreateLayerCommand
     */
    public UndoableMapCommand createCreateLayerCommand(URL resourceId){
    	return new CreateLayerCommand(resourceId);
    }
    
    /**
     * Create a {@linkplain CreateLayerCommand}
     * 
     * @see CreateLayerCommand
     */
    public UndoableMapCommand createCreateLayerCommand(IGeoResource resource){
    	return new CreateLayerCommand(resource);
    }
    
    /**
     * Create a {@linkplain ResetEditFeatureCommand} command
     * 
     * @return a new {@linkplain ResetEditFeatureCommand} object that deletes the feature.
     * @see ResetEditFeatureCommand
     */
    public UndoableMapCommand createResetEditFeatureCommand() {
        return new ResetEditFeatureCommand();
    }

    public MapCommand createDeleteFeatures( ILayer selectedLayer, Filter filter ) {
        return new DeleteManyFeaturesCommand(selectedLayer, filter);
    }

    public UndoableMapCommand createCopyFeaturesCommand( ILayer sourceLayer, Filter filter, ILayer destinationLayer) {
        return new CopyFeaturesCommand(sourceLayer, filter, (Layer) destinationLayer);
    }

    public UndoableMapCommand createAddFeatureCommand( SimpleFeature feature, ILayer layer ) {
        return new AddFeatureCommand(feature,layer );
    }
    
      
}
