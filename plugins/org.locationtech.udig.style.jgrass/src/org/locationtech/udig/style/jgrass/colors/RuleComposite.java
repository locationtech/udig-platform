/**
 * uDig - User Friendly Desktop Internet GIS client
 * (C) HydroloGIS - www.hydrologis.com
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the HydroloGIS BSD
 * License v1.0 (http://udig.refractions.net/files/hsd3-v10.html).
 */
package org.locationtech.udig.style.jgrass.colors;

import org.eclipse.swt.SWT;
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
 * A composite that represents a facility to create JGrass color rules
 *
 * @author Andrea Antonello - www.hydrologis.com
 */
public class RuleComposite implements MouseListener, SelectionListener, KeyListener {

    private Label fromLabel;

    private Label toLabel;

    private Button enableRuleCheckButton;

    private Text fromValueText;

    private Text toValueText;

    private boolean isActive = true;

    private Rule rule = null;

    private Composite parent = null;

    public RuleComposite(ColorEditor colorEditor, Composite parent, int style, Rule rule) {
        this.parent = parent;

        GridData gridData10 = new GridData();
        gridData10.horizontalAlignment = GridData.FILL;
        gridData10.grabExcessHorizontalSpace = true;
        gridData10.verticalAlignment = GridData.CENTER;
        GridData gridData7 = new GridData();
        gridData7.horizontalAlignment = GridData.FILL;
        gridData7.grabExcessHorizontalSpace = true;
        gridData7.verticalAlignment = GridData.CENTER;
        GridData gridData11 = new GridData();
        gridData11.horizontalAlignment = GridData.END;
        gridData11.verticalAlignment = GridData.CENTER;
        GridData gridData9 = new GridData();
        gridData9.horizontalAlignment = GridData.FILL;
        gridData9.grabExcessHorizontalSpace = true;
        gridData9.verticalAlignment = GridData.FILL;
        GridData gridData8 = new GridData();
        gridData8.horizontalAlignment = GridData.CENTER;
        gridData8.grabExcessHorizontalSpace = true;
        gridData8.verticalAlignment = GridData.CENTER;
        GridData gridData6 = new GridData();
        gridData6.horizontalAlignment = GridData.FILL;
        gridData6.grabExcessHorizontalSpace = true;
        gridData6.grabExcessVerticalSpace = false;
        gridData6.verticalAlignment = GridData.FILL;
        GridLayout gridLayout1 = new GridLayout();
        gridLayout1.numColumns = 6;
        gridLayout1.makeColumnsEqualWidth = true;
        GridData gridData5 = new GridData();
        gridData5.grabExcessHorizontalSpace = true;
        gridData5.verticalAlignment = GridData.BEGINNING;
        gridData5.horizontalAlignment = GridData.FILL;
        parent.setLayoutData(gridData5);
        parent.setLayout(gridLayout1);
        fromLabel = new Label(parent, SWT.BORDER);
        fromLabel.setText(""); //$NON-NLS-1$
        fromLabel.setLayoutData(gridData6);
        fromLabel.addMouseListener(this);
        fromValueText = new Text(parent, SWT.BORDER);
        fromValueText.setLayoutData(gridData7);
        Label dummylabel = new Label(parent, SWT.NONE);
        dummylabel.setText("-"); //$NON-NLS-1$
        dummylabel.setLayoutData(gridData8);
        toLabel = new Label(parent, SWT.BORDER);
        toLabel.setText(""); //$NON-NLS-1$
        toLabel.setLayoutData(gridData9);
        toLabel.addMouseListener(this);
        toValueText = new Text(parent, SWT.BORDER);
        toValueText.setLayoutData(gridData10);
        enableRuleCheckButton = new Button(parent, SWT.CHECK);
        enableRuleCheckButton.setLayoutData(gridData11);
        enableRuleCheckButton.addSelectionListener(this);

        fromValueText.addKeyListener(this);
        toValueText.addKeyListener(this);

        /**
         * set values if needed
         */
        if (rule == null) {
            this.rule = new Rule();
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
        fromLabel.setBackground(this.rule.getFromColor());
        toLabel.setBackground(this.rule.getToColor());

    }

    /**
     * @return the rule that created the widget and knows its last state
     */
    public Rule getRule() {
        return rule;
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {

    }

    @Override
    public void mouseDown(MouseEvent e) {
        Object o = e.getSource();
        if (o instanceof Label) {
            Label l = (Label) o;
            ColorDialog c = new ColorDialog(parent.getShell());
            c.setRGB(new RGB(l.getBackground().getRed(), l.getBackground().getGreen(),
                    l.getBackground().getBlue()));
            RGB color = c.open();
            if (color == null) {
                return;
            }
            Color colorObject = new Color(parent.getDisplay(), color);

            ((Label) o).setBackground(colorObject);
            if (o.equals(fromLabel)) {
                rule.setFromColor(colorObject);
            } else if (o.equals(toLabel)) {
                rule.setToColor(colorObject);
            }

        }
    }

    @Override
    public void mouseUp(MouseEvent e) {

    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {

    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        Button b = (Button) e.getSource();
        if (b.equals(enableRuleCheckButton)) {
            isActive = b.getSelection();
            rule.setActive(isActive);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        Object o = e.widget;
        if (o instanceof Text) {
            float[] tmp = rule.getFromToValues();
            try {
                tmp[0] = Float.parseFloat(fromValueText.getText());
            } catch (NumberFormatException ne) {
                fromValueText.setText("0"); //$NON-NLS-1$
                return;
            }
            try {
                tmp[1] = Float.parseFloat(toValueText.getText());
            } catch (NumberFormatException ne) {
                toValueText.setText("0"); //$NON-NLS-1$
                return;
            }
            rule.setFromToValues(tmp);
        }

    }
}
