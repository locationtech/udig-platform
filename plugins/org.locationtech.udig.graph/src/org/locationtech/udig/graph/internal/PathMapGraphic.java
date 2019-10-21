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
package org.locationtech.udig.graph.internal;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Node;

import org.locationtech.jts.geom.Point;

public class PathMapGraphic implements MapGraphic {
    public static final String ID = "mapgraphic:/localhost/mapgraphic#org.locationtech.udig.tutorials.tracking.graphics.path";
    
    public PathMapGraphic() {
    }

    public void draw( MapGraphicContext context ) {
        IBlackboard mapboard = context.getMap().getBlackboard();
        List<Node> waypoints = (List<Node>) mapboard.get("waypoints");
        if( waypoints == null ){
            waypoints = Collections.emptyList();
        }
        List<Edge> path = (List<Edge>) mapboard.get("path");
        if( path == null ){
            path = Collections.emptyList();
        }        
        ViewportGraphics graphics = context.getGraphics();
        graphics.setColor( Color.BLACK);
        graphics.setLineWidth(4);
        for( Edge edge : path ){
            Object obj = edge.getObject();
            Node start = edge.getNodeA();
            Node end = edge.getNodeB();
        
            Point startPoint = (Point) start.getObject();
            Point endPoint = (Point) end.getObject();            
            java.awt.Point startPixel = context.worldToPixel( startPoint.getCoordinate() );
            java.awt.Point endPixel = context.worldToPixel( endPoint.getCoordinate() );

            graphics.drawLine(startPixel.x, startPixel.y, endPixel.x, endPixel.y);            
        }
        graphics.setColor( Color.YELLOW );
        graphics.setLineWidth(2);
        for( Edge edge : path ){
            Object obj = edge.getObject();
            Node start = edge.getNodeA();
            Node end = edge.getNodeB();
        
            Point startPoint = (Point) start.getObject();
            Point endPoint = (Point) end.getObject();            
            java.awt.Point startPixel = context.worldToPixel( startPoint.getCoordinate() );
            java.awt.Point endPixel = context.worldToPixel( endPoint.getCoordinate() );

            graphics.drawLine(startPixel.x, startPixel.y, endPixel.x, endPixel.y);            
        }
        for( Node node : waypoints ){
            Point point = (Point) node.getObject();
            java.awt.Point pixel = context.worldToPixel( point.getCoordinate() );
            
            graphics.setColor( Color.YELLOW );                
            graphics.fillOval(pixel.x-3, pixel.y-3, 7, 7 );

            graphics.setColor( Color.BLACK );
            graphics.drawOval(pixel.x-3, pixel.y-3, 7, 7 );
        }
    }

}
