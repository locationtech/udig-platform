/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.catalog.jgrass.activeregion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.io.File;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.geotools.gce.grassraster.JGrassRegion;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.udig.catalog.jgrass.activeregion.dialogs.CatalogJGrassMapsetTreeViewerDialog;
import org.locationtech.udig.catalog.jgrass.core.JGrassMapsetGeoResource;
import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.IBlackboard;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.ui.graphics.AWTGraphics;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

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

    @Override
    public void draw(MapGraphicContext context) {
        context.getLayer().setStatus(ILayer.WORKING);

        // initialize the graphics handle
        ViewportGraphics g = context.getGraphics();
        if (g instanceof AWTGraphics) {
            AWTGraphics awtG = (AWTGraphics) g;
            Graphics2D g2D = awtG.g;
            // setting rendering hints
            RenderingHints hints = new RenderingHints(Collections.EMPTY_MAP);
            hints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON));
            g2D.addRenderingHints(hints);
        }

        Dimension screen = context.getMapDisplay().getDisplaySize();

        // get the active region
        IBlackboard blackboard = context.getMap().getBlackboard();
        ActiveRegionStyle style = (ActiveRegionStyle) blackboard.get(ActiveregionStyleContent.ID);
        if (style == null) {
            style = ActiveregionStyleContent.createDefault();
            blackboard.put(ActiveregionStyleContent.ID, style);
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

            Point xyRes = new Point((urPoint.x - ulPoint.x) / style.cols,
                    (llPoint.y - ulPoint.y) / style.rows);

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
                for (int x = ulPoint.x + xyRes.x; x < urPoint.x; x = x + xyRes.x) {
                    g.drawLine(x, ulPoint.y, x, llPoint.y);
                }
                for (int y = ulPoint.y + xyRes.y; y < llPoint.y; y = y + xyRes.y) {
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
                    g.drawString(sb.toString(), 10, 10, ViewportGraphics.ALIGN_LEFT,
                            ViewportGraphics.ALIGN_MIDDLE);
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
    private void getMapset(final IBlackboard styleBlackboard) {

        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    CatalogJGrassMapsetTreeViewerDialog mapsetDialog = new CatalogJGrassMapsetTreeViewerDialog();
                    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                    mapsetDialog.open(shell);
                    List<JGrassMapsetGeoResource> selectedLayers = mapsetDialog.getSelectedLayers();
                    if (selectedLayers == null || selectedLayers.isEmpty()) {
                        return;
                    }
                    JGrassMapsetGeoResource jGrassMapsetGeoResource = selectedLayers.get(0);
                    JGrassRegion activeRegionWindow = jGrassMapsetGeoResource
                            .getActiveRegionWindow();
                    ActiveRegionStyle style = (ActiveRegionStyle) styleBlackboard
                            .get(ActiveregionStyleContent.ID);
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
                        code = "EPSG:" + epsg; //$NON-NLS-1$
                    } catch (Exception e) {
                        // try non EPSG
                        code = CRS.lookupIdentifier(jGrassCrs, true);
                    }
                    style.crsString = code;

                    styleBlackboard.put(ActiveregionStyleContent.ID, style);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }

}
