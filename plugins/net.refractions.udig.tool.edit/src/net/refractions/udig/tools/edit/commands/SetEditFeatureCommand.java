/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package net.refractions.udig.tools.edit.commands;

import java.util.Collections;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.command.AbstractCommand;
import net.refractions.udig.project.command.UndoableMapCommand;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.IToolContext;
import net.refractions.udig.tools.edit.EditToolHandler;
import net.refractions.udig.tools.edit.support.Point;
import net.refractions.udig.tools.edit.support.PrimitiveShape;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;

/**
 * Sets the feature currently being edited in the edit manager.
 * 
 * @author chorner
 * @since 1.1.0
 */
public class SetEditFeatureCommand extends AbstractCommand implements UndoableMapCommand {

    EditToolHandler handler;
    Point clickPoint;
    PrimitiveShape shape;
    SimpleFeature oldFeature;
    ILayer oldLayer;
    private Layer editLayer;
    private Filter oldFilter;

    /**
     * @deprecated
     */
    public SetEditFeatureCommand (EditToolHandler handler, MapMouseEvent e, PrimitiveShape shape) {
        this.handler = handler;
        this.clickPoint = Point.valueOf(e.x, e.y);
        this.shape = shape;
    }
    public SetEditFeatureCommand (EditToolHandler handler, Point clickPoint, PrimitiveShape shape) {
        this.handler = handler;
        this.clickPoint = clickPoint;
        this.shape = shape;
    }
    
    public void run( IProgressMonitor monitor ) throws Exception {
        IToolContext context = handler.getContext();
        java.awt.Point point = new java.awt.Point(clickPoint.getX(), clickPoint.getY());
        ReferencedEnvelope bbox = handler.getContext().getBoundingBox(point,7);
        FeatureCollection<SimpleFeatureType, SimpleFeature> fc = context.getFeaturesInBbox(handler.getEditLayer(), bbox);
        FeatureIterator<SimpleFeature> it = fc.features();
        SimpleFeature feature = null;
        while (it.hasNext()) {
            SimpleFeature feat = it.next();
            if (feat.getID().equals(shape.getEditGeom().getFeatureIDRef().toString())) {
                feature = feat;
                break;
            }
        }
        it.close();
        oldFeature = getMap().getEditManagerInternal().getEditFeature();
        oldLayer = getMap().getEditManagerInternal().getEditLayer();
        editLayer = (Layer) handler.getEditLayer();
        oldFilter = editLayer.getFilter();
        getMap().getEditManagerInternal().setEditFeature(feature, editLayer);
        editLayer.setFilter(fidFilter(feature));
        
    }

    private Filter fidFilter( SimpleFeature feature ) {
        FilterFactory factory = CommonFactoryFinder
        .getFilterFactory(GeoTools.getDefaultHints());
        Id id = factory.id(Collections.singleton(factory.featureId(feature.getID())));
        return id;
    }
    public String getName() {
        return "Set Current Selection";
    }

    public void rollback( IProgressMonitor monitor ) throws Exception {
        getMap().getEditManagerInternal().setEditFeature(oldFeature, (Layer) oldLayer);
        editLayer.setFilter(oldFilter);
    }

}
