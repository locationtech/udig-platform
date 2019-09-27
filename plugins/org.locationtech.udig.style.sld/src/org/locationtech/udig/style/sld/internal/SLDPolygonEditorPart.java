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
package org.locationtech.udig.style.sld.internal;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.geotools.styling.Fill;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.StyleBuilder;
import org.locationtech.udig.style.sld.SLDEditorPart;
import org.locationtech.udig.ui.ColorEditor;
import org.locationtech.udig.ui.graphics.SLDs;

/**
 * Editor for polygon symbolizer
 * 
 * @author aalam
 * @since 0.6.0
 */
public class SLDPolygonEditorPart extends SLDEditorPart implements SelectionListener {

    private ColorEditor borderColour;
    private Button borderEnabled;
    private Spinner borderWidth;

    private ColorEditor fillColour;
    private Button fillEnabled;
    private Spinner borderOpacity;

    private int opacityMaxValue = 100;
    private double opacityMaxValueFloat = 100.0;
    private Spinner fillOpacity;

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#getContentType()
     */
    public Class getContentType() {
        return PolygonSymbolizer.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#init()
     */
    public void init() {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#reset()
     */
    public void reset() {
        setStylingElements((PolygonSymbolizer) getContent());
    }

    private void setStylingElements( PolygonSymbolizer symbolizer ) {
        Color fill = SLDs.polyFill(symbolizer);
        Color border = SLDs.polyColor(symbolizer);
        int width = SLDs.polyWidth(symbolizer);

        if (fill == null) {
            fill = SymbolizerContent.DEFAULT_FILL_COLOR;
            fillEnabled.setSelection(false);
        } else {
            fillEnabled.setSelection(true);
        }
        fillColour.setColorValue(new RGB(fill.getRed(), fill.getGreen(), fill.getBlue()));

        if (border == null) {
            border = SymbolizerContent.DEFAULT_BORDER_COLOR;
            borderEnabled.setSelection(false);
        } else {
            borderEnabled.setSelection(true);
        }
        borderColour.setColorValue(new RGB(border.getRed(), border.getGreen(), border.getBlue()));

        if (width == SLDs.NOTFOUND) {
            width = SymbolizerContent.DEFAULT_BORDER_WIDTH;
        }
        borderWidth.setSelection(width);

        double opacity = SLDs.polyBorderOpacity(symbolizer);
        if (Double.isNaN(opacity)) {
            opacity = SymbolizerContent.DEFAULT_POLY_BORDER_OPACITY;
        }
        borderOpacity.setSelection((int) (opacity * opacityMaxValue));

        opacity = SLDs.polyFillOpacity(symbolizer);
        if (Double.isNaN(opacity)) {
            opacity = SymbolizerContent.DEFAULT_POLY_FILL_OPACITY;
        }
        fillOpacity.setSelection((int) (opacity * opacityMaxValue));
    }

    public PolygonSymbolizer getContent() {
        return (PolygonSymbolizer) super.getContent();
    }

    private void applyFill( PolygonSymbolizer polygonSymbolizer, StyleBuilder styleBuilder ) {
        if (fillEnabled.getSelection()) {
            RGB c = fillColour.getColorValue();
            Fill fill = polygonSymbolizer.getFill();
            if (fill == null) {
                fill = styleBuilder.createFill();
                polygonSymbolizer.setFill(fill);
            }

            fill.setColor(styleBuilder.colorExpression(new Color(c.red, c.green, c.blue)));
            fill.setOpacity(styleBuilder.literalExpression(fillOpacity.getSelection()
                    / opacityMaxValueFloat));
        } else {
            polygonSymbolizer.setFill(null);
        }
    }

    private void applyBorder( PolygonSymbolizer polygonSymbolizer, StyleBuilder styleBuilder ) {
        if (borderEnabled.getSelection()) {
            RGB c = borderColour.getColorValue();
            Stroke stroke = polygonSymbolizer.getStroke();
            if (stroke == null) {
                stroke = styleBuilder.createStroke();
                polygonSymbolizer.setStroke(stroke);
            }

            stroke.setColor(styleBuilder.colorExpression(new Color(c.red, c.green, c.blue)));
            stroke.setWidth(styleBuilder.literalExpression(borderWidth.getSelection()));
            stroke.setOpacity(styleBuilder.literalExpression(borderOpacity.getSelection()
                    / opacityMaxValueFloat));
        } else {
            polygonSymbolizer.setStroke(null);
        }
    }

    /**
     * Construct a subpart labeled with the provided tag.
     * <p>
     * Creates a composite with a grid layout of the specifed columns, and a label with text from
     * tag.
     * </p>
     * 
     * @param parent
     * @param tag
     * @param numColumns number of columns (usually 2_
     * @return Composite with one label
     */
    private Composite subpart( Composite parent, String tag, int width ) {
        Composite subpart = new Composite(parent, SWT.NONE);
        RowLayout across = new RowLayout();
        across.type = SWT.HORIZONTAL;
        across.wrap = true;
        across.pack = true;
        across.fill = true;
        across.marginBottom = 1;
        across.marginRight = 2;

        subpart.setLayout(across);

        Label label = new Label(subpart, SWT.NONE);
        label.setText(tag);
        label.setAlignment(SWT.RIGHT);
        RowData data = new RowData();
        data.width = 40;
        data.height = 10;
        label.setLayoutData(data);

        return subpart;
    }
    private void borderPart( Composite parent ) {
        Composite border = subpart(parent, Messages.SLDPolygonEditorPart_label_border, 4);

        borderEnabled = new Button(border, SWT.CHECK);
        borderEnabled.addSelectionListener(new SelectionListener(){
            public void widgetSelected( SelectionEvent e ) {
                borderColour.getButton().setEnabled(borderEnabled.getSelection());
                borderWidth.setEnabled(borderEnabled.getSelection());
                borderOpacity.setEnabled(borderEnabled.getSelection());
            }
            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });
//        borderEnabled.setToolTipText(Messages.SLDMarkerEditorPart_boder_enabled_tooltip); 

        borderColour = new ColorEditor(border);
        borderColour.addButtonSelectionListener(this);

        borderWidth = new Spinner(border, SWT.NONE);
        borderWidth.setMinimum(1);
        borderWidth.setMaximum(30);
        borderWidth.setPageIncrement(5);
        borderWidth.addSelectionListener(this);
//        borderWidth.setToolTipText(Messages.SLDMarkerEditorPart_boder_width_tooltip); 

        borderOpacity = new Spinner(border, SWT.NONE);
        borderOpacity.setMinimum(0);
        borderOpacity.setMaximum(opacityMaxValue);
        borderOpacity.setPageIncrement(10);
//        borderOpacity.setToolTipText(Messages.SLDMarkerEditorPart_boder_opacity_tooltip); 
    }
    private void fillPart( Composite parent ) {
        Composite fill = subpart(parent, Messages.SLDPolygonEditorPart_label_fill, 3);
        fillEnabled = new Button(fill, SWT.CHECK);
        fillEnabled.addSelectionListener(this);
//        fillEnabled.setToolTipText(Messages.SLDMarkerEditorPart_marker_enabled_tooltip); 
        fillEnabled.addSelectionListener(new SelectionListener(){
            public void widgetSelected( SelectionEvent e ) {
                fillColour.getButton().setEnabled(fillEnabled.getSelection());
                fillOpacity.setEnabled(fillEnabled.getSelection());
            }
            public void widgetDefaultSelected( SelectionEvent e ) {
            }
        });

        fillColour = new ColorEditor(fill);
        fillColour.addButtonSelectionListener(this);

        fillOpacity = new Spinner(fill, SWT.NONE);
        fillOpacity.setMinimum(0);
        fillOpacity.setMaximum(opacityMaxValue);
        fillOpacity.setPageIncrement(10);
//        fillOpacity.setToolTipText(Messages.SLDMarkerEditorPart_fill_opacity_tooltip); 
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
        fillPart(parent);

        return parent;
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        // TODO: Commit the style
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    public void widgetSelected( SelectionEvent e ) {
        applyFill(getContent(), getStyleBuilder());
        applyBorder(getContent(), getStyleBuilder());
    }
}
