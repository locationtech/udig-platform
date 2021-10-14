/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.util.factory.GeoTools;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.udig.core.Pair;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditState;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.PrimitiveShape;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

/**
 * Removes an EditGeom from the edit blackboard and updates the handler's current geom if it has
 * been removed.
 * 
 * @author jones
 * @since 1.1.0
 */
public class DeselectEditGeomCommand extends AbstractCommand implements UndoableMapCommand {

    private EditToolHandler handler;
    private List<EditGeom> geoms;
    private List<EditGeom> removed;
    private EditGeom currentGeom;
    private PrimitiveShape currentShape;
    private EditState currentState;
    private boolean removedAll;
    private UndoableMapCommand nullEditFeatureCommand;
    private ILayer layer;

    public DeselectEditGeomCommand( EditToolHandler handler, List<EditGeom> geoms ) {
        this.handler = handler;
        this.geoms = geoms;
        this.layer = handler.getEditLayer();
    }

    private EditGeom setCurrentGeom( EditGeom newCurrentGeom, PrimitiveShape destination,
            PrimitiveShape shape ) {
        if (currentGeom != null && newCurrentGeom != null && shape == currentShape) {
            handler.setCurrentShape(destination);
            return null;
        }
        return newCurrentGeom;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.RemoveEditGeomCommand_runTaskMessage, 20);
        removeGeomsFromBlackboard();
        if (removed.contains(handler.getCurrentGeom())) {
            deselectCurrentEditFeature(monitor);
        }
        Envelope env = new Envelope();

        Set<String> fids = new HashSet<String>();
        for( EditGeom geom : removed ) {
            if (env.isNull()) {
                env.init(geom.getShell().getEnvelope());
            } else {
                env.expandToInclude(geom.getShell().getEnvelope());
            }
            fids.add(geom.getFeatureIDRef().get());
        }
        EditUtils.instance.refreshLayer(layer, fids, env, true, false);
        monitor.done();
    }

    private Pair<PrimitiveShape, SimpleFeature> newSelection( IProgressMonitor monitor )
            throws IOException {

        EditBlackboard editBlackboard = handler.getEditBlackboard(layer);
        if (!editBlackboard.isEmpty()) {
            EditGeom newSelection = editBlackboard.getGeoms().get(0);

            FilterFactory factory = CommonFactoryFinder
                    .getFilterFactory(GeoTools.getDefaultHints());
            Id id = factory.id(Collections.singleton(factory.featureId(newSelection
                    .getFeatureIDRef().get())));

            FeatureSource<SimpleFeatureType, SimpleFeature> source = layer.getResource(FeatureSource.class, monitor);
            FeatureCollection<SimpleFeatureType, SimpleFeature>  features = source.getFeatures(id);

            FeatureIterator<SimpleFeature> iter = features.features();
            try {
                if (iter.hasNext()) {
                    return new Pair<PrimitiveShape, SimpleFeature>(newSelection.getShell(), iter
                            .next());
                }
            } finally {
                iter.close();
            }
        }
        return new Pair<PrimitiveShape, SimpleFeature>(null, null);
    }

    private void deselectCurrentEditFeature( IProgressMonitor monitor ) throws Exception {
        this.currentGeom = handler.getCurrentGeom();
        this.currentShape = handler.getCurrentShape();
        this.currentState = handler.getCurrentState();

        Pair<PrimitiveShape, SimpleFeature> newSelection = newSelection(monitor);

        handler.setCurrentShape(newSelection.getLeft());

        if (newSelection.getLeft() == null) {
            handler.setCurrentState(EditState.NONE);
        }

        nullEditFeatureCommand = handler.getContext().getEditFactory().createSetEditFeatureCommand(
                newSelection.getRight(), layer);
        nullEditFeatureCommand.setMap(getMap());
        nullEditFeatureCommand.run(new SubProgressMonitor(monitor, 10));
    }

    private void removeGeomsFromBlackboard() {
        if (geoms.containsAll(handler.getEditBlackboard(layer).getGeoms())) {
            removedAll = true;
            this.removed = handler.getEditBlackboard(layer).getGeoms();
            handler.getEditBlackboard(layer).clear();
        } else {
            this.removed = handler.getEditBlackboard(layer).removeGeometries(geoms);
        }
    }

    public String getName() {
        return Messages.RemoveEditGeomCommand_commandName;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(Messages.RemoveEditGeomCommand_rollbackTaskMessage, 20);
        if (currentState != null)
            handler.setCurrentState(currentState);
        if (nullEditFeatureCommand != null)
            nullEditFeatureCommand.rollback(new SubProgressMonitor(monitor, 10));
        EditBlackboard bb = handler.getEditBlackboard(layer);
        EditGeom newCurrentGeom = null;
        List<EditGeom> empty = bb.getGeoms();
        for( EditGeom original : removed ) {
            EditGeom inBlackboard = bb.newGeom(original.getFeatureIDRef().get(), original
                    .getShapeType());
            inBlackboard.setChanged(original.isChanged());
            if (original == currentGeom)
                newCurrentGeom = inBlackboard;

            PrimitiveShape destination = inBlackboard.getShell();
            newCurrentGeom = setCurrentGeom(newCurrentGeom, destination, original.getShell());

            for( Iterator<Coordinate> iter = original.getShell().coordIterator(); iter.hasNext(); ) {
                bb.addCoordinate(iter.next(), destination);
            }

            for( PrimitiveShape shape : original.getHoles() ) {
                destination = inBlackboard.newHole();
                newCurrentGeom = setCurrentGeom(newCurrentGeom, destination, shape);
                for( Iterator<Coordinate> iter = shape.coordIterator(); iter.hasNext(); ) {
                    bb.addCoordinate(iter.next(), destination);
                }
            }
        }
        if (removedAll)
            bb.removeGeometries(empty);
    }

}
