package net.refractions.udig.graph.internal;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Node;

import com.vividsolutions.jts.geom.Point;

public class PathMapGraphic implements MapGraphic {
    public static final String ID = "mapgraphic:/localhost/mapgraphic#net.refractions.udig.tutorials.tracking.graphics.path";
    
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
