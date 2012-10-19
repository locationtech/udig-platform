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
import net.refractions.udig.project.internal.impl.LayerImpl;
import net.refractions.udig.ui.graphics.ViewportGraphics;

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
import org.geotools.resources.CRSUtilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

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

    @Override
    public void draw(MapGraphicContext context) {

//        long tic = System.currentTimeMillis();
        
        // Initialize
        ILayer graticule = context.getLayer();
        GraticuleStyle style = GraticuleStyle.getStyle(graticule);
        
        // Initialize CRS?        
        if(style.isInitCRS() && (graticule instanceof LayerImpl)) {
            // Only initialize once
            style.setInitCRS(false);
            // Apply change to workspace
            graticule.getStyleBlackboard().put(GraticuleStyle.ID, style);
            // Initialize CRS
            ((LayerImpl)graticule).setCRS(context.getCRS()); 
        }
        
        // Sanity checks        
        Unit<?> unit = CRSUtilities.getUnit(graticule.getCRS().getCoordinateSystem());
        if(!SI.METER.equals(unit)) {
            graticule.setStatus(ILayer.ERROR);
            graticule.setStatusMessage(Messages.GraticuleGraphic_Illegal_CRS);
            return;
        }
        final IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) return;

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

        // Get bounds of viewport
        ReferencedEnvelope bounds = context.getViewportModel().getBounds();
        
        try {

            // Get square size limited to minimum size of 100 pixels
            double size = size(context, 100);

            // Convert square size to pixels
            int sx = (int) (size / context.getViewportModel().getPixelSize().x);
            int sy = (int) (size / context.getViewportModel().getPixelSize().y);
            
            // Make transform from Graticule to map CRS
            MathTransform transform = CRS.findMathTransform(
                    graticule.getCRS(), 
                    context.getCRS(), 
                    false);
            
            // Transform bounds into Graticule CRS
            bounds = bounds.transform(graticule.getCRS(), true);
            
            // Get squares inside bounds
            SimpleFeatureIterator it = squares(bounds, size);

//            int count = 0;

            // Draw one squares at the time (only top and left lines are drawn)
            while (it.hasNext()) {

                SimpleFeature feature = it.next();

                // if(count++ < 27 || count > 29) continue;

                // Initialize states
                int i = 0;
                Point current = null;
                
                // Initialize lines
                List<Line> lines = new ArrayList<Line>(2);
                List<Label> labels = new ArrayList<Label>(2);

                // Get geometry
                Geometry geom = (Geometry) feature.getDefaultGeometry();
                
                // Get coordinates in graticule CRS
                Coordinate[] coords = geom.getCoordinates();
                
                // Get upper left corner
                Coordinate ulc = coords[0];
                
                // Get 0xx00 coordinate
                String tx = (Math.signum(ulc.x) == -1 ? DELIM : EMPTY) + cf.format(Math.abs(ulc.x)).substring(2, 4);

                // Get upper left corner
                Coordinate llc = coords[2];
                
                // Get 0yy00 coordinate
                String ty = (Math.signum(llc.y) == -1 ? DELIM : EMPTY) + cf.format(Math.abs(llc.y)).substring(2, 4);

                // Insert gap with label?
                boolean vgap = (Integer.valueOf(tx) % 10 == 0);
                boolean hgap = (Integer.valueOf(ty) % 10 == 0);
                
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
                        current = vert(display, g, sy, style.getLineWidth(), current, context.worldToPixel(c), hgap, vgap, lines);

                        // Add xx label?
                        if (hgap) { 
                            labels.add(new Label(current, tx, vgap ? bold : plain));
                            current = context.worldToPixel(c);
                        }

                        break;
                    case 2:

                        // -----------------------
                        // Horizontal line
                        // -----------------------

                        // Create line path
                        current = horz(display, g, sx, style.getLineWidth(), current, context.worldToPixel(c), vgap, hgap, lines);

                        // Add yy label?
                        if (vgap) { 
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
        } catch (FactoryException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        } catch (MismatchedDimensionException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        } catch (TransformException ex) {
            MapGraphicPlugin.log(Messages.GraticuleGraphic_Error, ex);
        }

        // Finished working on layer
        graticule.setStatus(ILayer.DONE);
        
//        System.out.println("Display (w,h): " + context.getMapDisplay().getWidth() + "," + context.getMapDisplay().getHeight());
//        System.out.println("Time: " + (System.currentTimeMillis() - tic) + "ms");
    }

    /**
     * Offset point from mid-point between p1 and p2 given length
     * 
     * @see {@linkplain http://www.teacherschoice.com.au/Maths_Library/Analytical%20Geometry/AnalGeom_3.htm}
     * 
     * @param p1
     * @param p2
     * @param length
     * @param internal
     * @return Point
     */
    private Point offset(Point p1, Point p2, int offset) {
        
        // Get segment length
        int segment = (int)p1.distance(p2);
        
        // Calculate length along line from p1
        int length = segment/2 + offset;
        
        // Calculate ratio        
        int k1 = length;
        int k2 = segment - length;
        
        // Calculate division of line segment
        int x = (k1*p2.x + k2*p1.x)/(k1 + k2); 
        int y = (k1*p2.y + k2*p1.y)/(k1 + k2); 
        
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
            int offset = Math.max(1,g.getFontHeight());
            
            // End first segment before mid-point
            Point p = offset(current,next,-offset);
            path.lineTo(p.x, p.y);
            paths.add(path);
            
            // Create second segment
            path = new Path(display);

            // Move past mid-point
            p = offset(current,next,offset);
            path.moveTo(p.x, p.y);
            
        }

        // Close path
        path.lineTo(next.x, next.y + (bold ? 0 : lw));
        paths.add(path);
        lines.add(new Line(paths, bold ? 2*lw : lw));

        // Finished
        return gap ? offset(current,next,0) : next;
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
            int offset = Math.max(1,g.getFontHeight());
            
            // End first segment before mid-point
            Point p = offset(current,next,-offset);
            path.lineTo(p.x, p.y);
            paths.add(path);
            
            // Create second segment
            path = new Path(display);

            // Move past mid-point
            p = offset(current,next,offset);
            path.moveTo(p.x, p.y);
            
        }

        // Close path
        path.lineTo(next.x - (bold ? 2*lw : lw), next.y);
        paths.add(path);
        lines.add(new Line(paths, bold ? 2*lw : lw));

        // Finished
        return gap ? offset(current,next,0) : next;
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
