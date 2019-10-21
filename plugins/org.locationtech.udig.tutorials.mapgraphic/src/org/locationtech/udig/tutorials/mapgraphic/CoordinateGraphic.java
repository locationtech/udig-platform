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
package org.locationtech.udig.tutorials.mapgraphic;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.List;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.locationtech.jts.geom.Coordinate;

public class CoordinateGraphic implements MapGraphic {

@SuppressWarnings("unchecked")
public void draw( MapGraphicContext context ) {
    //initialize the graphics handle
     ViewportGraphics g = context.getGraphics();
     g.setColor(Color.RED);
     g.setStroke(ViewportGraphics.LINE_SOLID, 2);
     
     //get the map blackboard
     IMap map = context.getLayer().getMap();
     IBlackboard blackboard = context.getLayer().getMap().getBlackboard();
     
     List<Coordinate> coordinates = 
         (List<Coordinate>) blackboard.get("locations");
     
     if (coordinates == null) {
         return; //no coordinates to draw
     }
         
     //for each coordnate, create a circle and draw
     for (Coordinate coordinate : coordinates) {
        Ellipse2D e = new Ellipse2D.Double(
                coordinate.x-4,
                coordinate.y-4,
                10,10);
        g.draw(e);
     }
 }


}
