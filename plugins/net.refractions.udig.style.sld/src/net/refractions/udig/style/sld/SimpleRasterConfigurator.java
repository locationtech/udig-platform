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

import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.internal.StyleBlackboard;
import net.refractions.udig.style.sld.simple.RasterViewer;
import net.refractions.udig.style.sld.simple.ScaleViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.geotools.data.wms.WebMapServer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.opengis.coverage.grid.GridCoverage;

/**
 * Allow editing of a RasterSymbolizaer.
 * <p>
 * Currently this is focused on opacity and scale settings
 * </p>
 * @author mleslie
 * @since 1.0.
 */
public class SimpleRasterConfigurator extends AbstractSimpleConfigurator {
	private RasterViewer raster = new RasterViewer();
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
    public SimpleRasterConfigurator() {
        super();
        this.raster.addListener(this.synchronize);
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
        RasterSymbolizer sym = SLD.rasterSymbolizer(style);
        this.raster.set(sym);

        Rule r = style.getFeatureTypeStyles()[0].getRules()[0];
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
        this.raster.createControl(parent, adapter);
        this.minScale.createControl(parent, adapter);
        this.maxScale.createControl(parent, adapter);
    }

    @Override
    public void synchronize() {
        RasterSymbolizer sym = this.raster.get(this.build);
        Rule rule = this.build.createRule(new Symbolizer[] {sym});
        double minScaleDen = minScale.getScale();
        double maxScaleDen = maxScale.getScale();
        if( minScale.isEnabled())
            rule.setMinScaleDenominator(minScaleDen);
        if ( maxScale.isEnabled() )
            rule.setMaxScaleDenominator(maxScaleDen);
        FeatureTypeStyle featureTypeStyle = this.build.createFeatureTypeStyle( "Feature", rule ); //$NON-NLS-1$
        Style style = this.build.createStyle();
        style.setDefault( true );
        style.setName( "simple" ); //$NON-NLS-1$
        style.setFeatureTypeStyles( new FeatureTypeStyle[]{featureTypeStyle,} );
        //attach the SLD
        SLDContent.createDefaultStyledLayerDescriptor(style);
        //put style on blackboard
        getStyleBlackboard().put(SLDContent.ID, style);

        ((StyleBlackboard) getStyleBlackboard()).setSelected(new String[]{SLDContent.ID});
    }

}
