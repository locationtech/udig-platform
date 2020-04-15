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
package org.locationtech.udig.style.sld.raster;

import java.awt.Color;

import javax.media.jai.Histogram;

import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.project.internal.StyleBlackboard;
import org.locationtech.udig.style.sld.AbstractSimpleConfigurator;
import org.locationtech.udig.style.sld.SLDContent;
import org.locationtech.udig.style.sld.raster.internal.Messages;
import org.locationtech.udig.style.sld.simple.ChannelViewer;
import org.locationtech.udig.style.sld.simple.OpacityViewer;
import org.locationtech.udig.style.sld.simple.RGBChannelViewer;
import org.locationtech.udig.style.sld.simple.ScaleViewer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.processing.OperationJAI;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.renderer.lite.gridcoverage2d.RasterSymbolizerHelper;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.SLD;
import org.geotools.styling.SelectedChannelType;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.filter.FilterFactory2;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Allow editing of a RasterSymbolizaer.
 * <p>
 * Currently this allows editing of the opacity, scale, and band selection.
 * </p>
 * 
 * @author mleslie
 * @author Emily Gouge (Refractions Research Inc.)
 * @since 1.0.
 */
public class SimpleRasterConfigurator extends AbstractSimpleConfigurator {

    private FilterFactory2 ff = null;
    private StyleFactory sf = null;

    private OpacityViewer opacity = new OpacityViewer();
    ScaleViewer minScale = new ScaleViewer(ScaleViewer.MIN);
    ScaleViewer maxScale = new ScaleViewer(ScaleViewer.MAX);

    private ChannelViewer redChannel = new ChannelViewer(
            Messages.SimpleRasterConfigurator_RedChannelLabel, Color.RED, 0);
    private ChannelViewer greenChannel = new ChannelViewer(
            Messages.SimpleRasterConfigurator_GreenChannelLabel, Color.GREEN, 1);
    private ChannelViewer blueChannel = new ChannelViewer(
            Messages.SimpleRasterConfigurator_BlueChannelLabel, Color.BLUE, 2);

    private RGBChannelViewer rgbViewer = new RGBChannelViewer(redChannel, greenChannel, blueChannel);

    private final UpdateHistoJob updateJob = new UpdateHistoJob();

    private Composite parent;
    private Label lblWarn;
    SelectionListener synchronize = new SelectionListener(){
        public void widgetSelected( SelectionEvent e ) {
            synchronize();
        }

        public void widgetDefaultSelected( SelectionEvent e ) {
            synchronize();
        }

    };

    /**
     * Construct <code>SimpleRasterConfigurator</code>.
     */
    public SimpleRasterConfigurator() {
        super();
        this.opacity.addListener(this.synchronize);
        this.minScale.addListener(this.synchronize);
        this.maxScale.addListener(this.synchronize);
        this.rgbViewer.addListener(this.synchronize);
    }

    @Override
    public boolean canStyle( Layer aLayer ) {
    	if (aLayer.hasResource(WebMapServer.class)){
    		return true;
    	}
    	
        if (aLayer.hasResource(GridCoverage.class)){
        	try{
        		GridCoverage gc = aLayer.getResource(GridCoverage.class, null);
        		if (gc.getNumSampleDimensions() >= 3){
        			return true;
        		}else{
        			return false;
        		}
        	}catch (Exception ex){
        		return false;
        	}
        }
        if (aLayer.hasResource(AbstractGridCoverage2DReader.class)){
        	return true;
        }
        return false;
    }

    @Override
    protected void refresh() {
        Display.getCurrent().asyncExec(new Runnable(){

            public void run() {
            	
            	boolean canStyle = canStyle(getLayer());
            	
            	lblWarn.setVisible(!canStyle);
            	for (Control c : parent.getChildren()){
            		if (!c.equals(lblWarn)){
            			c.setVisible(canStyle);
            		}
            	}
            	if (!canStyle){return;}
            	
                Style style = getStyle();
                RasterSymbolizer sym = SLD.rasterSymbolizer(style);

                // set opacity
                opacity.set(sym);

                // channel selection - setup band selection
                Layer l = getLayer();
                GridCoverage gc = null;
                try {
                    gc = (GridCoverage) l.getResource(GridCoverage.class, null);
                    String[] bands = new String[gc.getNumSampleDimensions()];
                    for( int i = 0; i < bands.length; i++ ) {
                        bands[i] = (i + 1) + Messages.SimpleRasterConfigurator_BandDelimiter
                                + gc.getSampleDimension(i).getDescription().toString();
                    }
                    rgbViewer.setBands(bands);
                } catch (Exception ex) {

                }

                Rule r = (SLD.rules(style))[0];
                double minScaleDen = r.getMinScaleDenominator();
                double maxScaleDen = r.getMaxScaleDenominator();
                minScale.setScale(minScaleDen, Math.round(getLayer().getMap().getViewportModel()
                        .getScaleDenominator()));
                maxScale.setScale(maxScaleDen, Math.round(getLayer().getMap().getViewportModel()
                        .getScaleDenominator()));

                // create a job to update histogram
                if (gc == null) {
                    // we need to set the rgb viewer to invalid
                    rgbViewer.setEditable(false);
                } else {
                    rgbViewer.setEditable(true);
                    rgbViewer.set(sym);
                    updateHistogram(sym, (GridCoverage2D) gc);
                }
            }
        });

    }

    private void updateHistogram( RasterSymbolizer rs, GridCoverage2D gc ) {
        updateJob.updateInfo(rs, gc);
        updateJob.schedule(500);
    }

    @Override
    public void createControl( Composite parent ) {

        // setLayout(parent);
    	this.parent = parent;
        parent.setLayout(new GridLayout(1, false));

        lblWarn = new Label(parent, SWT.WRAP);
        lblWarn.setText(Messages.SimpleRasterConfigurator_StyleUnavailable);
        lblWarn.setVisible(false);
        
        KeyAdapter adapter = new KeyAdapter(){
            @Override
            public void keyReleased( KeyEvent e ) {
                /*
                 * I don't like having different ways of checking for keypad enter and the normal
                 * one. Using the keyCode would be better, but I couldn't readily find the value for
                 * CR.
                 */
                if (e.keyCode == SWT.KEYPAD_CR || e.character == SWT.CR) {
                    makeActionDoStuff();
                }
            }
        };
        this.opacity.createControl(parent, adapter);

        // make a group out of the scale object
        Group g = new Group(parent, SWT.SHADOW_ETCHED_IN);
        g.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        g.setLayout(new GridLayout(2, true));
        g.setText(Messages.SimpleRasterConfigurator_ScaleGroup);
        this.minScale.createControl(g, adapter);
        this.maxScale.createControl(g, adapter);

        Composite c = rgbViewer.createControl(parent);
        c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    }

    @Override
    public void synchronize() {
    	boolean canStyle = canStyle(getLayer());
    	
    	lblWarn.setVisible(!canStyle);
    	for (Control c : parent.getChildren()){
    		if (!c.equals(lblWarn)){
    			c.setVisible(canStyle);
    		}
    	}
    	
    	
        // get the style off the blackboard and add/modify it
        Style s = (Style) getLayer().getStyleBlackboard().get(SLDContent.ID);

        // get raster symbolizer
        RasterSymbolizer rs = SLD.rasterSymbolizer(s);

        // setup scale
        Rule r = (SLD.rules(s))[0];
        if (minScale.isEnabled()) {
            r.setMinScaleDenominator(minScale.getScale());
        }
        if (maxScale.isEnabled()) {
            r.setMaxScaleDenominator(maxScale.getScale());
        }

        // setup opacity
        SLD.setRasterOpacity(s, this.opacity.getValue());

        // setup channels
        SelectedChannelType red = null;
        SelectedChannelType green = null;
        SelectedChannelType blue = null;

        if (rgbViewer.isEnabled()) {
            red = setChannel(rgbViewer.getRedChannel().getName(), rgbViewer.getRedChannel()
                    .getGamma());
            green = setChannel(rgbViewer.getGreenChannel().getName(), rgbViewer.getGreenChannel()
                    .getGamma());
            blue = setChannel(rgbViewer.getBlueChannel().getName(), rgbViewer.getBlueChannel()
                    .getGamma());
        }

        SLD.setChannelSelection(s, new SelectedChannelType[]{red, green, blue}, null);
        // get histogram and update
        GridCoverage2D gc = null;
        try {
            gc = (GridCoverage2D) getLayer().getResource(GridCoverage.class, null);
            if (gc != null) {
                updateHistogram(rs, gc);
            }
        } catch (Exception ex) {
        }
        // put the style on the blackboard
        getStyleBlackboard().put(SLDContent.ID, s);
        ((StyleBlackboard) getStyleBlackboard()).setSelected(new String[]{SLDContent.ID});
    }

    /*
     * sets up a channel based on the given values
     */
    private SelectedChannelType setChannel( String name, double gamma ) {
        return getStyleFactory().createSelectedChannelType(name,
                getStyleFactory().createContrastEnhancement(getFilterFactory().literal(gamma)));
    }

    private StyleFactory getStyleFactory() {
        if (sf == null) {
            sf = CommonFactoryFinder.getStyleFactory(null);
        }
        return sf;
    }

    private FilterFactory2 getFilterFactory() {
        if (ff == null) {
            ff = CommonFactoryFinder.getFilterFactory2(null);
        }
        return ff;

    }

    /**
     * Job of updating the histogram values.
     * <p>
     * </p>
     * 
     * @author Emily Gouge
     * @since 1.1.0
     */
    private class UpdateHistoJob extends Job {
        private RasterSymbolizer rs;
        private GridCoverage2D gc;

        public UpdateHistoJob() {
            super("Update Histogram Job"); //$NON-NLS-1$
        }
        public void updateInfo( RasterSymbolizer rs, GridCoverage2D gd ) {
            this.gc = gd;
            this.rs = rs;
        }

        @Override
        protected IStatus run( IProgressMonitor monitor ) {
            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    rgbViewer.updateHistograms(null);
                }
            });

            RasterSymbolizerHelper rsp = new RasterSymbolizerHelper(gc, null);
            rsp.visit(rs);
            GridCoverage2D recoloredGridCoverage = (GridCoverage2D) rsp.getOutput();

            final OperationJAI op = new OperationJAI("Histogram"); //$NON-NLS-1$
            ParameterValueGroup params = op.getParameters();
            params.parameter("Source").setValue(recoloredGridCoverage); //$NON-NLS-1$

            recoloredGridCoverage = (GridCoverage2D) op.doOperation(params, null);
            final Histogram h = (Histogram) recoloredGridCoverage.getProperty("histogram"); //$NON-NLS-1$

            Display.getDefault().asyncExec(new Runnable(){
                public void run() {
                    rgbViewer.updateHistograms(h.getBins());
                }
            });

            return Status.OK_STATUS;
        }

    };
}
