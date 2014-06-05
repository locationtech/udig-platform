/* uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.mapgraphic.grid;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.locationtech.udig.mapgraphic.grid.GridStyle.Type;
import org.locationtech.udig.mapgraphic.internal.Messages;
import org.locationtech.udig.project.internal.Layer;
import org.locationtech.udig.style.IStyleConfigurator;
import org.locationtech.udig.ui.ColorEditor;
import org.locationtech.udig.ui.graphics.ViewportGraphics;

/**
 * Edit GridStyle objects
 * 
 * @author Jesse
 * @since 1.1.0
 */
public class GridStyleConfigurator extends IStyleConfigurator implements Listener {

    private static final String LINE_DASH = "dash"; //$NON-NLS-1$
    private static final String LINE_DASHDOT = "dash-dot"; //$NON-NLS-1$
    private static final String LINE_DASHDOTDOT = "dash-dot-dot"; //$NON-NLS-1$
    private static final String LINE_DOT = "dot"; //$NON-NLS-1$
    private static final String LINE_SOLID = "solid"; //$NON-NLS-1$
	private static final String[] LINE_STYLES = new String[] { LINE_SOLID,
			LINE_DASH, LINE_DOT, LINE_DASHDOT, LINE_DASHDOTDOT };

    SpacerController xSpacer, ySpacer;
    Combo lineStyle;
    Spinner lineWidth;
    Label xlabel, ylabel, message, colorLabel, lineStyleLabel, lineWidthLabel;
    private ColorEditor lineColor;
    
    private GridStyle style;
    private UnitListener xUnitListener, yUnitListener;
    
    private Composite comp;

    @Override
    public boolean canStyle( Layer aLayer ) {
        return aLayer.hasResource(GridMapGraphic.class)
                && aLayer.getStyleBlackboard().get(GridStyle.ID) != null;
    }

    @Override
    public void createControl( Composite parent ) {
        parent.setLayout(new FillLayout());

        Composite comp = createWidgets(parent);
        addListeners();
        layoutWidgets(comp);
    }

    private void layoutWidgets( Composite comp ) {
        comp.setLayout(new GridLayout(3, false));

        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        xlabel.setLayoutData(layoutData);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        xSpacer.getSpinner().setLayoutData(layoutData);
        layoutData = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
        xSpacer.getUnit().setLayoutData(layoutData);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        ylabel.setLayoutData(layoutData);
        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        ySpacer.getSpinner().setLayoutData(layoutData);
        layoutData = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
        ySpacer.getUnit().setLayoutData(layoutData);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        lineStyleLabel.setLayoutData(layoutData);
        layoutData = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
        lineStyle.setLayoutData(layoutData);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        lineWidthLabel.setLayoutData(layoutData);
        layoutData = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
        lineWidth.setLayoutData(layoutData);

        layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        colorLabel.setLayoutData(layoutData);
       
        layoutData = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
        lineColor.getButton().setLayoutData(layoutData);
        
        layoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        message.setLayoutData(layoutData);

    }

    private void addListeners() {

        if (xUnitListener == null) {
            xUnitListener = new UnitListener(ySpacer, xSpacer, message);
            yUnitListener = new UnitListener(xSpacer, ySpacer, message);
        }
        xSpacer.addListeners(xUnitListener, this);
        ySpacer.addListeners(yUnitListener, this);

        lineColor.getButton().addListener(SWT.Selection, this);
        lineStyle.addListener(SWT.Modify, this);
        lineStyle.addListener(SWT.KeyUp, this);
        lineWidth.addListener(SWT.Modify, this);
        lineWidth.addListener(SWT.KeyUp, this);
    }

    private void removeListeners() {
        xSpacer.removeListeners(xUnitListener, this);
        ySpacer.removeListeners(yUnitListener, this);

        lineColor.getButton().removeListener(SWT.Selection, this);
        lineStyle.removeListener(SWT.Modify, this);
        lineWidth.removeListener(SWT.Modify, this);
        lineStyle.removeListener(SWT.KeyUp, this);
        lineWidth.removeListener(SWT.KeyUp, this);
    }

    private Composite createWidgets( Composite parent ) {
        comp = new Composite(parent, SWT.NONE);

        xlabel = new Label(comp, SWT.NONE);
        xlabel.setText(Messages.GridStyleConfigurator_HSpacing);
        Spinner x = new Spinner(comp, SWT.BORDER);
        Combo xUnit = new Combo(comp, SWT.READ_ONLY);

        xSpacer = new SpacerController(x, xUnit);

        ylabel = new Label(comp, SWT.NONE);
        ylabel.setText(Messages.GridStyleConfigurator_VSpacing);
        Spinner y = new Spinner(comp, SWT.BORDER);
        Combo yUnit = new Combo(comp, SWT.READ_ONLY);

        ySpacer = new SpacerController(y, yUnit);

        colorLabel = new Label(comp, SWT.NONE);
        colorLabel.setText(Messages.GridStyleConfigurator_LineColor);

        lineColor = new ColorEditor(comp);
        
        lineStyleLabel = new Label(comp, SWT.NONE);
        lineStyleLabel.setText(Messages.GridStyleConfigurator_LineStyle);
        lineStyle = new Combo(comp, SWT.READ_ONLY);
        lineStyle.setItems(LINE_STYLES);
        lineStyle.select(0);

        lineWidthLabel = new Label(comp, SWT.NONE);
        lineWidthLabel.setText(Messages.GridStyleConfigurator_LineWidth);
        lineWidth = new Spinner(comp, SWT.BORDER);
        lineWidth.setIncrement(1);
        lineWidth.setDigits(0);
        lineWidth.setMinimum(1);

        message = new Label(comp, SWT.WRAP);

        return comp;
    }

    @Override
    protected void refresh() {
        getApplyAction().setEnabled(false);
        removeListeners();
        try {
            GridStyle oldStyle = (GridStyle) getStyleBlackboard().get(GridStyle.ID);
            if (oldStyle == null) {
                oldStyle = GridStyle.DEFAULT_STYLE;
            }

            this.style = new GridStyle(oldStyle);

            message.setText(""); //$NON-NLS-1$
            lineWidth.setSelection(style.getLineWidth());
            setLineStyle(style);
            lineColor.setColor(style.getColor());

            switch( style.getType() ) {
            case SCREEN:
                xSpacer.setPixelSpacing(style.getGridSize()[0]);
                ySpacer.setPixelSpacing(style.getGridSize()[1]);
                break;
            case WORLD:
                xSpacer.setWorldSpacing(style.getGridSize()[0]);
                ySpacer.setWorldSpacing(style.getGridSize()[1]);
                break;

            default:
                throw new RuntimeException(Messages.bind(Messages.GridStyleConfigurator_0, style.getType() ));
            }
            xSpacer.getUnit().setData(selectedString(xSpacer.getUnit()));
            ySpacer.getUnit().setData(selectedString(ySpacer.getUnit()));
        } finally {
            addListeners();
        }
    }

    private void setLineStyle( GridStyle style2 ) {
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

    @Override
    public void preApply() {
        if( xSpacer.getSpinner().isFocusControl() ){
            kickWidget(xSpacer.getSpinner());
        } else if( ySpacer.getSpinner().isFocusControl() ){
            kickWidget( ySpacer.getSpinner() );
        } else if( lineWidth.isFocusControl() ) {
            kickWidget(lineWidth);
        } else if( lineStyle.isFocusControl() ) {
            kickWidget(lineStyle);
        }
    }

    /** 
     * Workaround solution that when apply is pressed the value in the spinners is set.
     * Spinners don't have the displayed value until enter or the focus changes
     * from the spinner.  So I'm going to kick the spinner to get it to synchronize with
     * the displayed value.
     */
    private void kickWidget( Control widget ) {
        comp.setFocus();
        widget.setFocus();
        while (widget.getDisplay().readAndDispatch());
    }

    public void handleEvent( Event e ) {

        if (e.type == SWT.KeyUp && e.character == SWT.CR) {
            makeActionDoStuff();
        } else {
            boolean changed = isChanged(e);
            if (!changed)
                return;
            getApplyAction().setEnabled(true);
            if (e.widget != xSpacer.getUnit() && e.widget != ySpacer.getUnit()) {
                message.setText(""); //$NON-NLS-1$
            }
            // parses all the values and updates the style on the Style blackboard

            style.setColor(lineColor.getColor());
            style.setLineStyle(parseLineStyle());
            style.setLineWidth(lineWidth.getSelection());

            Type type;

            if (xSpacer.getUnit().getSelectionIndex() == 0) {
                type = Type.SCREEN;
            } else {
                type = Type.WORLD;
            }

            style.setType(type);

            style.setGridSize(xSpacer.getSpacing(), ySpacer.getSpacing());
            getStyleBlackboard().put(GridStyle.ID, style);
        }

    }

    private boolean isChanged( Event e ) {

        if (style.getLineStyle() != parseLineStyle()) {
            return true;
        }

        if (!style.getColor().equals(lineColor.getColor())) {
            return true;
        }

        if (style.getLineWidth() != lineWidth.getSelection()) {
            return true;
        }

        if (!xSpacer.getUnit().getData().equals(selectedString(xSpacer.getUnit()))) {
            return true;
        }

        if (!ySpacer.getUnit().getData().equals(selectedString(ySpacer.getUnit()))) {
            return true;
        }

        if (Math.abs(xSpacer.getSpacing() - style.getGridSize()[0]) > 0.0000000001) {
            return true;
        }

        if (Math.abs(ySpacer.getSpacing() - style.getGridSize()[1]) > 0.0000000001) {
            return true;
        }

        if (e.widget == lineWidth || e.widget == xSpacer.getSpinner()
                || e.widget == ySpacer.getSpinner()) {
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
