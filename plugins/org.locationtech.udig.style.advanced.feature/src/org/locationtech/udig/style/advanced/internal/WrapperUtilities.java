package org.locationtech.udig.style.advanced.internal;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.graphics.Image;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Graphic;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.visitor.DuplicatingStyleVisitor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.FeatureTypeStyleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.RuleWrapper;
import org.locationtech.udig.style.advanced.common.styleattributeclasses.StyleWrapper;
import org.locationtech.udig.style.advanced.utils.Drawing;
import org.locationtech.udig.style.advanced.utils.Utilities;
import org.locationtech.udig.style.sld.SLD;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.filter.expression.Expression;

public final class WrapperUtilities {

    /** 
     * Utility class for working with Images, Features and Styles 
     */
    private static Drawing drawing = Drawing.create();
    
    /**
     * Creates an image from a set of {@link RuleWrapper}s.
     * 
     * @param ruleWrapperList the list of rule wrappers.
     * @param width the image width.
     * @param height the image height.
     * @param type the geometry type.
     * @return the new created {@link BufferedImage}.
     */
    public static BufferedImage rulesWrapperToImage( List<RuleWrapper> ruleWrappers, int width, int height, SLD type ) {
        switch( type ) {
        case POINT:
            return WrapperUtilities.pointRulesWrapperToImage(ruleWrappers, width, height);
        case LINE:
            return WrapperUtilities.lineRulesWrapperToImage(ruleWrappers, width, height);
        case POLYGON:
            return WrapperUtilities.polygonRulesWrapperToImage(ruleWrappers, width, height);
        default:
            return null;
        }
    }

    /**
     * Creates an image from a {@link RuleWrapper}.
     * 
     * @param ruleWrapper the rule wrapper.
     * @param width the image width.
     * @param height the image height.
     * @param type the geometry type.
     * @return the new created {@link BufferedImage}.
     */
    public static BufferedImage rulesWrapperToImage( RuleWrapper ruleWrapper, int width, int height, SLD type ) {
        switch( type ) {
        case POINT:
            return WrapperUtilities.pointRuleWrapperToImage(ruleWrapper, width, height);
        case LINE:
            return WrapperUtilities.lineRuleWrapperToImage(ruleWrapper, width, height);
        case POLYGON:
            return WrapperUtilities.polygonRuleWrapperToImage(ruleWrapper, width, height);
        default:
            return null;
        }
    }

    /**
     * Creates an image from a set of {@link RuleWrapper}s.
     * 
     * @param ruleWrapperList the list of rule wrappers.
     * @param width the image width.
     * @param height the image height.
     * @return the new created {@link BufferedImage}.
     */
    public static BufferedImage pointRulesWrapperToImage( final List<RuleWrapper> ruleWrapperList, int width, int height ) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for( RuleWrapper ruleWrapper : ruleWrapperList ) {
            BufferedImage tmpImage = WrapperUtilities.pointRuleWrapperToImage(ruleWrapper, width, height);
            g2d.drawImage(tmpImage, 0, 0, null);
        }
        g2d.dispose();
        return image;
    }

    /**
     * Creates an {@link Image} for the given {@link RuleWrapper}.
     * 
     * @param ruleWrapper the rule for which to create the image.
     * @param width the image width.
     * @param height the image height.
     * @return the generated image.
     */
    public static BufferedImage pointRuleWrapperToImage( RuleWrapper ruleWrapper, int width, int height ) {
        return pointRuleToImage(ruleWrapper.getRule(), width, height);
    }

    /**
     * Creates an {@link Image} for the given rule.
     * 
     * @param rule the rule for which to create the image.
     * @param width the image width.
     * @param height the image height.
     * @return the generated image.
     */
    public static BufferedImage pointRuleToImage( final Rule rule, int width, int height ) {
        DuplicatingStyleVisitor copyStyle = new DuplicatingStyleVisitor();
        rule.accept(copyStyle);
        Rule newRule = (Rule) copyStyle.getCopy();

        int pointSize = 0;
        Stroke stroke = null;
        Symbolizer[] symbolizers = newRule.getSymbolizers();
        if (symbolizers.length > 0) {
            Symbolizer symbolizer = newRule.getSymbolizers()[0];
            if (symbolizer instanceof PointSymbolizer) {
                PointSymbolizer pointSymbolizer = (PointSymbolizer) symbolizer;
                pointSize = SLDs.pointSize(pointSymbolizer);
                stroke = SLDs.stroke(pointSymbolizer);
            }
        }
        int strokeSize = 0;
        if (stroke != null) {
            strokeSize = SLDs.width(stroke);
            if (strokeSize < 0) {
                strokeSize = 1;
                stroke.setWidth(Utilities.ff.literal(strokeSize));
            }
        }
        pointSize = pointSize + 2 * strokeSize;
        if (pointSize <= 0) {
            pointSize = width;
        }

        // pointSize = width;
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage pointImage = new BufferedImage(pointSize, pointSize, BufferedImage.TYPE_INT_ARGB);
        Point point = drawing.point(pointSize / 2, pointSize / 2);
        drawing.drawDirect(pointImage, drawing.feature(point), newRule);
        Graphics2D g2d = finalImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (pointSize > width || pointSize > height) {
            g2d.drawImage(pointImage, 0, 0, width, height, 0, 0, pointSize, pointSize, null);
        } else {
            int x = width / 2 - pointSize / 2;
            int y = height / 2 - pointSize / 2;
            g2d.drawImage(pointImage, x, y, null);
        }
        g2d.dispose();

        return finalImage;
    }

    /**
     * Creates an image from a set of {@link RuleWrapper}s.
     * 
     * @param rulesWrapperList the list of rules wrapper.
     * @param width the image width.
     * @param height the image height.
     * @return the new created {@link BufferedImage}.
     */
    public static BufferedImage polygonRulesWrapperToImage( final List<RuleWrapper> rulesWrapperList, int width, int height ) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for( RuleWrapper rule : rulesWrapperList ) {
            BufferedImage tmpImage = WrapperUtilities.polygonRuleWrapperToImage(rule, width, height);
            g2d.drawImage(tmpImage, 0, 0, null);
        }
        g2d.dispose();
        return image;
    }

    /**
     * Creates an {@link Image} for the given ruleWrapper.
     * 
     * @param ruleWrapper the rule wrapper for which to create the image.
     * @param width the image width.
     * @param height the image height.
     * @return the generated image.
     */
    public static BufferedImage polygonRuleWrapperToImage( final RuleWrapper ruleWrapper, int width, int height ) {
        return polygonRuleToImage(ruleWrapper.getRule(), width, height);
    }

    /**
     * Creates an {@link Image} for the given rule.
     * 
     * @param rule the rule for which to create the image.
     * @param width the image width.
     * @param height the image height.
     * @return the generated image.
     */
    public static BufferedImage polygonRuleToImage( final Rule rule, int width, int height ) {
        DuplicatingStyleVisitor copyStyle = new DuplicatingStyleVisitor();
        rule.accept(copyStyle);
        Rule newRule = (Rule) copyStyle.getCopy();

        Stroke stroke = null;
        Symbolizer[] symbolizers = newRule.getSymbolizers();
        if (symbolizers.length > 0) {
            Symbolizer symbolizer = symbolizers[0];
            if (symbolizer instanceof PolygonSymbolizer) {
                PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
                stroke = SLDs.stroke(polygonSymbolizer);
            }
        }
        int strokeSize = 0;
        if (stroke != null) {
            strokeSize = SLDs.width(stroke);
            if (strokeSize < 0) {
                strokeSize = 0;
                stroke.setWidth(Utilities.ff.literal(strokeSize));
            }
        }

        // pointSize = width;
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Polygon polygon = d.polygon(new int[]{40,30, 60,70, 30,130, 130,130, 130,30});

        int[] xy = new int[]{(int) (height * 0.15), (int) (width * 0.20), (int) (height * 0.4), (int) (width * 0.3),
                (int) (height * 0.85), (int) (width * 0.15), (int) (height * 0.85), (int) (width * 0.85), (int) (height * 0.15),
                (int) (width * 0.85)};
        Polygon polygon = drawing.polygon(xy);
        drawing.drawDirect(finalImage, drawing.feature(polygon), newRule);
        Graphics2D g2d = finalImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(finalImage, 0, 0, null);
        g2d.dispose();

        return finalImage;
    }

    /**
     * Creates an image from a set of {@link RuleWrapper}s.
     * 
     * @param rulesWrapperList the list of rules wrapper.
     * @param width the image width.
     * @param height the image height.
     * @return the new created {@link BufferedImage}.
     */
    public static BufferedImage lineRulesWrapperToImage( final List<RuleWrapper> rulesWrapperList, int width, int height ) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for( RuleWrapper rule : rulesWrapperList ) {
            BufferedImage tmpImage = WrapperUtilities.lineRuleWrapperToImage(rule, width, height);
            g2d.drawImage(tmpImage, 0, 0, null);
        }
        g2d.dispose();
        return image;
    }

    /**
     * Creates an {@link Image} for the given ruleWrapper.
     * 
     * @param ruleWrapper the rule wrapper for which to create the image.
     * @param width the image width.
     * @param height the image height.
     * @return the generated image.
     */
    public static BufferedImage lineRuleWrapperToImage( RuleWrapper ruleWrapper, int width, int height ) {
        return Utilities.lineRuleToImage(ruleWrapper.getRule(), width, height);
    }


    /**
     * Checks if the list of {@link Rule}s supplied contains one with the supplied name.
     * 
     * <p>If the rule is contained it adds an index to the name.
     * 
     * @param rulesWrapper the list of rules to check.
     * @param ruleName the name of the rule to find.
     * @return the new name of the rule.
     */
    public static String checkSameNameRule( List<RuleWrapper> rulesWrapper, String ruleName ) {
        int index = 1;
        String name = ruleName.trim();
        for( int i = 0; i < rulesWrapper.size(); i++ ) {
            RuleWrapper ruleWrapper = rulesWrapper.get(i);
            String tmpName = ruleWrapper.getName();
            if (tmpName == null) {
                continue;
            }

            tmpName = tmpName.trim();
            if (tmpName.equals(name)) {
                // name exists, change the name of the entering
                if (name.endsWith(")")) {
                    name = name.trim().replaceFirst("\\([0-9]+\\)$", "(" + (index++) + ")");
                } else {
                    name = name + " (" + (index++) + ")";
                }
                // start again
                i = 0;
            }
            if (index == 1000) {
                // something odd is going on
                throw new RuntimeException();
            }
        }
        return name;
    }

    /**
     * Checks if the list of {@link FeatureTypeStyleWrapper}s supplied contains one with the supplied name.
     * 
     * <p>If the rule is contained it adds an index to the name.
     * 
     * @param ftsWrapperList the list of featureTypeStyles to check.
     * @param ftsName the name of the featureTypeStyle to find.
     * @return the new name of the featureTypeStyle.
     */
    public static String checkSameNameFeatureTypeStyle( List<FeatureTypeStyleWrapper> ftsWrapperList, String ftsName ) {
        int index = 1;
        String name = ftsName.trim();
        for( int i = 0; i < ftsWrapperList.size(); i++ ) {
            FeatureTypeStyleWrapper ftsWrapper = ftsWrapperList.get(i);
            String tmpName = ftsWrapper.getName();
            if (tmpName == null) {
                continue;
            }

            tmpName = tmpName.trim();
            if (tmpName.equals(name)) {
                // name exists, change the name of the entering
                if (name.endsWith(")")) {
                    name = name.trim().replaceFirst("\\([0-9]+\\)$", "(" + (index++) + ")");
                } else {
                    name = name + " (" + (index++) + ")";
                }
                // start again
                i = 0;
            }
            if (index == 1000) {
                // something odd is going on
                throw new RuntimeException();
            }
        }
        return name;
    }

    /**
     * Checks if the list of {@link StyleWrapper}s supplied contains one with the supplied name.
     * 
     * <p>If the style is contained it adds an index to the name.
     * 
     * @param styles the list of style wrappers to check.
     * @param styleName the name of the style to find.
     * @return the new name of the style.
     */
    public static String checkSameNameStyle( List<StyleWrapper> styles, String styleName ) {
        int index = 1;
        String name = styleName.trim();
        for( int i = 0; i < styles.size(); i++ ) {
            StyleWrapper styleWrapper = styles.get(i);
            String tmpName = styleWrapper.getName();
            if (tmpName == null) {
                continue;
            }

            tmpName = tmpName.trim();
            if (tmpName.equals(name)) {
                // name exists, change the name of the entering
                if (name.endsWith(")")) {
                    name = name.trim().replaceFirst("\\([0-9]+\\)$", "(" + (index++) + ")");
                } else {
                    name = name + " (" + (index++) + ")";
                }
                // start again
                i = 0;
            }
            if (index == 1000) {
                // something odd is going on
                throw new RuntimeException();
            }
        }
        return name;
    }

    /**
     * Generates a style based on a graphic.
     * 
     * @param graphicsPath the graphic.
     * @return the generated style.
     * @throws IOException
     */
    public static StyleWrapper createStyleFromGraphic( File graphicsPath ) throws IOException {
        String name = graphicsPath.getName();
        ExternalGraphic exGraphic = null;
        if (name.toLowerCase().endsWith(".png")) {
            exGraphic = Utilities.sf.createExternalGraphic(graphicsPath.toURI().toURL(), "image/png");
        } else if (name.toLowerCase().endsWith(".svg")) {
            exGraphic = Utilities.sf.createExternalGraphic(graphicsPath.toURI().toURL(), "image/svg+xml");
        } else if (name.toLowerCase().endsWith(".sld")) {
            StyledLayerDescriptor sld = Utilities.readStyle(graphicsPath);
            Style style = SLDs.getDefaultStyle(sld);
            return new StyleWrapper(style);
        }

        if (exGraphic == null) {
            throw new IOException("Style could not be created!");
        }

        Graphic gr = Utilities.sf.createDefaultGraphic();
        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(exGraphic);
        Expression size = Utilities.ff.literal(20);
        gr.setSize(size);

        Rule rule = Utilities.sf.createRule();
        PointSymbolizer pointSymbolizer = Utilities.sf.createPointSymbolizer(gr, null);
        rule.symbolizers().add(pointSymbolizer);

        FeatureTypeStyle featureTypeStyle = Utilities.sf.createFeatureTypeStyle();
        featureTypeStyle.rules().add(rule);

        Style namedStyle = Utilities.sf.createStyle();
        namedStyle.featureTypeStyles().add(featureTypeStyle);
        namedStyle.setName(FilenameUtils.removeExtension(name));

        return new StyleWrapper(namedStyle);
    }


}
