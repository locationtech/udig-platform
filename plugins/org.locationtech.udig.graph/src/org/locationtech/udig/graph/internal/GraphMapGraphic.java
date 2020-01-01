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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Point;

public class GraphMapGraphic implements MapGraphic {
    public static final String ID = "mapgraphic:/localhost/mapgraphic#org.locationtech.udig.tutorials.tracking.graphics.graph";
    
    public GraphMapGraphic() {
    }

    public void draw( MapGraphicContext context ) {
        IBlackboard mapboard = context.getMap().getBlackboard();
        if( !mapboard.contains("graph")){
            return;
        }
        List<Node> waypoints = (List<Node>) mapboard.get("waypoints");
        if( waypoints == null ){
            waypoints = Collections.emptyList();
        }
        List<Edge> path = (List<Edge>) mapboard.get("path");
        if( path == null ){
            path = Collections.emptyList();
        }        
        Graph graph = (Graph) mapboard.get("graph");
        ViewportGraphics graphics = context.getGraphics();
        graphics.setColor( Color.LIGHT_GRAY );
        Collection<Node> nodes = graph.getNodes();
        for( Node node : nodes ){
            if( waypoints.contains(node)){
                graphics.setColor( Color.BLACK );                
            }
            Object obj = node.getObject();
            if( obj instanceof Point ){
                Point point = (Point) obj;
                java.awt.Point pixel = context.worldToPixel( point.getCoordinate() );
                graphics.fillOval(pixel.x-2, pixel.y-2, 5, 5 );
            }
            if( waypoints.contains(node)){
                graphics.setColor( Color.LIGHT_GRAY );                                
            }
        }
        Collection<Edge> edges = graph.getEdges();
        graphics.setColor( Color.DARK_GRAY );
        for( Edge edge : edges ){
            Object obj = edge.getObject();
            if( path.contains(edge)){
                graphics.setColor( Color.BLACK );
                graphics.setLineWidth(3);
            }
            Node start = edge.getNodeA();
            Node end = edge.getNodeB();
        
            Point startPoint = (Point) start.getObject();
            Point endPoint = (Point) end.getObject();            
            java.awt.Point startPixel = context.worldToPixel( startPoint.getCoordinate() );
            java.awt.Point endPixel = context.worldToPixel( endPoint.getCoordinate() );
            
            graphics.drawLine(startPixel.x, startPixel.y, endPixel.x, endPixel.y);            
            double angle = Angle.angle(startPoint.getCoordinate(), endPoint.getCoordinate());
            double dx = Math.cos( angle+0.1 );
            double dy = Math.sin( angle+0.1 );
            graphics.drawLine(
                    endPixel.x, endPixel.y,
                    (int)(endPixel.x - (10.0*dx)),
                    (int)(endPixel.y + (10.0*dy)));
            dx = Math.cos( angle-0.1 );
            dy = Math.sin( angle-0.1 );
            graphics.drawLine(
                    endPixel.x, endPixel.y,
                    (int)(endPixel.x - (10.0*dx)),
                    (int)(endPixel.y + (10.0*dy)));
            if( path.contains(edge)){
                graphics.setColor( Color.DARK_GRAY);
                graphics.setLineWidth(1);
            }
        }
    }

}
