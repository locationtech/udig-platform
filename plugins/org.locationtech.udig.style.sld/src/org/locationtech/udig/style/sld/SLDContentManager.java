/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.style.sld;

import java.awt.Color;
import java.util.List;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.Symbolizer;
import org.geotools.util.Utilities;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.style.StyleFactory;

/**
 * A helpful class for sifting through an SLD Style object while implementing an
 * editor. Consider it a wrapper that allows you to easily edit (it will completely write
 * any and all changes directly to the wrapped style).
 * <p>
 * Used by a StyleConfigurator or IStyledEditorPage working with SLD style
 * content on the blackboard.
 * <p>
 * A Style object can contain any number of FeatureTypeStyle objects, which can in turn contain
 * any number of Rule objects, which can in turn contain any number of Symbolizer objects.
 * </p>
 * <p>
 * The SLDContentManager provides support for the following concepts:
 * <ul>
 * <li>notion of a default FeatureTypeStyle (with a single rule)
 * <li>notion of a default Rule
 * </ul>
 * This allows IStyleConfigurator implementation who simply want to add a new symbolizer
 * to the style to forgo the creation and setup of the style hierarchy.
 * </p>
 * <p>
 * To help keep everything straight here are a couple of naming conventions:
 * <ul>
 * <li>to access something:
 *    <ul>
 *    <li>access methods will drill down and provide the requested object or null if not available</li>
 *    <li>get methods will drill down and return the requested object (creating it if needed)</li>
 *    </ul>
 * </li>
 * <li>Create methods exist and will create the requested object and add it to the style in one go
 *     (if you want to just create something use the StyleBuilder; or directly use the StyleFactory)
 * </ul>
 * <p>
 * This class also provides the ability to look up symbolizers by class
 * against the "default" rule (ie the rule with the name "default").
 * This allows an SLDConfigurator interested in a particular symbolizer
 * to 'track' the instance of it as the default rule is changed.
 * </p>
 * @author Justin Deoliveira
 * @since 0.9
 */
public class SLDContentManager {
    /** The actual Style style */
    private Style style;

    /**
     * The actual FeatureTypeStyle we are focused on right now;
     * usually this is the last one created or searched for.
     */
    private FeatureTypeStyle featureTypeStyle;
    
    /**
     * The actual rule we are focused on right now; usualy
     * the last one added or searched for.
     */
    private Rule rule;
    
    /**
     * The actual symbolizer we are focused on right now; usaly
     * the last one added or searched for.
     */
    private Symbolizer symbolizer;
    
    /**
     * The style builder - basically a wrapper around StyleFactory
     * that knows default values from the SLD specification
     */
    private StyleBuilder styleBuilder;

    /**
     * Creates an empty SLDContentManager that is set up around a default style.
     */
    public SLDContentManager() {
        this( new StyleBuilder() );
    }
    /**
     * Creates an empty SLDContentManager that is setup around a default style
     * @param styleBuilder StyleBuilder used to create the default style
     */
    public SLDContentManager( StyleBuilder styleBuilder ) {
        init( styleBuilder, styleBuilder.createStyle() );
    }
    /**
     * Creates the SLD content manager.
     * 
     * @param styleBuilder The builder object used to create style content.
     * @param style The SLD style itself.
     */
    public SLDContentManager( StyleBuilder styleBuilder, Style style ) {
        init(styleBuilder, style);
    }

    public void init( Style style ){
        init( new StyleBuilder(), style );
    }
    
    /**
     * Initializes the content manager with a new style.
     * 
     * @param styleBuilder The builder object used to create style content.
     * @param style The SLD style itself.
     */
    public void init( StyleBuilder styleBuilder, Style style ) {
        if( styleBuilder == null ){
            throw new NullPointerException("StyleBuilder required");
        }
        this.styleBuilder = styleBuilder;
        
        if( style == null ){
            throw new NullPointerException("Style required");   
        }
        this.style = style;
        this.featureTypeStyle = null;
        this.rule = null;
        this.symbolizer = null;
    }

    /**
     * @return Returns the wrapped Style object
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
    public StyleFactory getStyleFactory(){
        if( styleBuilder.getStyleFactory() instanceof StyleFactory){
            return (StyleFactory) styleBuilder.getStyleFactory();
        }
        else {
            return (StyleFactory) CommonFactoryFinder.getStyleFactory(null);
        }
    }
    //
    // Default will check for a style or rule with the name "default"
    // or will return the first thing in the list - if there is nothing
    // in the list it will just sit down and make one!
    //
    /**
     * Returns the the first feature type style for the SLD style.
     * <p>
     * Please note a feature type style is created if it does not exist.
     * 
     * @return The default (ie first) feature type style.
     */
    public FeatureTypeStyle getDefaultFeatureTypeStyle() {
        List<FeatureTypeStyle> featureTypeStyles = style.featureTypeStyles();
        if( featureTypeStyles == null || featureTypeStyles.isEmpty() ){
            // create a feature type style
            return createFeatureTypeStyle("default"); //$NON-NLS-1$
        }
        return featureTypeStyles.get(0);
    }

    /**
     * Returns a feature type style with specific name, or null if no such feature type style
     * exists.
     * 
     * @param name The name of the feature type style.
     * @return the feature type style identified by name, or null.
     */
    public FeatureTypeStyle getFeatureTypeStyle( String name ) {
        List<FeatureTypeStyle> featureTypeStyles = style.featureTypeStyles();
        if (featureTypeStyles == null ){
            return null;
        }
        for( FeatureTypeStyle check : featureTypeStyles ){
            if( Utilities.equals(check.getName(), name)){
                return check;
            }
        }
        return null; // not found!
    }    
    public FeatureTypeStyle featureTypeStyle( String name ){
        FeatureTypeStyle featureTypeStyle = getFeatureTypeStyle( name );
        if( featureTypeStyle != null ) {
            return featureTypeStyle;
        }
        return createFeatureTypeStyle( name );
    }
    

    /**
     * @return the first rule in the default feature type style.
     */
    public Rule getDefaultRule() {
        FeatureTypeStyle ftStyle = getDefaultFeatureTypeStyle();
        if (ftStyle.rules() == null || ftStyle.rules().isEmpty()) {
            // create an empty rule
            Rule rule = createRule(ftStyle);            
            return rule;
        }
        return ftStyle.rules().get(0);
    }

    /**
     * Creates a new feature type style for the object, and adds it to the style.
     * 
     * @param name The name of feature type style.
     * @return The newly created feature type style.
     */
    public FeatureTypeStyle createFeatureTypeStyle( String name ) {
        FeatureTypeStyle ftStyle = styleBuilder.createFeatureTypeStyle(name, new Rule[]{});
        style.featureTypeStyles().add(ftStyle);
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
        ftStyle.rules().add(rule);

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
        if (symbolizers == null || theClass == null){
            return null;
        }

        for( int i = 0; i < symbolizers.length; i++ ) {
            if (symbolizers[i] == null){
                continue;
            }
            if (theClass.isAssignableFrom(symbolizers[i].getClass())){
                return theClass.cast( symbolizers[i] );
            }
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

        rule.symbolizers().clear();
        for (Symbolizer s : syms) rule.symbolizers().add(s);
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
            Rule r = getDefaultRule();
            r.symbolizers().clear();
            for (Symbolizer s : newSymbolizers) r.symbolizers().add(s);
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
            Rule r = getDefaultRule();
            r.symbolizers().clear();
            for (Symbolizer s : newSymbolizers) r.symbolizers().add(s);
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
