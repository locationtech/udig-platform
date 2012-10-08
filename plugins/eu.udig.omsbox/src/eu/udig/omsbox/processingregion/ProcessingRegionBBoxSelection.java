/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package eu.udig.omsbox.processingregion;

import java.awt.Point;
import java.awt.Rectangle;

import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.command.MapCommand;
import net.refractions.udig.project.internal.commands.selection.BBoxSelectionCommand;
import net.refractions.udig.project.ui.commands.SelectionBoxCommand;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.SimpleTool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import eu.udig.omsbox.OmsBoxPlugin;

public class ProcessingRegionBBoxSelection extends SimpleTool implements ModalTool {

    public static final String ID = "eu.udig.omsbox.processingregion.ProcessingRegionBBoxSelection"; //$NON-NLS-1$

    private Point start;

    private boolean selecting;

    private net.refractions.udig.project.ui.commands.SelectionBoxCommand shapeCommand;

    /**
     * Creates a new instance of BBoxSelection
     */
    public ProcessingRegionBBoxSelection() {
        super(MOUSE | MOTION);

        OmsBoxPlugin.getDefault().getProcessingRegionMapGraphic();
    }

    /**
     * @see net.refractions.udig.project.ui.tool.SimpleTool#onMouseDragged(net.refractions.udig.project.render.displayAdapter.MapMouseEvent)
     */
    protected void onMouseDragged( MapMouseEvent e ) {
        Point end = e.getPoint();
        shapeCommand.setShape(new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.abs(start.x - end.x), Math
                .abs(start.y - end.y)));
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

                Envelope newRegionBounds = new Envelope(c1, c2);

                ILayer processingRegionLayer = OmsBoxPlugin.getDefault().getProcessingRegionMapGraphic();
                IStyleBlackboard blackboard = processingRegionLayer.getStyleBlackboard();
                ProcessingRegionStyle style = (ProcessingRegionStyle) blackboard.get(ProcessingRegionStyleContent.ID);
                if (style == null) {
                    style = ProcessingRegionStyleContent.createDefault();
                }
                ProcessingRegion processinRegion = new ProcessingRegion(style.west, style.east, style.south, style.north,
                        style.rows, style.cols);
                ProcessingRegion newProcessingRegion = ProcessingRegion.adaptActiveRegionToEnvelope(newRegionBounds,
                        processinRegion);

                style.north = newProcessingRegion.getNorth();
                style.south = newProcessingRegion.getSouth();
                style.east = newProcessingRegion.getEast();
                style.west = newProcessingRegion.getWest();
                style.rows = newProcessingRegion.getRows();
                style.cols = newProcessingRegion.getCols();

                blackboard.put(ProcessingRegionStyleContent.ID, style);

                processingRegionLayer.refresh(null);

                newRegionBounds = newProcessingRegion.getEnvelope();
                sendSelectionCommand(e, newRegionBounds);
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
            command = getContext().getSelectionFactory().createBBoxSelectionCommand(bounds, BBoxSelectionCommand.ADD);
        } else if (e.isModifierDown(MapMouseEvent.MOD1_DOWN_MASK)) {
            command = getContext().getSelectionFactory().createBBoxSelectionCommand(bounds, BBoxSelectionCommand.SUBTRACT);
        } else {
            command = getContext().getSelectionFactory().createBBoxSelectionCommand(bounds, BBoxSelectionCommand.NONE);
        }

        getContext().sendASyncCommand(command);
        selecting = false;
        shapeCommand.setValid(false);
        getContext().getViewportPane().repaint();
    }
}
