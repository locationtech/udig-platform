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
package eu.udig.omsbox.processingregion;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.Collections;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.IStyleBlackboard;
import net.refractions.udig.ui.graphics.AWTGraphics;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import com.vividsolutions.jts.geom.Coordinate;

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

    public static final String ID = "eu.udig.omsbox.processingregion.ProcessingRegionMapGraphic";

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
