/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.internal.render.impl;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.Messages;
import org.locationtech.udig.project.internal.ProjectPlugin;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.style.SemanticType;
import org.geotools.ows.wms.StyleImpl;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.feature.NameImpl;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;

/**
 * A utility class for obtaining precanned or random styles
 * 
 * @author Jesse Eichar
 * @version $Revision: 1.9 $
 */
public class Styling {

    /** A map of default styles. */
    public static final Map STYLES;
    static {
        Map<String, Integer> styles = new HashMap<String, Integer>();
        styles.put(Messages.Styling_blueLine, Integer.valueOf(0)); 
        styles.put(Messages.Styling_greenLine, Integer.valueOf(1)); 
        styles.put(Messages.Styling_blackLine, Integer.valueOf(2)); 
        styles.put(Messages.Styling_blackLine_blueFill, Integer.valueOf(3)); 
        styles.put(Messages.Styling_blackLine_greenFill, Integer.valueOf(4)); 
        styles.put(Messages.Styling_blackLine_semitransparentBlueFill, Integer.valueOf(5)); 
        styles.put(Messages.Styling_blackLine_semitransparentYellowFill, Integer.valueOf(6)); 
        styles.put(Messages.Styling_pointStyle, Integer.valueOf(7)); 
        STYLES = Collections.unmodifiableMap(styles);
    }

    /**
     * Returns a Style object give a style name and a feature typename.
     * 
     * @param styleName The name of the style to creates. The list of name can be obtained from
     *        {@link #getStyleNames(Layer)}
     * @param typeName the TypeName of the feature type the style will style.
     * @return a Style object give a style name and a feature typename.
     */
    public static Style getStyle( String styleName, String typeName ) {
        return getStyle(((Integer) STYLES.get(styleName)).intValue(), typeName);
    }

    /**
     * Returns a Style object given a value from {@link #STYLES}and the feature typename.
     * 
     * @param index a value from {@link #STYLES}
     * @param typeName the TypeName of the feature type the style will style.
     * @return a Style object given a value from {@link #STYLES}and the feature typename
     */
    public static Style getStyle( int index, String typeName ) {
        switch( index ) {
        case 0:
            return createLineStyle(typeName, Color.BLUE);
        case 1:
            return createLineStyle(typeName, Color.GREEN);
        case 2:
            return createLineStyle(typeName, Color.BLACK);
        case 3:
            return createPolyStyle(typeName, Color.BLACK, Color.BLUE);
        case 4:
            return createPolyStyle(typeName, Color.BLACK, Color.GREEN);
        case 5:
            return createPolyStyle(typeName, Color.BLACK, new Color(0, 0, 255, 127));
        case 6:
            return createPolyStyle(typeName, Color.BLACK, new Color(127, 127, 127, 127));
        case 7:
            return createPointStyle(typeName);

        default:
            return createLineStyle(typeName, Color.BLUE);
        }
    }

    /**
     * Returns a simple style to use in default cases.
     * 
     * @param typeName the TypeName of the feature type the style will style.
     * @return a simple style to use in default cases.
     */
    public static Style createLineStyle( String typeName ) {
        return createLineStyle(typeName, Color.blue);
    }

    /**
     * Returns a simple style to use in default cases.
     * 
     * @param typeName the TypeName of the feature type the style will style.
     * @param color the color of the style
     * @return a simple style to use in default cases.
     */
    public static Style createLineStyle( String typeName, Color color ) {
        StyleBuilder sb = new StyleBuilder();
        Style linestyle = sb.createStyle();

        LineSymbolizer line = sb.createLineSymbolizer(color);
        linestyle.featureTypeStyles().add(sb.createFeatureTypeStyle(line));

        FeatureTypeStyle fts = linestyle.featureTypeStyles().get(0);
        fts.setName(Messages.Styling_name); //tag as simple 
        fts.featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
        
        fts.semanticTypeIdentifiers().clear();
        fts.semanticTypeIdentifiers().add(new SemanticType("generic:geometry")); //$NON-NLS-1$ 
        fts.semanticTypeIdentifiers().add(new SemanticType("simple")); //$NON-NLS-1$ 
        
        return linestyle;
    }

    /**
     * Returns a simple style to use in default cases.
     * 
     * @param typeName the TypeName of the feature type the style will style.
     * @return a simple style to use in default cases.
     */
    public static Style createPolyStyle( String typeName ) {
        return createPolyStyle(typeName, Color.BLACK, Color.GREEN);
    }

    /**
     * Returns a simple style to use in default cases.
     * 
     * @param typeName the TypeName of the feature type the style will style.
     * @param line the color of the outlines.
     * @param fill The color of the fills
     * @return a simple style to use in default cases.
     */
    public static Style createPolyStyle( String typeName, Color line, Color fill ) {
        StyleBuilder sb = new StyleBuilder();
        Style polystyle = sb.createStyle();

        PolygonSymbolizer poly = sb.createPolygonSymbolizer(fill, line, 1);
        polystyle.featureTypeStyles().add(sb.createFeatureTypeStyle(poly));

        polystyle.featureTypeStyles().get(0).featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
        return polystyle;
    }

    /**
     * Returns a simple style to use in default cases.
     * 
     * @param typeName the TypeName of the feature type the style will style.
     * @return a simple style to use in default cases.
     */
    public static Style createPointStyle( String typeName ) {
        StyleBuilder sb = new StyleBuilder();
        Style pointstyle = sb.createStyle();
        PointSymbolizer point = sb.createPointSymbolizer(sb.createGraphic());

        pointstyle.featureTypeStyles().add(sb.createFeatureTypeStyle(point));
        pointstyle.featureTypeStyles().get(0).featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME) );

        return pointstyle;
    }

    /**
     * Returns a simple style to use in default cases.
     * 
     * @param typeName the TypeName of the feature type the style will style.
     * @return as simple style to use in default cases.
     */
    public static Style createRasterStyle( String typeName ) {
        StyleBuilder sb = new StyleBuilder();
        Style rasterstyle = sb.createStyle();
        RasterSymbolizer raster = sb.createRasterSymbolizer();

        rasterstyle.featureTypeStyles().add(sb.createFeatureTypeStyle(raster));
        rasterstyle.featureTypeStyles().get(0).featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
        return rasterstyle;
    }

    /**
     * Returns a list of style names that can be used on the given layer.
     * 
     * @param currentLayer The layer to finds styles for.
     * @return a list of style names that can be used on the given layer.
     */
    @SuppressWarnings("unchecked")
    public static Collection getStyleNames( Layer currentLayer ) {
        // URI id=currentLayer.getID();
        // if( id.containsKey(WMSRegistryEntry.GET_CAPABILITIES_URL) ){
        if (currentLayer.isType(WebMapServer.class)) { // checking for wms

            List<String> l = new LinkedList<String>();
            try {
            	for (Iterator<StyleImpl> iterator = currentLayer.getResource(org.geotools.ows.wms.Layer.class, null).getStyles().iterator(); iterator.hasNext();) {
            		StyleImpl style = (StyleImpl) iterator.next();
            		l.add(style.getName());
				}
            } catch (IOException e) {
                ProjectPlugin.log(null, e);
            }
            l.add(Messages.Styling_default); 
            return l;
        }

        return STYLES.keySet();
    }
}
