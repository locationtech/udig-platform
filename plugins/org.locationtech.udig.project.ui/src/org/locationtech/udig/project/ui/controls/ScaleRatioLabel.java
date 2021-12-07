/**
 * uDig - User Friendly Desktop Internet GIS client
 * http://udig.refractions.net
 * (C) 2012, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 */
package org.locationtech.udig.project.ui.controls;

import java.text.NumberFormat;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.locationtech.udig.project.internal.commands.SetScaleCommand;
import org.locationtech.udig.project.render.IViewportModel;
import org.locationtech.udig.project.render.IViewportModelListener;
import org.locationtech.udig.project.render.ViewportModelEvent.EventType;
import org.locationtech.udig.project.ui.internal.MapPart;
import org.locationtech.udig.project.ui.internal.Messages;
import org.locationtech.udig.ui.ZoomingDialog;

/**
 * Displays the current scale ratio on the status bar.
 *
 * @author Andrea Aime
 */
public class ScaleRatioLabel extends ContributionItem implements KeyListener, FocusListener {
    /** ScaleRatioLabel editor field */
    private final MapPart mapPart;

    private static final int MINIMUM_WIDTH = 80;

    private static final String SCALE_ITEM_ID = "Current scale"; //$NON-NLS-1$

    private NumberFormat nf = NumberFormat.getIntegerInstance();

    private Combo combo;

    private IViewportModel viewportModel;

    /** Listens to viewport changes and updates the displayed scale accordingly */
    IViewportModelListener listener = event -> {
        if (event.getType() == EventType.CRS || event.getType() == EventType.BOUNDS) {
            Display display = PlatformUI.getWorkbench().getDisplay();
            if (display != null && !display.isDisposed()) {
                display.asyncExec(this::update);
            }
        }
    };

    public ScaleRatioLabel(MapPart mapPart) {
        super(SCALE_ITEM_ID);
        this.mapPart = mapPart;

        if (mapPart.getMap() != null) {
            setViewportModel(mapPart.getMap().getViewportModel());
        }
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public void dispose() {
        if (viewportModel != null) {
            viewportModel.removeViewportModelListener(listener);
            viewportModel = null;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (combo.getText().contains(":")) //$NON-NLS-1$
            formatForEditing();
        if (!isLegalKey(e)) {
            e.doit = false;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.character == SWT.Selection) {
            go();
        } else if (e.character == SWT.ESC) {
            update();
        }

    }

    @Override
    public void focusGained(FocusEvent e) {
        formatForEditing();
    }

    @Override
    public void focusLost(FocusEvent e) {
        update();
    }

    @Override
    public void update() {
        if (combo == null || combo.isDisposed())
            return;

        if (viewportModel != null) {
            combo.removeAll();
            for (double scaleDenominator : viewportModel.getPreferredScaleDenominators()) {
                String item = toLabel(scaleDenominator);
                combo.add(item);
            }
            combo.setText(toLabel(viewportModel.getScaleDenominator())); // $NON-NLS-1$
            combo.setToolTipText(combo.getText());
        } else {
            combo.setText(""); //$NON-NLS-1$
        }
    }

    @Override
    public void fill(Composite parent) {
        new Label(parent, SWT.SEPARATOR);
        combo = new Combo(parent, SWT.BORDER | SWT.CENTER);

        combo.addKeyListener(this);
        combo.addFocusListener(this);
        combo.addListener(SWT.MouseDown, e -> {
            if (combo.getText().contains(":")) //$NON-NLS-1$
                formatForEditing();
        });
        combo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (combo.getText().contains(":")) //$NON-NLS-1$
                    formatForEditing();
                go();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing here
            }
        });
        StatusLineLayoutData data = new StatusLineLayoutData();
        data.widthHint = MINIMUM_WIDTH;
        combo.setLayoutData(data);
        update();
    }

    /**
     * Sets the current viewport model. Should be called every time the map changes in order update
     * the shared ratio label
     */
    private void setViewportModel(IViewportModel newViewportModel) {
        if (newViewportModel != this.viewportModel) {
            if (viewportModel != null) {
                viewportModel.removeViewportModelListener(listener);
            }
            viewportModel = newViewportModel;
            viewportModel.addViewportModelListener(listener);
            update();
        }
    }

    private String toLabel(double scaleDenominator) {
        return "1:" + nf.format(scaleDenominator); //$NON-NLS-1$
    }

    private boolean isLegalKey(KeyEvent e) {
        char c = e.character;

        if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6'
                || c == '7' || c == '8' || c == '9' || c == SWT.DEL || c == SWT.BS) {
            return true;
        }

        switch (e.keyCode) {
        case SWT.ARROW_LEFT:
        case SWT.ARROW_RIGHT:
        case SWT.HOME:
        case SWT.END:
        case SWT.OK:
            return true;
        default:
            return false;
        }
    }

    private void go() {
        String newScale = combo.getText().trim();
        try {
            double d = nf.parse(newScale.replace(" ", "")).doubleValue(); //$NON-NLS-1$ //$NON-NLS-2$
            SetScaleCommand command = new SetScaleCommand(d);
            this.mapPart.getMap().sendCommandASync(command);
        } catch (Exception e) {
            org.eclipse.swt.graphics.Rectangle start = ZoomingDialog.calculateBounds(combo);

            ZoomingDialog.openErrorMessage(start, this.mapPart.getMapSite().getShell(),
                    Messages.MapEditor_illegalScaleTitle, Messages.MapEditor_illegalScaleMessage);
        }
    }

    private void formatForEditing() {
        String text = combo.getText();
        if (text.contains(":")) //$NON-NLS-1$
            text = text.substring(2);
        combo.setText(text);
        int end = combo.getText().length();
        combo.setSelection(new Point(0, end));
    }
}
