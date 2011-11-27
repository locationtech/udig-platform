/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * (C) C.U.D.A.M. Universita' di Trento
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
package eu.udig.catalog.jgrass.activeregion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.util.Collections;
import java.util.List;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.IBlackboard;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.graphics.AWTGraphics;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;

import eu.udig.catalog.jgrass.activeregion.dialogs.CatalogJGrassMapsetTreeViewerDialog;
import eu.udig.catalog.jgrass.core.JGrassMapsetGeoResource;

/**
 * <p>
 * Class representing the rendered Active Region of JGrass, i.e. the region inside which processing
 * occurs
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.1.0
 */
public class ActiveRegionMapGraphic implements MapGraphic {

    public ActiveRegionMapGraphic() {
    }

    public void draw( MapGraphicContext context ) {
        context.getLayer().setStatus(ILayer.WORKING);

        // initialize the graphics handle
        ViewportGraphics g = context.getGraphics();
        if (g instanceof AWTGraphics) {
            AWTGraphics awtG = (AWTGraphics) g;
            Graphics2D g2D = awtG.g;
            // setting rendering hints
            RenderingHints hints = new RenderingHints(Collections.EMPTY_MAP);
            hints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            g2D.addRenderingHints(hints);
        }

        Dimension screen = context.getMapDisplay().getDisplaySize();

        // get the active region
        IBlackboard blackboard = context.getMap().getBlackboard();
        ActiveRegionStyle style = (ActiveRegionStyle) blackboard.get(ActiveregionStyleContent.ID);
        if (style == null) {
            style = ActiveregionStyleContent.createDefault();
            blackboard.put(ActiveregionStyleContent.ID, style);
            // ((IBlackboard) styleBlackboard)
            // .setSelected(new String[]{ActiveregionStyleContent.ID});
        }

        if (style.windPath == null) {
            getMapset(blackboard);
        }

        CoordinateReferenceSystem destinationCRS = context.getCRS();
        try {
            CoordinateReferenceSystem crs = CRS.decode(style.crsString);
            MathTransform transform = CRS.findMathTransform(crs, destinationCRS, true);

            Coordinate ul = new Coordinate(style.west, style.north);
            Coordinate ur = new Coordinate(style.east, style.north);
            Coordinate ll = new Coordinate(style.west, style.south);
            Coordinate lr = new Coordinate(style.east, style.south);

            Coordinate newUL = JTS.transform(ul, null, transform);
            Coordinate newUR = JTS.transform(ur, null, transform);
            Coordinate newLL = JTS.transform(ll, null, transform);
            Coordinate newLR = JTS.transform(lr, null, transform);

            // draw the rectangle around the active region green:143
            float[] rgba = style.backgroundColor.getColorComponents(null);
            g.setColor(new Color(rgba[0], rgba[1], rgba[2], style.bAlpha));

            Point ulPoint = context.worldToPixel(newUL);
            Point urPoint = context.worldToPixel(newUR);
            Point llPoint = context.worldToPixel(newLL);
            Point lrPoint = context.worldToPixel(newLR);

            Point xyRes = new Point((urPoint.x - ulPoint.x) / style.cols, (llPoint.y - ulPoint.y) / style.rows);

            int screenWidth = screen.width;
            int screenHeight = screen.height;

            GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
            path.moveTo(0, 0);
            path.lineTo(screenWidth, 0);
            path.lineTo(screenWidth, screenHeight);
            path.lineTo(0, screenHeight);
            path.closePath();
            path.moveTo(ulPoint.x, ulPoint.y);
            path.lineTo(urPoint.x, urPoint.y);
            path.lineTo(lrPoint.x, lrPoint.y);
            path.lineTo(llPoint.x, llPoint.y);
            path.closePath();

            g.fill(path);
            if (style.doGrid && xyRes.x > 1 && xyRes.y > 1) {
                for( int x = ulPoint.x + xyRes.x; x < urPoint.x; x = x + xyRes.x ) {
                    g.drawLine(x, ulPoint.y, x, llPoint.y);
                }
                for( int y = ulPoint.y + xyRes.y; y < llPoint.y; y = y + xyRes.y ) {
                    g.drawLine(urPoint.x, y, ulPoint.x, y);
                }
            }

            rgba = style.foregroundColor.getColorComponents(null);
            g.setColor(new Color(rgba[0], rgba[1], rgba[2], style.fAlpha));
            g.setStroke(ViewportGraphics.LINE_SOLID, 2);
            g.draw(path);

            if (style.windPath != null) {
                File windFile = new File(style.windPath);
                if (windFile.exists()) {
                    File mapsetFile = windFile.getParentFile();
                    StringBuilder sb = new StringBuilder();
                    sb.append(mapsetFile.getParentFile().getName());
                    sb.append("/");
                    sb.append(mapsetFile.getName());
                    g.drawString(sb.toString(), 10, 10,
                            ViewportGraphics.ALIGN_LEFT, ViewportGraphics.ALIGN_MIDDLE);
                }
            }

        } catch (Exception e) {
            context.getLayer().setStatus(ILayer.ERROR);
            context.getLayer().setStatusMessage("Error in reprojection...");
            e.printStackTrace();
            return;
        }
        context.getLayer().setStatus(ILayer.DONE);
        context.getLayer().setStatusMessage("Layer rendered");

    }
    /**
     * @param styleBlackboard 
     * @param style
     */
    private void getMapset( final IBlackboard styleBlackboard ) {

        Display.getDefault().syncExec(new Runnable(){

            public void run() {
                try {
                    CatalogJGrassMapsetTreeViewerDialog mapsetDialog = new CatalogJGrassMapsetTreeViewerDialog();
                    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                    mapsetDialog.open(shell);
                    List<JGrassMapsetGeoResource> selectedLayers = mapsetDialog.getSelectedLayers();
                    if (selectedLayers.size() == 0) {
                        return;
                    }
                    JGrassMapsetGeoResource jGrassMapsetGeoResource = selectedLayers.get(0);
                    JGrassRegion activeRegionWindow = jGrassMapsetGeoResource.getActiveRegionWindow();
                    ActiveRegionStyle style = (ActiveRegionStyle) styleBlackboard.get(ActiveregionStyleContent.ID);
                    style.windPath = jGrassMapsetGeoResource.getActiveRegionWindowPath();
                    style.north = (float) activeRegionWindow.getNorth();
                    style.south = (float) activeRegionWindow.getSouth();
                    style.east = (float) activeRegionWindow.getEast();
                    style.west = (float) activeRegionWindow.getWest();
                    style.rows = activeRegionWindow.getRows();
                    style.cols = activeRegionWindow.getCols();

                    CoordinateReferenceSystem jGrassCrs = jGrassMapsetGeoResource.getLocationCrs();
                    String code = null;
                    try {
                        Integer epsg = CRS.lookupEpsgCode(jGrassCrs, true);
                        code = "EPSG:" + epsg;
                    } catch (Exception e) {
                        // try non epsg
                        code = CRS.lookupIdentifier(jGrassCrs, true);
                    }
                    style.crsString = code;

                    styleBlackboard.put(ActiveregionStyleContent.ID, style);
                    // ((StyleBlackboard) styleBlackboard).setSelected(new
                    // String[]{ActiveregionStyleContent.ID});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }

}
