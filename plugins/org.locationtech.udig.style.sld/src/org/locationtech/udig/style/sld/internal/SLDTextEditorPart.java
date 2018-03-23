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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.styling.HaloImpl;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.TextSymbolizer;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.sld.SLDEditorPart;
import org.locationtech.udig.style.sld.editor.FontEditor;
import org.locationtech.udig.ui.ColorEditor;
import org.locationtech.udig.ui.graphics.SLDs;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.expression.Expression;


/**
 * summary sentence.
 * <p>
 * Paragraph ...
 * </p>
 * <p>
 * Responsibilities:
 * <ul>
 * <li>
 * <li>
 * </ul>
 * </p>
 * <p>
 * Example:
 * 
 * <pre><code>
 * 
 *  SLDTextEditorPart x = new SLDTextEditorPart( ... );
 *  TODO code example
 *  
 * </code></pre>
 * 
 * </p>
 * 
 * @author aalam
 * @since 0.6.0
 */
public class SLDTextEditorPart extends SLDEditorPart {

    private static final int LABEL = 1;
    private static final int FONTCOLOR = 2;
    private static final int HALOCOLOR = 2;

    private ColorEditor labelHaloColorEditor;
    private FontEditor labelFont;
    private Combo labelCombo;
    private Button labelHaloEnabled;
    private Spinner haloWidthScale;
    private Spinner haloOpacityScale;
    private int opacityMaxValue = 100;
    private double opacityMaxValueFloat = 100.0;

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#getContentType()
     */
    public Class getContentType() {
        return TextSymbolizer.class;
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
    
    private void labelPart( Composite parent ) {
        Composite part = subpart(parent, Messages.SLDTextEditorPart_label_label, 2); 

        labelCombo = new Combo(part, SWT.READ_ONLY);
        labelCombo.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                apply(LABEL);
            }
        });

        labelFont = new FontEditor( part, new SelectionAdapter(){
            public void widgetSelected( SelectionEvent event ) {
                apply(FONTCOLOR);
            }
        });
    }
    private void haloPart( Composite parent ) {
        Composite halo = subpart(parent, Messages.SLDTextEditorPart_label_halo, 3);
        
        labelHaloEnabled = new Button(halo, SWT.CHECK);
        labelHaloEnabled.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent event ) {
                apply(HALOCOLOR);
            }
        });

        labelHaloColorEditor = new ColorEditor(halo, new SelectionAdapter(){
            public void widgetSelected( SelectionEvent event ) {
                apply(HALOCOLOR);
            }
        });
        
        haloWidthScale = new Spinner(halo, SWT.HORIZONTAL);
        haloWidthScale.setMinimum(0);
        haloWidthScale.setMaximum(10);
        haloWidthScale.setPageIncrement(1);
        haloWidthScale.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                apply(HALOCOLOR);
            }
        });

        
        haloOpacityScale = new Spinner(halo, SWT.HORIZONTAL);
        haloOpacityScale.setMinimum(0);
        haloOpacityScale.setMaximum(opacityMaxValue);
        haloOpacityScale.setPageIncrement(10);
        haloOpacityScale.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected( SelectionEvent e ) {
                apply(HALOCOLOR);
            }
        });

    }
    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
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

        labelPart( parent );
        haloPart( parent );
                
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#init()
     */
    public void init() {
        // do nothing
    }

    private void setStylingElements( TextSymbolizer symbolizer ) {
        
        Color fontFill = SLDs.textFontFill(symbolizer);
        FontData[] tempFD = SLDs.textFont(symbolizer);
        Expression label = SLDs.textLabel(symbolizer);
        if (fontFill == null) {
            fontFill = SymbolizerContent.DEFAULT_FONT_COLOR;
        }
        labelFont.setColorValue(
                new RGB(fontFill.getRed(), fontFill.getGreen(), fontFill.getBlue()));
        if (tempFD == null) {
            tempFD = new FontData[1];
            tempFD[0] = new FontData(SymbolizerContent.DEFAULT_FONT_FACE,
                                    SymbolizerContent.DEFAULT_FONT_SIZE,
                                    SymbolizerContent.DEFAULT_FONT_STYLE);
        }
        labelFont.setFontList(tempFD);
        
        // Need to get all available labels 
        //check if this layer has a feature 
        Layer currLayer = getLayer();
        List<AttributeDescriptor> attributeList = null;
        AttributeDescriptor defaultGeom = null;
        if (currLayer.hasResource(FeatureSource.class)) {
            SimpleFeatureType ft = currLayer.getSchema();
            attributeList = ft.getAttributeDescriptors();
            defaultGeom=ft.getGeometryDescriptor();
        }
        labelCombo.removeAll();
        if (attributeList != null) {
            for( int i = 0; i < attributeList.size(); i++ ) {
                AttributeDescriptor attributeDescriptor = attributeList.get(i);
				if( attributeDescriptor != defaultGeom )
                    labelCombo.add(attributeDescriptor.getName().getLocalPart());
                if( label != null && attributeDescriptor != null &&
                        attributeDescriptor.getName().equals(label.toString()) ) {
                    //Set the correct initial label
                    labelCombo.select(i);
                } else if( i == 0 ) {
                    labelCombo.select(i);
                }
            }
        }
        labelCombo.pack(true);

        Color haloFill = SLDs.textHaloFill(symbolizer);
        if (haloFill == null ) {
            haloFill = SymbolizerContent.DEFAULT_HALO_COLOR;
            labelHaloEnabled.setSelection(false);
        } else {
            labelHaloEnabled.setSelection(true);
        }
        labelHaloColorEditor.setColorValue(
                new RGB(haloFill.getRed(), haloFill.getGreen(), haloFill.getBlue()));
        
        int width = SLDs.textHaloWidth(symbolizer);
        if( width == 0 ) {
            width = (int)(SymbolizerContent.DEFAULT_HALO_WIDTH);
        }
        haloWidthScale.setSelection(width);
        
        double opacity = SLDs.textHaloOpacity(symbolizer);
        if( Double.isNaN(opacity) ) {
            opacity = SymbolizerContent.DEFAULT_HALO_OPACITY;
        }
        haloOpacityScale.setSelection((int)(opacity*opacityMaxValue));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.sld.SLDEditorPart#reset()
     */
    public void reset() {
        setStylingElements((TextSymbolizer) getContent());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.locationtech.udig.style.IStyleConfigurator#apply()
     */
    private void apply( int mask ) {
        TextSymbolizer textSymbolizer = (TextSymbolizer) getContent();
        if ((mask & LABEL) != 0) {
            applyLabel(textSymbolizer, getStyleBuilder());
        }
        if ((mask & FONTCOLOR) != 0) {
            applyLabel(textSymbolizer, getStyleBuilder());
        }
    }

    private void applyLabel( TextSymbolizer textSymbolizer, StyleBuilder styleBuilder ) {

        Expression currLabel = null;
        if (labelCombo.getSelectionIndex() != -1) {
            String selectedLabel = labelCombo.getItem(labelCombo.getSelectionIndex());

            RGB xfontColor = labelFont.getColorValue();
            textSymbolizer.setFill(styleBuilder.createFill(
                    new Color(xfontColor.red, xfontColor.green, xfontColor.blue)));

            FontData[] fd = labelFont.getFontList();
            String fontName = fd[0].getName();
            boolean fontBold = (fd[0].getStyle() == SWT.BOLD);
            boolean fontItalic = (fd[0].getStyle() == SWT.ITALIC);
            double fontSize = fd[0].getHeight();
            org.geotools.styling.Font[] font = new org.geotools.styling.Font[1];
            font[0] = styleBuilder.createFont(fontName, fontItalic, fontBold, fontSize);
            textSymbolizer.setFonts(font);

            if (labelHaloEnabled.getSelection()) {
                RGB haloColor = labelHaloColorEditor.getColorValue();
                if (textSymbolizer.getHalo() != null) {
                    if (textSymbolizer.getHalo().getFill() != null) {
                        textSymbolizer.getHalo().getFill().setColor(
                                styleBuilder.colorExpression(new Color(haloColor.red,
                                        haloColor.green, haloColor.blue)));
                    } else {
                        textSymbolizer.getHalo().setFill(
                                styleBuilder.createFill(new Color(haloColor.red, haloColor.green,
                                        haloColor.blue)));
                    }
                    textSymbolizer.getHalo().setRadius(
                            styleBuilder.literalExpression(haloWidthScale.getSelection()));
                    textSymbolizer.getHalo().getFill().setOpacity(
                            styleBuilder.literalExpression(
                                    haloOpacityScale.getSelection()/opacityMaxValueFloat));
                } else {
                    HaloImpl halo = new HaloImpl();
                    textSymbolizer.setHalo(halo);
                    textSymbolizer.getHalo().setFill(
                            styleBuilder.createFill(new Color(haloColor.red, haloColor.green,
                                    haloColor.blue)));
                    textSymbolizer.getHalo().setRadius(
                            styleBuilder.literalExpression(haloWidthScale.getSelection()));
                    textSymbolizer.getHalo().getFill().setOpacity(
                            styleBuilder.literalExpression(
                                    haloOpacityScale.getSelection()/opacityMaxValueFloat));
                }
            } else {
                textSymbolizer.setHalo(null);
            }

            currLabel = (Expression) CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints()).property(selectedLabel);
            textSymbolizer.setLabel(currLabel);
        }
    }
}
