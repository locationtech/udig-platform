/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.style.jgrass.legend;

import java.awt.Rectangle;

import net.refractions.udig.mapgraphic.style.LocationStyleContent;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.project.ui.ApplicationGIS;
import net.refractions.udig.project.ui.render.displayAdapter.MapMouseEvent;
import net.refractions.udig.project.ui.tool.ModalTool;
import net.refractions.udig.project.ui.tool.SimpleTool;

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
    private net.refractions.udig.legend.ui.LegendStyle legendStyle;

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
        legendStyle = (net.refractions.udig.legend.ui.LegendStyle) bBoard
                .get(net.refractions.udig.legend.ui.LegendStyleContent.ID);

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
