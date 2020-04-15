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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.Collections;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.IStyleBlackboard;
import org.locationtech.udig.ui.graphics.AWTGraphics;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

import org.locationtech.jts.geom.Coordinate;

/**
 * <p>
 * Class representing the rendered Processing Region of uDig, i.e. the region inside which processing
 * occurs
 * </p>
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @since 1.2.1
 */
public class ProcessingRegionMapGraphic implements MapGraphic {

    public static final String ID = "org.locationtech.udig.omsbox.processingregion.ProcessingRegionMapGraphic";

    public ProcessingRegionMapGraphic() {
    }

    public void draw( MapGraphicContext context ) {
        context.getLayer().setStatus(ILayer.WORKING);

        // initialize the graphics handle
        ViewportGraphics g = context.getGraphics();
        if (g instanceof AWTGraphics) {
            AWTGraphics awtG = (AWTGraphics) g;
            Graphics2D g2D = awtG.g;
            // setting rendering hints
            @SuppressWarnings("unchecked")
            RenderingHints hints = new RenderingHints(Collections.EMPTY_MAP);
            hints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            g2D.addRenderingHints(hints);
        }

        Dimension screen = context.getMapDisplay().getDisplaySize();

        // get the active region
        IStyleBlackboard blackboard = context.getLayer().getStyleBlackboard();
        ProcessingRegionStyle style = (ProcessingRegionStyle) blackboard.get(ProcessingRegionStyleContent.ID);
        if (style == null) {
            style = ProcessingRegionStyleContent.createDefault();
            blackboard.put(ProcessingRegionStyleContent.ID, style);
        }

        Coordinate ul = new Coordinate(style.west, style.north);
        Coordinate ur = new Coordinate(style.east, style.north);
        Coordinate ll = new Coordinate(style.west, style.south);
        Coordinate lr = new Coordinate(style.east, style.south);

        // draw the rectangle around the active region green:143
        float[] rgba = style.backgroundColor.getColorComponents(null);
        g.setColor(new Color(rgba[0], rgba[1], rgba[2], style.bAlpha));

        Point ulPoint = context.worldToPixel(ul);
        Point urPoint = context.worldToPixel(ur);
        Point llPoint = context.worldToPixel(ll);
        Point lrPoint = context.worldToPixel(lr);

        // Point xyRes = new Point((urPoint.x - ulPoint.x) / style.cols, (llPoint.y - ulPoint.y) /
        // style.rows);

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

        rgba = style.foregroundColor.getColorComponents(null);
        g.setColor(new Color(rgba[0], rgba[1], rgba[2], style.fAlpha));
        g.setStroke(ViewportGraphics.LINE_SOLID, 2);
        g.draw(path);

        context.getLayer().setStatus(ILayer.DONE);
        context.getLayer().setStatusMessage("Layer rendered");

    }

}
