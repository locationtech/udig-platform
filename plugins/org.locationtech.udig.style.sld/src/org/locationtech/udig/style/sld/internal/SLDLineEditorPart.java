/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.style.sld.internal;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleBuilder;
import org.locationtech.udig.style.sld.SLDEditorPart;
import org.locationtech.udig.ui.ColorEditor;
import org.locationtech.udig.ui.graphics.SLDs;

/**
 * Edit a line symbolizer.
 * 
 * @author aalam
 * @since 0.6.0
 */
public class SLDLineEditorPart extends SLDEditorPart implements SelectionListener {

    private ColorEditor lineColourEditor;
    Spinner lineWidth;
    Spinner lineOpacity;
    Combo linejoinCombo;
    Combo linecapCombo;
    int opacityMaxValue = 100;
    double opacityMaxValueFloat = 100.0;

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#getContentType()
     */
    public Class<?> getContentType() {
        return LineSymbolizer.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#init()
     */
    public void init() {
        //Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#reset()
     */
    public void reset() {
        // initialize the ui
        setStylingElements((LineSymbolizer) getContent());

    }

    private void setStylingElements( LineSymbolizer symbolizer ) {

        Color colour = null;
        int width = SLDs.NOTFOUND;

        Stroke stroke = symbolizer.getStroke();
        if (stroke != null) {
            colour = SLDs.lineColor(symbolizer);
            width = SLDs.lineWidth(symbolizer);

        }
        if (colour == null) {
            colour = SymbolizerContent.DEFAULT_LINE_COLOR;
        }
        lineColourEditor
                .setColorValue(new RGB(colour.getRed(), colour.getGreen(), colour.getBlue()));
        if (width == SLDs.NOTFOUND) {
            width = SymbolizerContent.DEFAULT_LINE_WIDTH;
        }
        lineWidth.setSelection(width);
        //lineWidthText.setText(Integer.toString(width));
        //lineWidthText.pack(true);
        
        double opacity = SLDs.lineOpacity(symbolizer);
        if( Double.isNaN(opacity) ) {
            opacity = SymbolizerContent.DEFAULT_LINE_OPACITY;
        }
        lineOpacity.setSelection((int)(opacity*opacityMaxValue));
        //lineOpacityText.setText(Integer.toString((int)(opacity*opacityMaxValue)) + "%"); //$NON-NLS-1$
        //lineOpacityText.pack(true);
        
        //TODO: fix all these hard coded defaults etc...
        String linejoin = SLDs.lineLinejoin(symbolizer);
        String[] options = new String[] {"mitre","round","bevel" };   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        linejoinCombo.setItems(options);
        if (linejoin == null) {
            linejoin = SymbolizerContent.DEFAULT_LINE_LINEJOIN;
        }
        int index = linejoinCombo.indexOf(linejoin);
        if (index == -1) {
            linejoinCombo.add(linejoin);
            linejoinCombo.select(0);
        } else {
            linejoinCombo.select(index);
        }
        
//      TODO: fix all these hard coded defaults etc...
        String linecap = SLDs.lineLinecap(symbolizer);
        String[] lcoptions = new String[] {"butt", "round", "square" };   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        linecapCombo.setItems(lcoptions);
        if (linecap == null) {
            linecap = SymbolizerContent.DEFAULT_LINE_LINECAP;
        }
        int lcindex = linecapCombo.indexOf(linecap);
        if (lcindex == -1) {
            linecapCombo.add(linecap);
            linecapCombo.select(0);
        } else {
            linecapCombo.select(lcindex);
        }
        
//        TODO: This functionality to be added later with a better UI...goes with other task
//        float[] lineDash = SLDs.lineDash(symbolizer);
//        if( lineDash.length == 2 ) {
//            lineDashText.setText(Float.toString(lineDash[0]));
//            lineSpaceText.setText(Float.toString(lineDash[1]));
//        } else {
//            lineDashText.setText(""); //$NON-NLS-1$
//            lineSpaceText.setText(""); //$NON-NLS-1$
//        }
    }
    
    /**
     * Construct a subpart labeled with the provided tag.
     * <p>
     * Creates a composite with a grid layout of the specifed columns,
     * and a label with text from tag.
     * </p>
     * @param parent
     * @param tag
     * @param numColumns number of columns (usually 2_
     * @return Composite with one label 
     */
    private Composite subpart( Composite parent, String tag, int width  ){
        Composite subpart = new Composite( parent, SWT.NONE );        
        RowLayout across = new RowLayout();
        across.type = SWT.HORIZONTAL;
        across.wrap = true;
        across.pack = true;
        across.fill = true;
        across.marginBottom = 1;
        across.marginRight = 2;
        
        subpart.setLayout( across );
        
        Label label = new Label( subpart, SWT.NONE );
        label.setText(tag);
        label.setAlignment( SWT.RIGHT );
        RowData data = new RowData();
        data.width = 40;
        data.height = 10;
        label.setLayoutData( data );
                
        return subpart;
    }
    private void strokePart( Composite parent ) {
        Composite stroke = subpart( parent, Messages.SLDLineEditorPart_label_stroke, 2 );
        
        linejoinCombo = new Combo( stroke, SWT.READ_ONLY);
        linejoinCombo.addSelectionListener(this);
    
        linecapCombo = new Combo(stroke, SWT.READ_ONLY);
        linecapCombo.addSelectionListener(this);
    }
    private void borderPart( Composite parent ) {
        Composite border = subpart( parent, Messages.SLDLineEditorPart_label_border , 4 ); 
        
        Button borderEnabled = new Button( border, SWT.CHECK);
        borderEnabled.setEnabled( false );
        borderEnabled.setSelection( true );
        
        lineColourEditor = new ColorEditor(border, this);
        
        lineWidth = new Spinner( border, SWT.HORIZONTAL);     
        lineWidth.setMinimum(1);
        lineWidth.setMaximum(30);
        lineWidth.setPageIncrement(5);
        lineWidth.addSelectionListener(this);
        
        lineOpacity = new Spinner( border, SWT.HORIZONTAL);
        lineOpacity.setMinimum(0);
        lineOpacity.setMaximum(opacityMaxValue);
        lineOpacity.setPageIncrement(10);
        lineOpacity.addSelectionListener( this );
    }
    
    protected Control createPartControl( Composite parent ) {
        RowLayout layout = new RowLayout();        
        layout.pack = false;
        layout.wrap = true;
        layout.type = SWT.HORIZONTAL;
        layout.fill = true;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        layout.spacing = 0;
        parent.setLayout(layout);
        
        borderPart(parent);
        strokePart(parent);        
                
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
     * Reflects the changes from the UI to the symbolizer
     * 
     */
    public void apply() {
        LineSymbolizer symbolizer = (LineSymbolizer) getContent();
        StyleBuilder styleBuilder = getStyleBuilder();

        Stroke stroke = symbolizer.getStroke();
        if (stroke == null) {
            stroke = getStyleBuilder().createStroke();
            symbolizer.setStroke(stroke);
        }

        Color c = lineColourEditor.getColor();

        stroke.setWidth(styleBuilder.literalExpression(lineWidth.getSelection()));
        stroke.setColor(styleBuilder.colorExpression(c));
        stroke.setOpacity(styleBuilder.literalExpression(lineOpacity.getSelection()
                / opacityMaxValueFloat));
        stroke.setLineJoin(styleBuilder.literalExpression(linejoinCombo.getText()));
        stroke.setLineCap(styleBuilder.literalExpression(linecapCombo.getText()));

//      TODO: This functionality to be added later with a better UI...goes with other task
//        if (!lineDashText.getText().equalsIgnoreCase("")
//                && !lineSpaceText.getText().equalsIgnoreCase("")) {
//            float dash = Float.parseFloat(lineDashText.getText());
//            float space = Float.parseFloat(lineSpaceText.getText());
//            if (dash > 0) {
//                stroke.setDashArray(new float[]{dash, space});
//            } else {
//                stroke.setDashArray(new float[]{10.0f, 0.0f});
//            }
//        } else {
//            stroke.setDashArray(new float[]{10.0f, 0.0f});
//        }
    }
}
