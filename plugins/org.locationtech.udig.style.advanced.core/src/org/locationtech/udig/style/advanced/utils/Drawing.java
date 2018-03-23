/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.utils;

import static java.lang.Math.toRadians;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.LiteShape;
import org.geotools.renderer.style.GraphicStyle2D;
import org.geotools.renderer.style.MarkStyle2D;
import org.geotools.renderer.style.SLDStyleFactory;
import org.geotools.renderer.style.Style2D;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.geotools.util.NumberRange;
import org.locationtech.udig.ui.graphics.AWTGraphics;
import org.locationtech.udig.ui.graphics.NonAdvancedSWTGraphics;
import org.locationtech.udig.ui.graphics.SLDs;
import org.locationtech.udig.ui.graphics.SWTGraphics;
import org.locationtech.udig.ui.graphics.ViewportGraphics;
import org.opengis.feature.IllegalAttributeException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.operation.MathTransform;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Drawing utility package - make your own previews and glyphs!
 * 
 * @author jones
 * @since 0.6.0
 */
public final class Drawing {
    private GeometryFactory gf = new GeometryFactory();

    private Drawing() {
        // prevent subclassing
    }

    /**
     * Retrieve the default Drawing implementation.
     * 
     * @return Drawing ready for use
     */
    public static Drawing create() {
        return new Drawing();
    }

    /**
     * Creates a ViewportGraphics object based backed by SWT. 
     * 
     * <p><b>REMEMBER to dispose of graphics.</b>
     * 
     * @param gc A SWT GC object that the ViewportGraphics object will draw on.
     * @param display The display object that will be used to create new 
     * @param displaySize
     * @return Wrapper around a normal SWT Image
     */
    public static ViewportGraphics createGraphics( GC gc, Display display, Dimension displaySize ) {
        if (Platform.getOS().equals(Platform.OS_LINUX))

            return new NonAdvancedSWTGraphics(gc, display, displaySize);

        return new SWTGraphics(gc, display);
    }
    /**
     * Creates a ViewportGraphics object based backed by SWT. 
     * 
     * @param graphics
     * @return Wrapper allowing system to draw onto j2d images
     */
    public static ViewportGraphics createGraphics( Graphics2D graphics ) {
        return new AWTGraphics(graphics);
    }

    /**
     * Used to draw a freature directly onto the provided image.
     * <p>
     * SimpleFeature coordintes are in the same coordinates as the image.
     * </p>
     * <p>
     * You may call this method multiple times to draw several features onto the same
     * Image (say for glyph creation).
     * </p>
     *  
     * @param image Image to render on to 
     * @param display Needed to create Colors for image
     * @param feature SimpleFeature to be rendered
     * @param style Style to render feature with
     */
    public void drawDirect( Image image, Display display, SimpleFeature feature, Style style ) {

        ViewportGraphics graphics = createSWTGraphics(image, display);
        drawFeature(graphics, feature, style, new AffineTransform());
        graphics.dispose();
    }

    public void drawDirect( Image image, Display display, SimpleFeature feature, Rule rule ) {
        AffineTransform worldToScreenTransform = new AffineTransform();

        ViewportGraphics graphics = createSWTGraphics(image, display);
        drawFeature(graphics, feature, worldToScreenTransform, false, getSymbolizers(rule), null);
        graphics.dispose();
    }

    public void drawDirect( BufferedImage image, SimpleFeature feature, Rule rule ) {
        AffineTransform worldToScreenTransform = new AffineTransform();
        ViewportGraphics graphics = createGraphics(image.createGraphics());
        drawFeature(graphics, feature, worldToScreenTransform, false, getSymbolizers(rule), null);
        graphics.dispose();
    }

    /**
     *
     * @param image
     * @param display
     * @return
     */
    private ViewportGraphics createSWTGraphics( Image image, Display display ) {
        if (Platform.getOS().equals(Platform.OS_LINUX))
            return new NonAdvancedSWTGraphics(image, display);
        return new SWTGraphics(image, display);
    }

    public void drawFeature( ViewportGraphics graphics, SimpleFeature feature, AffineTransform worldToScreenTransform,
            boolean drawVertices, MathTransform mt ) {
        if (feature == null)
            return;
        drawFeature(graphics, feature, worldToScreenTransform, drawVertices, getSymbolizers(feature), mt);
    }

    public void drawFeature( ViewportGraphics graphics, SimpleFeature feature, AffineTransform worldToScreenTransform ) {
        if (feature == null)
            return;
        drawFeature(graphics, feature, worldToScreenTransform, false, getSymbolizers(feature), null);
    }

    public void drawFeature( ViewportGraphics graphics, SimpleFeature feature, AffineTransform worldToScreenTransform, Style style ) {
        if (feature == null)
            return;
        drawFeature(graphics, feature, worldToScreenTransform, false, getSymbolizers(style), null);
    }

    public void drawFeature( ViewportGraphics graphics, SimpleFeature feature, Style style, AffineTransform worldToScreenTransform ) {
        if (feature == null)
            return;

        drawFeature(graphics, feature, worldToScreenTransform, false, getSymbolizers(style), null);
    }

    Symbolizer[] getSymbolizers( Style style ) {
        List<Symbolizer> symbs = new ArrayList<Symbolizer>();
        List<FeatureTypeStyle> styles = style.featureTypeStyles();
        for (FeatureTypeStyle fstyle: styles){
            for (Rule rule : fstyle.rules()){
                symbs.addAll(Arrays.asList(rule.getSymbolizers()));
            }
        }
        return symbs.toArray(new Symbolizer[symbs.size()]);
    }

    Symbolizer[] getSymbolizers( Rule rule ) {
        List<Symbolizer> symbs = new ArrayList<Symbolizer>();
        symbs.addAll(Arrays.asList(rule.getSymbolizers()));
        return symbs.toArray(new Symbolizer[symbs.size()]);
    }

    public void drawFeature( ViewportGraphics graphics, SimpleFeature feature, AffineTransform worldToScreenTransform,
            boolean drawVertices, Symbolizer[] symbs, MathTransform mt ) {

        LiteShape shape = new LiteShape(null, worldToScreenTransform, false);
        if (symbs == null)
            return;
        for( int m = 0; m < symbs.length; m++ ) {
            drawFeature(graphics, feature, worldToScreenTransform, drawVertices, symbs[m], mt, shape);
        }
    }

    public void drawFeature( ViewportGraphics graphics, SimpleFeature feature, AffineTransform worldToScreenTransform,
            boolean drawVertices, Symbolizer symbolizer, MathTransform mathTransform, LiteShape shape ) {
        if (symbolizer instanceof RasterSymbolizer) {
            // TODO
        } else {
            Geometry g = findGeometry(feature, symbolizer);
            if (g == null)
                return;
            if (mathTransform != null) {
                try {
                    g = JTS.transform(g, mathTransform);
                } catch (Exception e) {
                    // do nothing
                }
            }
            shape.setGeometry(g);

            paint(graphics, feature, shape, symbolizer);
            if (drawVertices) {
                double averageDistance = 0;
                Coordinate[] coords = g.getCoordinates();
                java.awt.Point oldP = worldToPixel(coords[0], worldToScreenTransform);
                for( int i = 1; i < coords.length; i++ ) {
                    Coordinate coord = coords[i];
                    java.awt.Point p = worldToPixel(coord, worldToScreenTransform);
                    averageDistance += p.distance(oldP) / i;
                    oldP = p;
                }
                int pixels = 1;
                if (averageDistance > 20)
                    pixels = 3;
                if (averageDistance > 60)
                    pixels = 5;
                if (pixels > 1) {
                    graphics.setColor(Color.RED);
                    for( int i = 0; i < coords.length; i++ ) {
                        Coordinate coord = coords[i];
                        java.awt.Point p = worldToPixel(coord, worldToScreenTransform);
                        graphics.fillRect(p.x - (pixels - 1) / 2, p.y - (pixels - 1) / 2, pixels, pixels);
                    }
                }
            }
        }
    }

    public java.awt.Point worldToPixel( Coordinate coord, AffineTransform worldToScreenTransform ) {
        Point2D w = new Point2D.Double(coord.x, coord.y);
        AffineTransform at = worldToScreenTransform;
        Point2D p = at.transform(w, new Point2D.Double());
        return new java.awt.Point((int) p.getX(), (int) p.getY());
    }

    /** Unsure if this is the paint for the border, or the fill? */
    private void paint( ViewportGraphics g, SimpleFeature feature, LiteShape shape, Symbolizer symb ) {
        if (symb instanceof PolygonSymbolizer) {
            PolygonSymbolizer polySymb = (PolygonSymbolizer) symb;
            double opacity = SLDs.polyFillOpacity(polySymb);
            Color fillColor = SLDs.polyFill(polySymb);
            Stroke stroke = SLDs.stroke(polySymb);
            Color strokeColor = SLDs.polyColor(polySymb);

            int width = SLDs.width(stroke);
            if (width == SLDs.NOTFOUND)
                width = 1;

            if (Double.isNaN(opacity))
                opacity = 1.0;
            if (fillColor != null) {
                fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), (int) (255 * opacity));
                g.setColor(fillColor);
                g.fill(shape);
            }
            if (stroke != null && strokeColor != null) {
                Graphics2D g2d = g.getGraphics(Graphics2D.class);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(strokeColor);

                float[] dashArray = stroke.getDashArray();
                Float dashOffset = stroke.getDashOffset().evaluate(null, Float.class);
                if (dashOffset == null) {
                    dashOffset = 0f;
                }
                String cap = stroke.getLineCap().evaluate(null, String.class);
                int capInt = Utilities.sld2awtCap(cap);
                String join = stroke.getLineJoin().evaluate(null, String.class);
                int joinInt = Utilities.sld2awtJoin(join);
                BasicStroke bStroke = new BasicStroke(width, capInt, joinInt, 1f, dashArray, dashOffset);
                g2d.setStroke(bStroke);
                g2d.draw(shape);
            }
        }
        if (symb instanceof LineSymbolizer) {
            LineSymbolizer lineSymbolizer = (LineSymbolizer) symb;
            Color c = SLDs.color(lineSymbolizer);
            int w = SLDs.width(lineSymbolizer);
            Stroke stroke = SLDs.stroke(lineSymbolizer);
            if (c != null && w > 0) {
                Graphics2D g2d = g.getGraphics(Graphics2D.class);
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(c);

                float[] dashArray = stroke.getDashArray();
                Float dashOffset = stroke.getDashOffset().evaluate(null, Float.class);
                if (dashOffset == null) {
                    dashOffset = 0f;
                }
                String cap = stroke.getLineCap().evaluate(null, String.class);
                int capInt = Utilities.sld2awtCap(cap);
                String join = stroke.getLineJoin().evaluate(null, String.class);
                int joinInt = Utilities.sld2awtJoin(join);
                BasicStroke bStroke = new BasicStroke(w, capInt, joinInt, 1f, dashArray, dashOffset);
                g2d.setStroke(bStroke);
                g2d.draw(shape);
            }
        }
        if (symb instanceof PointSymbolizer) {
            PointSymbolizer pointSymbolizer = (PointSymbolizer) symb;

            // offset
            Point2D offset = Utilities.getOffset(pointSymbolizer);
            if (offset != null) {
                java.awt.Point off = new java.awt.Point((int) offset.getX(), -1 * (int) offset.getY());
                g.translate(off);
            }

            // rotation
            Graphic graphic = SLDs.graphic(pointSymbolizer);
            Double rotation = graphic.getRotation().evaluate(null, Double.class);
            Double gSize = graphic.getSize().evaluate(null, Double.class);
            if (gSize != null && rotation != null) {
                g.setTransform(AffineTransform.getRotateInstance(toRadians(rotation), gSize / 2, gSize / 2));
            }

            Color c = SLDs.pointStrokeColorWithAlpha(pointSymbolizer);
            Color fill = SLDs.pointFillWithAlpha(pointSymbolizer);
            int width = SLDs.width(SLDs.stroke(pointSymbolizer));
            if (width < 0) {
                width = 0;
            }
            float[] point = new float[6];
            PathIterator pathIterator = shape.getPathIterator(null);
            pathIterator.currentSegment(point);

            SLDStyleFactory styleFactory = new SLDStyleFactory();
            Style2D tmp = null;
            try {
                tmp = styleFactory.createStyle(feature, pointSymbolizer, new NumberRange<Double>(Double.class, Double.NEGATIVE_INFINITY,
                        Double.POSITIVE_INFINITY));
            } catch (Exception e) {
                tmp = styleFactory.createStyle(feature, pointSymbolizer, new NumberRange<Double>(Double.class, Double.NEGATIVE_INFINITY,
                        Double.POSITIVE_INFINITY));
            }
            if (tmp instanceof MarkStyle2D) {
                MarkStyle2D style = (MarkStyle2D) tmp;
                Shape shape2 = style.getTransformedShape(point[0], point[1]);

                if (c == null && fill == null) {
                    // g.setColor(Color.GRAY);
                    // g.fill(shape2);
                }

                if (fill != null) {
                    g.setColor(fill);
                    g.fill(shape2);
                }
                // else {
                // g.setColor(Color.GRAY);
                // g.fill(shape2);
                // }
                if (c != null) {
                    g.setStroke(ViewportGraphics.LINE_SOLID, width);
                    g.setColor(c);
                    g.draw(shape2);
                }
                // else {
                // g.setStroke(ViewportGraphics.LINE_SOLID, width);
                // g.setColor(Color.DARK_GRAY);
                // g.draw(shape2);
                // }
            } else if (tmp instanceof GraphicStyle2D) {
                GraphicStyle2D style = (GraphicStyle2D) tmp;

                RenderedImage image = (RenderedImage) style.getImage();
                g.drawImage(image, (int) (point[0] - ((double) image.getWidth()) / (double) 2),
                        (int) (point[1] - ((double) image.getHeight()) / (double) 2));
            }
        }
    }
    @SuppressWarnings("unchecked")
	public static Symbolizer[] getSymbolizers( SimpleFeature feature ) {
        return getSymbolizers((Class< ? extends Geometry>) feature.getDefaultGeometry().getClass(), Color.RED);
    }

    public static Symbolizer[] getSymbolizers( Class< ? extends Geometry> type, Color baseColor ) {
        return getSymbolizers(type, baseColor, true);
    }
    public static Symbolizer[] getSymbolizers( Class< ? extends Geometry> type, Color baseColor, boolean useTransparency ) {

        StyleBuilder builder = new StyleBuilder();
        Symbolizer[] syms = new Symbolizer[1];
        if (LineString.class.isAssignableFrom(type) || MultiLineString.class.isAssignableFrom(type))
            syms[0] = builder.createLineSymbolizer(baseColor, 2);
        if (Point.class.isAssignableFrom(type) || MultiPoint.class.isAssignableFrom(type)) {
            PointSymbolizer point = builder.createPointSymbolizer(builder.createGraphic());
            FilterFactory ff = builder.getFilterFactory();
            // point.getGraphic().getMarks()[0].setSize((Expression) ff.literal(10));
            point.getGraphic().setSize(ff.literal(10));
            Mark mark = (Mark) point.getGraphic().graphicalSymbols().get(0);
            mark.setFill(builder.createFill(baseColor));
            syms[0] = point;
        }
        if (Polygon.class.isAssignableFrom(type) || MultiPolygon.class.isAssignableFrom(type)) {
            syms[0] = builder.createPolygonSymbolizer(builder.createStroke(baseColor, 2),
                    builder.createFill(baseColor, useTransparency ? .6 : 1.0));
        }
        return syms;
    }

    /**
     * Finds the geometric attribute requested by the symbolizer.
     * 
     * @param feature The victim
     * @param symbolizer The symbolizer
     * @param style the resolved style for the specified victim
     * @return The geometry requested in the symbolizer, or the default geometry if none is
     *         specified
     */
    private com.vividsolutions.jts.geom.Geometry findGeometry( SimpleFeature feature, Symbolizer symbolizer ) {
        String geomName = getGeometryPropertyName(symbolizer);
        // get the geometry
        com.vividsolutions.jts.geom.Geometry geometry;
        if (geomName == null || feature.getType().getDescriptor(geomName) == null) {
            geometry = (Geometry) feature.getDefaultGeometry();
        } else {
            geometry = (com.vividsolutions.jts.geom.Geometry) feature.getAttribute(geomName);
        }
        if (geometry == null) {
            return null; // nothing to see here
        }
        // if the symbolizer is a point or text symbolizer generate a suitable
        // location to place the
        // point in order to avoid recomputing that location at each rendering
        // step

        if ((symbolizer instanceof PointSymbolizer || symbolizer instanceof TextSymbolizer) && !(geometry instanceof Point)) {
            if (geometry instanceof LineString && !(geometry instanceof LinearRing)) {
                // use the mid point to represent the point/text symbolizer
                // anchor
                Coordinate[] coordinates = geometry.getCoordinates();
                Coordinate start = coordinates[0];
                Coordinate end = coordinates[1];
                Coordinate mid = new Coordinate((start.x + end.x) / 2, (start.y + end.y) / 2);
                geometry = geometry.getFactory().createPoint(mid);
            } else {
                // otherwise use the centroid of the polygon
                geometry = geometry.getCentroid();
            }
        }
        return geometry;
    }
    private String getGeometryPropertyName( Symbolizer s ) {
        String geomName = null;
        // TODO: fix the styles, the getGeometryPropertyName should probably be
        // moved into an interface...
        if (s instanceof PolygonSymbolizer) {
            geomName = ((PolygonSymbolizer) s).getGeometryPropertyName();
        } else if (s instanceof PointSymbolizer) {
            geomName = ((PointSymbolizer) s).getGeometryPropertyName();
        } else if (s instanceof LineSymbolizer) {
            geomName = ((LineSymbolizer) s).getGeometryPropertyName();
        } else if (s instanceof TextSymbolizer) {
            geomName = ((TextSymbolizer) s).getGeometryPropertyName();
        }
        return geomName;
    }
    /**
     * TODO summary sentence for worldToScreenTransform ...
     * 
     * @param bounds
     * @param rectangle
     * @return
     */
    public static AffineTransform worldToScreenTransform( BoundingBox mapExtent, Rectangle screenSize ) {
        double scaleX = screenSize.getWidth() / mapExtent.getWidth();
        double scaleY = screenSize.getHeight() / mapExtent.getHeight();

        double tx = -mapExtent.getMinX() * scaleX;
        double ty = (mapExtent.getMinY() * scaleY) + screenSize.getHeight();

        AffineTransform at = new AffineTransform(scaleX, 0.0d, 0.0d, -scaleY, tx, ty);

        return at;
    }
    /**
     * Create a SimpleFeatureType schema using a type short hand.
     * <p>
     * Code Example:<pre><code>
     * new Drawing().schema("namespace.typename", "id:0,*geom:LineString,name:String,*centroid:Point");
     * </code></pre>
     * <ul>
     * <li>SimpleFeatureType with identifier "namespace.typename"
     * <li>Default Geometry "geom" of type LineStirng indicated with a "*"
     * <li>Three attributes: id of type Integer, name of type String and centroid of type Point
     * </ul>
     * </p>
     * @param name namespace.name
     * @param spec
     * @return Generated SimpleFeatureType
     */
    public SimpleFeatureType schema( String name, String spec ) {
        try {
            return DataUtilities.createType(name, spec);
        } catch (SchemaException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static SimpleFeatureType pointSchema;
    static SimpleFeatureType lineSchema;
    static SimpleFeatureType polygonSchema;
    static SimpleFeatureType multipointSchema;
    static SimpleFeatureType multilineSchema;
    static SimpleFeatureType multipolygonSchema;
    static {
        try {
            pointSchema = DataUtilities.createType("generated:point", "*point:Point"); //$NON-NLS-1$ //$NON-NLS-2$
            lineSchema = DataUtilities.createType("generated:linestring", "*linestring:LineString"); //$NON-NLS-1$ //$NON-NLS-2$
            polygonSchema = DataUtilities.createType("generated:polygon", "*polygon:Polygon"); //$NON-NLS-1$ //$NON-NLS-2$
            multipointSchema = DataUtilities.createType("generated:multipoint", "*multipoint:MultiPoint"); //$NON-NLS-1$ //$NON-NLS-2$
            multilineSchema = DataUtilities.createType("generated:multilinestring", "*multilinestring:MultiLineString"); //$NON-NLS-1$ //$NON-NLS-2$
            multipolygonSchema = DataUtilities.createType("generated:multipolygon", "*multipolygon:MultiPolygon"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (SchemaException unExpected) {
            System.err.println(unExpected);
        }
    }

    /**
     * Just a convinient method to create feature from geometry.
     * 
     * @param geom the geometry to create feature from
     * @return feature instance
     */
    public SimpleFeature feature( Geometry geom ) {
        if (geom instanceof Polygon) {
            return feature((Polygon) geom);
        } else if (geom instanceof MultiPolygon) {
            return feature((MultiPolygon) geom);
        } else if (geom instanceof Point) {
            return feature((Point) geom);
        } else if (geom instanceof LineString) {
            return feature((LineString) geom);
        } else if (geom instanceof MultiPoint) {
            return feature((MultiPoint) geom);
        } else if (geom instanceof MultiLineString) {
            return feature((MultiLineString) geom);
        } else {
            throw new IllegalArgumentException("Geometry is not supported to create feature"); //$NON-NLS-1$
        }
    }

    /**
     * Simple feature with one attribute called "point".
     * @param point 
     * @return SimpleFeature with a default geometry and no attribtues 
     */
    public SimpleFeature feature( Point point ) {
        if (point == null)
            throw new NullPointerException("Point required"); //$NON-NLS-1$
        try {
            return SimpleFeatureBuilder.build(pointSchema, new Object[]{point}, null);
        } catch (IllegalAttributeException e) {
            // this should not happen because we *know* the parameter matches schame
            throw new RuntimeException("Could not generate feature for point " + point); //$NON-NLS-1$
        }
    }
    /**
     * Simple SimpleFeature with a default geometry and no attribtues.
     * @param line 
     * @return SimpleFeature with a default geometry and no attribtues 
     */
    public SimpleFeature feature( LineString line ) {
        if (line == null)
            throw new NullPointerException("line required"); //$NON-NLS-1$
        try {
            return SimpleFeatureBuilder.build(lineSchema, new Object[]{line}, null);
        } catch (IllegalAttributeException e) {
            // this should not happen because we *know* the parameter matches schame
            throw new RuntimeException("Could not generate feature for point " + line); //$NON-NLS-1$
        }
    }

    /**
     * Simple SimpleFeature with a default geometry and no attribtues.
     * @param polygon 
     * @return SimpleFeature with a default geometry and no attribtues 
     */
    public SimpleFeature feature( Polygon polygon ) {
        if (polygon == null)
            throw new NullPointerException("polygon required"); //$NON-NLS-1$
        try {
            return SimpleFeatureBuilder.build(polygonSchema, new Object[]{polygon}, null);
        } catch (IllegalAttributeException e) {
            // this should not happen because we *know* the parameter matches schame
            throw new RuntimeException("Could not generate feature for point " + polygon); //$NON-NLS-1$
        }
    }

    /**
     * Simple SimpleFeature with a default geometry and no attribtues.
     * @param multipoint 
     * @return SimpleFeature with a default geometry and no attribtues 
     */
    public SimpleFeature feature( MultiPoint multipoint ) {
        if (multipoint == null)
            throw new NullPointerException("multipoint required"); //$NON-NLS-1$
        try {
            return SimpleFeatureBuilder.build(multipointSchema, new Object[]{multipoint}, null);
        } catch (IllegalAttributeException e) {
            // this should not happen because we *know* the parameter matches schame
            throw new RuntimeException("Could not generate feature for point " + multipoint); //$NON-NLS-1$
        }
    }
    /**
     * Simple SimpleFeature with a default geometry and no attribtues.
     * @param multilinestring
     * @return SimpleFeature with a default geometry and no attribtues 
     */
    public SimpleFeature feature( MultiLineString multilinestring ) {
        if (multilinestring == null)
            throw new NullPointerException("multilinestring required"); //$NON-NLS-1$
        try {
            return SimpleFeatureBuilder.build(multilineSchema, new Object[]{multilinestring}, null);
        } catch (IllegalAttributeException e) {
            // this should not happen because we *know* the parameter matches schame
            throw new RuntimeException("Could not generate feature for point " + multilinestring); //$NON-NLS-1$
        }
    }
    /**
     * Simple SimpleFeature with a default geometry and no attribtues.
     * @param multipolygon 
     * @return SimpleFeature with a default geometry and no attribtues 
     */
    public SimpleFeature feature( MultiPolygon multipolygon ) {
        if (multipolygon == null)
            throw new NullPointerException("multipolygon required"); //$NON-NLS-1$
        try {
            return SimpleFeatureBuilder.build(multipolygonSchema, new Object[]{multipolygon}, null);
        } catch (IllegalAttributeException e) {
            // this should not happen because we *know* the parameter matches schame
            throw new RuntimeException("Could not generate feature for point " + multipolygon); //$NON-NLS-1$
        }
    }

    /**
     * Generate Point from two dimensional ordinates
     * 
     * @param x
     * @param y
     * @return Point
     */
    public Point point( int x, int y ) {
        return gf.createPoint(new Coordinate(x, y));
    }
    /**
     * Generate LineStrings from two dimensional ordinates
     * 
     * @param xy
     * @return LineStirng
     */
    public LineString line( int[] xy ) {
        Coordinate[] coords = new Coordinate[xy.length / 2];

        for( int i = 0; i < xy.length; i += 2 ) {
            coords[i / 2] = new Coordinate(xy[i], xy[i + 1]);
        }

        return gf.createLineString(coords);
    }

    /**
     * Generate a MultiLineString from two dimensional ordinates
     * 
     * @param xy
     * @return MultiLineStirng
     */
    public MultiLineString lines( int[][] xy ) {
        LineString[] lines = new LineString[xy.length];

        for( int i = 0; i < xy.length; i++ ) {
            lines[i] = line(xy[i]);
        }

        return gf.createMultiLineString(lines);
    }
    /**
     * Convience constructor for GeometryFactory.createPolygon.
     * <p>
     * The provided xy ordinates are turned into a linear rings.
     * </p>
     * @param xy Two dimensional ordiantes.
     * @return Polygon
     */
    public Polygon polygon( int[] xy ) {
        LinearRing shell = ring(xy);
        return gf.createPolygon(shell, null);
    }

    /**
     * Convience constructor for GeometryFactory.createPolygon.
     * <p>
     * The provided xy and holes are turned into linear rings.
     * </p>
     * @param xy Two dimensional ordiantes.
     * @param holes Holes in polygon or null.
     * 
     * @return Polygon 
     */
    public Polygon polygon( int[] xy, int[] holes[] ) {
        if (holes == null || holes.length == 0) {
            return polygon(xy);
        }
        LinearRing shell = ring(xy);

        LinearRing[] rings = new LinearRing[holes.length];

        for( int i = 0; i < xy.length; i++ ) {
            rings[i] = ring(holes[i]);
        }
        return gf.createPolygon(shell, rings);
    }

    /**
     * Convience constructor for GeometryFactory.createLinearRing.
     * 
     * @param xy Two dimensional ordiantes.
     * @return LinearRing for use with polygon
     */
    public LinearRing ring( int[] xy ) {
        int length = xy.length / 2;
        if (xy[0] != xy[xy.length - 2] || xy[1] != xy[xy.length - 1]) {
            length++;
        }
        Coordinate[] coords = new Coordinate[length];

        for( int i = 0; i < xy.length; i += 2 ) {
            coords[i / 2] = new Coordinate(xy[i], xy[i + 1]);
        }
        if (xy[0] != xy[xy.length - 2] || xy[1] != xy[xy.length - 1]) {
            coords[length - 1] = coords[0];
        }
        return gf.createLinearRing(coords);
    }
}
