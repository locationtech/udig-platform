/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.udig.style.advanced.editorpages;

import java.util.ArrayList;
import java.util.MissingResourceException;

import net.refractions.udig.catalog.IGeoResource;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.style.internal.StyleLayer;
import net.refractions.udig.style.sld.SLDContent;
import net.refractions.udig.style.sld.SLDContentManager;
import net.refractions.udig.style.sld.editor.StyleEditorPage;
import net.refractions.udig.ui.ExceptionDetailsDialog;
import net.refractions.udig.ui.graphics.SLDs;

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
import org.geotools.styling.Symbolizer;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.filter.expression.Expression;

import eu.udig.style.advanced.StylePlugin;
import eu.udig.style.advanced.internal.Messages;
import eu.udig.style.advanced.raster.CoverageColorEditor;
import eu.udig.style.advanced.raster.CoverageRule;

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
        rule.setSymbolizers(new Symbolizer[]{rasterSymbolizer});

        Style style = styleBuilder.createStyle();
        SLDContentManager sldContentManager = new SLDContentManager(styleBuilder, style);
        sldContentManager.addSymbolizer(rasterSymbolizer);

        // set the feature type name
        FeatureTypeStyle fts = sldContentManager.getDefaultFeatureTypeStyle();
        fts.setFeatureTypeName(SLDs.GENERIC_FEATURE_TYPENAME);
        fts.setName("simple"); //$NON-NLS-1$
        fts.setSemanticTypeIdentifiers(new String[]{"generic:geometry", "simple"}); //$NON-NLS-1$ //$NON-NLS-2$

        fts.addRule(rule);
        style.addFeatureTypeStyle(fts);
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

}
