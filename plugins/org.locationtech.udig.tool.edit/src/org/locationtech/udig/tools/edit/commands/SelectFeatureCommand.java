/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tools.edit.commands;

import java.util.Collection;

import org.locationtech.udig.project.command.AbstractCommand;
import org.locationtech.udig.project.command.UndoableMapCommand;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.tool.edit.internal.Messages;
import org.locationtech.udig.tools.edit.EditToolHandler;
import org.locationtech.udig.tools.edit.support.EditBlackboard;
import org.locationtech.udig.tools.edit.support.EditGeom;
import org.locationtech.udig.tools.edit.support.EditUtils;
import org.locationtech.udig.tools.edit.support.Point;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

/**
 * Adds the feature's geometry to the edit blackboard (effectively selecting it).
 * 
 * @author jones
 * @since 1.1.0
 */
public class SelectFeatureCommand extends AbstractCommand implements UndoableMapCommand {

    private Collection<EditGeom> added;
    private SimpleFeature feature;
    private EditBlackboard blackboard;
    private Layer editLayer;
    private ReferencedEnvelope refreshBounds;
    private EditToolHandler handler;
    private Point mouseLocation;
    private SetEditFeatureCommand setEditFeatureCommand;
    
    /**
     * new instance
     * @param handler used to obtain the blackboard for the currently selected layer.
     * @param feature the feature to add.
     * @param mouseLocation If not null it will set the current shape to be the Geom that intersects the mouseLocation
     */
    public SelectFeatureCommand( EditToolHandler handler, SimpleFeature feature, Point mouseLocation) {
        this.feature=feature;
        editLayer = (Layer) handler.getEditLayer();
        this.blackboard=handler.getEditBlackboard(editLayer);
        this.handler=handler;
        this.mouseLocation=mouseLocation;
    }

    /**
     * New Instance
     * @param blackboard blackboard to add features to.
     * @param feature2 the feature to add
     */
    public SelectFeatureCommand( EditBlackboard blackboard2, SimpleFeature feature2 ) {
        this.feature=feature2;
        this.blackboard=blackboard2;
    }
    

    public void run( IProgressMonitor monitor ) throws Exception {
        if (editLayer==null)
            editLayer=(Layer) getMap().getEditManager().getSelectedLayer();
        this.added=blackboard.addGeometry((Geometry) feature.getDefaultGeometry(), feature.getID()).values();
        if( !added.isEmpty() && mouseLocation!=null ){
            Class<?> type = editLayer.getSchema().getGeometryDescriptor().getType().getBinding();
            boolean polygonLayer=Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type);
            EditGeom geom = EditUtils.instance.getGeomWithMouseOver(added, mouseLocation, polygonLayer);
            handler.setCurrentShape(geom.getShell());
            setEditFeatureCommand = new SetEditFeatureCommand(handler, mouseLocation, geom.getShell());
            setEditFeatureCommand.setMap(getMap());
            setEditFeatureCommand.run(SubMonitor.convert(monitor));
        }
        
        //make sure the display refreshes
        this.refreshBounds=new ReferencedEnvelope(feature.getBounds());
        EditUtils.instance.refreshLayer(editLayer, feature, refreshBounds, false, true);
    }
    
    public String getName() {
        return Messages.AddGeomCommand_name;
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        if( setEditFeatureCommand!=null ){
            setEditFeatureCommand.rollback(SubMonitor.convert(monitor));
        }
        blackboard.removeGeometries(added);

        refreshBounds.expandToInclude(new ReferencedEnvelope(feature.getBounds()));
        EditUtils.instance.refreshLayer(editLayer, feature, refreshBounds, true, false);
    }

}
