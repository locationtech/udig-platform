/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.advanced.raster;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A composite that represents a facility to create color rules
 * 
 * @author Andrea Antonello - www.hydrologis.com
 * @author Frank Gasdorf
 */
public class CoverageRuleComposite implements MouseListener, SelectionListener, KeyListener, DisposeListener {

    private Label fromLabel;
    private Label toLabel;
    private Button enableRuleCheckButton;
    private Text fromValueText;
    private Text toValueText;
    private boolean isActive = true;
    private CoverageRule rule = null;
    private Composite parent = null;
    private Text alphaText;
    private Color fromLabelBGColor = null;
    private Color toLabelBGColor = null;
    private Map<RGB, Color> colors = new HashMap<RGB, Color>();

    public CoverageRuleComposite( Composite parent, int style, CoverageRule rule ) {
        this.parent = parent;
        parent.addDisposeListener(this);
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 7;
        gridLayout1.makeColumnsEqualWidth = false;

        GridData gridData5 = new GridData();
        gridData5.grabExcessHorizontalSpace = true;
        gridData5.verticalAlignment = GridData.BEGINNING;
        gridData5.horizontalAlignment = GridData.FILL;
        parent.setLayoutData(gridData5);
        parent.setLayout(gridLayout1);

        int colorLabelWidth = 40;
        int textWidth = 130;
        int alphaTextWidth = 40;

        GridData fromLabelGD = new GridData();
        fromLabelGD.horizontalAlignment = GridData.FILL;
        fromLabelGD.verticalAlignment = GridData.FILL;
        fromLabelGD.widthHint = colorLabelWidth;
        fromLabel = new Label(parent, SWT.BORDER);
        fromLabel.setText(""); //$NON-NLS-1$
        fromLabel.setLayoutData(fromLabelGD);
        fromLabel.addMouseListener(this);

        GridData fromValueTextGD = new GridData();
        fromValueTextGD.horizontalAlignment = GridData.FILL;
        fromValueTextGD.verticalAlignment = GridData.CENTER;
        fromValueTextGD.widthHint = textWidth;
        fromValueText = new Text(parent, SWT.BORDER);
        fromValueText.setLayoutData(fromValueTextGD);

        GridData dummyLabelGD = new GridData();
        dummyLabelGD.horizontalAlignment = GridData.CENTER;
        dummyLabelGD.verticalAlignment = GridData.CENTER;
        dummyLabelGD.widthHint = 5;
        Label dummylabel = new Label(parent, SWT.NONE);
        dummylabel.setText("-"); //$NON-NLS-1$
        dummylabel.setLayoutData(dummyLabelGD);

        GridData toLabelGD = new GridData();
        toLabelGD.horizontalAlignment = GridData.FILL;
        toLabelGD.verticalAlignment = GridData.FILL;
        toLabelGD.widthHint = colorLabelWidth;
        toLabel = new Label(parent, SWT.BORDER);
        toLabel.setText(""); //$NON-NLS-1$
        toLabel.setLayoutData(toLabelGD);
        toLabel.addMouseListener(this);

        GridData toValueTextGD = new GridData();
        toValueTextGD.horizontalAlignment = GridData.FILL;
        toValueTextGD.verticalAlignment = GridData.CENTER;
        toValueTextGD.widthHint = textWidth;
        toValueText = new Text(parent, SWT.BORDER);
        toValueText.setLayoutData(toValueTextGD);

        GridData enableRuleCheckButtonGD = new GridData();
        enableRuleCheckButtonGD.horizontalAlignment = GridData.END;
        enableRuleCheckButtonGD.verticalAlignment = GridData.CENTER;
        enableRuleCheckButton = new Button(parent, SWT.CHECK);
        enableRuleCheckButton.setLayoutData(enableRuleCheckButtonGD);
        enableRuleCheckButton.addSelectionListener(this);

        GridData alphaGD = new GridData();
        alphaGD.horizontalAlignment = GridData.FILL;
        alphaGD.verticalAlignment = GridData.CENTER;
        alphaGD.widthHint = alphaTextWidth;
        alphaText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
        alphaText.setLayoutData(alphaGD);
        alphaText.setText("1.0"); //$NON-NLS-1$

        alphaText.addKeyListener(this);
        fromValueText.addKeyListener(this);
        toValueText.addKeyListener(this);

        /*
         * set values if needed
         */
        if (rule == null) {
            this.rule = new CoverageRule();
        } else {
            this.rule = rule;
        }
        enableRuleCheckButton.setSelection(this.rule.isActive());
        String value = null;
        if (this.rule.getFromToValues()[0] != this.rule.getFromToValues()[0]) {
            value = ""; //$NON-NLS-1$
        } else {
            value = String.valueOf(this.rule.getFromToValues()[0]);
        }
        fromValueText.setText(value);
        if (this.rule.getFromToValues()[1] != this.rule.getFromToValues()[1]) {
            value = ""; //$NON-NLS-1$
        } else {
            value = String.valueOf(this.rule.getFromToValues()[1]);
        }
        toValueText.setText(value);
        
        this.fromLabelBGColor = registerAndSetBackground(fromLabel, this.rule.getFromColor());
        this.toLabelBGColor = registerAndSetBackground(toLabel, this.rule.getToColor());

        alphaText.setText(String.valueOf(this.rule.getOpacity()));
    }

    private Color registerAndSetBackground(final Label label, final RGB rgbValue) {
        Color color = null;
        if (rgbValue != null && label != null && !label.isDisposed()) {
            // test if color has been created 
            color = colors.get(rgbValue);
            
            if (color == null) {
                color = new Color(label.getDisplay(), rgbValue);
                // required to dispose later
                colors.put(rgbValue, color);
            }
            
            label.setBackground(color);
       }
        return color;
    }

    /**
     * @return the rule that created the widget and knows its last state
     */
    public CoverageRule getRule() {
        return rule;
    }

    public void mouseDoubleClick( MouseEvent e ) {
    }

    public void mouseDown( MouseEvent e ) {
        Object o = e.getSource();
        if (o instanceof Label) {
            Label l = (Label) o;
            ColorDialog c = new ColorDialog(parent.getShell());
            c.setRGB(new RGB(l.getBackground().getRed(), l.getBackground().getGreen(), l.getBackground().getBlue()));
            RGB rgbColor = c.open();
            if (rgbColor == null) {
                return;
            }
            

            if (o.equals(fromLabel)) {
                this.fromLabelBGColor = registerAndSetBackground(fromLabel, rgbColor);
                rule.setFromColor(rgbColor);
            } else if (o.equals(toLabel)) {
                this.toLabelBGColor = registerAndSetBackground(toLabel, rgbColor);
                rule.setToColor(rgbColor);
            }

        }
    }

    public void mouseUp( MouseEvent e ) {
    }

    public void widgetDefaultSelected( SelectionEvent e ) {
    }

    public void widgetSelected( SelectionEvent e ) {
        Button b = (Button) e.getSource();
        if (b.equals(enableRuleCheckButton)) {
            isActive = b.getSelection();
            rule.setActive(isActive);
        }
    }

    public void keyPressed( KeyEvent e ) {

    }

    public void keyReleased( KeyEvent e ) {
        Object o = e.widget;
        if (o instanceof Text) {
            double[] tmp = rule.getFromToValues();
            try {
                tmp[0] = Double.parseDouble(fromValueText.getText());
            } catch (NumberFormatException ne) {
                fromValueText.setText("0"); //$NON-NLS-1$
                return;
            }
            try {
                tmp[1] = Double.parseDouble(toValueText.getText());
            } catch (NumberFormatException ne) {
                toValueText.setText("0"); //$NON-NLS-1$
                return;
            }
            rule.setFromToValues(tmp);
            try {
                double alpha = Double.parseDouble(alphaText.getText());
                rule.setOpacity(alpha);
            } catch (NumberFormatException ne) {
                alphaText.setText("1.0"); //$NON-NLS-1$
                return;
            }
        }
    }

    @Override
    public void widgetDisposed(DisposeEvent e) {
        // dispose system resources such as colors here
        disposeInternal(fromLabelBGColor);
        disposeInternal(toLabelBGColor);
        fromLabelBGColor = null;
        toLabelBGColor = null;

        for (Entry<RGB, Color> entry : colors.entrySet()) {
            disposeInternal(entry.getValue());
        }
        colors.clear();
    }

    private void disposeInternal(final Color color) {
        if (color != null && !color.isDisposed()) {
            color.dispose();
        }
    }
    

}
