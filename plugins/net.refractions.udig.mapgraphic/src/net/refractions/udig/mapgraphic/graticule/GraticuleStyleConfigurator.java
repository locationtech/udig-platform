/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004-2012, Refractions Research Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package net.refractions.udig.mapgraphic.graticule;

import java.awt.Color;

import net.refractions.udig.mapgraphic.internal.Messages;
import net.refractions.udig.project.internal.Layer;
import net.refractions.udig.style.IStyleConfigurator;
import net.refractions.udig.ui.graphics.ViewportGraphics;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;

/**
 * Edit GraticuleStyle objects
 * 
 * @author Jesse
 * @author kengu
 * @since 1.3.3
 */
public class GraticuleStyleConfigurator extends IStyleConfigurator implements Listener {

    private static final String LINE_DASH = "dash"; //$NON-NLS-1$
    private static final String LINE_DASHDOT = "dash-dot"; //$NON-NLS-1$
    private static final String LINE_DASHDOTDOT = "dash-dot-dot"; //$NON-NLS-1$
    private static final String LINE_DOT = "dot"; //$NON-NLS-1$
    private static final String LINE_SOLID = "solid"; //$NON-NLS-1$
    private static final String[] LINE_STYLES = new String[]
    {
        LINE_SOLID, LINE_DASH, LINE_DOT, LINE_DASHDOT, LINE_DASHDOTDOT
    };

    Combo lineStyle;
    Button fontColor;
    Button lineColor;
    Button showLabels;
    Spinner lineWidth;
    Scale opacity;
    Label messageLabel;
    Label fontColorLabel;
    Label fontColorPreview;
    Label lineColorLabel;
    Label lineColorPreview;
    Label lineStyleLabel;
    Label lineWidthLabel;
    Label showLabelsLabel;
    Label opacityLabel;
    
    private GraticuleStyle style;
    private ColorListener colorListener;
    private Composite container;

    @Override
    public boolean canStyle( Layer aLayer ) {
        return aLayer.hasResource(GraticuleGraphic.class)
                && aLayer.getStyleBlackboard().get(GraticuleStyle.ID) != null;
    }

    @Override
    public void createControl( Composite parent ) {
        parent.setLayout(new FillLayout());

        Composite widgets = createWidgets(parent);
        addListeners();
        layoutWidgets(widgets);
    }
    
    protected GraticuleStyle getStyle() {
        return style;
    }

    private void layoutWidgets( Composite comp ) {
        comp.setLayout(new GridLayout(3, false));

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);

        layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
        lineStyleLabel.setLayoutData(layoutData);        
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        lineStyle.setLayoutData(layoutData);

        layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
        lineWidthLabel.setLayoutData(layoutData);        
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        lineWidth.setLayoutData(layoutData);

        layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        lineColorLabel.setLayoutData(layoutData);
        layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        lineColorPreview.setLayoutData(layoutData);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        lineColor.setLayoutData(layoutData);

        layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        fontColorLabel.setLayoutData(layoutData);
        layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
        fontColorPreview.setLayoutData(layoutData);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        fontColor.setLayoutData(layoutData);
        
        layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
        showLabelsLabel.setLayoutData(layoutData);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        showLabels.setLayoutData(layoutData);
        
        layoutData = new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1);
        opacityLabel.setLayoutData(layoutData);
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        opacity.setLayoutData(layoutData);
        
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        messageLabel.setLayoutData(layoutData);

    }

    private void addListeners() {
        colorListener = new ColorListener(this);
        fontColor.addSelectionListener(colorListener);
        lineColor.addSelectionListener(colorListener);
        lineStyle.addListener(SWT.Modify, this);
        lineStyle.addListener(SWT.KeyUp, this);
        lineWidth.addListener(SWT.Modify, this);
        lineWidth.addListener(SWT.KeyUp, this);
        showLabels.addListener(SWT.Selection, this);
        opacity.addListener(SWT.Selection, this);
    }

    private void removeListeners() {

        fontColor.removeSelectionListener(colorListener);
        lineColor.removeSelectionListener(colorListener);
        lineStyle.removeListener(SWT.Modify, this);
        lineWidth.removeListener(SWT.Modify, this);
        lineStyle.removeListener(SWT.KeyUp, this);
        lineWidth.removeListener(SWT.KeyUp, this);
        showLabels.removeListener(SWT.Selection, this);
        opacity.removeListener(SWT.Selection, this);
    }

    private Composite createWidgets( Composite parent ) {
        container = new Composite(parent, SWT.NONE);

        fontColorLabel = new Label(container, SWT.NONE);
        fontColorLabel.setText(Messages.GraticuleStyleConfigurator_Font_Color);
        fontColorPreview = new Label(container, SWT.NONE);
        fontColorPreview.addListener(SWT.Resize, new Listener(){
            
            public void handleEvent( Event event ) {
                updateColorButton(fontColor, fontColorLabel, fontColorPreview);
            }
            
        });
        fontColor = new Button(container, SWT.PUSH);        
        fontColor.setText(Messages.GridStyleConfigurator_ChangeColor);
        
        lineColorLabel = new Label(container, SWT.NONE);
        lineColorLabel.setText(Messages.GridStyleConfigurator_LineColor);

        lineColorPreview = new Label(container, SWT.NONE);
        lineColorPreview.addListener(SWT.Resize, new Listener(){
            
            public void handleEvent( Event event ) {
                updateColorButton(lineColor, lineColorLabel, lineColorPreview);
            }
            
        });
        lineColor = new Button(container, SWT.PUSH);
        lineColor.setText(Messages.GridStyleConfigurator_ChangeColor);

        lineStyleLabel = new Label(container, SWT.NONE);
        lineStyleLabel.setText(Messages.GridStyleConfigurator_LineStyle);
        lineStyle = new Combo(container, SWT.NONE);
        lineStyle.setItems(LINE_STYLES);
        lineStyle.select(0);

        lineWidthLabel = new Label(container, SWT.NONE);
        lineWidthLabel.setText(Messages.GridStyleConfigurator_LineWidth);
        lineWidth = new Spinner(container, SWT.NONE);
        lineWidth.setIncrement(1);
        lineWidth.setDigits(0);
        lineWidth.setMinimum(1);
        
        showLabelsLabel = new Label(container, SWT.NONE);
        showLabelsLabel.setText(Messages.GraticuleStyleConfigurator_Show_Labels);
        showLabels = new Button(container, SWT.CHECK);
        showLabels.setData(true);

        opacityLabel = new Label(container, SWT.NONE);
        opacityLabel.setText(Messages.GraticuleStyleConfigurator_Opacity);
        opacity = new Scale(container, SWT.BORDER);
        Rectangle clientArea = container.getClientArea();
        opacity.setBounds (clientArea.x, clientArea.y, 200, 64);
        opacity.setMaximum(255);
        opacity.setPageIncrement(5);        
        opacity.setSelection(100);
        
        messageLabel = new Label(container, SWT.WRAP);

        return container;
    }

    @Override
    protected void refresh() {
        getApplyAction().setEnabled(false);
        removeListeners();
        try {
            GraticuleStyle oldStyle = (GraticuleStyle) getStyleBlackboard().get(GraticuleStyle.ID);
            if (oldStyle == null) {
                oldStyle = GraticuleStyle.DEFAULT;
            }

            this.style = new GraticuleStyle(oldStyle);

            messageLabel.setText(""); //$NON-NLS-1$
            lineWidth.setSelection(style.getLineWidth());
            setLineStyle(style);
            fontColor.setData(style.getFontColor());
            updateColorButton(fontColor, fontColorLabel, fontColorPreview);
            lineColor.setData(style.getLineColor());
            updateColorButton(lineColor, lineColorLabel, lineColorPreview);
            showLabels.setData(style.isShowLabels());
            opacity.setSelection(style.getOpacity());

        } finally {
            addListeners();
        }
    }

    private void setLineStyle( GraticuleStyle style2 ) {
        switch( style.getLineStyle() ) {
        case ViewportGraphics.LINE_DASH:
            lineStyle.select(lineStyle.indexOf(LINE_DASH));
            break;
        case ViewportGraphics.LINE_DASHDOT:
            lineStyle.select(lineStyle.indexOf(LINE_DASHDOT));
            break;
        case ViewportGraphics.LINE_DASHDOTDOT:
            lineStyle.select(lineStyle.indexOf(LINE_DASHDOTDOT));
            break;
        case ViewportGraphics.LINE_DOT:
            lineStyle.select(lineStyle.indexOf(LINE_DOT));
            break;
        case ViewportGraphics.LINE_SOLID:
            lineStyle.select(lineStyle.indexOf(LINE_SOLID));
            break;

        default:

            throw new RuntimeException(Messages.bind(Messages.GridStyleConfigurator_1, style.getLineStyle()));
        }
    }

    void updateColorButtons() {
        updateColorButton(fontColor, fontColorLabel, fontColorPreview);
        updateColorButton(lineColor, lineColorLabel, lineColorPreview);        
    }
    
    void updateColorButton(Button color, Label label, Label preview) {
        Image oldImage = preview.getImage();

        Rectangle bounds = preview.getBounds();
        if (bounds.width == 0 || bounds.height == 0)
            return;

        Display display = preview.getDisplay();
        Image newImage = new Image(display, bounds.width, bounds.width);

        Color awtColor = (Color) color.getData();
        int red = awtColor.getRed();
        int green = awtColor.getGreen();
        int blue = awtColor.getBlue();
        org.eclipse.swt.graphics.Color color2 = new org.eclipse.swt.graphics.Color(display, red,
                green, blue);

        GC gc = new GC(newImage);
        try {
            gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
            gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
            int alpha = awtColor.getAlpha();
            gc.setAlpha(alpha);
            gc.setBackground(color2);
            gc.fillRectangle(1, 1, bounds.width - 2, bounds.height - 2);
        } finally {
            gc.dispose();
            color2.dispose();
        }

        preview.setImage(newImage);
        if (oldImage != null)
            oldImage.dispose();
        label.redraw();


    }

    @Override
    public void preApply() {
        if( lineWidth.isFocusControl() ) {
            kickWidget(lineWidth);
        } else if( lineStyle.isFocusControl() ) {
            kickWidget(lineStyle);
        }
    }

    /** 
     * Stupid hack so that when apply is pressed the value in the spinners is set.
     * Spinners don't have the displayed value until enter or the focus changes
     * from the spinner.  So I'm going to kick the spinner to get it to synchronize with
     * the displayed value.
     */
    private void kickWidget( Control widget ) {
        container.setFocus();
        widget.setFocus();
        while (widget.getDisplay().readAndDispatch());
    }

    public void handleEvent( Event e ) {

        if (e.type == SWT.KeyUp && e.character == SWT.CR) {
            makeActionDoStuff();
        } else {
            
            // Consume?
            if (!isChanged(e)) return;
            
            getApplyAction().setEnabled(true);

            // parses all the values and updates the style on the Style blackboard
            style.setLineColor((Color) lineColor.getData());
            style.setLineStyle(parseLineStyle());
            style.setLineWidth(lineWidth.getSelection());
            style.setFontColor((Color) fontColor.getData());
            style.setShowLabels((Boolean) showLabels.getData());  
            style.setOpacity((Integer) opacity.getSelection());  

            getStyleBlackboard().put(GraticuleStyle.ID, style);
        }

    }

    private boolean isChanged( Event e ) {

        if (style.getLineStyle() != parseLineStyle()) {
            return true;
        }

        if (!style.getLineColor().equals(lineColor.getData())) {
            return true;
        }

        if (style.getLineWidth() != lineWidth.getSelection()) {
            return true;
        }

        if (style.isShowLabels() != showLabels.getSelection()) {
            return true;
        }
        
        if (style.getOpacity() != opacity.getSelection()) {
            return true;
        }
        
        return false;
    }

    private int parseLineStyle() {
        String selectedString = selectedString(lineStyle);
        if (selectedString.equals(LINE_SOLID)) {
            return ViewportGraphics.LINE_SOLID;
        }
        if (selectedString.equals(LINE_DASH)) {
            return ViewportGraphics.LINE_DASH;
        }
        if (selectedString.equals(LINE_DOT)) {
            return ViewportGraphics.LINE_DOT;
        }
        if (selectedString.equals(LINE_DASHDOT)) {
            return ViewportGraphics.LINE_DASHDOT;
        }
        if (selectedString.equals(LINE_DASHDOTDOT)) {
            return ViewportGraphics.LINE_DASHDOTDOT;
        }
        throw new IllegalArgumentException(Messages.bind(Messages.GridStyleConfigurator_2, selectedString));
    }

    static String selectedString( Combo item ) {
        int selectionIndex = item.getSelectionIndex();
        return item.getItem(selectionIndex);
    }

}
