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
package org.locationtech.udig.style.sld.raster.internal;

import org.locationtech.udig.style.sld.SLDEditorPart;
import org.locationtech.udig.ui.graphics.SLDs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.StyleBuilder;

/**
 * A quick raster symbolizer that can only vary opacity.
 * 
 * @author aalam
 * @since 0.6.0
 */
public class SLDRasterEditorPart extends SLDEditorPart implements SelectionListener {

    private Composite myparent;
    private Scale opacityScale;
    private Text opacityText;

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#getContentType()
     */
    public Class getContentType() {
        return RasterSymbolizer.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#init()
     */
    public void init() {
        //Nothing to do...
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#reset()
     */
    public void reset() {
        // initialize the ui
        setStylingElements((RasterSymbolizer) getContent());
    }

    private void setStylingElements( RasterSymbolizer symbolizer ) {
         double number = SLDs.rasterOpacity(symbolizer);
         int opacity = (new Double(number * 100)).intValue();
               
         opacityScale.setSelection(opacity);
         opacityText.setText(Integer.toString(opacity) + "%"); //$NON-NLS-1$
         opacityText.pack(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    protected Control createPartControl( Composite parent ) {
        myparent = parent;
        RowLayout layout = new RowLayout();
        myparent.setLayout(layout);
        layout.pack = false;
        layout.wrap = true;
        layout.type = SWT.HORIZONTAL;
        
        /* Border Opacity */
        Group borderOpacityArea = new Group(myparent, SWT.NONE);
        borderOpacityArea.setLayout(new GridLayout(2, false));
        borderOpacityArea.setText(Messages.SLDRasterEditorPart_rasterOpactity_label); 
        
        opacityScale = new Scale(borderOpacityArea, SWT.HORIZONTAL);
        opacityScale.setMinimum(0);
        opacityScale.setMaximum(100);
        opacityScale.setPageIncrement(10);
        opacityScale.setBounds(0,0,10,SWT.DEFAULT);
        opacityScale.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                opacityText.setText(String.valueOf(opacityScale.getSelection()) + "%"); //$NON-NLS-1$
                opacityText.pack(true);
            }
        });
        opacityScale.addSelectionListener(this);

        opacityText = new Text(borderOpacityArea, SWT.BORDER | SWT.READ_ONLY);
        opacityText.pack(true);

        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetDefaultSelected( SelectionEvent e ) {
        // Meh! Meh I say!
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected( SelectionEvent e ) {
        apply();
    }

    /**
     * TODO summary sentence for apply ...
     */
    public void apply() {
        RasterSymbolizer symbolizer = (RasterSymbolizer) getContent();
        StyleBuilder styleBuilder = getStyleBuilder();

        double opacity = ((double) opacityScale.getSelection()) / 100;
        symbolizer.setOpacity(styleBuilder.literalExpression(opacity));
    }
}
