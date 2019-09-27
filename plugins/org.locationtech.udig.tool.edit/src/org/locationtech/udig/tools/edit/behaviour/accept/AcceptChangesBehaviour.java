/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.behaviour.accept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.core.IBlockingProvider;
import org.locationtech.udig.core.StaticBlockingProvider;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.UndoableComposite;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.command.provider.FIDFeatureProvider;
import org.locationtech.udig.project.internal.commands.edit.CreateFeatureCommand;
import org.locationtech.udig.project.internal.commands.edit.DeleteFeatureCommand;
import org.locationtech.udig.project.internal.commands.edit.SetGeometryCommand;
import org.locationtech.udig.project.render.displayAdapter.IMapDisplay;
import org.locationtech.udig.project.ui.IAnimation;
import org.locationtech.udig.project.ui.commands.DrawCommandFactory;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.Behaviour;
import org.locationtech.udig.tools.edit.EditPlugin;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.animation.GeometryOperationAnimation;
import org.locationtech.udig.tools.edit.commands.AddVertexCommand;
import org.locationtech.udig.tools.edit.commands.CreateAndSelectNewFeature;
import org.locationtech.udig.tools.edit.commands.CreateNewOrSelectExitingFeatureCommand;
import org.locationtech.udig.tools.edit.commands.SetEditGeomChangedStateCommand;
import org.locationtech.udig.tools.edit.commands.SetEditStateCommand;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.EditGeomPathIterator;
import org.locationtech.udig.tools.edit.support.GeometryCreationUtil;
import org.locationtech.udig.tools.edit.support.IsBusyStateProvider;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.locationtech.udig.tools.edit.support.ShapeType;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Creates a geometry from the currentGeom in the {@link EditToolHandler}.
 * <p>
 * Requirements:
 * <ul>
 * <li>handler.getCurrentGeom() != null</li>
 * <li>EditManager has a edit feature and a edit layer</li>
 * <li>Geometry can be assigned to feature's geometry</li>
 * </ul>
 * </p>
 * <p>
 * Action:
 * <ul>
 * <li>Creates a geometry of the indicated type.</li>
 * <li>Set the edit feature's default geometry to be the created geometry if it is not null</li>
 * <li>Also writes the changes to other geoms in the blackboard. The type created for those depends
 * on the what the ShapeType of those geometries are.</li>
 * </ul>
 * </p>
 * 
 * @author jones
 * @since 1.1.0
 */
public class AcceptChangesBehaviour implements Behaviour {

    private final Class< ? extends Geometry> geomToCreate;
    /**
     * This is true if we need to check and close polgons on the blackboad.
     */
    private boolean updateBlackboard = true;
    private boolean deselectCreatedFeatures;

    /**
     * @param geomToCreate must be polygon, point, multipoint, linestring or linearring
     * @param deselectCreatedFeatures TODO
     * @param if true created features will be removed from the editblackboard.
     */
    public AcceptChangesBehaviour( Class< ? extends Geometry> geomToCreate,
            boolean deselectCreatedFeatures ) {
        if (geomToCreate != Polygon.class && geomToCreate != Point.class
                && geomToCreate != LineString.class && geomToCreate != LinearRing.class) {
            throw new IllegalArgumentException(
                    "Must be one of Polygon, Point, LineString, MultiPoint or LinearRing"); //$NON-NLS-1$
        }
        this.geomToCreate = geomToCreate;
        this.deselectCreatedFeatures = deselectCreatedFeatures;
    }

    public boolean isValid( EditToolHandler handler ) {
        EditGeom currentGeom = handler.getCurrentGeom();

        boolean currentGeomNotNull = currentGeom != null;
        return currentGeomNotNull
                && typeCanBeAssignedToLayer(handler.getEditLayer().getSchema()
                        .getGeometryDescriptor().getType().getBinding());
    }

    /**
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean typeCanBeAssignedToLayer( Class type ) {
        if (type.isAssignableFrom(geomToCreate))
            return true;
        if (geomToCreate == Polygon.class && type.isAssignableFrom(MultiPolygon.class)) {
            return true;
        } else if (geomToCreate == LinearRing.class && type.isAssignableFrom(MultiLineString.class)) {
            return true;
        } else if (geomToCreate == LineString.class && type.isAssignableFrom(MultiLineString.class)) {
            return true;
        } else if (geomToCreate == Point.class && type.isAssignableFrom(MultiPoint.class)) {
            return true;
        }
        return false;
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public UndoableMapCommand getCommand( EditToolHandler handler ) {
        if (!isValid(handler))
            throw new IllegalArgumentException("Not in a valid state for this to run"); //$NON-NLS-1$
        Map<String, GeometryCreationUtil.Bag> idToGeom;
        try {
            idToGeom = GeometryCreationUtil.createAllGeoms(handler.getCurrentGeom(), geomToCreate,
                    handler.getEditLayer().getSchema().getGeometryDescriptor(), true);
        } catch (IllegalStateException e) {
            return null;
        }
        if (idToGeom.isEmpty()) {
            return null;
        }
        EditState oldState = handler.getCurrentState();

        // This is the list of commands we are going to send off
        List<UndoableMapCommand> commands = new ArrayList<UndoableMapCommand>();
        commands.add(new SetEditStateCommand(handler, EditState.COMMITTING));

        ILayer layer = handler.getEditLayer();

        Set<Entry<String, GeometryCreationUtil.Bag>> entries = idToGeom.entrySet();
        for( Entry<String, GeometryCreationUtil.Bag> entry : entries ) {

            commands.addAll(processIntoCommands(handler, layer, entry));
        }

        UndoableComposite composite = new UndoableComposite(commands);
        composite.setName(Messages.WriteChangesBehaviour_commandName);
        composite.getFinalizerCommands().add(new SetEditStateCommand(handler, EditState.MODIFYING));

        handler.setCurrentState(oldState);

        return composite;
    }
    /**
     * Process the provided entry into a list of commands.
     * <p>
     * Depending on the provided entry the following will be generated:
     * <ul>
     * <li>null geometry: a DeleteFeatureCommand
     * <li>edit feature is null: a CreateAndSetNewFeature
     * <li>otherwise the editGeom.getFeatureIDRef() feature will be modified
     * <ul>
     * This method uses GeometryCreationUtil.ceateGeometryCollection to modify the origional
     * geometry.
     * 
     * @param handler
     * @param commands
     * @param layer
     * @param entry
     * @return List of commands based on the current entry
     */
    @SuppressWarnings("unchecked")
    private List<UndoableMapCommand> processIntoCommands( EditToolHandler handler, ILayer layer,
            Entry<String, GeometryCreationUtil.Bag> entry ) {
        IMapDisplay display = handler.getContext().getMapDisplay();
        DrawCommandFactory drawfactory = handler.getContext().getDrawFactory();
        SimpleFeatureType schema = layer.getSchema();
        Class<Geometry> binding = (Class<Geometry>) schema.getGeometryDescriptor().getType()
                .getBinding();

        List<UndoableMapCommand> commands = new ArrayList<UndoableMapCommand>();

        EditGeom editGeom = entry.getValue().geom;
        List<Geometry> geoms = entry.getValue().jts;
        Geometry geom = GeometryCreationUtil.ceateGeometryCollection(geoms, binding);

        if (geom == null) { // null is used to mark things for delete?
            IBlockingProvider<ILayer> layerProvider = new StaticBlockingProvider<ILayer>(layer);
            FIDFeatureProvider featureProvider = new FIDFeatureProvider(entry.getKey(),
                    layerProvider);
            DeleteFeatureCommand deleteFeatureCommand = new DeleteFeatureCommand(featureProvider,
                    layerProvider);
            commands.add(deleteFeatureCommand);
        } else {
            // geometry is going to be written out
            if (updateBlackboard) {
                // mostly used to tack on an extra addVertex command
                // so the display is drawn as a closed polygon.
                updateBlackboardGeometry(handler, editGeom, geom, commands);
            }
            GeometryOperationAnimation animation = new GeometryOperationAnimation(
                    EditGeomPathIterator.getPathIterator(editGeom).toShape(),
                    new IsBusyStateProvider(handler));

            UndoableMapCommand startAnimationCommand = drawfactory.createStartAnimationCommand(
                    display, Collections.singletonList((IAnimation) animation));
            commands.add(startAnimationCommand);

            if (isCurrentGeometry(handler, editGeom)) {
                if (isCreatingNewFeature(handler)) {
                    int attributeCount = schema.getAttributeCount();
                    SimpleFeature feature;
                    try {
                        feature = SimpleFeatureBuilder.template(schema, "newFeature"
                                + new Random().nextInt());
                        // feature = SimpleFeatureBuilder.build(schema, new
                        // Object[attributeCount],"newFeature");
                        feature.setDefaultGeometry(geom);
                    } catch (IllegalAttributeException e) {
                        throw new IllegalStateException(
                                "Could not create an empty " + schema.getTypeName() + ":" + e, e); //$NON-NLS-1$//$NON-NLS-2$
                    }
                    
                    CreateFeatureCommand.runFeatureCreationInterceptors(feature);
                    
                    // FeaturePanelProcessor panels = ProjectUIPlugin.getDefault()
                    // .getFeaturePanelProcessor();
                    // List<FeaturePanelEntry> popup = panels.search(schema);
                    // if (popup.isEmpty()) {
                    CreateAndSelectNewFeature newFeatureCommand = new CreateAndSelectNewFeature(
                            handler.getCurrentGeom(), feature, layer, deselectCreatedFeatures);
                    commands.add(newFeatureCommand);
                    // } else {
                    // CreateDialogAndSelectNewFeature newFeatureCommand = new
                    // CreateDialogAndSelectNewFeature(
                    // handler.getCurrentGeom(), feature, layer, deselectCreatedFeatures,
                    // popup);
                    // commands.add(newFeatureCommand);
                    // }
                } else {
                    // not creating it so don't need to set it.
                    UndoableMapCommand setGeometryCommand = new SetGeometryCommand(editGeom
                            .getFeatureIDRef().get(), new StaticBlockingProvider<ILayer>(layer),
                            SetGeometryCommand.DEFAULT, geom);
                    commands.add(setGeometryCommand);
                }
            } else {
                commands.add(new CreateNewOrSelectExitingFeatureCommand(editGeom.getFeatureIDRef()
                        .get(), layer, geom));

            }
            commands.add(new SetEditGeomChangedStateCommand(editGeom, false));
            commands.add(drawfactory.createStopAnimationCommand(display, Collections
                    .singletonList((IAnimation) animation)));
        }
        return commands;
    }

    private boolean isCreatingNewFeature( EditToolHandler handler ) {
        return handler.getContext().getEditManager().getEditFeature() == null;
    }
    /**
     * Checks if the EditGeom is the one the user is currently working on.
     * <p>
     * Several edit geometries can be in play at once.
     * 
     * @param handler
     * @param editGeom
     * @return true if the editGeom is in use by the user
     */
    private boolean isCurrentGeometry( EditToolHandler handler, EditGeom editGeom ) {
        return editGeom == handler.getCurrentGeom();
    }
    /**
     * This method will add a AddVertextCommand if needed to close a polygon.
     * 
     * @param handler
     * @param editGeom
     * @param geom
     * @param commands
     */
    private void updateBlackboardGeometry( EditToolHandler handler, EditGeom editGeom,
            Geometry geom, List<UndoableMapCommand> commands ) {
        if (handler.getCurrentGeom() == editGeom) {
            if (Polygon.class.isAssignableFrom(geomToCreate)) {
                for( PrimitiveShape shape : editGeom ) {
                    if (shape.getNumPoints() > 0
                            && !shape.getPoint(0).equals(shape.getPoint(shape.getNumPoints() - 1)))
                        commands.add(new AddVertexCommand(handler, editGeom.getEditBlackboard(),
                                shape.getPoint(0)));
                }
            }
        } else {
            if (editGeom.getShapeType() == ShapeType.POLYGON
                    || (editGeom.getShapeType() == ShapeType.UNKNOWN && Polygon.class
                            .isAssignableFrom(geomToCreate))) {
                for( PrimitiveShape shape : editGeom ) {
                    if (shape.getNumPoints() > 0
                            && !shape.getPoint(0).equals(shape.getPoint(shape.getNumPoints() - 1)))
                        commands.add(new AddVertexCommand(handler, editGeom.getEditBlackboard(),
                                shape.getPoint(0)));
                }
            }
        }
    }

    public void handleError( EditToolHandler handler, Throwable error, UndoableMapCommand command ) {
        EditPlugin.log("", error); //$NON-NLS-1$
    }

    /**
     * @param updateBlackboard The updateBlackboard to set.
     */
    public void setUpdateBlackboard( boolean autoFix ) {
        this.updateBlackboard = autoFix;
    }
}
