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
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.NameImpl;
import org.geotools.filter.IllegalFilterException;
import org.geotools.filter.function.FilterFunction_geometryType;
import org.geotools.styling.FeatureTypeConstraint;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.TextSymbolizer;
import org.geotools.styling.UserLayer;
import org.geotools.styling.visitor.DuplicatingStyleVisitor;
import org.geotools.util.factory.GeoTools;
import org.geotools.xml.styling.SLDParser;
import org.geotools.xml.styling.SLDTransformer;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.ILayer;
import org.locationtech.udig.project.StyleContent;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.style.SemanticType;

/**
 * StyleContent allowing a Style Layer Descriptor (SLD) document to be saved on the style blackboard.
 * <p>
 * This class is final and not intended for extension.
 * </p>
 * We have added several utility methods to this class to assist programmers in working with
 * the Style data structure.
 * <p>
 * Other recommended utility classes are:
 * <ul>
 * <li>org.locationtech.udig.graphics.SLDs - a utility class for handling the "Default" rule in a style</li>
 * <li>org.geotools.styling.SLD - a port of our SLDs class to GeoTools/li>
 * <li>org.locationtech.udig.style.sld.SLD - an enum with methods for checking for POINT, LINE, POLYGON
 * <li>SLDContentManager 
 * <li>StyleFactory, StyleFactory2 - direct creation of style objects
 * <li>StyleBuilder - creation of style objects; but allowing for default values
 * </ul>
 * 
 * @author Justin Deoliveira, Refractions Research Inc.
 */
public final class SLDContent extends StyleContent {

    /** style id, used to identify sld style on a blackboard * */
    public static final String ID = "org.locationtech.udig.style.sld"; //$NON-NLS-1$

    /** factory used to create style and builder * */
    private static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());

    /** factory used to create style content * */
    private static StyleBuilder styleBuilder = new StyleBuilder(styleFactory);

    /** random number generator used to create random colors * */
    private static Random random = new Random();

    /**
     * SLDContent constructor.
     */
    public SLDContent() {
        super(ID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.project.StyleContent#getStyleClass()
     */
    public Class<?> getStyleClass() {
        return Style.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.project.StyleContent#save(org.eclipse.ui.IMemento,
     *      java.lang.Object)
     */
    public void save( IMemento memento, Object value ) {
        Style style = (Style) value;

        // serialize out the style objects
        SLDTransformer sldWriter = new SLDTransformer();
        String out = ""; //$NON-NLS-1$
        try {
            out = sldWriter.transform(style);
        } catch (TransformerException e) {
            SLDPlugin.log("SLDTransformer failed", e); //$NON-NLS-1$
            e.printStackTrace();
        } catch (Exception e) {
            SLDPlugin.log("SLDTransformer failed", e); //$NON-NLS-1$
        }
        memento.putTextData(out);
        memento.putString("type", "SLDStyle"); //$NON-NLS-1$ //$NON-NLS-2$
        memento.putString("version", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.project.StyleContent#load(org.eclipse.ui.IMemento)
     */
    public Object load( IMemento momento ) {
        // parse the sld object
        if( momento.getTextData()==null )
            return null;
        StringReader reader = new StringReader(momento.getTextData());
        SLDParser sldParser = new SLDParser(getStyleFactory(), reader);

        Style[] parsed = sldParser.readXML();
        if (parsed != null && parsed.length > 0)
            return parsed[0];

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.project.StyleContent#load(java.net.URL)
     */
    public Object load( URL url, IProgressMonitor m ) throws IOException {
        return parse(url);
    }

    public static void apply(ILayer layer, Style style, IProgressMonitor m)
    	throws IOException {
    	
    	if (layer == null) return;
    	if (style == null) return;
    		
    	if (layer.hasResource(FeatureSource.class)) {
    		IGeoResource resource = layer.findGeoResource(FeatureSource.class);
    		SimpleFeatureSource featureSource 
    			= resource.resolve(SimpleFeatureSource.class, m);
    		
    		if (featureSource != null) {
    			//match up the feature type style name and the feature type name
    			SimpleFeatureType type = featureSource.getSchema();
    			FeatureTypeStyle fstyle = SLDs.featureTypeStyle(style,type);
    			if (fstyle == null) {
    				//force a name match
    				List<FeatureTypeStyle> fstyles = style.featureTypeStyles();
    				if (fstyles != null && !fstyles.isEmpty()) {
    					fstyle = fstyles.get(0);
    				}
    			}
    			
    			if (fstyle != null) {
    				fstyle.setName(type.getName().getLocalPart());
    				StyleBlackboard styleBlackboard = (StyleBlackboard) layer.getStyleBlackboard();
                    styleBlackboard.put(SLDContent.ID, style);
			        styleBlackboard.setSelected(new String[]{SLDContent.ID});

//    				//force a rerender, TODO: blackboard events
//    				layer.getMap().getRenderManager().refresh(
//    					layer, resource.getInfo(m).getBounds()	
//    				);
    			}
    		}
    	}
    }
    
    /**
     * This will need to know the "scheme."
     */
    public Object createDefaultStyle( IGeoResource resource, Color colour, 
            IProgressMonitor m ) throws IOException {
        
        if( resource.canResolve(Style.class)){
            Style style = resource.resolve( Style.class, null);
            if( style != null ){
                DuplicatingStyleVisitor v = new DuplicatingStyleVisitor();
                style.accept(v);
                return v.getCopy();
            }
        }
        
        if( resource.canResolve(FeatureSource.class) ){
            SimpleFeatureSource featureSource = null;
            try {
                featureSource = resource.resolve(SimpleFeatureSource.class, m);
            } 
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            Style style = createDefaultFeatureStyle(resource, colour, featureSource);
            if( style != null ) return style;
        }
        if (resource.canResolve(GridCoverage.class)){
            //GridCoverage grid = resource.resolve(GridCoverage.class, m );
            return createDefaultGridCoverageStyle( resource, colour );
        }
        return null; // there is no good Style default for this resource type
    }

    private Style createDefaultGridCoverageStyle( IGeoResource resource, Color colour ) {
        RasterSymbolizer rasterSymbolizer = styleFactory.createRasterSymbolizer();
        
        Style style = styleBuilder.createStyle();
        SLDContentManager sldContentManager = new SLDContentManager(styleBuilder, style);
        sldContentManager.addSymbolizer(rasterSymbolizer);
        
        style.setName("simpleStyle");
              
        return style;
    }

    private Style createDefaultFeatureStyle( IGeoResource resource, Color colour,
             FeatureSource<SimpleFeatureType, SimpleFeature> featureSource ) throws IOException {
        if (featureSource == null) {
            return null;
        }
        
        SimpleFeatureType schema = featureSource.getSchema();
        GeometryDescriptor geom = schema.getGeometryDescriptor();
        if (geom == null) {
            return null;
        }

        Style style = styleBuilder.createStyle();
        SLDContentManager sldContentManager = new SLDContentManager(styleBuilder, style);

        // initialize the symbolizers based on geometry type
        if (SLD.isLine(geom)) {
            sldContentManager.addSymbolizer(createLineSymbolizer(colour));
        } else if (SLD.isPoint(geom)) {
            sldContentManager.addSymbolizer(createPointSymbolizer(colour));
        } else if (SLD.isPolygon(geom)) {
            PolygonSymbolizer symbol = createPolygonSymbolizer(colour);
            sldContentManager.addSymbolizer(symbol);
        } else {
            try {
                createGeometrySLD(colour, schema.getGeometryDescriptor().getName().getLocalPart(),
                        sldContentManager);
            } catch (Exception e) {
                SLDPlugin.log("Failed to create geometry SLD", e); //$NON-NLS-1$
                sldContentManager.addSymbolizer(styleBuilder.createLineSymbolizer());
            }
        }

        //set the feature type name
        FeatureTypeStyle fts = sldContentManager.getDefaultFeatureTypeStyle();
        fts.featureTypeNames().clear();
        fts.featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
        fts.setName("simple"); //$NON-NLS-1$
        fts.semanticTypeIdentifiers().clear();
        fts.semanticTypeIdentifiers().add(new SemanticType("generic:geometry")); //$NON-NLS-1$
        fts.semanticTypeIdentifiers().add(new SemanticType("simple")); //$NON-NLS-1$
                
        return style;
    }
    
    private void createGeometrySLD( Color colour, String geomXPath, SLDContentManager sldContentManager ) throws IllegalFilterException {
        // create Point rule.
        Rule rule=sldContentManager.getDefaultRule();
        PropertyIsEqualTo filter = createGeometryFunctionFilter(geomXPath, Point.class.getSimpleName());
        rule.setFilter(filter);
        rule.symbolizers().add(createPointSymbolizer(colour));
        sldContentManager.getDefaultFeatureTypeStyle().rules().add(rule);

        // create MultiPoint rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, MultiPoint.class.getSimpleName());
        rule.setFilter(filter);
        rule.symbolizers().add( createPointSymbolizer(colour));
        sldContentManager.getDefaultFeatureTypeStyle().rules().add(rule);
        
        // create LineString rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, LineString.class.getSimpleName());
        rule.setFilter(filter);
        rule.symbolizers().add(createLineSymbolizer(colour));
        sldContentManager.getDefaultFeatureTypeStyle().rules().add(rule);
        
        // create LinearRing rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, LinearRing.class.getSimpleName());
        rule.setFilter(filter);
        rule.symbolizers().add(createLineSymbolizer(colour));
        sldContentManager.getDefaultFeatureTypeStyle().rules().add(rule);
        
        // create MultiLineString rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, MultiLineString.class.getSimpleName());
        rule.setFilter(filter);
        rule.symbolizers().add(createLineSymbolizer(colour));
        sldContentManager.getDefaultFeatureTypeStyle().rules().add(rule);
 
        // create Polygon rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, Polygon.class.getSimpleName());
        rule.setFilter(filter);
        rule.symbolizers().add(createPolygonSymbolizer(colour));
        sldContentManager.getDefaultFeatureTypeStyle().rules().add(rule);

        // create MultiPolygon rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, MultiPolygon.class.getSimpleName());
        rule.setFilter(filter);
        rule.symbolizers().add(createPolygonSymbolizer(colour));
        sldContentManager.getDefaultFeatureTypeStyle().rules().add(rule);

    }

    private PropertyIsEqualTo createGeometryFunctionFilter( String geomXPath, Object geometryClassSimpleName ) throws IllegalFilterException {
        FilterFactory factory=CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
        FilterFunction_geometryType geomTypeExpr=new FilterFunction_geometryType();
        List<Expression> params = new ArrayList<Expression>();
        params.add(factory.property(geomXPath));
        geomTypeExpr.setParameters(params);
        
        return factory.equals(geomTypeExpr, factory.literal(geometryClassSimpleName));
    }
    
    public static Style parse(URL url) throws IOException {
        return SLDs.parseStyle(url);
    }
    
    public static StyledLayerDescriptor createDefaultStyledLayerDescriptor() {
        StyledLayerDescriptor sld = styleFactory.createStyledLayerDescriptor();
        return sld;
    }
    
    /**
     * Creates an SLD and UserLayer, and nests the style (SLD-->UserLayer-->Style). 
     * 
     * @see net.refractions.project.internal.render.SelectionStyleContent#createDefaultStyledLayerDescriptor
     * @param style
     * @return SLD
     */
    public static StyledLayerDescriptor createDefaultStyledLayerDescriptor(Style style) {
        StyledLayerDescriptor sld = createDefaultStyledLayerDescriptor();
        UserLayer layer = styleFactory.createUserLayer();
        //FeatureTypeConstraint ftc = styleFactory.createFeatureTypeConstraint(null, Filter.INCLUDE, null);
        layer.setLayerFeatureConstraints(new FeatureTypeConstraint[] {null});
        sld.addStyledLayer(layer);
        layer.addUserStyle(style);
        return sld;
    }
    
    public static Style createDefaultStyle() {
        Style style = styleBuilder.createStyle();
        SLDContentManager sldContentManager = new SLDContentManager(styleBuilder, style);
                
        // sldContentManager.addSymbolizer(styleBuilder.createPointSymbolizer());
        sldContentManager.addSymbolizer(createLineSymbolizer(createRandomColor()));
        sldContentManager.addSymbolizer(createPolygonSymbolizer(createRandomColor()));
        sldContentManager.addSymbolizer(createTextSymbolizer());
        // sldContentManager.addSymbolizer(styleBuilder.createRasterSymbolizer());

        //tag as a simple FeatureTypeStyle
        FeatureTypeStyle fts = style.featureTypeStyles().get(0);
        fts.setName("simple"); //$NON-NLS-1$
        fts.semanticTypeIdentifiers().clear();
        fts.semanticTypeIdentifiers().add(new SemanticType("generic:geometry")); //$NON-NLS-1$
        fts.semanticTypeIdentifiers().add(new SemanticType("simple")); //$NON-NLS-1$
        
        //TODO: add StyledLayerDescriptor to sldContentManager?
        return style;
    }

    public static StyleFactory getStyleFactory() {
        return styleFactory;
    }

    public static StyleBuilder getStyleBuilder() {
        return styleBuilder;
    }

    protected static PointSymbolizer createPointSymbolizer(Color colour) {
        PointSymbolizer symb=styleBuilder.createPointSymbolizer();
        Fill fill = styleBuilder.createFill(colour, 1.0);        

        Stroke outline=styleBuilder.createStroke(Color.BLACK,1,1);

        // check existing default graphics
        if (symb.getGraphic().graphicalSymbols() != null && symb.getGraphic().graphicalSymbols().size() == 1) {
            symb.getGraphic().graphicalSymbols().clear();
        }
        symb.getGraphic().graphicalSymbols().add(styleBuilder.createMark(StyleBuilder.MARK_SQUARE, fill, outline));

        symb.getGraphic().setSize( styleBuilder.literalExpression(6.0));
        return symb;
    }

    /**
     * Creates a simple LineSymbolizer using the specified colour.
     * 
     * @author Pati
     * @param colour 
     * @return LineSymbolizer
     */
    protected static LineSymbolizer createLineSymbolizer(Color colour) {
		if (colour == null) {
			colour = createRandomColor();
		}
        Stroke stroke = styleBuilder.createStroke();
        stroke.setColor(styleBuilder.colorExpression(colour));
        stroke.setWidth(styleBuilder.literalExpression(1));

        LineSymbolizer symbolizer = styleBuilder.createLineSymbolizer(stroke);

        return symbolizer;
    }

    /**
     * Creates a simple PolygonSymbolizer using the specified colour.
     * 
     * @author Pati
     * @param colour 
     * @return LineSymbolizer
     */
    protected static PolygonSymbolizer createPolygonSymbolizer(Color colour) {
    	if (colour == null) {
			colour = createRandomColor();
		}

        Stroke stroke = styleBuilder.createStroke();
        stroke.setColor(styleBuilder.colorExpression(colour));
        stroke.setWidth(styleBuilder.literalExpression(1));

        Fill fill = styleBuilder.createFill();
        fill.setColor(styleBuilder.colorExpression(colour));
        fill.setOpacity(styleBuilder.literalExpression(.5));

        PolygonSymbolizer symbolizer = styleBuilder.createPolygonSymbolizer(stroke, fill);

        return symbolizer;
    }

    protected static TextSymbolizer createTextSymbolizer() {
        TextSymbolizer symbolizer = styleBuilder.createTextSymbolizer();
        return symbolizer;
    }

    protected static RasterSymbolizer createRasterSymbolizer() {
        RasterSymbolizer symbolizer = styleBuilder.createRasterSymbolizer();
        return symbolizer;
    }

    protected static Color createRandomColor() {
        return new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200));
    }
    
    
}
