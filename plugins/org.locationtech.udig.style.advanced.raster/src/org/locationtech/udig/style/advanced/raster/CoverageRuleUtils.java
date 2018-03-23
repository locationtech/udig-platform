/*
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.raster;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

import org.eclipse.swt.graphics.RGB;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.renderer.i18n.ErrorKeys;
import org.geotools.renderer.i18n.Errors;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.locationtech.udig.style.advanced.raster.internal.Messages;
import org.opengis.filter.expression.Expression;

/**
 * Utility class to convert from / to CoverageRules to RasterSymbolizer with a ColorMap
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @author Frank Gasdorf
 *
 */
public final class CoverageRuleUtils {

    /**
     * @param list CoverageRule-List to apply, <br><b>NOTE:</b> only active rules are used 
     * @param alpha Alpha Channel as values from 0 to 100 (percent)
     * @return RasterSymbolizer with a ColorMap created from given CoverageRule-List  
     */
    public static RasterSymbolizer createColorMapForCoverageRules(final List<CoverageRule> list, final int alpha) {
        final StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
        final StyleBuilder sB = new StyleBuilder(sf);
        final RasterSymbolizer rasterSym = sf.createRasterSymbolizer();

        ColorMap colorMap = sf.createColorMap();
        colorMap.setType(ColorMap.TYPE_RAMP);

        ColorMapEntry lastColorMapEntry = null;
        
        for( int i = 0; i < list.size(); i++ ) {
            CoverageRule coverageRule = list.get(i);
            if (!coverageRule.isActive()) {
                continue;
            }
            RGB fromColor = coverageRule.getFromColor();
            RGB toColor = coverageRule.getToColor();
            double[] values = coverageRule.getFromToValues();
            double opacity = coverageRule.getOpacity();

            Expression fromColorExpr = sB.colorExpression(new java.awt.Color(fromColor.red, fromColor.green,
                  fromColor.blue, 255));
            Expression toColorExpr = sB.colorExpression(new java.awt.Color(toColor.red, toColor.green,
                    toColor.blue, 255));
            Expression fromExpr = sB.literalExpression(values[0]);
            Expression toExpr = sB.literalExpression(values[1]);
            Expression opacityExpr = sB.literalExpression(opacity);

            ColorMapEntry entry = sf.createColorMapEntry();
            entry.setQuantity(fromExpr);
            entry.setColor(fromColorExpr);
            entry.setOpacity(opacityExpr);
            colorMap.addColorMapEntry(entry);

            lastColorMapEntry = sf.createColorMapEntry();
            lastColorMapEntry.setQuantity(toExpr);
            lastColorMapEntry.setOpacity(opacityExpr);
            lastColorMapEntry.setColor(toColorExpr);
        }
        // add last Entry
        if (lastColorMapEntry != null) {
            colorMap.addColorMapEntry(lastColorMapEntry);
        }
        
        rasterSym.setColorMap(colorMap);

        /*
         * set global transparency for the map
         */
        rasterSym.setOpacity(sB.literalExpression(alpha / 100.0));
        return rasterSym;
    }

    /**
     * @param rasterSymbolizer RasterSymbolizer with ColorMap elements
     * @return CoverageRule-List extracted from RasterSymbolizer ColorMap definitions 
     */
    public static List<CoverageRule> createCoverageRulesForRasterSymbolizer(
            RasterSymbolizer rasterSymbolizer) {
        List<CoverageRule> listOfRules = new ArrayList<CoverageRule>();
        
        ColorMap colorMap = rasterSymbolizer.getColorMap();
        ColorMapEntry[] colorMapEntries = colorMap.getColorMapEntries();
        for( int i = 0; i < colorMapEntries.length - 1; i++ ) {
            double fromQuantity = getQuantity(colorMapEntries[i]);
            java.awt.Color f = getColor(colorMapEntries[i]);
            double fromOpacity = getOpacity(colorMapEntries[i]);

            double toQuantity = getQuantity(colorMapEntries[i + 1]);
            java.awt.Color t = getColor(colorMapEntries[i + 1]);
            // double toOpacity = getOpacity(colorMapEntries[i + 1]);

            RGB fromRGB = new RGB(f.getRed(), f.getGreen(), f.getBlue());
            RGB toRGB = new RGB(t.getRed(), t.getGreen(), t.getBlue());
            CoverageRule rule = new CoverageRule(new double[]{fromQuantity, toQuantity}, fromRGB, toRGB, fromOpacity,
                    true);
            listOfRules.add(rule);
        }
        
        return listOfRules;
    }
    
    /**
     * @param entry
     * @return
     * @throws NumberFormatException
     */
    private static java.awt.Color getColor( ColorMapEntry entry ) throws NumberFormatException {
        final Expression color = entry.getColor();
        final String colorString = (String) color.evaluate(null, String.class);
        return java.awt.Color.decode(colorString);
    }

    /**
     * @param entry
     * @return
     * @throws IllegalArgumentException
     * @throws MissingResourceException
     */
    private static double getOpacity( ColorMapEntry entry ) throws IllegalArgumentException, MissingResourceException {

        Expression opacity = entry.getOpacity();
        Double opacityValue = null;
        if (opacity != null)
            opacityValue = (Double) opacity.evaluate(null, Double.class);
        else
            return 1.0;
        if ((opacityValue.doubleValue() - 1) > 0 || opacityValue.doubleValue() < 0) {
            throw new IllegalArgumentException(Errors.format(ErrorKeys.ILLEGAL_ARGUMENT_$2, Messages.CoverageStyleEditorPage_4, opacityValue));
        }
        return opacityValue.doubleValue();
    }

    /**
     * @param entry
     * @return
     */
    private static double getQuantity( ColorMapEntry entry ) {
        Expression quantity = entry.getQuantity();
        Double quantityString = (Double) quantity.evaluate(null, Double.class);
        double q = quantityString.doubleValue();
        return q;
    }

}
