/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.graticule;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import si.uom.SI;
import javax.measure.Unit;

import org.locationtech.udig.mapgraphic.MapGraphic;
import org.locationtech.udig.mapgraphic.MapGraphicContext;
import org.locationtech.udig.mapgraphic.MapGraphicPlugin;
import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.mapgraphic.style.FontStyle;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.grid.Grids;
import org.geotools.referencing.CRS;
import org.geotools.referencing.util.CRSUtilities;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

/**
 * <b>Graticule Graphic class</b>
 * <p>
 * Draws a geographic coordinate grid on given {@link ViewportGraphics} (also known as a
 * 'graticule')
 * <p>
 * <b>NOTE</b>: Only CRS with unit {@link SI#METER} is supported.
 * </p>
 */
public class GraticuleGraphic implements MapGraphic {

    private static final String DELIM = "-"; //$NON-NLS-1$

    private static final String EMPTY = ""; //$NON-NLS-1$

    private static final String GEOM = "element"; //$NON-NLS-1$

    private static final String FORMAT = "0000000"; //$NON-NLS-1$

    private static final int MAX_SCALE = 100000000;

    SimpleFeatureSource grid;

    NumberFormat cf = new DecimalFormat(FORMAT);

    @Override
    public void draw(MapGraphicContext context) {

        // Initialize
        ILayer graticule = context.getLayer();
        GraticuleStyle style = GraticuleStyle.getStyle(graticule);
        FontStyle fontStyle = GraticuleStyle.getFontStyle(context);
        
        // Ensure CRS?
        if (graticule instanceof Layer) {
            // Initialize CRS?
            if (style.isInitCRS()) {
                // Only initialize once
                style.setInitCRS(false);
                // Apply CRS from context
                GraticuleCRSConfigurator.apply((Layer) graticule, context.getCRS());
            } else if (mismatch(graticule, style)) {
                // Apply CRS from
                GraticuleCRSConfigurator.apply((Layer) graticule, style.getCRS());
            }
        }

        // Sanity checks
        if (MAX_SCALE < context.getMap().getViewportModel().getScaleDenominator()) {
            graticule.setStatus(ILayer.WARNING);
            graticule.setStatusMessage(Messages.GraticuleGraphic_Maximum_Scale + MAX_SCALE);
            return;
        }
        Unit<?> unit = CRSUtilities.getUnit(graticule.getCRS().getCoordinateSystem());
        if (!(unit==null || SI.METRE.equals(unit))) {
//        if (!(unit==null || SI.METER.equals(unit) || SI.RADIAN.equals(unit.getStandardUnit()))) { // $NON-NLS-1$
            graticule.setStatus(ILayer.ERROR);
            graticule.setStatusMessage(Messages.GraticuleGraphic_Illegal_CRS);
            return;
        }
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null)
            return;

        // Start working on layer
        graticule.setStatus(ILayer.WORKING);
        graticule.setStatusMessage(null);

        // Get display to work on
        final Display display = workbench.getDisplay();

        // Set styles
        Font plain = GraticuleStyle.getFontStyle(context).getFont();
        Font bold = plain.deriveFont(Font.BOLD);

        // Initialize the graphics handle
        ViewportGraphics g = context.getGraphics();

        // Set font size
        g.setFont(bold);
        
        // Show labels?
        boolean isShowLabels = style.isShowLabels();

        // Get bounds of viewport
        ReferencedEnvelope bounds = context.getViewportModel().getBounds();

        try {

            // Get square size limited to minimum size of 100 pixels
            double size = size(context, unit, 10);

            // Convert square size to pixels
            int sx = (int) (size / context.getViewportModel().getPixelSize().x);
            int sy = (int) (size / context.getViewportModel().getPixelSize().y);

//          // Sanity check
            if (sx < 10 || sy < 10) {
                graticule.setStatus(ILayer.ERROR);
                graticule.setStatusMessage(Messages.GraticuleGraphic_Error_Too_Many_Squares);
                return;
            }

            // Make transform from Graticule to map CRS
            MathTransform transform = CRS.findMathTransform(graticule.getCRS(), context.getCRS(),
                    false);

            // Transform bounds into Graticule CRS
            bounds = bounds.transform(graticule.getCRS(), true);

            // Get squares inside bounds
            SimpleFeatureIterator it = squares(bounds, size);

//            // Initialize border tick positions
//            List<Geometry> ticks = new ArrayList<Geometry>();
//
//            // Get map borders as geometry in graticule CRS
//            Geometry outer = JTS.toGeometry(bounds);
//            Geometry inner = JTS.toGeometry(expand(bounds,-500));

            // Draw one squares at the time (only top and left lines are drawn)
            while (it.hasNext()) {

                // Initialize states
                int i = 0;
                Point current = null;

                // Initialize lines
                List<Line> lines = new ArrayList<Line>(2);
                List<Label> labels = new ArrayList<Label>(2);

                // Get next geometry
                Geometry geom = (Geometry) it.next().getDefaultGeometry();

                // Get coordinates in graticule CRS
                Coordinate[] coords = geom.getCoordinates();

                // Get x-coordinate label from upper left corner
                String tx = getLabel(coords[0].x, size, unit, style);

                // Get y-coordinate label from lower left corner
                String ty = getLabel(coords[2].y, size, unit, style);

                // Insert gap with label?
                boolean vgap = isGap(tx, unit, style);
                boolean hgap = isGap(ty, unit, style);

                // Transform coordinates into Map CRS
                coords = JTS.transform(geom, transform).getCoordinates();

                // Create lines and labels for this square
                for (Coordinate c : coords) {

                    // Build paths
                    switch (i) {
                    case 1:

                        // -----------------------
                        // Vertical line
                        // -----------------------

                        // Create line path
                        current = vert(display, g, sy, style.getLineWidth(), current,
                                context.worldToPixel(c), isShowLabels & hgap, vgap, lines);

                        // Add xx label?
                        if (isShowLabels & hgap) {
                            labels.add(new Label(current, tx, vgap ? bold : plain));
                            current = context.worldToPixel(c);
                        }

                        break;
                    case 2:

                        // -----------------------
                        // Horizontal line
                        // -----------------------

                        // Create line path
                        current = horz(display, g, sx, style.getLineWidth(), current,
                                context.worldToPixel(c), isShowLabels & vgap, hgap, lines);

                        // Add yy label?
                        if (isShowLabels & vgap) {
                            labels.add(new Label(current, ty, hgap ? bold : plain));
                            current = context.worldToPixel(c);
                        }

                        break;

                    default:
                        current = context.worldToPixel(c);
                        break;

                    }
                    i++;
                }

//                // Is square on border?
//                if (geom.crosses(outer) || geom.overlaps(outer)) {
//                    // Get intersection as multi-point geometry
//                    ticks.add(geom.intersection(outer));
//                }

                // Draw lines
                for (Line line : lines)
                    line.draw(g, style);

                // Draw labels?
                if (style.isShowLabels())
                    for (Label label : labels)
                        label.draw(g, fontStyle);

            }

//            // Draw borders?
//            if (false && !ticks.isEmpty()) {
//
////                System.out.println("Ticks: " + ticks.size() + ", " + ticks);
////                outer = JTS.transform(outer, context.worldToScreenMathTransform());
//                
//                // Print borders
//                g.setColor(Color.RED);
//                g.setStroke(ViewportGraphics.LINE_SOLID, 2);
//                for (Geometry tick : ticks) {
//                    int i = 0;
//                    Path path = new Path(display);
////                    tick = tick.intersection(inner);
//                    if(tick.isEmpty()) continue;
//                    // Transform coordinates to map CRS
//                    tick = JTS.transform(tick, transform);
//                    
//                    int b = 10;
//                    int w = context.getMapDisplay().getWidth();
//                    int h = context.getMapDisplay().getHeight();
//                    
////                    tick = tick.getEnvelope();
//                    for(Coordinate c : tick.getCoordinates()) {
//                        Point p = context.worldToPixel(c);
////                        if(p.x<b) p.x=b;
////                        if(p.y<b) p.y=b;
////                        if(p.x>w-b) p.x=w-b;
////                        if(p.y>h-b) p.y=h-b;
//                        if(i==0) path.moveTo(p.x, p.y);
//                        else path.lineTo(p.x, p.y);
//                        i++;
//                    }
//                    // Calculate intersection with inner border
//                    
////                    Point p = context.worldToPixel(null);
////                    g.drawOval(p.x, p.y, 10, 10);                        
//                    g.drawPath(path);
//                }
//            }

            // // Get lower left corner coordinates
            // int x = llc.x;
            // int y = llc.y;
            //
            // // Print borders
            // g.setColor(lc);
            // g.setStroke(ViewportGraphics.LINE_SOLID, 5);
            //
            // // Inner rectangle
            // g.drawRect(x + d + l, y + d + l, w - 2 * (d + l), h - 2 * (d + l));
            //
            // // Make white border
            // g.setColor(Color.WHITE);
            //
            // // Left
            // g.drawRect(x, y, d, h);
            // g.fillRect(x, y, d, h);
            //
            // // Bottom
            // g.drawRect(x, y, w, d);
            // g.fillRect(x, y, w, d);
            //
            // // Right
            // g.drawRect(x + w - d, y, d, h);
            // g.fillRect(x + w - d, y, d, h);
            //
            // // Top
            // g.drawRect(x, y + h - d, w, d);
            // g.fillRect(x, y + h - d, w, d);

        } catch (IOException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        } catch (FactoryException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        } catch (MismatchedDimensionException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        } catch (TransformException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        }

        // Finished working on layer
        graticule.setStatus(ILayer.DONE);

    }

    private boolean mismatch(ILayer graticule, GraticuleStyle style) {
        String code;
        try {
            code = "EPSG:" + CRS.lookupEpsgCode(graticule.getCRS(), false); //$NON-NLS-1$
            return !code.equals(style.getCRS());
        } catch (FactoryException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        }
        return true;
    }

    /**
     * Generate squares inside given bounds
     * 
     * @param bounds
     * @param size
     * @return
     * @throws IOException
     */
    private SimpleFeatureIterator squares(ReferencedEnvelope bounds, double size)
            throws IOException {

        // Limit squares to bounding box
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
        Filter filter = ff.bbox(ff.property(GEOM), bounds);

        // Align bound to square size and expand by 150%
        bounds = align(bounds, size, 1.6);

        // Create grid for given bounds
        SimpleFeatureSource grid = Grids.createSquareGrid(bounds, size);

        // Finished
        return grid.getFeatures(filter).features();
    }

    /**
     * Expand bounds by given fraction
     * 
     * 
     * @param bounds
     * @param distance
     * @return ReferencedEnvelope
     */
    private ReferencedEnvelope expand(ReferencedEnvelope bounds, double distance) {
        bounds = new ReferencedEnvelope(bounds);
        bounds.expandBy(distance);
        return bounds;
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
        bounds = expand(bounds, expand * size);
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
     * Calculate appropriate square size in CRS units.
     * 
     * @param context - {@link MapGraphicContext} instance
     * @param unit - CRS {@link Unit} 
     * @param min - minimum pixel size
     * @return double
     */
    private double size(MapGraphicContext context, Unit<?> unit, int min) {
        // Get scale
        double scale = context.getMap().getViewportModel().getScaleDenominator();
        // 1 km square?
        if (scale < 100000)
            return 1000.0;
        // 10 km square?
        if (scale < 1000000)
            return 10000.0;
        // 100 km square?
        if (scale < 10000000)
            return 100000.0;
        // 1000 km square?
        if (scale < 100000000)
            return 1000000.0;
        // 10000 km square
        return 100000000.0;
//        // Get flags
//        boolean meter = SI.METER.equals(unit);
//        // Get scale
//        double scale = context.getMap().getViewportModel().getScaleDenominator();
//        // 1 km square?
//        if (scale < 100000)
//            return meter ? 1000.0 : unit.getConverterTo(SI.METER).convert(0.1);
//        // 10 km square?
//        if (scale < 1000000)
//            return meter ? 10000.0 : unit.getConverterTo(SI.METER).convert(1.0);
//        // 100 km square?
//        if (scale < 10000000)
//            return meter ? 100000.0 : unit.getConverterTo(SI.METER).convert(5.0);
//        // 1000 km square?
//        if (scale < 100000000)
//            return meter ? 1000000.0 : unit.getConverterTo(SI.METER).convert(10.0);
//        // 10000 km square
//        return meter ? 100000000.0 : unit.getConverterTo(SI.METER).convert(50.0);
    }

    /**
     * Check if given coordinate label is shown
     * 
     * @param coordinate - coordinate label
     * @param unit - coordinate unit
     * @param style - graticule style
     * @return boolean
     */
    private boolean isGap(String coordinate, Unit<?> unit, GraticuleStyle style) {
        return (Integer.valueOf(coordinate) % 10 == 0);
    }

    /**
     * Get grid-line label
     * 
     * @param coordinate - line coordinate
     * @param size - square size in coordinate units
     * @param unit - coordinate unit
     * @param style - graticule style
     * @return String
     */
    private String getLabel(double coordinate, double size, Unit<?> unit, GraticuleStyle style) {

        // Set number of digits
        int digits = 2;

        // Calculate offset
        int offset = Math.max(1, 3 - String.valueOf(Math.round(size / 1000)).length());

        // Finished
        return (Math.signum(coordinate) == -1 ? DELIM : EMPTY)
                + cf.format(Math.abs(coordinate)).substring(offset, offset + digits);
    }

    /**
     * Offset point from mid-point between p1 and p2 given length
     * 
     * @see {@linkplain http
     *      ://www.teacherschoice.com.au/Maths_Library/Analytical%20Geometry/AnalGeom_3.htm}
     * 
     * @param p1
     * @param p2
     * @param length
     * @param internal
     * @return Point
     */
    private Point offset(Point p1, Point p2, int offset) {

        // Get segment length
        int segment = (int) p1.distance(p2);

        // Calculate length along line from p1
        int length = segment / 2 + offset;

        // Calculate ratio
        int k1 = length;
        int k2 = segment - length;

        // Calculate division of line segment
        int x = (k1 * p2.x + k2 * p1.x) / (k1 + k2);
        int y = (k1 * p2.y + k2 * p1.y) / (k1 + k2);

        // Finished
        return new Point(x, y);
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
    private Point vert(Display display, ViewportGraphics g, int sy, int lw, Point current,
            Point next, boolean gap, boolean bold, List<Line> lines) {

        // Initialize
        List<Path> paths = new ArrayList<Path>(2);

        // Create first segment
        Path path = new Path(display);

        // Move to last point
        path.moveTo(current.x, current.y - (bold ? 2 * lw : lw));

        // Insert gap?
        if (gap) {

            // Calculate gap/2
            int offset = Math.max(1, g.getFontHeight());

            // End first segment before mid-point
            Point p = offset(current, next, -offset);
            path.lineTo(p.x, p.y);
            paths.add(path);

            // Create second segment
            path = new Path(display);

            // Move past mid-point
            p = offset(current, next, offset);
            path.moveTo(p.x, p.y);

        }

        // Close path
        path.lineTo(next.x, next.y + (bold ? 0 : lw));
        paths.add(path);
        lines.add(new Line(paths, bold ? 2 * lw : lw));

        // Finished
        return gap ? offset(current, next, 0) : next;
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
    private Point horz(Display display, ViewportGraphics g, int sx, int lw, Point current,
            Point next, boolean gap, boolean bold, List<Line> lines) {

        // Initialize
        List<Path> paths = new ArrayList<Path>(2);

        // Create first segment
        Path path = new Path(display);

        // Move to last point
        path.moveTo(current.x, current.y);

        // Insert gap?
        if (gap) {

            // Calculate gap/2
            int offset = Math.max(1, g.getFontHeight());

            // End first segment before mid-point
            Point p = offset(current, next, -offset);
            path.lineTo(p.x, p.y);
            paths.add(path);

            // Create second segment
            path = new Path(display);

            // Move past mid-point
            p = offset(current, next, offset);
            path.moveTo(p.x, p.y);

        }

        // Close path
        path.lineTo(next.x - (bold ? 2 * lw : lw), next.y);
        paths.add(path);
        lines.add(new Line(paths, bold ? 2 * lw : lw));

        // Finished
        return gap ? offset(current, next, 0) : next;
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
            for (Path path : paths)
                g.drawPath(path);
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

        public void draw(ViewportGraphics g, FontStyle style) {
            Color old = g.getColor();
            g.setColor(style.getColor());
            g.setFont(font);
            g.drawString(text, anchor.x, anchor.y, ViewportGraphics.ALIGN_MIDDLE,
                    ViewportGraphics.ALIGN_MIDDLE);
            // Restore old state
            g.setColor(old);
        }
    }

}
