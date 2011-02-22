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

import org.eclipse.swt.graphics.FontData;
import org.geotools.filter.Expression;
import org.geotools.filter.Filters;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Font;
import org.geotools.styling.PointPlacement;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.TextSymbolizer;

/**
 * Utility class for working with Geotools SLD objects.
 * <p>
 * This class assumes a subset of the SLD specification:
 * <ul>
 * <li>Single Rule - matching Filter.NONE
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
public final class SLDs extends SLD {

    public static final double ALIGN_LEFT = 1.0;
    public static final double ALIGN_CENTER = 0.5;
    public static final double ALIGN_RIGHT = 0.0;
    public static final double ALIGN_BOTTOM = 1.0;
    public static final double ALIGN_MIDDLE = 0.5;
    public static final double ALIGN_TOP = 0.0;

    /**
     * Grabs the font from the first TextSymbolizer.
     * <p>
     * If you are using something fun like symbols you
     * will need to do your own thing.
     * </p>
     * @param symbolizer Text symbolizer information.
     * @return FontData[] of the font's fill, or null if unavailable.
     */
    public static FontData[] textFont( TextSymbolizer symbolizer ){

        Font font = font(symbolizer);
        if(font == null) return null;
        //FIXME: font style isn't being set properly here...seems screwy so leaving till later
//        String fontStyle = font[0].getFontStyle().toString();
//        if(fontStyle == null) return null;
//        else if(fontStyle.equalsIgnoreCase("italic"))

        FontData[] tempFD = new FontData[1];
        Expression fontFamily = font.getFontFamily();
        if(font.getFontSize() == null || fontFamily == null) return null;

        Number size = (Number)Filters.asType(font.getFontSize(), Number.class);

        Object asType = Filters.asType(fontFamily, String.class);
        tempFD[0] = new FontData((String)asType, size.intValue(), 1);

        if( tempFD[0] != null ) return tempFD;
        return null;
    }

    public static Font font(TextSymbolizer symbolizer) {
        if(symbolizer == null) return null;
        Font[] font = symbolizer.getFonts();
        if(font == null || font[0] == null ) return null;
        return font[0];
    }

    public static Style getDefaultStyle(StyledLayerDescriptor sld) {
        Style[] styles = styles(sld);
        for (int i = 0; i < styles.length; i++) {
            Style style = styles[i];
            FeatureTypeStyle[] ftStyles = style.getFeatureTypeStyles();
            genericizeftStyles(ftStyles);
            if (style.isDefault()) {
                return style;
            }
        }
        //no default, so just grab the first one
        return styles[0];
    }

    /**
     * Converts the type name of all FeatureTypeStyles to Feature so that the all apply to any feature type.  This is admittedly dangerous
     * but is extremely useful because it means that the style can be used with any feature type.
     *
     * @param ftStyles
     */
    private static void genericizeftStyles( FeatureTypeStyle[] ftStyles ) {
        for( FeatureTypeStyle featureTypeStyle : ftStyles ) {
            featureTypeStyle.setFeatureTypeName(SLDs.GENERIC_FEATURE_TYPENAME);
        }
    }

    public static boolean isSemanticTypeMatch(FeatureTypeStyle fts, String regex) {
        String[] identifiers = fts.getSemanticTypeIdentifiers();
        for (int i = 0; i < identifiers.length; i++) {
            if (identifiers[i].matches(regex)) return true;
        }
        return false;
    }

    /**
     * Returns the min scale of the default rule, or 0 if none is set
     */
    public static double minScale(FeatureTypeStyle fts) {
    	if(fts == null || fts.getRules().length == 0)
    		return 0.0;

    	Rule r = fts.getRules()[0];
    	return r.getMinScaleDenominator();
    }

    /**
     * Returns the max scale of the default rule, or {@linkplain Double#NaN} if none is set
     */
    public static double maxScale(FeatureTypeStyle fts) {
    	if(fts == null || fts.getRules().length == 0)
    		return Double.NaN;

    	Rule r = fts.getRules()[0];
    	return r.getMaxScaleDenominator();
    }

    public static PointPlacement getPlacement(double horizAlign, double vertAlign, double rotation) {
        return builder.createPointPlacement(horizAlign, vertAlign, rotation);
    }

    /**
     * The type name that can be used in an SLD in the featuretypestyle that matches all feature types.
     */
    public static final String GENERIC_FEATURE_TYPENAME = "Feature";

    //TODO: port these methods to the geotools parent class

 }
