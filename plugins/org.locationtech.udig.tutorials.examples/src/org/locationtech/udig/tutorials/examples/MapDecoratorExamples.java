/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.tutorials.examples;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;

import org.locationtech.udig.catalog.CatalogPlugin;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IRepository;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * The following code examples show how to do a few things with map graphics.
 * 
 * @author Jody Garnett (LISAsoft)
 * 
 */
public class MapDecoratorExamples {

    public void lookupExample() {
        IRepository local = CatalogPlugin.getDefault().getLocal();

        final ID GRID_ID = new ID("mapgraphic:///localhost/mapgraphic#grid", null);
        IGeoResource gridResource = local.getById(IGeoResource.class, GRID_ID, new NullProgressMonitor());
        // You can then use this with the AddLayersCommand
    }

    public void draw(MapGraphicContext context) {
        // initialize the graphics handle
        ViewportGraphics g = context.getGraphics();
        g.setColor(Color.RED);
        g.setStroke(ViewportGraphics.LINE_SOLID, 2);

        // get the map blackboard
        IMap map = context.getLayer().getMap();
        IBlackboard blackboard = context.getLayer().getMap().getBlackboard();

        List<Coordinate> coordinates = (List<Coordinate>) blackboard.get("locations");

        if (coordinates == null) {
            return; // no coordinates to draw
        }
        try {
            MathTransform dataToWorldTransform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, context.getCRS() );
    
            // for each location, create a circle and draw
            for (Coordinate location : coordinates) {
                Coordinate world = JTS.transform(location,  null, dataToWorldTransform);
                Point pixel = context.worldToPixel(world);
                Ellipse2D e = new Ellipse2D.Double(pixel.x - 4, pixel.y - 4, 10, 10);
                g.draw(e);
            }
        }
        catch (FactoryException unableToTransform){
            context.getLayer().setStatusMessage(unableToTransform.getMessage());
        } catch (TransformException outOfBounds) {
            context.getLayer().setStatusMessage(outOfBounds.getMessage());
        }
    }
}
