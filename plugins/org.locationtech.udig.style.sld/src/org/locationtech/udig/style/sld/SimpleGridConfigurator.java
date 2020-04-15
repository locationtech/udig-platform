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
package org.locationtech.udig.style.sld;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.NameImpl;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.util.factory.GeoTools;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.sld.simple.ScaleViewer;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.style.SemanticType;

/**
 * Allow editing of a RasterSymbolizaer.
 * <p>
 * Currently this is focused on opacity and scale settings
 * </p>
 * @author mleslie
 * @since 1.0.
 */
public class SimpleGridConfigurator extends AbstractSimpleConfigurator {
    ScaleViewer minScale = new ScaleViewer(ScaleViewer.MIN);
    ScaleViewer maxScale = new ScaleViewer(ScaleViewer.MAX);
    
    SelectionListener synchronize = new SelectionListener(){

        public void widgetSelected( SelectionEvent e ) {
            synchronize();
        }

        public void widgetDefaultSelected( SelectionEvent e ) {
            synchronize();
            // apply(); // and apply?
        }
        
    };

    /**
     * Construct <code>SimpleRasterConfigurator</code>.
     *
     */
    public SimpleGridConfigurator() {
        super();
        
        this.minScale.addListener(this.synchronize);
        this.maxScale.addListener(this.synchronize);
    }

    @Override
    public boolean canStyle( Layer aLayer ) {
        if (aLayer.hasResource(GridCoverage.class) 
                || aLayer.hasResource(WebMapServer.class))
            return true;        
        return false;
    }

    @Override
    protected void refresh() {
        Style style = getStyle();
        SLD.rasterSymbolizer(style);
        Rule r = style.featureTypeStyles().get(0).rules().get(0);
        double minScaleDen=r.getMinScaleDenominator();
        double maxScaleDen=r.getMaxScaleDenominator();
        this.minScale.setScale(minScaleDen, Math.round(getLayer().getMap().getViewportModel().getScaleDenominator()));            
        this.maxScale.setScale(maxScaleDen, Math.round(getLayer().getMap().getViewportModel().getScaleDenominator()));
    }

    @Override
    public void createControl( Composite parent ) {
        setLayout(parent);
        KeyAdapter adapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                /*
                 * I don't like having different ways of checking for keypad enter
                 * and the normal one.  Using the keyCode would be better, but
                 * I couldn't readily find the value for CR.
                 */
                if(e.keyCode == SWT.KEYPAD_CR 
                        || e.character == SWT.CR) {
                    makeActionDoStuff();
                }
            }
        };
        
        /** Removing this to remove opacity option... when we do this the min/max 
        are loaded without default values**/
        this.minScale.createControl(parent, adapter);
        this.maxScale.createControl(parent, adapter);
    }

    @Override
    public void synchronize() {
        
        StyleBlackboard styleBlackboard = (StyleBlackboard) getStyleBlackboard();

        // grab the style defined by the user
        Style style = (Style) styleBlackboard.get( SLDContent.ID );
        if (style == null) {
            style = createDefaultStyle();
            
            //put style back on blackboard
            getStyleBlackboard().put(SLDContent.ID, style);
            ((StyleBlackboard) getStyleBlackboard()).setSelected(new String[]{SLDContent.ID});
        }
         
        Rule ruleToUpdate = SLDs.getRasterSymbolizerRule(style);
        
        ruleToUpdate.setMinScaleDenominator(minScale.getScale());
        ruleToUpdate.setMaxScaleDenominator(maxScale.getScale());
               
    }

    
    public Style createDefaultStyle() {
        
        StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());
        StyleBuilder styleBuilder = new StyleBuilder(styleFactory);
        
        RasterSymbolizer rasterSymbolizer = styleFactory.createRasterSymbolizer();
        Rule rule = styleFactory.createRule();
        rule.symbolizers().clear();
        rule.symbolizers().add(rasterSymbolizer);
        
        Style style = styleBuilder.createStyle();
        SLDContentManager sldContentManager = new SLDContentManager(styleBuilder, style);
        sldContentManager.addSymbolizer(rasterSymbolizer);
        
        //set the feature type name
        FeatureTypeStyle fts = sldContentManager.getDefaultFeatureTypeStyle();
        fts.featureTypeNames().add(new NameImpl(SLDs.GENERIC_FEATURE_TYPENAME));
        fts.setName("simple"); //$NON-NLS-1$
        fts.semanticTypeIdentifiers().add(new SemanticType("generic:geometry"));  //$NON-NLS-1$
        fts.semanticTypeIdentifiers().add(new SemanticType("simple"));  //$NON-NLS-1$
        
        fts.rules().add(rule);
        style.featureTypeStyles().add(fts);
        
        style.setName("simpleStyle");        
        
        return style;
    }
    
    
}
