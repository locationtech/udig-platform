package net.refractions.udig.tutorials.mapgraphic;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.List;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IMap;
import net.refractions.udig.tutorials.tool.coordinate.CoordinateTool;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinateGraphic implements MapGraphic {

    @SuppressWarnings("unchecked")
    public void draw( MapGraphicContext context ) {
        // initialize the graphics handle
        ViewportGraphics g = context.getGraphics();
        g.setColor(Color.BLUE);
        g.setStroke(ViewportGraphics.LINE_SOLID, 2);

        // get the map blackboard
        IMap map = context.getLayer().getMap();
        IBlackboard blackboard = map.getBlackboard();

        List<Coordinate> coordinates = (List<Coordinate>) blackboard
                .get(CoordinateTool.BLACKBOARD_KEY);

        if (coordinates == null) {
            return; // no coordinates to draw
        }
        for( Coordinate coordinate : coordinates ) {
            g.drawOval((int) coordinate.x - 2, (int) coordinate.y - 5, 3, 5);
        }
    }

    /**
     * One of the bonus questions asks you to draw coordinates on the map.
     * <p>
     * This solution uses the viewport CRS and utility methods. The solution
     * works but changing the map projection will make your stored points wrong.
     * <p>
     * To make this example work you will need to modify CoordianteTool
     * to call pixelToWorld when storing points on the blackboard.
     * </p>
     * @param context
     */
    public void draw2( MapGraphicContext context ) {
        ViewportGraphics g = context.getGraphics();
        g.setColor(Color.BLACK);
        g.setStroke(ViewportGraphics.LINE_SOLID, 2);

        // get the map blackboard
        IMap map = context.getLayer().getMap();
        IBlackboard blackboard = map.getBlackboard();

        List<Coordinate> coordinates = (List<Coordinate>) blackboard
                .get(CoordinateTool.BLACKBOARD_KEY);

        if (coordinates == null) {
            return; // no coordinates to draw
        }
        for( Coordinate coordinate : coordinates ) {
            Point point = context.worldToPixel(coordinate);
            g.drawOval(point.x - 1, point.y - 2, 3, 3);
        }
    }

    /**
     * One of the bonus questions asks you to draw coordinates on the map.
     * <p>
     * This solution uses DefaultGeographicCRS.WGS84 to store and a MathTransform
     * to convert the data into the viewport CRS. And then another transform
     * to convert from the viewport CRS to the screen.
     * <p>
     * To make this example work you will need to modify CoordianteTool
     * to transform your points into WGS84 when they are stored on the blackboard.
     * </p>
     * @param context
     */
    public void draw3( MapGraphicContext context ) {
        // initialize the graphics handle
        ViewportGraphics g = context.getGraphics();
        g.setColor(Color.GREEN);
        g.setStroke(ViewportGraphics.LINE_SOLID, 2);

        // get the map blackboard
        IMap map = context.getLayer().getMap();
        IBlackboard blackboard = map.getBlackboard();

        List<Coordinate> coordinates = (List<Coordinate>) blackboard
                .get(CoordinateTool.BLACKBOARD_KEY);

        if (coordinates == null) {
            return; // no coordinates to draw
        }
        // for each coordinate, create a circle and draw
        MathTransform data2world;
        try {
            data2world = CRS.findMathTransform(DefaultGeographicCRS.WGS84, context.getCRS());
        } catch (FactoryException e2) {
            return;
        }

        for( Coordinate coordinate : coordinates ) {
            try {
                Coordinate worldCoord = JTS.transform(coordinate, null, data2world);
                Point point = context.worldToPixel(worldCoord);
                g.drawOval(point.x - 1, point.y - 2, 3, 3);
            } catch (TransformException e1) {
            }
        }
    }

}
