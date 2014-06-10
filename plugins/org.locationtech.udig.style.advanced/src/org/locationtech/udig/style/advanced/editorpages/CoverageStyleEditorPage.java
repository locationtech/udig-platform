/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.editorpages;

import java.util.ArrayList;
import java.util.MissingResourceException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.NameImpl;
import org.geotools.renderer.i18n.ErrorKeys;
import org.geotools.renderer.i18n.Errors;
import org.geotools.styling.ColorMap;
import org.geotools.styling.ColorMapEntry;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.locationtech.udig.catalog.IGeoResource;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.advanced.StylePlugin;
import org.locationtech.udig.style.advanced.internal.Messages;
import org.locationtech.udig.style.advanced.raster.CoverageColorEditor;
import org.locationtech.udig.style.advanced.raster.CoverageRule;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.style.sld.SLDContentManager;
import org.locationtech.udig.style.sld.editor.StyleEditorPage;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.filter.expression.Expression;
import org.opengis.style.SemanticType;

/**
 * The style editor for single banded {@link GridCoverage2D coverages};
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CoverageStyleEditorPage extends StyleEditorPage {

    public static String COVERAGERASTERSTYLEID = "eu.hydrologis.jgrass.coveragerasterstyle"; //$NON-NLS-1$

    private StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);

    private CoverageColorEditor colorRulesEditor = null;
    private boolean editorSupported = false;

    public CoverageStyleEditorPage() {
        super();
        setSize(new Point(500, 450));
    }

    public void createPageContent( Composite parent ) {
        Layer layer = getSelectedLayer();
        IGeoResource resource = layer.getGeoResource();

        if (resource.canResolve(GridCoverage.class)) {
            editorSupported = true;
        } else {
            editorSupported = false;
        }

        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginBottom = 0;
        gridLayout.marginHeight = 0;
        gridLayout.marginLeft = 0;
        gridLayout.marginRight = 0;
        gridLayout.marginTop = 0;
        gridLayout.marginWidth = 0;
        parent.setLayout(gridLayout);

        if (editorSupported) {
            colorRulesEditor = new CoverageColorEditor(parent, SWT.NONE);

            StyleBlackboard styleBlackboard = layer.getStyleBlackboard();

            Style style = (Style) styleBlackboard.get(SLDContent.ID);
            if (style == null) {
                style = createDefaultStyle();

                // put style back on blackboard
                styleBlackboard.put(SLDContent.ID, style);
                styleBlackboard.setSelected(new String[]{SLDContent.ID});
            }
            style.setName(layer.getName());
            ArrayList<CoverageRule> listOfRules = new ArrayList<CoverageRule>();
            // Rule ruleToUpdate = SLDs.getRasterSymbolizerRule(style);
            RasterSymbolizer rasterSymbolizer = SLDs.rasterSymbolizer(style);

            ColorMap colorMap = rasterSymbolizer.getColorMap();
            ColorMapEntry[] colorMapEntries = colorMap.getColorMapEntries();
            for( int i = 0; i < colorMapEntries.length - 1; i = i + 2 ) {
                double fromQuantity = getQuantity(colorMapEntries[i]);
                java.awt.Color f = getColor(colorMapEntries[i]);
                double fromOpacity = getOpacity(colorMapEntries[i]);

                double toQuantity = getQuantity(colorMapEntries[i + 1]);
                java.awt.Color t = getColor(colorMapEntries[i + 1]);
                // double toOpacity = getOpacity(colorMapEntries[i + 1]);

                Color fromColor = new Color(Display.getDefault(), f.getRed(), f.getGreen(), f.getBlue());
                Color toColor = new Color(Display.getDefault(), t.getRed(), t.getGreen(), t.getBlue());
                CoverageRule rule = new CoverageRule(new double[]{fromQuantity, toQuantity}, fromColor, toColor, fromOpacity,
                        true);
                listOfRules.add(rule);
            }

            colorRulesEditor.setLayer(layer);

            Double globalOpacity = rasterSymbolizer.getOpacity().evaluate(null, Double.class);
            if (globalOpacity != null) {
                colorRulesEditor.setAlphaValue((int) (globalOpacity * 100.0));
            } else {
                colorRulesEditor.setAlphaValue(100);
            }

            colorRulesEditor.setRulesList(listOfRules);
        } else {
            Label problemLabel = new Label(parent, SWT.NONE);
            problemLabel.setText(Messages.CoverageStyleEditorPage_0);
        }

    }
    public String getErrorMessage() {
        return null;
    }

    public String getLabel() {
        return null;
    }

    public boolean performCancel() {
        return false;
    }

    public boolean okToLeave() {
        return true;
    }

    public boolean performApply() {
        return applyCurrentStyle();
    }

    public boolean performOk() {
        return applyCurrentStyle();
    }

    private boolean applyCurrentStyle() {

        try {
            if (editorSupported) {
                StyleLayer layer = getSelectedLayer();
                // IGeoResource resource = layer.getGeoResource();
                // GridCoverage2D coverage2d = resource.resolve(GridCoverage2D.class,
                // new NullProgressMonitor());
                // int numBands = coverage2d.getNumSampleDimensions();
                // if (numBands > 1) {
                // System.out.println("BANDS: " + numBands);
                // // return false;
                // }

                StyleBuilder sB = new StyleBuilder(sf);
                RasterSymbolizer rasterSym = sf.createRasterSymbolizer();

                ColorMap colorMap = sf.createColorMap();
                ArrayList<CoverageRule> rulesList = colorRulesEditor.getRulesList();
                for( int i = 0; i < rulesList.size(); i++ ) {
                    CoverageRule coverageRule = rulesList.get(i);
                    if (!coverageRule.isActive()) {
                        continue;
                    }
                    Color fromColor = coverageRule.getFromColor();
                    Color toColor = coverageRule.getToColor();
                    double[] values = coverageRule.getFromToValues();
                    double opacity = coverageRule.getOpacity();

                    Expression fromColorExpr = sB.colorExpression(new java.awt.Color(fromColor.getRed(), fromColor.getGreen(),
                            fromColor.getBlue(), 255));
                    Expression toColorExpr = sB.colorExpression(new java.awt.Color(toColor.getRed(), toColor.getGreen(), toColor
                            .getBlue(), 255));
                    Expression fromExpr = sB.literalExpression(values[0]);
                    Expression toExpr = sB.literalExpression(values[1]);
                    Expression opacityExpr = sB.literalExpression(opacity);

                    ColorMapEntry entry = sf.createColorMapEntry();
                    entry.setQuantity(fromExpr);
                    entry.setColor(fromColorExpr);
                    entry.setOpacity(opacityExpr);
                    colorMap.addColorMapEntry(entry);

                    entry = sf.createColorMapEntry();
                    entry.setQuantity(toExpr);
                    entry.setOpacity(opacityExpr);
                    entry.setColor(toColorExpr);
                    colorMap.addColorMapEntry(entry);
                }

                rasterSym.setColorMap(colorMap);

                /*
                 * set global transparency for the map
                 */
                rasterSym.setOpacity(sB.literalExpression(colorRulesEditor.getAlphaVAlue() / 100.0));

                Style newStyle = SLD.wrapSymbolizers(rasterSym);
                Layer selLayer = getSelectedLayer();
                newStyle.setName(selLayer.getName());

                StyleBlackboard styleBlackboard = layer.getStyleBlackboard();

                // put style back on blackboard
                styleBlackboard.put(SLDContent.ID, newStyle);
                styleBlackboard.setSelected(new String[]{SLDContent.ID});
            }
        } catch (Exception e) {
            e.printStackTrace();
            String message = Messages.CoverageStyleEditorPage_2;
            ExceptionDetailsDialog.openError(null, message, IStatus.ERROR, StylePlugin.PLUGIN_ID, e);
            return false;
        }
        return true;
    }

    public void refresh() {
    }

    public void dispose() {
        if (editorSupported) {
            colorRulesEditor = null;
        }
        super.dispose();
    }

    public void styleChanged( Object source ) {

    }

    public Style createDefaultStyle() {

        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());
        StyleBuilder styleBuilder = new StyleBuilder(styleFactory);

        RasterSymbolizer rasterSymbolizer = styleFactory.createRasterSymbolizer();
        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(rasterSymbolizer);

        Style style = styleBuilder.createStyle();
        SLDContentManager sldContentManager = new SLDContentManager(styleBuilder, style);
        sldContentManager.addSymbolizer(rasterSymbolizer);

        // set the feature type name
        FeatureTypeStyle fts = sldContentManager.getDefaultFeatureTypeStyle();
        fts.featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
        fts.setName("simple"); //$NON-NLS-1$
        fts.semanticTypeIdentifiers().add(SemanticType.valueOf("generic:geometry")); //$NON-NLS-1$
        fts.semanticTypeIdentifiers().add(SemanticType.valueOf("simple")); //$NON-NLS-1$
        fts.rules().add(rule);
        style.featureTypeStyles().add(fts);
        
        style.setName("simpleStyle"); //$NON-NLS-1$

        return style;
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

    public void gotFocus() {
        // do nothing here
    }

}
