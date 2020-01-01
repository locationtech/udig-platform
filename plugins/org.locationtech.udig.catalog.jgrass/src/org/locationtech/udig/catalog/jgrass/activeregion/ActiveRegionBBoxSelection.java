/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.activeregion;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IMap;
import org.locationtech.udig.project.command.MapCommand;
import org.locationtech.udig.project.internal.commands.selection.BBoxSelectionCommand;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.commands.SelectionBoxCommand;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.tool.SimpleTool;

import org.geotools.gce.grassraster.JGrassRegion;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

import org.locationtech.udig.catalog.jgrass.JGrassPlugin;

public class ActiveRegionBBoxSelection extends SimpleTool implements ModalTool {

    /**
     * Comment for <code>ID</code>
     */
    public static final String ID = "org.locationtech.udig.catalog.jgrass.activeregion.ui.activeregionbbox"; //$NON-NLS-1$

    private Point start;

    private boolean selecting;

    private org.locationtech.udig.project.ui.commands.SelectionBoxCommand shapeCommand;

    /**
     * Creates a new instance of BBoxSelection
     */
    public ActiveRegionBBoxSelection() {
        super(MOUSE | MOTION);

        JGrassPlugin.getDefault().getActiveRegionMapGraphic();
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

                Envelope bounds = new Envelope(c1, c2);

                ILayer activeRegionLayer = JGrassPlugin.getDefault().getActiveRegionMapGraphic();
                IMap activeMap = ApplicationGIS.getActiveMap();
                IBlackboard blackboard = activeMap.getBlackboard();
                ActiveRegionStyle style = (ActiveRegionStyle) blackboard.get(ActiveregionStyleContent.ID);
                if (style != null) {
                    try {
                        JGrassRegion activeRegion = new JGrassRegion(style.windPath);
                        JGrassRegion newActiveRegion = JGrassRegion.adaptActiveRegionToEnvelope(bounds, activeRegion);
                        JGrassRegion.writeWINDToMapset(new File(style.windPath).getParent(), newActiveRegion);
                        style.north = (float) newActiveRegion.getNorth();
                        style.south = (float) newActiveRegion.getSouth();
                        style.east = (float) newActiveRegion.getEast();
                        style.west = (float) newActiveRegion.getWest();
                        style.rows = newActiveRegion.getRows();
                        style.cols = newActiveRegion.getCols();

                        blackboard.put(ActiveregionStyleContent.ID, style);

                        activeRegionLayer.refresh(null);
                    } catch (IOException e1) {
                        e1.printStackTrace();
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
