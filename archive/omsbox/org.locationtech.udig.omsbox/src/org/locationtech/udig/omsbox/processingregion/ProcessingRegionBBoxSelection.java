/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.omsbox.processingregion;

import java.awt.Point;
import java.awt.Rectangle;

import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.commands.selection.BBoxSelectionCommand;
import org.locationtech.udig.project.ui.commands.SelectionBoxCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.tool.SimpleTool;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import org.locationtech.udig.omsbox.OmsBoxPlugin;

public class ProcessingRegionBBoxSelection extends SimpleTool implements ModalTool {

    public static final String ID = "org.locationtech.udig.omsbox.processingregion.ProcessingRegionBBoxSelection"; //$NON-NLS-1$

    private Point start;

    private boolean selecting;

    private org.locationtech.udig.project.ui.commands.SelectionBoxCommand shapeCommand;

    /**
     * Creates a new instance of BBoxSelection
     */
    public ProcessingRegionBBoxSelection() {
        super(MOUSE | MOTION);

        OmsBoxPlugin.getDefault().getProcessingRegionMapGraphic();
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.SimpleTool#onMouseDragged(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
     */
    protected void onMouseDragged( MapMouseEvent e ) {
        Point end = e.getPoint();
        shapeCommand.setShape(new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.abs(start.x - end.x), Math
                .abs(start.y - end.y)));
        context.getViewportPane().repaint();
    }

    /**
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mousePressed(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
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
     * @see org.locationtech.udig.project.ui.tool.AbstractTool#mouseReleased(org.locationtech.udig.project.render.displayAdapter.MapMouseEvent)
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
