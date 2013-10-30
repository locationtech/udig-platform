/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package net.refractions.udig.tools.edit.commands;

import java.util.Collections;
import java.util.List;

import net.refractions.udig.core.internal.FeatureUtils;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.commands.edit.AddFeatureCommand;
import net.refractions.udig.tool.edit.internal.Messages;
import net.refractions.udig.tools.edit.support.EditBlackboard;
import net.refractions.udig.tools.edit.support.EditGeom;
import net.refractions.udig.tools.edit.support.EditUtils;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.geotools.data.FeatureEvent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;

/**
 * Creates a new feature and sets it as the EditFeature
 * 
 * @author jones
 * @since 1.1.0
 */
public class CreateAndSelectNewFeature extends AbstractCommand implements UndoableMapCommand {

    private Layer layer;
    private SimpleFeature feature;
    private AddFeatureCommand addFeatureCommand;
    private SimpleFeature oldFeature;
    private ILayer oldLayer;
    private EditGeom geom;
    private String oldID;
    private Filter oldSelection;
    private boolean deselectCreatedFeature;

    /**
     * New instance
     * 
     * @param geom the EditGeom to update with the new feature's fid (after the fid has been added)
     * @param feature the feature created from the geom and that will be added to to the layer.
     * @param layer the layer to add the feature from. It must have a FeatureStore resource
     * @param deselectCreatedFeature if true the geometry will be cleared from the EditBlackboard.
     *        If false the layer will be notified that the feature is selected an it should not be
     *        rendered.
     */
    public CreateAndSelectNewFeature( EditGeom geom, SimpleFeature feature, ILayer layer,
            boolean deselectCreatedFeature ) {
        this.layer = (Layer) layer;
        this.feature = feature;
        this.geom = geom;
        this.deselectCreatedFeature = deselectCreatedFeature;
    }

    public void run( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(getName(), 14);
        monitor.worked(2);
        boolean prev = layer.eDeliver();
        try {
            layer.eSetDeliver(false);
            addFeatureCommand = new AddFeatureCommand(feature, layer);
            addFeatureCommand.setMap(getMap());
            SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 10);
            
            // run the addFeature command (should result in a featureId we can use for selection)
            addFeatureCommand.run(subProgressMonitor);
            subProgressMonitor.done();

            this.oldFeature = getMap().getEditManager().getEditFeature();
            this.oldLayer = getMap().getEditManager().getEditLayer();
            this.oldID = geom.getFeatureIDRef().get();
            
            String fid = addFeatureCommand.getFid();
            if( fid != null ){
                System.out.println("Create and select feature:"+fid );
            }
            else {
                System.out.println("Create and select feature did not produce a feature id to select" );
            }
            geom.getFeatureIDRef().set(fid);

            SimpleFeature newFeature = addFeatureCommand.getNewFeature();
            getMap().getEditManagerInternal().setEditFeature(newFeature,layer);
            oldSelection = layer.getFilter();
            Filter filter = fid == null ? null : FeatureUtils.id(fid);

            if (deselectCreatedFeature) {
                geom.getEditBlackboard().removeGeometries(Collections.singleton(geom));
                // set the layer to deliver events now so that the selection will be re-rendered.
                // this is not needed in the other case because the feature will not be rendered
                layer.eSetDeliver(prev);
            } else {
                EditUtils.instance.refreshLayer(layer, Collections.singleton(fid), null, false, true);
            }
            // since the layer didn't send an event (see eSetDeliver() above) we need to send the
            // command - I'm just smacking it not making any real changes
            if( filter != null ){
                // filter selecting the new feature
                layer.setFilter(filter);
            }
            else {
                System.out.println("New feature did not have a feature id to select");
            }
            fireFeatureEvent(prev);

        } finally {
            layer.eSetDeliver(prev);
        }

        monitor.done();
    }

    private void fireFeatureEvent( boolean prev ) {
        List<FeatureEvent> featureChanges = layer.getFeatureChanges();

        layer.eSetDeliver(prev);
        int index = featureChanges.size() - 1;
        if( index != -1 ){
            FeatureEvent featureEvent = featureChanges.get(index);
            featureChanges.set(index, featureEvent);
        }
    }

    public String getName() {
        return Messages.CreateAndSetNewFeature_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        monitor.beginTask(getName(), 14);
        monitor.worked(2);
        boolean prev = layer.eDeliver();
        try {
            layer.eSetDeliver(false);
            layer.setFilter(oldSelection);

            SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, 10);
            addFeatureCommand.rollback(subProgressMonitor);
            subProgressMonitor.done();
            geom.getFeatureIDRef().set(oldID);

            getMap().getEditManagerInternal().setEditFeature(oldFeature, (Layer) oldLayer);
            if (deselectCreatedFeature) {
                EditBlackboard bb = geom.getEditBlackboard();
                EditGeom newGeom = bb.newGeom(geom.getFeatureIDRef().get(), geom.getShapeType());
                PrimitiveShape shell = geom.getShell();
                for( net.refractions.udig.tools.edit.support.Point point : shell ) {
                    bb.addPoint(point.getX(), point.getY(), newGeom.getShell());
                }

                List<PrimitiveShape> holes = geom.getHoles();
                for( PrimitiveShape primitiveShape : holes ) {
                    PrimitiveShape newHole = newGeom.newHole();
                    for( Point point2 : primitiveShape ) {
                        bb.addPoint(point2.getX(), point2.getY(), newHole);
                    }
                }
                geom = newGeom;
            } else {
                EditUtils.instance.refreshLayer(layer, Collections.singleton(addFeatureCommand
                        .getFid()), null, false, false);
            }
        } finally {
            layer.eSetDeliver(prev);
        }
        monitor.done();
    }

}
