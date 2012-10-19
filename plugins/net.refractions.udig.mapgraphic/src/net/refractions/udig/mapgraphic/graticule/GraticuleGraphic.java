/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
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
package net.refractions.udig.mapgraphic.graticule;


import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import net.refractions.udig.mapgraphic.MapGraphic;
import net.refractions.udig.mapgraphic.MapGraphicContext;
import net.refractions.udig.mapgraphic.MapGraphicPlugin;
import net.refractions.udig.mapgraphic.internal.Messages;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.Grids;
import org.geotools.resources.CRSUtilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.DirectPosition;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

/**
 * <b>Graticule Graphic class</b>
 * <p>
 * Draws a geographic coordinate grid on given {@link ViewportGraphics} (also known as a 'graticule')
 * <p>
 * <b>NOTE</b>: Only CRS with unit {@link SI#METER} is supported.
 * </p>
 */
public class GraticuleGraphic implements MapGraphic {

    private static final String DELIM = "-"; //$NON-NLS-1$

    private static final String EMPTY = ""; //$NON-NLS-1$

    private static final String GEOM = "element"; //$NON-NLS-1$

    private static final String FORMAT = "0000000"; //$NON-NLS-1$

    SimpleFeatureSource grid;

    NumberFormat cf = new DecimalFormat(FORMAT);

    private SimpleFeatureIterator squares(ReferencedEnvelope bounds, double size)
            throws IOException {

        // Limit squares to bounding box
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        Filter filter = ff.bbox(ff.property(GEOM), bounds);

        // Align bound to square size and expand by 150%
        bounds = align(bounds, size, 1.5);

        // Create grid for given bounds
        SimpleFeatureSource grid = Grids.createSquareGrid(bounds, size);

//        System.out.println("Square Count: " + grid.getCount(new Query("", filter)));

        // Finished
        return grid.getFeatures(filter).features();

    }

    /**
     * Align given bounds with square size and expand by given fraction
     * 
     * 
     * @param bounds
     * @param size
     * @return
     */
    private ReferencedEnvelope align(ReferencedEnvelope bounds, double size, double expand) {
        bounds = new ReferencedEnvelope(bounds);
        bounds.expandBy(expand * size);
        double x = offset(bounds.getLowerCorner(), 0, size);
        double y = offset(bounds.getLowerCorner(), 1, size);
        bounds.translate(-x, -y);
        return bounds;
    }

    /**
     * Get ordinate offset from given size.
     * 
     * @param point
     * @param ordinate
     * @param size
     * @return
     */
    private double offset(DirectPosition point, int ordinate, double size) {
        return point.getOrdinate(ordinate) % size;
    }

    /**
     * Calculate appropriate square size.
     * 
     * @param context
     * @param min
     * @return
     */
    private double size(MapGraphicContext context, int min) {
        double scale = context.getMap().getViewportModel().getScaleDenominator();
        if(scale<100000) return 1000.0;         //   1 km square
        if(scale<1000000) return 10000.0;       //  10 km square
        return 100000.0;                        // 100 km square
    }

    public void draw(MapGraphicContext context) {

//        long tic = System.currentTimeMillis();
        
        // Sanity checks        
//        Unit<?> unit = CRSUtilities.getUnit(context.getCRS().getCoordinateSystem());
        Unit<?> unit = CRSUtilities.getUnit(context.getLayer().getCRS().getCoordinateSystem());
        if(!SI.METER.equals(unit)) {
            context.getLayer().setStatus(ILayer.ERROR);
            context.getLayer().setStatusMessage(Messages.GraticuleGraphic_Illegal_CRS);
            return;
        }
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) return;

        // Start working on layer
        context.getLayer().setStatus(ILayer.WORKING);
        context.getLayer().setStatusMessage(null);
        
        // Get display to work on
        final Display display = workbench.getDisplay();

        // Set styles
        GraticuleStyle style = GraticuleStyle.getStyle(context.getLayer());
        Font plain = GraticuleStyle.getFontStyle(context).getFont();
        Font bold = plain.deriveFont(Font.BOLD);

        // Initialize the graphics handle
        ViewportGraphics g = context.getGraphics();
        
        // Set font size
        g.setFont(bold);

        // Get bounds of viewport
        ReferencedEnvelope bounds = context.getViewportModel().getBounds();

        try {

            // Get square size limited to minimum size of 100 pixels
            double size = size(context, 100);

            // Convert square size to pixels
            int sx = (int) (size / context.getViewportModel().getPixelSize().x);
            int sy = (int) (size / context.getViewportModel().getPixelSize().y);

            SimpleFeatureIterator it = squares(bounds, size);

//            int count = 0;

            // Draw one squares at the time (only top and left lines are drawn)
            while (it.hasNext()) {

                SimpleFeature feature = it.next();

                // if(count++ < 27 || count > 29) continue;

                // Initialize
                int i = 0;
                Point current = null;
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                List<Line> lines = new ArrayList<Line>(2);
                List<Label> labels = new ArrayList<Label>(2);

                // Create lines and labels for this square
                for (Coordinate c : geom.getCoordinates()) {

                    // Get 0xx00 coordinate
                    String tx = (Math.signum(c.x) == -1 ? DELIM : EMPTY) + cf.format(Math.abs(c.x)).substring(2, 4);

                    // Get 0yy00 coordinate
                    String ty = (Math.signum(c.x) == -1 ? DELIM : EMPTY) + cf.format(Math.abs(c.y)).substring(2, 4);

                    // Insert gap with label?
                    boolean vgap = (Integer.valueOf(tx) % 10 == 0);
                    boolean hgap = (Integer.valueOf(ty) % 10 == 0);

                    // Build paths
                    switch (i) {
                    case 1:

                        // -----------------------
                        // Vertical line
                        // -----------------------

                        // Create line path
                        current = vert(display, g, sy, style.getLineWidth(), current, context.worldToPixel(c), hgap, vgap, lines);

                        // Add xx label?
                        if (hgap) labels.add(new Label(new Point(current.x, current.y + sy / 2), tx, vgap ? bold : plain));

                        break;
                    case 2:

                        // -----------------------
                        // Horizontal line
                        // -----------------------

                        // Create line path
                        current = horz(display, g, sx, style.getLineWidth(), current, context.worldToPixel(c), vgap, hgap, lines);

                        // Add yy label?
                        if (vgap) labels.add(new Label(new Point(current.x - sx / 2, current.y), ty, hgap ? bold : plain));

                        break;

                    default:
                        current = context.worldToPixel(c);
                        break;

                    }
                    i++;
                }
                
//                if(i>5) System.out.println("Coordinate Count: " + i); //$NON-NLS-1$

                // Draw lines
                for (Line line : lines)
                    line.draw(g, style);
                
                // Draw labels?
                if(style.isShowLabels()) {
                    for (Label label : labels)
                        label.draw(g, style);
                }

            }

//            // Get lower left corner coordinates
//            int x = llc.x;
//            int y = llc.y;
//
//            // Print borders
//            g.setColor(lc);
//            g.setStroke(ViewportGraphics.LINE_SOLID, 5);
//
//            // Inner rectangle
//            g.drawRect(x + d + l, y + d + l, w - 2 * (d + l), h - 2 * (d + l));
//
//            // Make white border
//            g.setColor(Color.WHITE);
//
//            // Left
//            g.drawRect(x, y, d, h);
//            g.fillRect(x, y, d, h);
//
//            // Bottom
//            g.drawRect(x, y, w, d);
//            g.fillRect(x, y, w, d);
//
//            // Right
//            g.drawRect(x + w - d, y, d, h);
//            g.fillRect(x + w - d, y, d, h);
//
//            // Top
//            g.drawRect(x, y + h - d, w, d);
//            g.fillRect(x, y + h - d, w, d);

        } catch (IOException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        }

        // Finished working on layer
        context.getLayer().setStatus(ILayer.DONE);
        
//        System.out.println("Display (w,h): " + context.getMapDisplay().getWidth() + "," + context.getMapDisplay().getHeight());
//        System.out.println("Time: " + (System.currentTimeMillis() - tic) + "ms");
    }

    /**
     * Create vertical line
     *  
     * @param display - active {@link Display}
     * @param g - active {@link ViewportGraphics}
     * @param sy - Square height
     * @param lw - line width
     * @param current - Current square coordinate (corner)
     * @param next - Next square coordinate (corner)
     * @param gap - Gap where label is inserted (pixels)
     * @param bold - flag controlling line width
     * @param lines - already created square lines
     * @return 'next' coordinate
     */
    private Point vert(Display display, ViewportGraphics g, 
            int sy, int lw, Point current, Point next,
            boolean gap, boolean bold, List<Line> lines) {

        // Initialize
        List<Path> paths = new ArrayList<Path>(2);

        // Create first segment
        Path path = new Path(display);

        // Move to last point
        path.moveTo(current.x, current.y - (bold ? 2*lw : lw));

        // Insert gap?
        if (gap) {
            
            // Calculate gap/2
            int gy = Math.max(1,g.getFontHeight());
            
            // Make gap in line
            path.lineTo(next.x, next.y + sy / 2 + gy);
            paths.add(path);

            // Create second segment
            path = new Path(display);

            // Move to last point
            path.moveTo(next.x, next.y + sy / 2 - gy);
        }

        // Close path
        path.lineTo(next.x, next.y + (bold ? 0 : lw));
        paths.add(path);
        lines.add(new Line(paths, bold ? 2*lw : lw));

        // Finished
        return next;
    }

    /**
     * Create horizontal line
     *  
     * @param display - active {@link Display}
     * @param g - active {@link ViewportGraphics}
     * @param sx - Square width
     * @param lw - line width
     * @param current - Current square coordinate (corner)
     * @param next - Next square coordinate (corner)
     * @param gap - Gap where label is inserted (pixels)
     * @param bold - flag controlling line width
     * @param lines - already created square lines
     * @return 'next' coordinate
     */
    private Point horz(Display display, ViewportGraphics g, 
            int sx, int lw, Point current, Point next,
            boolean gap, boolean bold, List<Line> lines) {

        // Initialize
        List<Path> paths = new ArrayList<Path>(2);

        // Create first segment
        Path path = new Path(display);

        // Move to last point
        path.moveTo(current.x, current.y);

        // Insert gap?
        if (gap) {
            
            // Calculate gap/2
            int gx = Math.max(1,g.getFontHeight());

            // Make gap in line
            path.lineTo(next.x - sx / 2 - gx, next.y);
            paths.add(path);

            // Create second segment
            path = new Path(display);

            // Move to last point
            path.moveTo(next.x - sx / 2 + gx, next.y);
        }

        // Close path
        path.lineTo(next.x - (bold ? 2*lw : lw), next.y);
        paths.add(path);
        lines.add(new Line(paths, bold ? 2*lw : lw));

        // Finished
        return next;
    }

    /**
     * Line definition class  
     */
    private static class Line {

        int w;

        List<Path> paths;

        public Line(List<Path> paths, int w) {
            this.w = w;
            this.paths = paths;
        }

        public void draw(ViewportGraphics g, GraticuleStyle style) {
            g.setColor(style.getLineColor());
            g.setStroke(style.getLineStyle(), w);
            for (Path path : paths) g.drawPath(path);
        }
    }

    /**
     * Label definition class  
     */
    private static class Label {

        Font font;

        String text;

        Point anchor;

        public Label(Point anchor, String text, Font font) {
            this.font = font;
            this.text = text;
            this.anchor = anchor;
        }

        public void draw(ViewportGraphics g, GraticuleStyle style) {
            Color old = g.getColor();
            g.setColor(style.getFontColor());
            g.setFont(font);
            g.drawString(text, anchor.x, anchor.y, ViewportGraphics.ALIGN_MIDDLE, ViewportGraphics.ALIGN_MIDDLE);
            // Restore old state
            g.setColor(old);
        }
    }

}
