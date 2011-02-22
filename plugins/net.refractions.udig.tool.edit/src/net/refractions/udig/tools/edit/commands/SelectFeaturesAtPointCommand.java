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
package net.refractions.udig.tools.edit.commands;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.core.Pair;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableComposite;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.ProjectPlugin;
import net.refractions.udig.project.ui.AnimationUpdater;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.EditPlugin;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.EditBlackboard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.BBoxExpression;
import org.geotools.filter.Filter;
import org.geotools.filter.FilterFactory;
import org.geotools.filter.FilterFactoryFinder;
import org.geotools.filter.GeometryFilter;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * Searches a layer for an intersection between a mouse click and a feature and selects the features
 * if found.
 *
 * @author Jesse
 * @since 1.1.0
 */
public class SelectFeaturesAtPointCommand extends AbstractCommand implements UndoableMapCommand {

    private EditToolHandler handler;
    private final Set<Class< ? extends Geometry>> acceptableClasses = new HashSet<Class< ? extends Geometry>>();
    private MapMouseEvent event;
    private short filterType;

    private UndoableMapCommand command;
    private final SelectionParameter parameters;
    // boolean indicating that transformation problems have been reported so don't want to fill logs with the same
    // report
    boolean warned = false;

    /**
     * @deprecated Use {@link #SelectFeaturesAtPointCommand(SelectionParameter)} instead
     */
    public SelectFeaturesAtPointCommand( EditToolHandler handler, MapMouseEvent e,
            Class< ? extends Geometry>[] acceptableClasses, short filterType, boolean permitClear,
            boolean onlyAdd ) {
        this(
                new SelectionParameter(handler, e, acceptableClasses, filterType, permitClear,
                        onlyAdd));
    }

    public SelectFeaturesAtPointCommand( SelectionParameter parameterObject ) {
        this.parameters = parameterObject;
        this.handler = parameterObject.handler;
        this.event = parameterObject.event;
        this.acceptableClasses.addAll(Arrays.asList(parameterObject.acceptableClasses));

        this.filterType = parameterObject.filterType;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        if (command != null) {
            // indicates a redo
            command.run(monitor);
        } else {
            IToolContext context = handler.getContext();
            ILayer editLayer = handler.getEditLayer();

            EditBlackboard editBlackboard = handler.getEditBlackboard(editLayer);
            editBlackboard.startBatchingEvents();
            BlockingSelectionAnim animation = new BlockingSelectionAnim(event.x, event.y);
            AnimationUpdater.runTimer(context.getMapDisplay(), animation);
            Pair<FeatureIterator, Boolean> state = getFeatureIterator();
            try {

                boolean hasFeature = state.getRight();
                if (hasFeature) {
                    runSelectionStrategies(monitor, state.getLeft());
                } else {
                    runDeselectionStrategies(monitor);
                }

                setAndRun(monitor, command);
            } finally {
                if (state != null) {
                    state.getLeft().close();
                }
                if (animation != null) {
                    animation.setValid(false);
                    animation = null;
                }
                editBlackboard.fireBatchedEvents();
            }
        }
    }

    private Pair<FeatureIterator, Boolean> getFeatureIterator() throws IOException {
        ILayer editLayer = parameters.handler.getEditLayer();
        FeatureStore store = editLayer.getResource(FeatureStore.class, null);
        ReferencedEnvelope bbox = handler.getContext().getBoundingBox(event.getPoint(), 7);
        Filter createBBoxFilter = createBBoxFilter(bbox, editLayer, filterType);
        FeatureCollection collection = store.getFeatures(createBBoxFilter);
        FeatureIterator reader = collection.features();
        boolean hasFeature = false;
        try {
            hasFeature = reader.hasNext();
        } catch (Exception e2) {
            EditPlugin.log("", e2); //$NON-NLS-1$
        }

        return new Pair<FeatureIterator, Boolean>(reader, hasFeature);
    }

    private void runDeselectionStrategies( IProgressMonitor monitor ) {

        List<DeselectionStrategy> strategies = parameters.deselectionStrategies;
        UndoableComposite compositeCommand = new UndoableComposite();
        for( DeselectionStrategy strategy : strategies ) {
            strategy.run(monitor, parameters, compositeCommand);
        }
        this.command = compositeCommand;

    }

    private void runSelectionStrategies( IProgressMonitor monitor, FeatureIterator reader ) {
        List<SelectionStrategy> strategies = parameters.selectionStrategies;
        UndoableComposite compositeCommand = new UndoableComposite();
        compositeCommand.setName(Messages.SelectGeometryCommand_name);

        boolean firstFeature = true;

        ReferencedEnvelope bbox = handler.getContext().getBoundingBox(event.getPoint(), 7);
            try {
                bbox = bbox.transform(parameters.handler.getEditLayer().getCRS(), true);
            } catch (TransformException e) {
                logTransformationWarning(e);
            } catch (FactoryException e) {
                logTransformationWarning(e);
            }

        do {
            Feature feature = reader.next();

            if (bboxIntersects(bbox, feature)) {
                for( SelectionStrategy selectionStrategy : strategies ) {
                    selectionStrategy.run(monitor, compositeCommand, parameters, feature,
                            firstFeature);
                }
                firstFeature = false;
            }
        } while( reader.hasNext() );

        this.command = compositeCommand;
    }

    private void logTransformationWarning( Exception e ) {
        if (!warned) {
            EditPlugin.log("Error transforming bbox from viewportmodel CRS to LayerCRS", e); //$NON-NLS-1$
        }
    }

    private boolean bboxIntersects( ReferencedEnvelope bbox, Feature feature ) {
        Geometry bboxGeom = new GeometryFactory().toGeometry(bbox);

        Geometry geom = (Geometry) feature.getDefaultGeometry();

        try{
            return geom.intersects(bboxGeom);
        }catch (Exception e) {
            // ok so exception happened during intersection.  This usually means geometry is a little crazy
            // what to do?...
            // for now I'm saying we can't use the geometry.
            EditPlugin.log("Can't do intersection so I'm assuming they intersect", e); //$NON-NLS-1$
            return true;
        }
    }

    /**
     * Creates A geometry filter for the given layer.
     *
     * @param boundingBox in the same crs as the viewport model.
     * @return a Geometry filter in the correct CRS or null if an exception occurs.
     */
    public Filter createBBoxFilter( Envelope boundingBox, ILayer layer, short filterType ) {
        FilterFactory factory = FilterFactoryFinder.createFilterFactory();
        GeometryFilter bboxFilter = null;
        if (!layer.hasResource(FeatureSource.class))
            return Filter.ALL;
        try {

            Envelope bbox;
            try {
                MathTransform transform = layer.mapToLayerTransform();
                bbox = JTS.transform(boundingBox, transform);
            } catch (Exception e) {
                bbox = boundingBox;
            }
            BBoxExpression bb = factory.createBBoxExpression(bbox);
            bboxFilter = factory.createGeometryFilter(filterType);
            bboxFilter.addRightGeometry(bb);

            String geom = layer.getSchema().getDefaultGeometry().getName();

            bboxFilter.addLeftGeometry(factory.createAttributeExpression(geom));
        } catch (Exception e) {
            ProjectPlugin.getPlugin().log(e);
        }
        return bboxFilter;
    }

    private void setAndRun( IProgressMonitor monitor, UndoableMapCommand undoableComposite )
            throws Exception {
        undoableComposite.setMap(getMap());
        undoableComposite.run(monitor);
    }

    public String getName() {
        return Messages.SelectGeometryCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        command.rollback(monitor);
    }

}
