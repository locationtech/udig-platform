/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2011, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package eu.udig.omsbox.processingregion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.Collections;

import org.geotools.geometry.jts.ReferencedEnvelope;

import net.refractions.udig.boundary.IBoundaryService;
import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.ui.PlatformGIS;
import net.refractions.udig.ui.graphics.AWTGraphics;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * <p>
 * Class representing the rendered Boundary
 * </p>
 * 
 * @author paul.pfeiffer
 * @version 1.3.0
 */
public class BoundaryLayerDecorator implements MapGraphic {

    public static final String ID = "eu.udig.omsbox.processingregion.boundaryLayerDecorator";

    public BoundaryLayerDecorator() {
    }

    public void draw( MapGraphicContext context ) {
        context.getLayer().setStatus(ILayer.WORKING);

        // initialize the graphics handle
        ViewportGraphics graphic = context.getGraphics();
        if (graphic instanceof AWTGraphics) {
            AWTGraphics awtG = (AWTGraphics) graphic;
            Graphics2D g2D = awtG.g;
            // setting rendering hints
            @SuppressWarnings("unchecked")
            RenderingHints hints = new RenderingHints(Collections.EMPTY_MAP);
            hints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
            g2D.addRenderingHints(hints);
        }

        Dimension screen = context.getMapDisplay().getDisplaySize();

        // get the boundary
        IBoundaryService boundaryService = PlatformGIS.getBoundaryService();
        Geometry multiGeometry = boundaryService.getGeometry();
        if (multiGeometry != null) {
        
            // draw the rectangle around the active region green:143
            //float[] rgba = style.backgroundColor.getColorComponents(null);
            //g.setColor(new Color(rgba[0], rgba[1], rgba[2], style.bAlpha));
            graphic.setColor(new Color((float)0.5, (float)0.5, (float)0.5, (float)0.5));
    
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
            
            // if multi geometry - loop through each geometry
            for (int g=multiGeometry.getNumGeometries(); g>0; g--) {
                Geometry geometry = multiGeometry.getGeometryN(g);
                
                // draw the boundary
                Coordinate[] coordinates = geometry.getCoordinates();
                
                //geometry.
                
                // move to the first point
                Point point = null;
                if (coordinates.length > 0) {
                    point = context.worldToPixel(coordinates[0]);
                    path.moveTo(point.x, point.y);
                }
                // draw all points
                for (int c=0; c<coordinates.length; c++) {
                    point = context.worldToPixel(coordinates[c]);
                    path.lineTo(point.x, point.y);
                }
                
                path.closePath();
    
            }
            
            graphic.fill(path);
    
            //rgba = style.foregroundColor.getColorComponents(null);
            //g.setColor(new Color(rgba[0], rgba[1], rgba[2], style.fAlpha));
            graphic.setColor(new Color((float)0.5, (float)0.5, (float)0.5, (float)0.75));
            graphic.setStroke(ViewportGraphics.LINE_SOLID, 2);
            graphic.draw(path);
            
        }

        context.getLayer().setStatus(ILayer.DONE);
        context.getLayer().setStatusMessage("Layer rendered");

    }

}
