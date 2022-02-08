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
package org.locationtech.udig.catalog.internal.shp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.styling.AnchorPoint;
import org.geotools.styling.ChannelSelection;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.ContrastEnhancement;
import org.geotools.styling.Displacement;
import org.geotools.styling.ExternalGraphic;
import org.geotools.styling.FeatureTypeConstraint;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Halo;
import org.geotools.styling.ImageOutline;
import org.geotools.styling.LinePlacement;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Mark;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.OverlapBehavior;
import org.geotools.styling.PointPlacement;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.ShadedRelief;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleVisitor;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.geotools.styling.UserLayer;
import org.locationtech.udig.catalog.ID;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.catalog.IGeoResourceInfo;
import org.locationtech.udig.catalog.IService;
import org.locationtech.udig.catalog.URLUtils;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.style.GraphicalSymbol;

/**
 * Connect to a shapefile.
 *
 * @author David Zwiers, Refractions Research
 * @since 0.6
 */
public class ShpGeoResourceImpl extends IGeoResource {
    ShpServiceImpl parent;
    String typename = null;
    private URL identifier;
    private ID id;

    /**
     * Construct <code>ShpGeoResourceImpl</code>.
     *
     * @param parent
     * @param typename
     */
    public ShpGeoResourceImpl( ShpServiceImpl parent, String typename ) {
        this.service = parent;
        this.parent = parent;
        this.typename = typename;
        try {
            identifier = new URL(parent.getIdentifier().toString() + "#" + typename); //$NON-NLS-1$
            id = new ID(parent.getID(), typename);
        } catch (MalformedURLException e) {
            identifier = parent.getIdentifier();
        }
    }

    @Override
    public URL getIdentifier() {
        return identifier;
    }
    @Override
    public ID getID() {
        return id;
    }

    /*
     * @see org.locationtech.udig.catalog.IGeoResource#getStatus()
     */
    @Override
    public Status getStatus() {
        return parent.getStatus();
    }

    /*
     * @see org.locationtech.udig.catalog.IGeoResource#getStatusMessage()
     */
    @Override
    public Throwable getMessage() {
        return parent.getMessage();
    }

    /*
     * Required adaptions:
     * <ul>
     * <li>IGeoResourceInfo.class
     * <li>IService.class
     * </ul>
     * @see org.locationtech.udig.catalog.IResolve#resolve(java.lang.Class, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public <T> T resolve( Class<T> adaptee, IProgressMonitor monitor ) throws IOException {
        if (adaptee == null) {
            return null;
        }
        if (adaptee.isAssignableFrom(IGeoResource.class)) {
            return adaptee.cast(this);
        }
        if (adaptee.isAssignableFrom(IGeoResourceInfo.class)) {
            return adaptee.cast(createInfo(monitor));
        }
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            SimpleFeatureSource fs = featureSource(monitor);
            if (fs instanceof SimpleFeatureStore) {
                return adaptee.cast(fs);
            }
        }
        if (adaptee.isAssignableFrom(SimpleFeatureStore.class)) {
            SimpleFeatureSource fs = featureSource(monitor);
            if (fs instanceof SimpleFeatureStore) {
                return adaptee.cast(fs);
            }
        }
        if (adaptee.isAssignableFrom(SimpleFeatureSource.class)) {
            return adaptee.cast(featureSource(monitor));
        }
//        if (adaptee.isAssignableFrom(IndexedShapefileDataStore.class)) {
//            return adaptee.cast(parent.getDS(monitor));
//        }
        if (adaptee.isAssignableFrom(Style.class)) {
            Style style = style(monitor);
            if (style != null) {
                return adaptee.cast(style(monitor));
            }
            // proceed to ask the super class, someone may
            // of written an IResolveAdapaterFactory providing us
            // with a style ...
        }
        return super.resolve(adaptee, monitor);
    }

    private SimpleFeatureSource featureSource( IProgressMonitor monitor ) throws IOException {
        return parent.getDS(monitor).getFeatureSource();
    }


    public Style style( IProgressMonitor monitor ) throws IOException {
        SimpleFeatureSource source  = featureSource(null);

        SimpleFeatureType featureType = source.getSchema();

        ID fileID = parent.getID();
        if( !fileID.isFile() ){
            return null; // we are only checking for sidecar files
        }
        File file = fileID.toFile("sld");
        if( !file.exists()){
            file =fileID.toFile("SLD");
            if( !file.exists()){
                return null; // sidecar file not avaialble
            }
        }
        StyledLayerDescriptor sld = SLDs.parseSLD( file );
        if( sld == null ){
            return null; // well that is unexpected since f.exists()
        }

        Style[] styles = SLDs.styles( sld );
        // Style[] styles = parser.readXML();

        // put the first one on
        if (styles != null && styles.length > 0) {
            Style style = SLDs.matchingStyle(styles, featureType);
            if (style == null) {
                style = styles[0];
            }
            makeGraphicsAbsolute(file, style);
            return style;
        }
        return null; // well nothing worked out; make your own style
    }

    /**
     * This transforms all external graphics references that are relative to absolute.
     * This is a workaround to be able to visualize png and svg in relative mode, which
     * doesn't work right now in geotools. See: http://jira.codehaus.org/browse/GEOT-3235
     *
     * This will not be necessary any more as soon as the geotools bug is fixed.
     *
     * @param relatedFile the related shapefile.
     * @param style the style to check.
     */
    private void makeGraphicsAbsolute( File relatedFile, Style style ) {
        File parentFolder = relatedFile.getParentFile();

        ExternalGraphicsAbsolutePathMaker visitor = new ExternalGraphicsAbsolutePathMaker(parentFolder);
        visitor.visit(style);
    }

    private class ExternalGraphicsAbsolutePathMaker implements StyleVisitor {

        private final File parentFolder;

        public ExternalGraphicsAbsolutePathMaker( File parentFolder ) {
            this.parentFolder = parentFolder;
        }

        @Override
        public void visit( StyledLayerDescriptor sld ) {
        }

        @Override
        public void visit( NamedLayer layer ) {
        }

        @Override
        public void visit( UserLayer layer ) {
        }

        @Override
        public void visit( Style style ) {
            List<FeatureTypeStyle> fts = style.featureTypeStyles();
            for( FeatureTypeStyle featureTypeStyle : fts ) {
                featureTypeStyle.accept(this);
            }
        }

        @Override
        public void visit( Rule rule ) {
            List<Symbolizer> syms = rule.symbolizers();
            for( Symbolizer symbolizer : syms ) {
                symbolizer.accept(this);
            }
        }

        @Override
        public void visit( FeatureTypeStyle fts ) {
            List<Rule> rules = fts.rules();
            for( Rule rule : rules ) {
                rule.accept(this);
            }
        }

        @Override
        public void visit( Fill fill ) {
            Graphic graphicFill = fill.getGraphicFill();
            if (graphicFill != null) {
                graphicFill.accept(this);
            }
        }

        @Override
        public void visit( Stroke stroke ) {
            Graphic graphicFill = stroke.getGraphicFill();
            if (graphicFill != null) {
                graphicFill.accept(this);
            }
            Graphic graphicStroke = stroke.getGraphicStroke();
            if (graphicStroke != null) {
                graphicStroke.accept(this);
            }
        }

        @Override
        public void visit( Symbolizer sym ) {
            if (sym instanceof RasterSymbolizer) {
                visit((RasterSymbolizer) sym);
            } else if (sym instanceof LineSymbolizer) {
                visit((LineSymbolizer) sym);
            } else if (sym instanceof PolygonSymbolizer) {
                visit((PolygonSymbolizer) sym);
            } else if (sym instanceof PointSymbolizer) {
                visit((PointSymbolizer) sym);
            } else if (sym instanceof TextSymbolizer) {
                visit((TextSymbolizer) sym);
            } else
                throw new RuntimeException("visit(Symbolizer) unsupported");
        }

        @Override
        public void visit( PointSymbolizer ps ) {
            Graphic graphic = ps.getGraphic();
            if (graphic != null) {
                graphic.accept(this);
            }
        }

        @Override
        public void visit( LineSymbolizer line ) {
            Stroke stroke = line.getStroke();
            if (stroke != null) {
                stroke.accept(this);
            }
        }

        @Override
        public void visit( PolygonSymbolizer poly ) {
            Stroke stroke = poly.getStroke();
            if (stroke != null) {
                stroke.accept(this);
            }
            Fill fill = poly.getFill();
            if (fill != null) {
                fill.accept(this);
            }
        }

        @Override
        public void visit( TextSymbolizer text ) {}

        @Override
        public void visit( RasterSymbolizer raster ) {}

        @Override
        public void visit( Graphic gr ) {
            List<GraphicalSymbol> graphicalSymbols = gr.graphicalSymbols();
            for( GraphicalSymbol graphicalSymbol : graphicalSymbols ) {
                if (graphicalSymbol instanceof ExternalGraphic) {
                    ExternalGraphic ext = (ExternalGraphic) graphicalSymbol;
                    ext.accept(this);
                }
            }
        }

        @Override
        public void visit( Mark mark ) {}

        @Override
        public void visit( ExternalGraphic exgr ) {
            try {
                URL location = exgr.getLocation();
                File urlToFile = URLUtils.urlToFile(location);
                if (urlToFile != null && !urlToFile.exists()) {
                    File newFile = new File(parentFolder, urlToFile.getPath());
                    if (newFile.exists()) {
                        exgr.setLocation(newFile.toURI().toURL());
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void visit( PointPlacement pp ) {}

        @Override
        public void visit( AnchorPoint ap ) {}

        @Override
        public void visit( Displacement dis ) {}

        @Override
        public void visit( LinePlacement lp ) {}

        @Override
        public void visit( Halo halo ) {}

        @Override
        public void visit( FeatureTypeConstraint ftc ) {}

        @Override
        public void visit( ColorMap colorMap ) {}

        @Override
        public void visit( ColorMapEntry colorMapEntry ) {}

        @Override
        public void visit( ContrastEnhancement contrastEnhancement ) {}

        @Override
        public void visit( ImageOutline outline ) {}

        @Override
        public void visit( ChannelSelection cs ) {}

        @Override
        public void visit( OverlapBehavior ob ) {}

        @Override
        public void visit( SelectedChannelType sct ) {}

        @Override
        public void visit( ShadedRelief sr ) {}
    }

    /**
     * Helper method performing the same function as service( monitor ) without the
     * monitor or chance of IOException.
     * <p>
     * @return ShpServiceImpl responsible for this ShpGeoResourceImpl
     */
    public ShpServiceImpl service() {
        return parent;
    }

    /*
     * @see org.locationtech.udig.catalog.IResolve#canResolve(java.lang.Class)
     */
    @Override
    public <T> boolean canResolve( Class<T> adaptee ) {
        if (adaptee == null) {
            return false;
        }
        return (adaptee.isAssignableFrom(IGeoResourceInfo.class) || adaptee.isAssignableFrom(SimpleFeatureStore.class)
                || adaptee.isAssignableFrom(FeatureSource.class)
                || adaptee.isAssignableFrom(SimpleFeatureSource.class)
                || adaptee.isAssignableFrom(IService.class)
                || adaptee.isAssignableFrom(Style.class)
                )
                || super.canResolve(adaptee);
    }
    @Override
    public IGeoResourceInfo getInfo( IProgressMonitor monitor ) throws IOException {
        return super.getInfo(monitor);
    }
    @Override
    protected ShpGeoResourceInfo createInfo( IProgressMonitor monitor ) throws IOException {
        return new ShpGeoResourceInfo(this);
    }
}
