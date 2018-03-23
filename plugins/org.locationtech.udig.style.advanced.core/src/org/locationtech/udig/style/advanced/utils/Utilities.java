/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Spinner;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.filter.function.FilterFunction_offset;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeConstraint;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.Graphic;
import org.geotools.styling.LabelPlacement;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLDParser;
import org.geotools.styling.SLDTransformer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.geotools.styling.UserLayer;
import org.geotools.styling.visitor.DuplicatingStyleVisitor;
import org.locationtech.udig.style.sld.SLD;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.style.GraphicalSymbol;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.vividsolutions.jts.geom.LineString;

/**
 * Style related utilities.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class Utilities {
    public static final String NONE = "- none -";

    public static final String DEFAULT_SIZE = "5";
    public static final String DEFAULT_WIDTH = "1";
    public static final String DEFAULT_ROTATION = "0";
    public static final String DEFAULT_OFFSET = "0";
    public static final String DEFAULT_OPACITY = "1";
    public static final String DEFAULT_COLOR = "#000000";
    public static final String DEFAULT_MINSCALE = "0";
    public static final String DEFAULT_MAXSCALE = "infinity";

    // offset values
    public static final int OFFSET_MAX = 1000;
    public static final int OFFSET_MIN = -1000;
    public static final int OFFSET_STEP = 10;
    // displacement values
    public static final int DISPLACEMENT_MAX = 1000;
    public static final int DISPLACEMENT_MIN = -1000;
    public static final int DISPLACEMENT_STEP = 10;

    public static final String DEFAULT_GROUPNAME = "group ";
    public static final String DEFAULT_STYLENAME = "default style";

    public static final String SLD_EXTENTION = ".sld";

    /**
     * The SLD defined well known mark codes.
     */
    public static final String[] wkMarkDefs = {//
    "", //
            "cross", //
            "circle", //
            "triangle", //
            "X", //
            "star", //
            "arrow", //
            "hatch", //
            "square"//
    };

    /**
     * The SLD defined well known mark names for gui use.
     */
    public static final String[] wkMarkNames = {//
    "", //
            "cross", //
            "circle", //
            "triangle", //
            "X", //
            "star", //
            "arrow", //
            "hatch", //
            "square"//
    };

    public static final String SHAPE_PREFIX = "shape://";

    /**
     * The custom shape mark names as needed by geotools.
     */
    public static final String[] shapeMarkDefs = { //
    "", //
            SHAPE_PREFIX + "vertline", //
            SHAPE_PREFIX + "horline", //
            SHAPE_PREFIX + "slash", //
            SHAPE_PREFIX + "backslash", //
            SHAPE_PREFIX + "times", //
            SHAPE_PREFIX + "dot", //
            SHAPE_PREFIX + "plus" //
    };
    /**
     * The custom shape mark names for gui.
     */
    public static final String[] shapeMarkNames = { //
    "", //
            "vertical lines", //
            "horizontal lines", //
            "diagonal lines", //
            "inverse diagonal lines", //
            "crossed diagonal lines", //
            "dots", //
            "plus" //
    };

    /**
     * A map of names for all the marks.
     */
    public static final BiMap<String, String> markNamesToDef = HashBiMap.create();
    static {

        // well known marks
        markNamesToDef.put(wkMarkNames[0], wkMarkDefs[0]);
        markNamesToDef.put(wkMarkNames[1], wkMarkDefs[1]);
        markNamesToDef.put(wkMarkNames[2], wkMarkDefs[2]);
        markNamesToDef.put(wkMarkNames[3], wkMarkDefs[3]);
        markNamesToDef.put(wkMarkNames[4], wkMarkDefs[4]);
        markNamesToDef.put(wkMarkNames[5], wkMarkDefs[5]);
        markNamesToDef.put(wkMarkNames[6], wkMarkDefs[6]);
        markNamesToDef.put(wkMarkNames[7], wkMarkDefs[7]);
        markNamesToDef.put(wkMarkNames[8], wkMarkDefs[8]);
        // custom shapes
        markNamesToDef.put(shapeMarkNames[1], shapeMarkDefs[1]);
        markNamesToDef.put(shapeMarkNames[2], shapeMarkDefs[2]);
        markNamesToDef.put(shapeMarkNames[3], shapeMarkDefs[3]);
        markNamesToDef.put(shapeMarkNames[4], shapeMarkDefs[4]);
        markNamesToDef.put(shapeMarkNames[5], shapeMarkDefs[5]);
        markNamesToDef.put(shapeMarkNames[6], shapeMarkDefs[6]);
        markNamesToDef.put(shapeMarkNames[7], shapeMarkDefs[7]);
    }

    /**
     * Getter for an array of all available marks.
     * 
     * @return all mark names (for gui use).
     */
    public static String[] getAllMarksArray() {
        Set<String> keySet = markNamesToDef.keySet();
        return (String[]) keySet.toArray(new String[keySet.size()]);
    }

    /**
     * The SLD names of the line cap definitions.
     */
    public static final String[] lineCapNames = { //
    "", //
            "butt", //
            "round", //
            "square" //
    };

    /**
     * The SLD names of the line join definitions.
     */
    public static final String[] verticalPlacementNames = { //
    "bevel", //
            "miter", //
            "round" //
    };

    /**
     * The SLD names of the line join definitions.
     */
    public static final String[] lineJoinNames = { //
    "", //
            "bevel", //
            "miter", //
            "round" //
    };

    /**
     * A map of user friendly names to the SLD names of line 
     * end styles.
     */    
    public static final BiMap<String, String> lineEndStyles = HashBiMap.create();
    static {
    	lineEndStyles.put("arrow - open", "shape://oarrow");
    	lineEndStyles.put("arrow - closed", "shape://carrow");
    	lineEndStyles.put("circle", "circle");
    	lineEndStyles.put("square", "square");
    }
    
    /**
     * The default {@link StyleFactory} to use.
     */
    public static StyleFactory sf = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());

    /**
     * The default {@link FilterFactory} to use.
     */
    public static FilterFactory ff = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());

    /**
     * The default {@link StyleBuilder} to use.
     */
    public static StyleBuilder sb = new StyleBuilder(sf, ff);

    /** 
     * Utility class for working with Images, Features and Styles 
     */
    private static Drawing d = Drawing.create();

    /**
     * Parse a file and extract the {@link StyledLayerDescriptor}.
     * 
     * @param file the sld file to parse.
     * @return the styled layer descriptor.
     * @throws IOException
     */
    public static StyledLayerDescriptor readStyle( File file ) throws IOException {
        SLDParser stylereader = new SLDParser(sf, file);
        StyledLayerDescriptor sld = stylereader.parseSLD();
        return sld;
    }

    /**
     * Creates an {@link Image} for the given rule.
     * 
     * @param rule the rule for which to create the image.
     * @param width the image width.
     * @param height the image height.
     * @return the generated image.
     */
    public static BufferedImage lineRuleToImage( final Rule rule, int width, int height ) {
        DuplicatingStyleVisitor copyStyle = new DuplicatingStyleVisitor();
        rule.accept(copyStyle);
        Rule newRule = (Rule) copyStyle.getCopy();

        Stroke stroke = null;
        Symbolizer[] symbolizers = newRule.getSymbolizers();
        if (symbolizers.length > 0) {
            Symbolizer symbolizer = newRule.getSymbolizers()[0];
            if (symbolizer instanceof LineSymbolizer) {
                LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;
                stroke = SLDs.stroke(lineSymbolizer);
            }
        }

        int strokeSize = 0;
        if (stroke != null) {
            strokeSize = SLDs.width(stroke);
            if (strokeSize < 0) {
                strokeSize = 0;
                stroke.setWidth(ff.literal(strokeSize));
            }
        }

        // pointSize = width;
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Polygon polygon = d.polygon(new int[]{40,30, 60,70, 30,130, 130,130, 130,30});

        int[] xy = new int[]{(int) (height * 0.15), (int) (width * 0.85), (int) (height * 0.35), (int) (width * 0.15),
                (int) (height * 0.75), (int) (width * 0.85), (int) (height * 0.85), (int) (width * 0.15)};
        LineString line = d.line(xy);
        d.drawDirect(finalImage, d.feature(line), newRule);
        Graphics2D g2d = finalImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(finalImage, 0, 0, null);
        g2d.dispose();

        return finalImage;
    }

    /**
     * Collect all {@link ExternalGraphic}s from the given {@link Rule}.
     * 
     * @param rule the rule to check.
     * @return the extracted {@link ExternalGraphic}s.
     */
    public static List<ExternalGraphic> externalGraphicsFromRule( Rule rule ) {
        List<ExternalGraphic> gList = new ArrayList<ExternalGraphic>();
        List<Symbolizer> symbolizers = rule.symbolizers();
        if (symbolizers.size() != 0) {
            for( Symbolizer symbolizer : symbolizers ) {
                Graphic[] graphics = new Graphic[2];
                if (symbolizer instanceof PointSymbolizer) {
                    PointSymbolizer pointSymbolizer = (PointSymbolizer) symbolizer;
                    graphics[0] = pointSymbolizer.getGraphic();
                } else if (symbolizer instanceof LineSymbolizer) {
                    LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;
                    Stroke stroke = lineSymbolizer.getStroke();
                    graphics[0] = stroke.getGraphicStroke();
                } else if (symbolizer instanceof PolygonSymbolizer) {
                    PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
                    Stroke stroke = polygonSymbolizer.getStroke();
                    if (stroke != null)
                        graphics[0] = stroke.getGraphicStroke();
                    Fill fill = polygonSymbolizer.getFill();
                    if (fill != null)
                        graphics[1] = fill.getGraphicFill();
                }
                for( int i = 0; i < graphics.length; i++ ) {
                    if (graphics[i] != null) {
                        for( GraphicalSymbol gs : graphics[i].graphicalSymbols() ) {
                            if ((gs != null) && (gs instanceof ExternalGraphic)) {
                                ExternalGraphic externalGraphic = (ExternalGraphic) gs;
                                gList.add(externalGraphic);
                            }
                        }
                    }
                }
            }
            return gList;
        }
        return Collections.emptyList();
    }

    /**
     * Collect all {@link ExternalGraphic}s from the given {@link Graphic}.
     * 
     * @param graphic the graphic to check.
     * @return the extracted {@link ExternalGraphic}s.
     */
    public static List<ExternalGraphic> externalGraphicsFromGraphic( Graphic graphic ) {
        List<ExternalGraphic> gList = new ArrayList<ExternalGraphic>();
        for( GraphicalSymbol gs : graphic.graphicalSymbols() ) {
            if ((gs != null) && (gs instanceof ExternalGraphic)) {
                ExternalGraphic externalGraphic = (ExternalGraphic) gs;
                gList.add(externalGraphic);
            }
        }
        return gList;
    }

    /**
     * Creates a default {@link Style} for a point.
     * 
     * @return the default style.
     */
    public static Style createDefaultPointStyle() {

        FeatureTypeStyle featureTypeStyle = sf.createFeatureTypeStyle();
        featureTypeStyle.rules().add(createDefaultPointRule());

        Style style = sf.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);

        return style;
    }

    /**
     * Creates a default {@link Rule} for a point.
     * 
     * @return the default rule.
     */
    public static Rule createDefaultPointRule() {
        Graphic graphic = sf.createDefaultGraphic();
        Mark circleMark = sf.getCircleMark();
        circleMark.setFill(sf.createFill(ff.literal("#" + Integer.toHexString(Color.RED.getRGB() & 0xffffff))));
        circleMark.setStroke(sf.createStroke(ff.literal("#" + Integer.toHexString(Color.BLACK.getRGB() & 0xffffff)), ff.literal(DEFAULT_WIDTH)));
        graphic.graphicalSymbols().clear();
        graphic.graphicalSymbols().add(circleMark);
        graphic.setSize(ff.literal(DEFAULT_SIZE));

        PointSymbolizer pointSymbolizer = sf.createPointSymbolizer();
        Rule rule = sf.createRule();
        rule.setName("New rule");
        rule.symbolizers().add(pointSymbolizer);

        pointSymbolizer.setGraphic(graphic);
        return rule;
    }

    /**
     * Creates a default {@link Style} for a polygon.
     * 
     * @return the default style.
     */
    public static Style createDefaultPolygonStyle() {
        FeatureTypeStyle featureTypeStyle = sf.createFeatureTypeStyle();
        featureTypeStyle.rules().add(createDefaultPolygonRule());

        Style style = sf.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);

        return style;
    }

    /**
     * Creates a default {@link Rule} for a polygon.
     * 
     * @return the default rule.
     */
    public static Rule createDefaultPolygonRule() {
        PolygonSymbolizer polygonSymbolizer = sf.createPolygonSymbolizer();
        polygonSymbolizer.setFill(sf.createFill(ff.literal("#" + Integer.toHexString(Color.RED.getRGB() & 0xffffff))));
        polygonSymbolizer.getFill().setOpacity(ff.literal(0.50));
        polygonSymbolizer.setStroke(sf.createStroke(ff.literal("#" + Integer.toHexString(Color.BLACK.getRGB() & 0xffffff)), ff.literal(DEFAULT_WIDTH)));

        Rule rule = sf.createRule();
        rule.setName("New rule");
        rule.symbolizers().add(polygonSymbolizer);

        return rule;
    }

    /**
     * Creates a default {@link Style} for a line.
     * 
     * @return the default style.
     */
    public static Style createDefaultLineStyle() {
        FeatureTypeStyle featureTypeStyle = sf.createFeatureTypeStyle();
        featureTypeStyle.rules().add(createDefaultLineRule());

        Style style = sf.createStyle();
        style.featureTypeStyles().add(featureTypeStyle);

        return style;
    }

    /**
     * Creates a default {@link Rule} for a line.
     * 
     * @return the default rule.
     */
    public static Rule createDefaultLineRule() {
        LineSymbolizer lineSymbolizer = sf.createLineSymbolizer();
        lineSymbolizer.setStroke(sf.createStroke(ff.literal("#" + Integer.toHexString(Color.BLACK.getRGB() & 0xffffff)), ff.literal(1)));

        Rule rule = sf.createRule();
        rule.setName("New rule");
        rule.symbolizers().add(lineSymbolizer);

        return rule;
    }

    /**
     * Creates a default {@link TextSymbolizer} for a given type.
     * 
     * @return the default symbolizer.
     */
    public static TextSymbolizer createDefaultTextSymbolizer( SLD type ) {
        LabelPlacement labelPlacement = null;

        switch( type ) {
        case POINT:
        case POLYGON:
            labelPlacement = sf.createPointPlacement(sf.createAnchorPoint(ff.literal(0.0), ff.literal(0.0)),
                    sf.createDisplacement(ff.literal(0.0), ff.literal(0.0)), ff.literal(0.0));
            break;
        case LINE:
            labelPlacement = sf.createLinePlacement(ff.literal(10.0));
            break;

        default:
            throw new IllegalArgumentException();
        }

        Font font = sb.createFont("Arial", false, false, 12); //$NON-NLS-1$
        TextSymbolizer textSymbolizer = sf.createTextSymbolizer(sf.createFill(ff.literal(DEFAULT_COLOR)), new Font[]{font}, null,
                ff.literal("dummy"), labelPlacement, null);

        return textSymbolizer;
    }

    /**
     * Creates a default {@link TextSymbolizer} for a point.
     * 
     * @return the default symbolizer.
     */
    public static Symbolizer createDefaultGeometrySymbolizer( SLD type ) {
        Symbolizer symbolizer = null;
        switch( type ) {
        case POINT:
            Rule defaultPointRule = createDefaultPointRule();
            symbolizer = defaultPointRule.getSymbolizers()[0];
            break;
        case POLYGON:
            Rule defaultPolygonRule = createDefaultPolygonRule();
            symbolizer = defaultPolygonRule.getSymbolizers()[0];
            break;
        case LINE:
            Rule defaultLineRule = createDefaultLineRule();
            symbolizer = defaultLineRule.getSymbolizers()[0];
            break;

        default:
            throw new IllegalArgumentException();
        }

        return symbolizer;
    }

    /**
     * Get the {@link PointSymbolizer} from the given rule.
     * 
     * @param rule the rule to check for symbolizers.
     * @return the first symbolizer found.
     */
    public static PointSymbolizer pointSymbolizerFromRule( Rule rule ) {
        List<Symbolizer> symbolizers = rule.symbolizers();
        PointSymbolizer pointSymbolizer = null;
        for( Symbolizer symbolizer : symbolizers ) {
            if (symbolizer instanceof PointSymbolizer) {
                pointSymbolizer = (PointSymbolizer) symbolizer;
                break;
            }
        }
        if (pointSymbolizer == null) {
            throw new IllegalArgumentException();
        }
        return pointSymbolizer;
    }

    /**
     * Get the {@link PolygonSymbolizer} from the given rule.
     * 
     * @param rule the rule to check for symbolizers.
     * @return the first symbolizer found.
     */
    public static PolygonSymbolizer polygonSymbolizerFromRule( Rule rule ) {
        List<Symbolizer> symbolizers = rule.symbolizers();
        PolygonSymbolizer polygonSymbolizer = null;
        for( Symbolizer symbolizer : symbolizers ) {
            if (symbolizer instanceof PolygonSymbolizer) {
                polygonSymbolizer = (PolygonSymbolizer) symbolizer;
                break;
            }
        }
        if (polygonSymbolizer == null) {
            throw new IllegalArgumentException();
        }
        return polygonSymbolizer;
    }

    /**
     * Get the {@link LineSymbolizer} from the given rule.
     * 
     * @param rule the rule to check for symbolizers.
     * @return the first symbolizer found.
     */
    public static LineSymbolizer lineSymbolizerFromRule( Rule rule ) {
        List<Symbolizer> symbolizers = rule.symbolizers();
        LineSymbolizer lineSymbolizer = null;
        for( Symbolizer symbolizer : symbolizers ) {
            if (symbolizer instanceof LineSymbolizer) {
                lineSymbolizer = (LineSymbolizer) symbolizer;
                break;
            }
        }
        if (lineSymbolizer == null) {
            throw new IllegalArgumentException();
        }
        return lineSymbolizer;
    }

    /**
     * Change the mark shape in a rule.
     * 
     * @param rule the rule of which the mark has to be changed.
     * @param wellKnownMarkName the name of the new mark.
     */
    public static void substituteMark( Rule rule, String wellKnownMarkName ) {
        PointSymbolizer pointSymbolizer = Utilities.pointSymbolizerFromRule(rule);
        Mark oldMark = SLDs.mark(pointSymbolizer);

        Graphic graphic = SLDs.graphic(pointSymbolizer);
        graphic.graphicalSymbols().clear();
        
        Mark mark = Utilities.sf.createMark();
        mark.setWellKnownName(Utilities.ff.literal(wellKnownMarkName));
        if (oldMark != null) {
            mark.setFill(oldMark.getFill());
            mark.setStroke(oldMark.getStroke());
        }
        graphic.graphicalSymbols().add(mark);
    }

    /**
     * Change the external graphic in a rule.
     * 
     * @param rule the rule of which the external graphic has to be changed.
     * @param path the path of the image.
     */
    public static void substituteExternalGraphics( Rule rule, URL externalGraphicsUrl ) {
        String urlString = externalGraphicsUrl.toString();
        String format = "";
        if (urlString.toLowerCase().endsWith(".png")) {
            format = "image/png";
        } else if (urlString.toLowerCase().endsWith(".jpg")) {
            format = "image/jpg";
        } else if (urlString.toLowerCase().endsWith(".svg")) {
            format = "image/svg+xml";
        } else {
            urlString = "";
            try {
                externalGraphicsUrl = new URL("file:");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        PointSymbolizer pointSymbolizer = Utilities.pointSymbolizerFromRule(rule);
        Graphic graphic = SLDs.graphic(pointSymbolizer);
        graphic.graphicalSymbols().clear();
        ExternalGraphic exGraphic = sf.createExternalGraphic(externalGraphicsUrl, format);

        graphic.graphicalSymbols().add(exGraphic);
    }

    public static String getFormat( String path ) {
        String format = "";
        if (path.toLowerCase().endsWith(".png")) {
            format = "image/png";
        } else if (path.toLowerCase().endsWith(".jpg")) {
            format = "image/jpg";
        } else if (path.toLowerCase().endsWith(".gif")) {
            format = "image/gif";
        } else if (path.toLowerCase().endsWith(".svg")) {
            format = "image/svg+xml";
        }
        return format;
    }

    /**
     * Get the format of an {@link ExternalGraphic} from its path or name.
     * 
     * @param name the path or file name to test against.
     * @return teh format definition.
     */
    public static String getExternalGraphicFormat( String name ) {
        String format = "";
        if (name.toLowerCase().endsWith(".png")) {
            format = "image/png";
        } else if (name.toLowerCase().endsWith(".jpg")) {
            format = "image/jpg";
        } else if (name.toLowerCase().endsWith(".gif")) {
            format = "image/gif";
        } else if (name.toLowerCase().endsWith(".svg")) {
            format = "image/svg+xml";
        } else {
            return null;
        }

        return format;
    }

    /**
     * Changes the size of a mark inside a rule.
     * 
     * @param rule the {@link Rule}.
     * @param newSize the new size.
     */
    public static void changeMarkSize( Rule rule, int newSize ) {
        PointSymbolizer pointSymbolizer = Utilities.pointSymbolizerFromRule(rule);
        Graphic graphic = SLDs.graphic(pointSymbolizer);
        graphic.setSize(ff.literal(newSize));
        // Mark oldMark = SLDs.mark(pointSymbolizer);
        // oldMark.setSize(ff.literal(newSize));
        // Graphic graphic = SLDs.graphic(pointSymbolizer);
    }

    /**
     * Changes the rotation value inside a rule.
     * 
     * @param rule the {@link Rule}.
     * @param newRotation the new rotation value in degrees.
     */
    public static void changeRotation( Rule rule, int newRotation ) {
        PointSymbolizer pointSymbolizer = Utilities.pointSymbolizerFromRule(rule);
        Graphic graphic = SLDs.graphic(pointSymbolizer);
        graphic.setRotation(ff.literal(newRotation));
        // Mark oldMark = SLDs.mark(pointSymbolizer);
        // oldMark.setSize(ff.literal(newRotation));
    }

    /**
     * Get the offset from a {@link Symbolizer}.
     * 
     * @param symbolizer the symbolizer.
     * @return the offset.
     */
    @SuppressWarnings("rawtypes")
    public static Point2D getOffset( Symbolizer symbolizer ) {
        Expression geometry = symbolizer.getGeometry();
        if (geometry != null) {
            if (geometry instanceof FilterFunction_offset) {
                FilterFunction_offset offsetFunction = (FilterFunction_offset) geometry;
                List parameters = offsetFunction.getParameters();
                Expression xOffsetExpr = (Expression) parameters.get(1);
                Expression yOffsetExpr = (Expression) parameters.get(2);
                Double xOffsetDouble = xOffsetExpr.evaluate(null, Double.class);
                Double yOffsetDouble = yOffsetExpr.evaluate(null, Double.class);
                if (xOffsetDouble != null && yOffsetDouble != null) {
                    Point2D.Double point = new Point2D.Double(xOffsetDouble, yOffsetDouble);
                    return point;
                }
            }
        }
        return null;
    }

    /**
     * Sets the offset in a symbolizer.
     * 
     * @param symbolizer the symbolizer.
     * @param text the text representing the offsets in the CSV form.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setOffset( Symbolizer symbolizer, String text ) {
        if (text.indexOf(',') == -1) {
            return;
        }
        String[] split = text.split(",");
        if (split.length != 2) {
            return;
        }
        double xOffset = Double.parseDouble(split[0]);
        double yOffset = Double.parseDouble(split[1]);

        Expression geometry = symbolizer.getGeometry();
        if (geometry != null) {
            if (geometry instanceof FilterFunction_offset) {
                FilterFunction_offset offsetFunction = (FilterFunction_offset) geometry;
                List parameters = offsetFunction.getParameters();
                parameters.set(1, ff.literal(xOffset));
                parameters.set(2, ff.literal(yOffset));
            }
        } else {
            Function function = ff.function("offset", ff.property("the_geom"), ff.literal(xOffset), ff.literal(yOffset));
            symbolizer.setGeometry(function);
        }
    }

    /**
     * Converts a list of {@link Rule}s to a {@link Style} with the given name.
     * 
     * @param rules the list of rules.
     * @param name the name of the new style.
     * @param oneFeaturetypestylePerRule switch to create a {@link FeatureTypeStyle} per {@link Rule}. 
     * @return the new style created.
     */
    public static Style rulesToStyle( List<Rule> rules, String name, boolean oneFeaturetypestylePerRule ) {
        Style namedStyle = Utilities.sf.createStyle();
        if (!oneFeaturetypestylePerRule) {
            FeatureTypeStyle featureTypeStyle = Utilities.sf.createFeatureTypeStyle();
            List<Rule> currentRules = featureTypeStyle.rules();
            for( int i = 0; i < rules.size(); i++ ) {
                Rule rule = rules.get(i);
                currentRules.add(rule);
            }
            namedStyle.featureTypeStyles().add(featureTypeStyle);
        } else {
            for( int i = 0; i < rules.size(); i++ ) {
                FeatureTypeStyle featureTypeStyle = Utilities.sf.createFeatureTypeStyle();
                Rule rule = rules.get(i);
                featureTypeStyle.rules().add(rule);
                namedStyle.featureTypeStyles().add(featureTypeStyle);
            }
        }
        namedStyle.setName(name);
        return namedStyle;
    }

    /**
     * Converts a style to its string representation to be written to file.
     * 
     * @param style the style to convert.
     * @return the style string.
     * @throws Exception
     */
    public static String styleToString( Style style ) throws Exception {
        StyledLayerDescriptor sld = sf.createStyledLayerDescriptor();
        UserLayer layer = sf.createUserLayer();
        layer.setLayerFeatureConstraints(new FeatureTypeConstraint[]{null});
        sld.addStyledLayer(layer);
        layer.addUserStyle(style);

        SLDTransformer aTransformer = new SLDTransformer();
        aTransformer.setIndentation(4);
        String xml = aTransformer.transform(sld);
        return xml;
    }


    /**
     * Returns a dash array from a dash string.
     * 
     * @param dashStr the dash string definition. 
     * @return the dash array or null if the definition can't be parsed.
     */
    public static float[] getDash( String dashStr ) {
        if (dashStr == null) {
            return null;
        }
        String[] dashSplit = dashStr.split(","); //$NON-NLS-1$
        int size = dashSplit.length;
        float[] dash = new float[size];
        try {
            for( int i = 0; i < dash.length; i++ ) {
                dash[i] = Float.parseFloat(dashSplit[i].trim());
            }
            return dash;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Converts teh array to string.
     * 
     * @param dashArray the dash array.
     * @return the converted string.
     */
    public static String getDashString( float[] dashArray ) {
        StringBuilder sb = null;
        for( float f : dashArray ) {
            if (sb == null) {
                sb = new StringBuilder(String.valueOf(f));
            } else {
                sb.append(",");
                sb.append(String.valueOf(f));
            }
        }
        return sb.toString();
    }

    /**
     * Checks if a string is a number (currently Double, Float, Integer).
     * 
     * @param value the string to check. 
     * @param adaptee the class to check against. If null, the more permissive {@link Double} will be used.
     * @return the number or null, if the parsing fails.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T isNumber( String value, Class<T> adaptee ) {
        if (value == null) {
            return null;
        }
        if (adaptee == null) {
            adaptee = (Class<T>) Double.class;
        }
        if (adaptee.isAssignableFrom(Double.class)) {
            try {
                Double parsed = Double.parseDouble(value);
                return adaptee.cast(parsed);
            } catch (Exception e) {
                return null;
            }
        } else if (adaptee.isAssignableFrom(Float.class)) {
            try {
                Float parsed = Float.parseFloat(value);
                return adaptee.cast(parsed);
            } catch (Exception e) {
                return null;
            }
        } else if (adaptee.isAssignableFrom(Integer.class)) {
            try {
                Integer parsed = Integer.parseInt(value);
                return adaptee.cast(parsed);
            } catch (Exception e) {
                // still try the double
                Double number = isNumber(value, Double.class);
                if (number != null) {
                    return adaptee.cast(number.intValue());
                }
                return null;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Get the double value from a spinner that has digits.
     * 
     * @param spinner the spinner to get the value from.
     * @return the selected value.
     */
    public static double getDoubleSpinnerSelection( Spinner spinner ) {
        int selection = spinner.getSelection();
        int digits = spinner.getDigits();
        double value = selection / Math.pow(10, digits);
        return value;
    }

    /**
     * Convert a sld line join definition to the java awt value. 
     * 
     * @param sldJoin the sld join string.
     * @return the awt value.
     */
    public static int sld2awtJoin( String sldJoin ) {
        if (sldJoin.equals(lineJoinNames[1])) {
            return BasicStroke.JOIN_BEVEL;
        } else if (sldJoin.equals("") || sldJoin.equals(lineJoinNames[2])) {
            return BasicStroke.JOIN_MITER;
        } else if (sldJoin.equals(lineJoinNames[3])) {
            return BasicStroke.JOIN_ROUND;
        } else {
            throw new IllegalArgumentException("unsupported line join");
        }
    }

    /**
     * Convert a sld line cap definition to the java awt value. 
     * 
     * @param sldCap the sld cap string.
     * @return the awt value.
     */
    public static int sld2awtCap( String sldCap ) {
        if (sldCap.equals("") || sldCap.equals(lineCapNames[1])) {
            return BasicStroke.CAP_BUTT;
        } else if (sldCap.equals(lineCapNames[2])) {
            return BasicStroke.CAP_ROUND;
        } else if (sldCap.equals(lineCapNames[3])) {
            return BasicStroke.CAP_SQUARE;
        } else {
            throw new IllegalArgumentException("unsupported line cap");
        }
    }

}
