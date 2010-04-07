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
package net.refractions.udig.style.sld.internal;

import net.refractions.udig.style.sld.SLDEditorPart;
import net.refractions.udig.ui.graphics.SLDs;

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
     * @see net.refractions.udig.style.sld.SLDEditorPart#getContentType()
     */
    public Class getContentType() {
        return RasterSymbolizer.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.style.sld.SLDEditorPart#init()
     */
    public void init() {
        //Nothing to do...
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.refractions.udig.style.sld.SLDEditorPart#reset()
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
     * @see net.refractions.udig.style.sld.SLDEditorPart#createPartControl(org.eclipse.swt.widgets.Composite)
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
        borderOpacityArea.setText("Raster Opacity"); //$NON-NLS-1$
        
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
