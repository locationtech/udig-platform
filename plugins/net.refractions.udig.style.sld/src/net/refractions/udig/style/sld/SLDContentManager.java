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
package net.refractions.udig.style.sld;

import java.awt.Color;

import net.refractions.udig.ui.graphics.SLDs;

import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;

/**
 * A context sensitive SLD content editor.
 * <p>
 * This class is intended to be used by a StyleConfigurator providing
 * SLD style content.
 * <p>
 * An SLD style object can contain any number of FeatureTypeStyle objects, which can in turn contain
 * any number of Rule objects, which can in turn contain any number of Symbolizer objects.
 * </p>
 * <p>
 * Concepts:
 * <ul>
 * <li>notion of a default FeatureTypeStyle (with a single rule)
 * <li>notion of a default Rule
 * </ul>
 * This allows Configurators who simply want to add a new symbolizer
 * to the style to forgo the creation and setup of the style hierarchy.
 * </p>
 *
 * <p>
 * This class also provides the ability to look up symbolizers by class
 * against the "default rule".
 * This allows configurators interested in a particular symbolizer
 * to 'track' the instance of it as the default rule is changed.
 * </p>
 * <h3>Instructions for Subclassing</h3>
 * <p>
 * This base implementation works against the "first" rule in the Style.
 * When providing a SLDConfigurator specific subclass (say for themeing)
 * you may wish to connect the "defaultRule" up to the current rule.
 * </p>
 * <p>
 * You may also supply context sensitive overrrides for color, simply
 * override the methods such as color( LineSymbolizer line ) to return
 * the correct default when a line is null.
 * </p>
 *
 * @author Justin Deoliveira
 * @since 0.9
 */
public class SLDContentManager {

    /** the actual SLD style * */
    private Style style;

    /** the style builder * */
    private StyleBuilder styleBuilder;

    /**
     * Creates the SLD content manager.
     */
    public SLDContentManager() {
    }

    /**
     * Creates the SLD content manager.
     *
     * @param styleBuilder The builder object used to create style content.
     * @param style The SLD style itself.
     */
    public SLDContentManager( StyleBuilder styleBuilder, Style style ) {
        init(styleBuilder, style);
        // styleBuilder = new StyleBuilder(StyleFactory.createStyleFactory());
        // style = styleBuilder.createStyle();
    }

    /**
     * Initializes the content manager with a new style.
     *
     * @param styleBuilder The builder object used to create style content.
     * @param style The SLD style itself.
     */
    public void init( StyleBuilder styleBuilder, Style style ) {
        this.styleBuilder = styleBuilder;
        this.style = style;
    }

    /**
     * @return Returns the SLD style.
     */
    public Style getStyle() {
        return style;
    }

    /**
     * @return Returns the styleBuilder.
     */
    public StyleBuilder getStyleBuilder() {
        return styleBuilder;
    }

    /**
     * Returns the the first feature type style for the SLD style. The feature type style is created
     * if it does not exist.
     *
     * @return The default (first) feature type style.
     */
    public FeatureTypeStyle getDefaultFeatureTypeStyle() {
        FeatureTypeStyle[] styles = style.getFeatureTypeStyles();
        if (styles == null || styles.length == 0 || styles[0].getRules().length == 0) {
            // create a feature type style
            return createFeatureTypeStyle("default"); //$NON-NLS-1$
        }

        return styles[0];
    }

    /**
     * Returns a feature type style with specific name, or null if no such feature type style
     * exists.
     *
     * @param name The name of the feature type style.
     * @return the feature type style identified by name, or null.
     */
    public FeatureTypeStyle getFeatureTypeStyle( String name ) {
        FeatureTypeStyle[] styles = style.getFeatureTypeStyles();
        if (styles == null)
            return null;

        for( int i = 0; i < styles.length; i++ ) {
            if (styles[i].getName().equals(name))
                return styles[i];
        }

        return null;
    }

    /**
     * @return the first rule in the default feature type style.
     */
    public Rule getDefaultRule() {
        FeatureTypeStyle ftStyle = getDefaultFeatureTypeStyle();
        if (ftStyle.getRules() == null || ftStyle.getRules().length == 0) {
            // create an empty rule
            Rule rule = createRule(ftStyle);
            return rule;
        }

        return ftStyle.getRules()[0];
    }

    /**
     * Creates a new feature type style for the object, and adds it to the style.
     *
     * @param name The name of feature type style.
     * @return The newly created feature type style.
     */
    public FeatureTypeStyle createFeatureTypeStyle( String name ) {
        FeatureTypeStyle ftStyle = styleBuilder.createFeatureTypeStyle(name, new Rule[]{});
        style.addFeatureTypeStyle(ftStyle);
        return ftStyle;
    }

    /**
     * Creates an empty rule. This method does not associate the rule with the SLD style. The rule
     * is created with an empty symbolizer list.
     *
     * @return The newly created rule.
     */
    public Rule createRule() {
        return styleBuilder.createRule(new Symbolizer[]{});
    }

    /**
     * Creates a new rule for a specific feature type style. The rule is created with an empty
     * symbolizer list.
     *
     * @param ftStyle The feature type style for the rule.
     * @return The newly created rule.
     */
    public Rule createRule( FeatureTypeStyle ftStyle ) {
        Rule rule = createRule();
        ftStyle.addRule(rule);

        return rule;
    }

    /**
     * Returns the first symbolizer of a particular class for a rule or none if no such symbolizer
     * exists.
     *
     * @param rule The rule containing the symbolizer to be returned.
     * @param theClass The typed class of the symbolizer.
     * @return The symbolizer of type T, or null if none exists.
     */
    public <T> T getSymbolizer( Rule rule, Class<T> theClass ) {
        Symbolizer[] symbolizers = rule.getSymbolizers();
        if (symbolizers == null || theClass == null)
            return null;

        for( int i = 0; i < symbolizers.length; i++ ) {
            if (symbolizers[i] == null)
                continue;
            if (theClass.isAssignableFrom(symbolizers[i].getClass()))
                return theClass.cast( symbolizers[i] );
        }

        return null;
    }

    /**
     * Returns the first symbolizer of a particular class from the default rule or none if no such
     * symbolizer exists.
     *
     * @param theClass The typed class of the symbolizer.
     * @return The symbolizer of type T, or null if none exists.
     */
    public <T extends Symbolizer> T getSymbolizer( Class<T> theClass ) {
        return getSymbolizer(getDefaultRule(), theClass);
    }

    /**
     * Adds a symbolizer to the default rule.
     *
     * @param symbolizer The symbolizer to add.
     */
    public void addSymbolizer( Symbolizer symbolizer ) {
        addSymbolizer(getDefaultRule(), symbolizer);
    }

    /**
     * Adds a symbolizer to a rule.
     *
     * @param rule The rule.
     * @param symbolizer The symbolizer
     */
    public void addSymbolizer( Rule rule, Symbolizer symbolizer ) {
        Symbolizer[] syms = rule.getSymbolizers();
        if (syms == null) {
            syms = new Symbolizer[]{symbolizer};
        } else {
            Symbolizer[] newSyms = new Symbolizer[syms.length + 1];
            System.arraycopy(syms, 0, newSyms, 0, syms.length);
            newSyms[syms.length] = symbolizer;
            syms = newSyms;
        }

        rule.setSymbolizers(syms);
    }

    /**
     * Adds a symbolizer by class.
     *
     * @param theClass The class of the symbolizer.
     */
    public void addSymbolizer( Class<Symbolizer> theClass ) {
        addSymbolizer(SLD.createDefault(theClass));
        // if (PointSymbolizer.class.isAssignableFrom(theClass)) {
        // addSymbolizer(SLDContent.createPointSymbolizer());
        // }
        // else if (LineSymbolizer.class.isAssignableFrom(theClass)) {
        // addSymbolizer(SLDContent.createLineSymbolizer());
        // }
        // else if (PolygonSymbolizer.class.isAssignableFrom(theClass)) {
        // addSymbolizer(SLDContent.createPolygonSymbolizer());
        // }
        // else if (TextSymbolizer.class.isAssignableFrom(theClass)) {
        // addSymbolizer(SLDContent.createTextSymbolizer());
        // }
        // else if (RasterSymbolizer.class.isAssignableFrom(theClass)) {
        // addSymbolizer(SLDContent.createRasterSymbolizer());
        // }
    }

    public void removeSymbolizer( Symbolizer symbolizer ) {
        if (symbolizer == null)
            return;

        Symbolizer[] symbolizers = getDefaultRule().getSymbolizers();
        int i = 0;
        for( ; i < symbolizers.length; i++ ) {
            if (symbolizers[i].equals(symbolizer))
                break;
        }

        if (i < symbolizers.length) {
            Symbolizer[] newSymbolizers = new Symbolizer[symbolizers.length - 1];
            System.arraycopy(symbolizers, 0, newSymbolizers, 0, i);
            System.arraycopy(symbolizers, i + 1, newSymbolizers, i, symbolizers.length - (i + 1));
            getDefaultRule().setSymbolizers(newSymbolizers);
        }
    }

    /**
     * Removes the first symbolizer of the specified class from the default rule.
     *
     * @param theClass The class of the symbolizer.
     */
    public void removeSymbolizer( Class<Symbolizer> theClass ) {
        if (theClass == null)
            return;

        Symbolizer[] symbolizers = getDefaultRule().getSymbolizers();
        int i = 0;
        for( ; i < symbolizers.length; i++ ) {
            if (theClass.isAssignableFrom(symbolizers[i].getClass()))
                break;
        }

        if (i < symbolizers.length) {
            Symbolizer[] newSymbolizers = new Symbolizer[symbolizers.length - 1];
            System.arraycopy(symbolizers, 0, newSymbolizers, 0, i);
            System.arraycopy(symbolizers, i + 1, newSymbolizers, i, symbolizers.length - (i + 1));
            getDefaultRule().setSymbolizers(newSymbolizers);
        }
    }

    //
    // Context (ie "default" rule sensitive access)
    //
    public Color color( LineSymbolizer line ) {
        return SLDs.color( line );
    }
    public double width( LineSymbolizer line ) {
        return SLDs.lineWidth( line );
    }
    public double opacity( LineSymbolizer line ) {
        return SLDs.lineOpacity( line );
    }
}
