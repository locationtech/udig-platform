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
package net.refractions.udig.legend.ui;

import java.awt.Color;

import net.refractions.udig.legend.internal.Messages;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.project.ui.internal.dialogs.ColorEditor;
import net.refractions.udig.style.IStyleConfigurator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class LegendGraphicStyleConfigurator extends IStyleConfigurator implements SelectionListener, ModifyListener {

    private Text verticalMargin;
    private Text horizontalMargin;
    private Text verticalSpacing;
    private Text horizontalSpacing;
    private Text indentSize;
    private ColorEditor fontColour;
    private ColorEditor backgroundColour;
    
    private LegendStyle style = null;
    
    private boolean fireEvents = true;
    
    /*
     *         verticalMargin = 3; 
        horizontalMargin = 2; 
        verticalSpacing = 5; 
        horizontalSpacing = 3; 
        indentSize = 10;
        imageHeight = 16;
        imageWidth = 16;
        maxWidth = -1;
        maxHeight = -1;
               
        foregroundColour = Color.BLACK;
        backgroundColour = Color.WHITE;
        
        location = new Point(30, 10);
     */
    
    public void createControl( Composite parent) {
        ScrolledComposite scrollComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        Composite composite = new Composite(scrollComposite, SWT.BORDER);
        
        GridLayout layout = new GridLayout(2, true);
        composite.setLayout(layout);
        
        GridData layoutData = null;

        Label verticalMarginLabel = new Label(composite, SWT.NONE);
        verticalMarginLabel.setText(Messages.LegendGraphicStyleConfigurator_vertical_margin);
        verticalMarginLabel.setLayoutData(layoutData);
        verticalMargin = new Text(composite, SWT.BORDER);
        verticalMargin.setLayoutData(layoutData);
        

        Label horizontalMarginLabel = new Label(composite, SWT.NONE);
        horizontalMarginLabel.setLayoutData(layoutData);
        horizontalMarginLabel.setText(Messages.LegendGraphicStyleConfigurator_horizontal_margin);
        horizontalMargin = new Text(composite, SWT.BORDER);     
        horizontalMargin.setLayoutData(layoutData);
        
        Label verticalSpacingLabel = new Label(composite, SWT.NONE);
        verticalSpacingLabel.setLayoutData(layoutData);
        verticalSpacingLabel.setText(Messages.LegendGraphicStyleConfigurator_vertical_spacing);
        verticalSpacing = new Text(composite, SWT.BORDER);
        verticalSpacing.setLayoutData(layoutData);
        
        Label horizontalSpacingLabel = new Label(composite, SWT.NONE);
        horizontalSpacingLabel.setLayoutData(layoutData);
        horizontalSpacingLabel.setText(Messages.LegendGraphicStyleConfigurator_horizontal_spacing);
        horizontalSpacing = new Text(composite, SWT.BORDER);
        horizontalSpacing.setLayoutData(layoutData);
        
        Label indentSizeLabel = new Label(composite, SWT.NONE);
        indentSizeLabel.setLayoutData(layoutData);
        indentSizeLabel.setText(Messages.LegendGraphicStyleConfigurator_indent_size);
        indentSize = new Text(composite, SWT.BORDER);
        indentSize.setLayoutData(layoutData);
        
        Label fontColourLabel = new Label(composite, SWT.NONE);
        fontColourLabel.setLayoutData(layoutData);
        fontColourLabel.setText(Messages.LegendGraphicStyleConfigurator_font_colour);
        fontColour = new ColorEditor(composite);
       
        Label backgroundColourLabel = new Label(composite, SWT.NONE);
        backgroundColourLabel.setLayoutData(layoutData);
        backgroundColourLabel.setText(Messages.LegendGraphicStyleConfigurator_background_colour);
        backgroundColour = new ColorEditor(composite);
        
        composite.layout();
        Point size = composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        composite.setSize(size);
        scrollComposite.setContent(composite);
 
        verticalMargin.addModifyListener(this);
        horizontalMargin.addModifyListener(this);
        verticalSpacing.addModifyListener(this);
        horizontalSpacing.addModifyListener(this);
        indentSize.addModifyListener(this);
        backgroundColour.addSelectionListener(this);
        fontColour.addSelectionListener(this);
    }
    
    @Override
    public boolean canStyle( Layer aLayer ) {
        return aLayer.hasResource(LegendGraphic.class);
    }

    @Override
    protected void refresh() {
        LegendStyle oldstyle = (LegendStyle) getStyleBlackboard().get(LegendStyleContent.ID);
        
        if (oldstyle == null) {
            oldstyle = LegendStyleContent.createDefault();
        }
        style = new LegendStyle(oldstyle);
        
        //update components
        fireEvents = false;
        verticalMargin.setText(Integer.toString(style.verticalMargin));
        horizontalMargin.setText(Integer.toString(style.horizontalMargin));
        verticalSpacing.setText(Integer.toString(style.verticalSpacing));
        horizontalSpacing.setText(Integer.toString(style.horizontalSpacing));
        indentSize.setText(Integer.toString(style.indentSize));
        fontColour.setColorValue(new RGB(
                style.foregroundColour.getRed(),
                style.foregroundColour.getGreen(), 
                style.foregroundColour.getBlue()));
        backgroundColour.setColorValue(new RGB(
                style.backgroundColour.getRed(),
                style.backgroundColour.getGreen(),
                style.backgroundColour.getBlue()
                ));
        fireEvents = true;
        updateBlackboard();
    }
    
    private void updateBlackboard() {
        
    	RGB bg = backgroundColour.getColorValue();
        style.backgroundColour = new Color(bg.red, bg.green, bg.blue);
        
        RGB fg = fontColour.getColorValue();
        style.foregroundColour = new Color(fg.red, fg.green, fg.blue);
        
        style.horizontalMargin = Integer.parseInt(horizontalMargin.getText());
        style.horizontalSpacing = Integer.parseInt(horizontalSpacing.getText());
        style.indentSize = Integer.parseInt(indentSize.getText());
        style.verticalMargin = Integer.parseInt(verticalMargin.getText());
        style.verticalSpacing = Integer.parseInt(verticalSpacing.getText());
        
        getStyleBlackboard().put(LegendStyleContent.ID, style);
    }

    public void widgetSelected( SelectionEvent e ) {
        updateBlackboard();
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
        updateBlackboard();
    }

    public void modifyText( ModifyEvent e ) {
    	if (fireEvents){
    		updateBlackboard();
    	}
    }
}
