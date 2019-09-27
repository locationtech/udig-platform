/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.core;

import org.geotools.factory.CommonFactoryFinder;
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
import org.geotools.util.factory.GeoTools;
import org.opengis.filter.FilterFactory;

/**
 * Removes the transparency from a style.
 * 
 * @author Jesse
 */
public class TransparencyRemovingVisitor implements StyleVisitor {

    public void visit( StyledLayerDescriptor arg0 ) {
        // nothing
    }

    public void visit( NamedLayer arg0 ) {
        // nothing
    }

    public void visit( UserLayer arg0 ) {
        // nothing
    }

    public void visit( FeatureTypeConstraint arg0 ) {
        // nothing
    }

    public void visit( Style arg0 ) {
        for( FeatureTypeStyle fts : arg0.featureTypeStyles() ) {
            fts.accept(this);
        }
    }

    public void visit( Rule arg0 ) {
        for( Symbolizer s : arg0.getSymbolizers() ) {
            s.accept(this);
        }
    }

    public void visit( FeatureTypeStyle arg0 ) {
        for( Rule s : arg0.rules() ) {
            s.accept(this);
        }
    }

    FilterFactory fac = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());

    public void visit( Fill arg0 ) {
        arg0.setOpacity( fac.literal(1.0));
    }

    public void visit( Stroke arg0 ) {
        arg0.setOpacity( fac.literal(1.0));
    }

    public void visit( Symbolizer arg0 ) {
        if (arg0 instanceof PointSymbolizer) {
            PointSymbolizer ps = (PointSymbolizer) arg0;
            ps.accept(this);
        }
        if (arg0 instanceof PolygonSymbolizer) {
            PolygonSymbolizer ps = (PolygonSymbolizer) arg0;
            ps.accept(this);
        }
        if (arg0 instanceof LineSymbolizer) {
            LineSymbolizer ps = (LineSymbolizer) arg0;
            ps.accept(this);
        }
        if (arg0 instanceof TextSymbolizer) {
            TextSymbolizer ps = (TextSymbolizer) arg0;
            ps.accept(this);
        }
        if (arg0 instanceof RasterSymbolizer) {
            RasterSymbolizer ps = (RasterSymbolizer) arg0;
            ps.accept(this);
        }
    }

    public void visit( PointSymbolizer arg0 ) {
        arg0.getGraphic().accept(this);
    }

    public void visit( LineSymbolizer arg0 ) {
        Stroke stroke = arg0.getStroke();
        if( stroke!=null )
            stroke.accept(this);
    }

    public void visit( PolygonSymbolizer arg0 ) {
        Fill fill = arg0.getFill();
        if (fill != null)
            fill.accept(this);
        Stroke stroke = arg0.getStroke();
        if( stroke!=null )
            stroke.accept(this);
    }

    public void visit( TextSymbolizer arg0 ) {
        Fill fill = arg0.getFill();
        if (fill != null)
            fill.accept(this);
    }

    public void visit( RasterSymbolizer arg0 ) {
        arg0.setOpacity( fac.literal(1.0));
    }

    public void visit( Graphic arg0 ) {
        arg0.setOpacity( fac.literal(1.0));
    }

    public void visit( Mark arg0 ) {
        Fill fill = arg0.getFill();
        if (fill != null)
            fill.accept(this);

        Stroke stroke = arg0.getStroke();
        if( stroke!=null )
            stroke.accept(this);
    }

    public void visit( ExternalGraphic arg0 ) {
        // nothing
    }

    public void visit( PointPlacement arg0 ) {
        // nothing

    }

    public void visit( AnchorPoint arg0 ) {
        // nothing
    }

    public void visit( Displacement arg0 ) {
        // nothing
    }

    public void visit( LinePlacement arg0 ) {
        // nothing
    }

    public void visit( Halo arg0 ) {
        Fill fill = arg0.getFill();
        if (fill != null)
            fill.accept(this);
    }

    public void visit( ColorMap colorMap ) {
        // nothing
    }

    public void visit( ColorMapEntry colorMapEntry ) {
        // nothing
    }

    public void visit( ContrastEnhancement arg0 ) {
    }

    public void visit( ImageOutline arg0 ) {
    }

    public void visit( ChannelSelection arg0 ) {
    }

    public void visit( OverlapBehavior arg0 ) {
    }

    public void visit( SelectedChannelType arg0 ) {
    }

    public void visit( ShadedRelief arg0 ) {
    }

}
