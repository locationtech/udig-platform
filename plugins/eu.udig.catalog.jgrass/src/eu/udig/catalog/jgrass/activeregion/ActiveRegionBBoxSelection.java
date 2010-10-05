package eu.udig.catalog.jgrass.activeregion;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.commands.selection.BBoxSelectionCommand;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.SimpleTool;

import org.geotools.gce.grassraster.JGrassRegion;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class ActiveRegionBBoxSelection extends SimpleTool implements ModalTool {

    /**
     * Comment for <code>ID</code>
     */
    public static final String ID = "eu.hydrologis.jgrass.activeregion.ui.activeregionbbox"; //$NON-NLS-1$

    private Point start;

    private boolean selecting;

    private net.refractions.udig.project.ui.commands.SelectionBoxCommand shapeCommand;

    private Set<String> selectedFids = new HashSet<String>();

    /**
     * Creates a new instance of BBoxSelection
     */
    public ActiveRegionBBoxSelection() {
        super(MOUSE | MOTION);
    }

    /**
     * @see net.refractions.udig.project.ui.tool.SimpleTool#onMouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    protected void onMouseDragged( MapMouseEvent e ) {
        Point end = e.getPoint();
        shapeCommand.setShape(new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y),
                Math.abs(start.x - end.x), Math.abs(start.y - end.y)));
        context.getViewportPane().repaint();
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mousePressed(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void onMousePressed( MapMouseEvent e ) {
        shapeCommand = new SelectionBoxCommand();

        if (((e.button & MapMouseEvent.BUTTON1) != 0)) {
            selecting = true;

            start = e.getPoint();
            shapeCommand.setValid(true);
            shapeCommand.setShape(new Rectangle(start.x, start.y, 0, 0));
            context.sendASyncCommand(shapeCommand);
        }
    }

    /**
     * @see net.refractions.udig.project.ui.tool.AbstractTool#mouseReleased(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    public void onMouseReleased( MapMouseEvent e ) {
        if (selecting) {
            Point end = e.getPoint();
            if (start == null || start.equals(end)) {

                Envelope bounds = getContext().getBoundingBox(e.getPoint(), 3);
                sendSelectionCommand(e, bounds);
            } else {
                Coordinate c1 = context.getMap().getViewportModel().pixelToWorld(start.x, start.y);
                Coordinate c2 = context.getMap().getViewportModel().pixelToWorld(end.x, end.y);

                Envelope bounds = new Envelope(c1, c2);

                List<ILayer> mapLayers = ApplicationGIS.getActiveMap().getMapLayers();
                for( ILayer layer : mapLayers ) {
                    IStyleBlackboard styleBlackboard = layer.getStyleBlackboard();
                    ActiveRegionStyle style = (ActiveRegionStyle) styleBlackboard.get(
                            ActiveregionStyleContent.ID);
                    if (style != null) {
                        try {
                            JGrassRegion activeRegion = new JGrassRegion(style.windPath);
                            JGrassRegion newActiveRegion = JGrassRegion
                                    .adaptActiveRegionToEnvelope(bounds, activeRegion);
                            JGrassRegion.writeWINDToMapset(new File(style.windPath).getParent(),
                                    newActiveRegion);
                            style.north = (float) newActiveRegion.getNorth();
                            style.south = (float) newActiveRegion.getSouth();
                            style.east = (float) newActiveRegion.getEast();
                            style.west = (float) newActiveRegion.getWest();
                            style.rows = newActiveRegion.getRows();
                            style.cols = newActiveRegion.getCols();
                            
                            styleBlackboard.put(ActiveregionStyleContent.ID, style);
                            
                            layer.refresh(null);
                            break;
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                sendSelectionCommand(e, bounds);
            }
        }
    }
    /**
     * @param e
     * @param bounds
     */
    protected void sendSelectionCommand( MapMouseEvent e, Envelope bounds ) {
        MapCommand command;
        if (e.isModifierDown(MapMouseEvent.MOD2_DOWN_MASK)) {
            command = getContext().getSelectionFactory().createBBoxSelectionCommand(bounds,
                    BBoxSelectionCommand.ADD);
        } else if (e.isModifierDown(MapMouseEvent.MOD1_DOWN_MASK)) {
            command = getContext().getSelectionFactory().createBBoxSelectionCommand(bounds,
                    BBoxSelectionCommand.SUBTRACT);
        } else {
            command = getContext().getSelectionFactory().createBBoxSelectionCommand(bounds,
                    BBoxSelectionCommand.NONE);
        }

        getContext().sendASyncCommand(command);
        selecting = false;
        shapeCommand.setValid(false);
        getContext().getViewportPane().repaint();
    }
}
