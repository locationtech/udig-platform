/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.core;

import java.util.HashSet;
import java.util.Set;

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
import org.geotools.styling.StyledLayer;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.geotools.styling.UserLayer;
import org.geotools.util.Range;

/**
 *
 * Traverses a Style and determines the min and max scale that the style is valid for.
 *
 * @author jesse
 * @since 1.1.0
 */
public class MinMaxScaleCalculator implements StyleVisitor {

    private Set<Range<Double>> ranges = new HashSet<>();

    private double max;

    private double min;

    @Override
    public void visit(StyledLayerDescriptor sld) {
        StyledLayer[] layers = sld.getStyledLayers();
        for (StyledLayer styledLayer : layers) {
            if (styledLayer instanceof NamedLayer) {
                ((NamedLayer) styledLayer).accept(this);
            } else if (styledLayer instanceof UserLayer) {
                ((UserLayer) styledLayer).accept(this);
            }
        }
    }

    @Override
    public void visit(NamedLayer layer) {
        Style[] styles = layer.getStyles();
        for (Style style : styles) {
            style.accept(this);
        }
    }

    @Override
    public void visit(UserLayer layer) {
        Style[] styles = layer.getUserStyles();
        for (Style style : styles) {
            style.accept(this);
        }
    }

    @Override
    public void visit(FeatureTypeConstraint ftc) {
    }

    @Override
    public void visit(Style style) {
        for (FeatureTypeStyle featureTypeStyle : style.featureTypeStyles()) {
            featureTypeStyle.accept(this);
        }
    }

    @Override
    public void visit(Rule rule) {
        double min = rule.getMinScaleDenominator();
        double max = rule.getMaxScaleDenominator();
        this.min = Math.min(min, this.min);
        this.max = Math.max(max, this.max);
        ranges.add(new Range<>(Double.class, min, max));
    }

    @Override
    public void visit(FeatureTypeStyle fts) {
        for (Rule rule : fts.rules()) {
            rule.accept(this);
        }
    }

    @Override
    public void visit(Fill fill) {

    }

    @Override
    public void visit(Stroke stroke) {

    }

    @Override
    public void visit(Symbolizer sym) {

    }

    @Override
    public void visit(PointSymbolizer ps) {

    }

    @Override
    public void visit(LineSymbolizer line) {

    }

    @Override
    public void visit(PolygonSymbolizer poly) {

    }

    @Override
    public void visit(TextSymbolizer text) {

    }

    @Override
    public void visit(RasterSymbolizer raster) {

    }

    @Override
    public void visit(Graphic gr) {

    }

    @Override
    public void visit(Mark mark) {

    }

    @Override
    public void visit(ExternalGraphic exgr) {

    }

    @Override
    public void visit(PointPlacement pp) {

    }

    @Override
    public void visit(AnchorPoint ap) {

    }

    @Override
    public void visit(Displacement dis) {

    }

    @Override
    public void visit(LinePlacement lp) {

    }

    @Override
    public void visit(Halo halo) {

    }

    @Override
    public void visit(ColorMap colorMap) {

    }

    @Override
    public void visit(ColorMapEntry colorMapEntry) {

    }

    public Set<Range<Double>> getRanges() {
        return ranges;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    @Override
    public void visit(ContrastEnhancement arg0) {

    }

    @Override
    public void visit(ImageOutline arg0) {

    }

    @Override
    public void visit(ChannelSelection arg0) {

    }

    @Override
    public void visit(OverlapBehavior arg0) {

    }

    @Override
    public void visit(SelectedChannelType arg0) {

    }

    @Override
    public void visit(ShadedRelief arg0) {

    }

    // UTILITY FUNCTIONS
    public static Set<Range<Double>> getValidScaleRanges(Style style) {
        if (style == null) {
            return new HashSet<>();
        }
        MinMaxScaleCalculator minMaxScaleCalculator = new MinMaxScaleCalculator();
        style.accept(minMaxScaleCalculator);
        return minMaxScaleCalculator.getRanges();
    }

}
