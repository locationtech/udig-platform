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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.NameImpl;
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
import org.locationtech.udig.style.advanced.raster.CoverageRuleUtils;
import org.locationtech.udig.style.internal.StyleLayer;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.style.sld.SLDContentManager;
import org.locationtech.udig.style.sld.editor.StyleEditorPage;
import org.locationtech.udig.ui.ExceptionDetailsDialog;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.style.SemanticType;

/**
 * The style editor for single banded {@link GridCoverage2D coverages};
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class CoverageStyleEditorPage extends StyleEditorPage {

    public static String COVERAGERASTERSTYLEID = "eu.hydrologis.jgrass.coveragerasterstyle"; //$NON-NLS-1$


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

            // Rule ruleToUpdate = SLDs.getRasterSymbolizerRule(style);
            RasterSymbolizer rasterSymbolizer = SLDs.rasterSymbolizer(style);

            List<CoverageRule> listOfRules = CoverageRuleUtils.createCoverageRulesForRasterSymbolizer(rasterSymbolizer);
            
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

                RasterSymbolizer rasterSym = CoverageRuleUtils.createColorMapForCoverageRules(colorRulesEditor.getRulesList(), colorRulesEditor.getAlphaVAlue());

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

    public void gotFocus() {
        // do nothing here
    }

}
