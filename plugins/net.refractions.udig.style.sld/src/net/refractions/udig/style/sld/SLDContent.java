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
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.transform.TransformerException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.ILayer;
import net.refractions.udig.project.StyleContent;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.ui.graphics.SLDs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IMemento;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.filter.IllegalFilterException;
import org.geotools.filter.function.FilterFunction_geometryType;
import org.geotools.styling.FeatureTypeConstraint;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
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
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

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
 * <li>net.refractions.udig.graphics.SLDs - a utility class for handling the "Default" rule in a style</li>
 * <li>org.geotools.styling.SLD - a port of our SLDs class to GeoTools/li>
 * <li>net.refractions.udig.style.sld.SLD - an enum with methods for checking for POINT, LINE, POLYGON
 * <li>SLDContentManager 
 * <li>StyleFactory, StyleFactory2 - direct creation of style objects
 * <li>StyleBuilder - creation of style objects; but allowing for default values
 * </ul>
 * 
 * @author Justin Deoliveira, Refractions Research Inc.
 */
public final class SLDContent extends StyleContent {

    /** style id, used to identify sld style on a blackboard * */
    public static final String ID = "net.refractions.udig.style.sld"; //$NON-NLS-1$

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
     * @see net.refractions.udig.project.StyleContent#getStyleClass()
     */
    public Class<?> getStyleClass() {
        return Style.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.project.StyleContent#save(org.eclipse.ui.IMemento,
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
     * @see net.refractions.udig.project.StyleContent#load(org.eclipse.ui.IMemento)
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
     * @see net.refractions.udig.project.StyleContent#load(java.net.URL)
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
    		FeatureSource<SimpleFeatureType, SimpleFeature> featureSource 
    			= resource.resolve(FeatureSource.class, m);
    		
    		if (featureSource != null) {
    			//match up the feature type style name and the feature type name
    			SimpleFeatureType type = featureSource.getSchema();
    			FeatureTypeStyle fstyle = SLDs.featureTypeStyle(style,type);
    			if (fstyle == null) {
    				//force a name match
    				FeatureTypeStyle[] fstyles = style.getFeatureTypeStyles();
    				if (fstyles != null && fstyles.length > 0) {
    					fstyle = fstyles[0];
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
             FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = null;
            try {
                featureSource = resource.resolve(FeatureSource.class, m);
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
        if( geom==null )
            return null;
        
        Style style = null;

        SLDContentManager sldContentManager; 
        
        if (style == null) {
            // fall back to create some default
            style = styleBuilder.createStyle();
            sldContentManager = new SLDContentManager(styleBuilder, style);

            // initialize the symbolizers based on geometry type
            if (geom != null) {
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
            }
        } else {
            sldContentManager = new SLDContentManager(styleBuilder, style);
        }

        //set the feature type name
        FeatureTypeStyle fts = sldContentManager.getDefaultFeatureTypeStyle();
        fts.setFeatureTypeName(SLDs.GENERIC_FEATURE_TYPENAME);
        fts.setName("simple"); //$NON-NLS-1$
        fts.setSemanticTypeIdentifiers(new String[] {"generic:geometry", "simple"}); //$NON-NLS-1$ //$NON-NLS-2$
        
        return style;
    }
    
    private void createGeometrySLD( Color colour, String geomXPath, SLDContentManager sldContentManager ) throws IllegalFilterException {
        // create Point rule.
        Rule rule=sldContentManager.getDefaultRule();
        PropertyIsEqualTo filter = createGeometryFunctionFilter(geomXPath, Point.class.getSimpleName());
        rule.setFilter(filter);
        rule.setSymbolizers(new Symbolizer[]{createPointSymbolizer(colour)});

        // create MultiPoint rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, MultiPoint.class.getSimpleName());
        rule.setFilter(filter);
        rule.setSymbolizers(new Symbolizer[]{createPointSymbolizer(colour)});
        sldContentManager.getDefaultFeatureTypeStyle().addRule(rule);
        
        // create LineString rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, LineString.class.getSimpleName());
        rule.setFilter(filter);
        rule.setSymbolizers(new Symbolizer[]{createLineSymbolizer(colour)});
        sldContentManager.getDefaultFeatureTypeStyle().addRule(rule);
        
        // create LinearRing rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, LinearRing.class.getSimpleName());
        rule.setFilter(filter);
        rule.setSymbolizers(new Symbolizer[]{createLineSymbolizer(colour)});
        sldContentManager.getDefaultFeatureTypeStyle().addRule(rule);
        
        // create MultiLineString rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, MultiLineString.class.getSimpleName());
        rule.setFilter(filter);
        rule.setSymbolizers(new Symbolizer[]{createLineSymbolizer(colour)});
        sldContentManager.getDefaultFeatureTypeStyle().addRule(rule);
 
        // create Polygon rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, Polygon.class.getSimpleName());
        rule.setFilter(filter);
        rule.setSymbolizers(new Symbolizer[]{createPolygonSymbolizer(colour)});
        sldContentManager.getDefaultFeatureTypeStyle().addRule(rule);

        // create MultiPolygon rule
        rule=sldContentManager.createRule();
        filter = createGeometryFunctionFilter(geomXPath, MultiPolygon.class.getSimpleName());
        rule.setFilter(filter);
        rule.setSymbolizers(new Symbolizer[]{createPolygonSymbolizer(colour)});
        sldContentManager.getDefaultFeatureTypeStyle().addRule(rule);

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
    	StyleFactory factory = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());
        SLDParser styleReader = new SLDParser(factory, url);
        Style style = styleReader.readXML()[0];

        return style;
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
        FeatureTypeStyle fts = style.getFeatureTypeStyles()[0];
        fts.setName("simple"); //$NON-NLS-1$
        fts.setSemanticTypeIdentifiers(new String[] {"generic:geometry", "simple"}); //$NON-NLS-1$ //$NON-NLS-2$
        
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
        symb.getGraphic().setMarks(new Mark[]{styleBuilder.createMark(StyleBuilder.MARK_SQUARE, fill, outline)});
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