/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.legend;

import java.awt.Rectangle;

import org.locationtech.udig.mapgraphic.style.LocationStyleContent;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.project.ui.ApplicationGIS;
import org.locationtech.udig.project.ui.render.displayAdapter.MapMouseEvent;
import org.locationtech.udig.project.ui.tool.ModalTool;
import org.locationtech.udig.project.ui.tool.SimpleTool;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * A tool that gives a way to drag mapgraphics around with the mouse.
 * 
 * @author Andrea Antonello - www.hydrologis.com
 */
public class MapgraphicDragger extends SimpleTool implements ModalTool {

    private IStyleBlackboard bBoard;
    private ILayer selectedLayer;
    private boolean goGo = true;
    private Point startPoint;
    private Point runningPoint;
    private RasterLegendStyle rasterLegendStyle;
    private VectorLegendStyle vectorLegendStyle;
    private Rectangle scaleBarRectangle;
    private org.locationtech.udig.legend.ui.LegendStyle legendStyle;

    public MapgraphicDragger() {
        super(MOUSE | MOTION);
    }

    protected void onMousePressed( MapMouseEvent e ) {
        super.onMousePressed(e);
        // reset
        goGo = true;
        rasterLegendStyle = null;
        vectorLegendStyle = null;
        scaleBarRectangle = null;
        legendStyle = null;

        startPoint = new Point(e.x, e.y);
        selectedLayer = ApplicationGIS.getActiveMap().getEditManager().getSelectedLayer();
        bBoard = selectedLayer.getStyleBlackboard();
        rasterLegendStyle = (RasterLegendStyle) bBoard.get(RasterLegendStyleContent.ID);
        vectorLegendStyle = (VectorLegendStyle) bBoard.get(VectorLegendStyleContent.ID);
        scaleBarRectangle = (Rectangle) bBoard.get(LocationStyleContent.ID);
        legendStyle = (org.locationtech.udig.legend.ui.LegendStyle) bBoard
                .get(org.locationtech.udig.legend.ui.LegendStyleContent.ID);

        if (selectedLayer == null
                || (rasterLegendStyle == null ? (vectorLegendStyle == null ? (scaleBarRectangle == null ? (legendStyle == null
                        ? true
                        : false) : false) : false) : false)) {
            goGo = false;
            Thread thread = new Thread(){
                public void run() {
                    Display.getDefault().syncExec(new Runnable(){
                        public void run() {
                            Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
                            MessageDialog.openWarning(shell, "Unsupported", "The selected layer type is not supported.");
                        }
                    });
                }
            };
            thread.start();
        }

    }
    protected void onMouseDragged( MapMouseEvent e ) {
        super.onMouseDragged(e);

        if (goGo) {
            runningPoint = new Point(e.x, e.y);

            int dx = runningPoint.x - startPoint.x;
            int dy = runningPoint.y - startPoint.y;

            if (rasterLegendStyle != null) {
                rasterLegendStyle.xPos = rasterLegendStyle.xPos + dx;
                rasterLegendStyle.yPos = rasterLegendStyle.yPos + dy;
            }
            if (vectorLegendStyle != null) {
                vectorLegendStyle.xPos = vectorLegendStyle.xPos + dx;
                vectorLegendStyle.yPos = vectorLegendStyle.yPos + dy;
            }
            if (scaleBarRectangle != null) {
                scaleBarRectangle.setLocation(scaleBarRectangle.x + dx, scaleBarRectangle.y + dy);
            }
            // TODO put the positon x,y
            // if (legendStyle!=null) {
            // legendStyle.xPos = legendStyle.xPos + dx;
            // legendStyle.yPos = legendStyle.yPos + dy;
            // }

            // selectedLayer.refresh(null);
            startPoint = runningPoint;

        }
    }

    protected void onMouseReleased( MapMouseEvent e ) {
        selectedLayer.refresh(null);
        super.onMouseReleased(e);
    }

}
