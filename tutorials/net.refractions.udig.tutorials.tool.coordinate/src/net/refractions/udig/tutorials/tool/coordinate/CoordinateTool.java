package net.refractions.udig.tutorials.tool.coordinate;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.IMap;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.internal.commands.draw.DrawShapeCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.AbstractModalTool;

import com.vividsolutions.jts.geom.Coordinate;

public class CoordinateTool extends AbstractModalTool {

    public static final String BLACKBOARD_KEY = "net.refractions.udig.tutorials.tool.coordinate"; //$NON-NLS-1$

    DrawShapeCommand command;

    public CoordinateTool() {
        super(MOUSE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void mousePressed( MapMouseEvent e ) {

        // throw a coordinate onto the current map blackboard
        IMap map = ApplicationGIS.getActiveMap();
        if (map == null)
            return;

        IBlackboard blackboard = map.getBlackboard();
        List<Coordinate> points = (List<Coordinate>) blackboard.get(BLACKBOARD_KEY);
        if (points == null) {
            points = new ArrayList<Coordinate>();
            blackboard.put(BLACKBOARD_KEY, points);
        }
        Coordinate coordinate;
        coordinate = new Coordinate(e.x, e.y);

        // coordinate = getContext().pixelToWorld(e.x,e.y) );
        points.add(coordinate);

        Rectangle2D r = new Rectangle2D.Double(e.x, e.y, 2, 2);
        command = getContext().getDrawFactory().createDrawShapeCommand(r, Color.BLACK);

        getContext().sendASyncCommand(command);
        getContext().getSelectedLayer().refresh(null);
    }

    @Override
    public void mouseReleased( MapMouseEvent e ) {
        // remember the command and set it to invalid to release
        command.setValid(false);
    }
    /**
     * One of the examples asks you to store the points "on the map" rather than on the scren.
     * <p>
     * Using the viewport pixel2world method is the easiest way to do this...
     *
     * @param e
     */
    public void mousePressed2( MapMouseEvent e ) {
        IMap map = ApplicationGIS.getActiveMap();
        if (map == null)
            return;

        IBlackboard blackboard = map.getBlackboard();
        List<Coordinate> points = (List<Coordinate>) blackboard.get(BLACKBOARD_KEY);
        if (points == null) {
            points = new ArrayList<Coordinate>();
            blackboard.put(BLACKBOARD_KEY, points);
        }
        Coordinate coordinate;
        coordinate = getContext().pixelToWorld(e.x, e.y);
        points.add(coordinate);

        Rectangle2D r = new Rectangle2D.Double(e.x, e.y, 2, 2);
        command = getContext().getDrawFactory().createDrawShapeCommand(r, Color.BLACK);

        getContext().sendASyncCommand(command);
        getContext().getSelectedLayer().refresh(null);
    }
    /**
     * One of the examples asks you to store the points "on the map" rather than on the scren.
     * <p>
     * Transforming your points to a known CRS (say DefaultGeographic.WGS84) will be even better;
     * that way your data will still be correct even if the map projection is changed.
     *
     * @param e
     */
    public void mousePressed3( MapMouseEvent e ) {
        IMap map = ApplicationGIS.getActiveMap();
        if (map == null)
            return;

        IBlackboard blackboard = map.getBlackboard();
        List<Coordinate> points = (List<Coordinate>) blackboard.get(BLACKBOARD_KEY);
        if (points == null) {
            points = new ArrayList<Coordinate>();
            blackboard.put(BLACKBOARD_KEY, points);
        }
        Coordinate coordinate;
        coordinate = getContext().pixelToWorld(e.x, e.y);

        CoordinateReferenceSystem crs = getContext().getCRS();
        if (!CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs)) {
            try {
                MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crs);
                coordinate = JTS.transform(coordinate, null, transform);
            } catch (FactoryException unknownTransform) {
                return; // should update the status bar...we cannot transform to WGS84
            } catch (TransformException transformFailed) {
                return; // should update the status bar...we are outside of the valid range
            }
        }
        points.add(coordinate);

        Rectangle2D r = new Rectangle2D.Double(e.x, e.y, 2, 2);
        command = getContext().getDrawFactory().createDrawShapeCommand(r, Color.BLACK);

        getContext().sendASyncCommand(command);
        getContext().getSelectedLayer().refresh(null);
    }
}
