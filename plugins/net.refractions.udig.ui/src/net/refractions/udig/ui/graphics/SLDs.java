/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 */
package net.refractions.udig.ui.graphics;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.graphics.FontData;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.NameImpl;
import org.geotools.filter.Filters;
import org.geotools.filter.expression.AbstractExpressionVisitor;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Font;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;

/**
 * Utility class for working with Geotools SLD objects.
 * <p>
 * This class assumes a subset of the SLD specification:
 * <ul>
 * <li>Single Rule - matching Filter.INCLUDE
 * <li>Symbolizer lookup by name
 * </ul>
 * </p>
 * <p>
 * When you start to branch out to SLD information that contains
 * multiple rules you will need to modify this class.
 * </p>
 * @author Jody Garnett, Refractions Research.
 * @since 0.7.0
 */
public class SLDs extends SLD {
    private static StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    private static FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);

    public static final double ALIGN_LEFT = 1.0;
    public static final double ALIGN_CENTER = 0.5;
    public static final double ALIGN_RIGHT = 0.0;
    public static final double ALIGN_BOTTOM = 1.0;
    public static final double ALIGN_MIDDLE = 0.5;
    public static final double ALIGN_TOP = 0.0;

    public static int size( Graphic graphic ) {
        if (graphic == null) {
            return NOTFOUND;
        }
        return Filters.asInt(graphic.getSize());
    }

    public static Color polyFill(PolygonSymbolizer symbolizer) {
        if (symbolizer == null) {
            return null;
        }

        Fill fill = symbolizer.getFill();

        if (fill == null) {
            return null;
        }

        Expression color = fill.getColor();
        return color(color);
    }
    public static Color color(Expression expr) {
        if (expr == null) {
            return null;
        }
        try {
            return expr.evaluate(null, Color.class );
        }
        catch( Throwable t ){
            class ColorVisitor implements ExpressionVisitor {
                Color found;
                public Object visit( Literal expr, Object data ) {
                    if( found != null ) return null;
                    try {
                        Color color = expr.evaluate(expr, Color.class );
                        if( color != null ){
                            found = color;
                        }
                    }
                    catch (Throwable t){
                        // not a color
                    }
                    return data;
                }
                public Object visit( NilExpression arg0, Object data ) {
                    return data;
                }
                public Object visit( Add arg0, Object data ) {
                    return data;
                }
                public Object visit( Divide arg0, Object data ) {
                    return null;
                }
                public Object visit( Function function, Object data ) {
                    for( Expression param : function.getParameters() ){
                        param.accept(this, data ); 
                    }
                    return data;
                }
                public Object visit( Multiply arg0, Object data ) {
                    return data;
                }
                public Object visit( PropertyName arg0, Object data ) {
                    return data;
                }
                public Object visit( Subtract arg0, Object data ) {
                    return data;
                }
            }
            ColorVisitor search = new ColorVisitor();
            expr.accept(search, null );
            
            return search.found;            
        }
    }
    
    /**
     * Grabs the font from the first TextSymbolizer.
     * <p>
     * If you are using something fun like symbols you 
     * will need to do your own thing.
     * </p>
     * @param symbolizer Text symbolizer information.
     * @return FontData[] of the font's fill, or null if unavailable.
     */
    public static FontData[] textFont( TextSymbolizer symbolizer ) {

        Font font = font(symbolizer);
        if (font == null)
            return null;
        // FIXME: font style isn't being set properly here...seems screwy so leaving till later
        // String fontStyle = font[0].getFontStyle().toString();
        // if(fontStyle == null) return null;
        // else if(fontStyle.equalsIgnoreCase("italic"))

        FontData[] tempFD = new FontData[1];
        Expression fontFamilyExpression = font.getFamily().get(0);
        Expression sizeExpression = font.getSize();
        if (sizeExpression == null || fontFamilyExpression == null)
            return null;

        Double size = sizeExpression.evaluate(null, Double.class);

        try {
            String fontFamily = fontFamilyExpression.evaluate(null, String.class);
            tempFD[0] = new FontData(fontFamily, size.intValue(), 1);
        } catch (NullPointerException ignore) {
            return null;
        }
        if (tempFD[0] != null)
            return tempFD;
        return null;
    }

    /**
     * Retrieves all colour names defined in a rule
     * @param rule the rule
     * @return an array of unique colour names
     */
    public static String[] colors( Rule rule ) {
        Set<String> colorSet = new HashSet<String>();

        Color color = null;
        for( Symbolizer sym : rule.symbolizers() ) {
            if (sym instanceof PolygonSymbolizer) {
                PolygonSymbolizer symb = (PolygonSymbolizer) sym;
                color = polyFill(symb);

            } else if (sym instanceof LineSymbolizer) {
                LineSymbolizer symb = (LineSymbolizer) sym;
                color = color(symb);

            } else if (sym instanceof PointSymbolizer) {
                PointSymbolizer symb = (PointSymbolizer) sym;
                color = pointFillWithAlpha(symb);
            }

            if (color != null) {
                colorSet.add(SLD.colorToHex(color));
            }
        }

        if (colorSet.size() > 0) {
            return colorSet.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    /**
     * Extracts the fill color with a given opacity from the {@link PointSymbolizer}.
     * 
     * @param symbolizer the point symbolizer from which to get the color.
     * @return the {@link Color} with transparency if available. Returns null if no color is available.
     */
    public static Color pointFillWithAlpha( PointSymbolizer symbolizer ) {
        if (symbolizer == null) {
            return null;
        }

        Graphic graphic = symbolizer.getGraphic();
        if (graphic == null) {
            return null;
        }

        for( GraphicalSymbol gs : graphic.graphicalSymbols() ) {
            if ((gs != null) && (gs instanceof Mark)) {
                Mark mark = (Mark) gs;
                Fill fill = mark.getFill();
                if (fill != null) {
                    Color colour = color(fill.getColor());
                    if (colour == null) {
                        return null;
                    }
                    Expression opacity = fill.getOpacity();
                    if (opacity == null)
                        opacity = ff.literal(1.0);
                    float alpha = (float) Filters.asDouble(opacity);
                    colour = new Color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, alpha);
                    if (colour != null) {
                        return colour;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Extracts the stroke color with a given opacity from the {@link PointSymbolizer}.
     * 
     * @param symbolizer the point symbolizer from which to get the color.
     * @return the {@link Color} with transparency if available. Returns null if no color is available.
     */
    public static Color pointStrokeColorWithAlpha( PointSymbolizer symbolizer ) {
        if (symbolizer == null) {
            return null;
        }

        Graphic graphic = symbolizer.getGraphic();
        if (graphic == null) {
            return null;
        }

        for( GraphicalSymbol gs : graphic.graphicalSymbols() ) {
            if ((gs != null) && (gs instanceof Mark)) {
                Mark mark = (Mark) gs;
                Stroke stroke = mark.getStroke();
                if (stroke != null) {
                    Color colour = color(stroke);
                    if (colour == null) {
                        return null;
                    }
                    Expression opacity = stroke.getOpacity();
                    if (opacity == null)
                        opacity = ff.literal(1.0);
                    float alpha = (float) Filters.asDouble(opacity);
                    colour = new Color(colour.getRed() / 255f, colour.getGreen() / 255f, colour.getBlue() / 255f, alpha);
                    if (colour != null) {
                        return colour;
                    }
                }
            }
        }

        return null;
    }

    public static Font font( TextSymbolizer symbolizer ) {
        if (symbolizer == null)
            return null;
        Font font = symbolizer.getFont();
        return font;
    }

    public static Style getDefaultStyle( StyledLayerDescriptor sld ) {
        Style[] styles = styles(sld);
        for( int i = 0; i < styles.length; i++ ) {
            Style style = styles[i];
            List<FeatureTypeStyle> ftStyles = style.featureTypeStyles();
            genericizeftStyles(ftStyles);
            if (style.isDefault()) {
                return style;
            }
        }
        // no default, so just grab the first one
        return styles[0];
    }

    /**
     * Converts the type name of all FeatureTypeStyles to Feature so that the all apply to any feature type.  This is admittedly dangerous
     * but is extremely useful because it means that the style can be used with any feature type.
     *
     * @param ftStyles
     */
    private static void genericizeftStyles( List<FeatureTypeStyle> ftStyles ) {
        for( FeatureTypeStyle featureTypeStyle : ftStyles ) {
            featureTypeStyle.featureTypeNames().clear();
            featureTypeStyle.featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
        }
    }

    public static boolean isSemanticTypeMatch( FeatureTypeStyle fts, String regex ) {
        String[] identifiers = fts.getSemanticTypeIdentifiers();
        for( int i = 0; i < identifiers.length; i++ ) {
            if (identifiers[i].matches(regex))
                return true;
        }
        return false;
    }

    /**
     * Returns the min scale of the default rule, or 0 if none is set 
     */
    public static double minScale( FeatureTypeStyle fts ) {
        if (fts == null || fts.getRules().length == 0)
            return 0.0;

        Rule r = fts.getRules()[0];
        return r.getMinScaleDenominator();
    }

    /**
     * Returns the max scale of the default rule, or {@linkplain Double#NaN} if none is set 
     */
    public static double maxScale( FeatureTypeStyle fts ) {
        if (fts == null || fts.getRules().length == 0)
            return Double.NaN;

        Rule r = fts.getRules()[0];
        return r.getMaxScaleDenominator();
    }

    /**
     * gets the first FeatureTypeStyle
     */
    public static FeatureTypeStyle getFeatureTypeStyle( Style s ) {
        FeatureTypeStyle[] fts = s.getFeatureTypeStyles();
        if (fts.length > 0) {
            return fts[0];
        }
        return null;
    }

    /**
     * Find the first rule which contains a rastersymbolizer, and return it
     *
     * @param s A style to search in
     * @return a rule, or null if no raster symbolizers are found.
     */
    public static Rule getRasterSymbolizerRule( Style s ) {
        FeatureTypeStyle[] fts = s.getFeatureTypeStyles();
        for( int i = 0; i < fts.length; i++ ) {
            FeatureTypeStyle featureTypeStyle = fts[i];
            Rule[] rules = featureTypeStyle.getRules();
            for( int j = 0; j < rules.length; j++ ) {
                Rule rule = rules[j];
                Symbolizer[] symbolizers = rule.getSymbolizers();
                for( int k = 0; k < symbolizers.length; k++ ) {
                    Symbolizer symbolizer = symbolizers[k];
                    if (symbolizer instanceof RasterSymbolizer) {
                        return rule;
                    }
                }

            }
        }
        return null;
    }

    /**
     * The type name that can be used in an SLD in the featuretypestyle that matches all feature types.
     */
    public static final String GENERIC_FEATURE_TYPENAME = "Feature";

    // TODO: port these methods to the geotools parent class

}
